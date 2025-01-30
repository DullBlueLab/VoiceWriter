package com.dullbluelab.voicewriter3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dullbluelab.voicewriter3.MainActivity
import com.dullbluelab.voicewriter3.VoiceWriterApplication
import com.dullbluelab.voicewriter3.data.SymbolTable
import com.dullbluelab.voicewriter3.unit.ConvertMode
import com.dullbluelab.voicewriter3.data.UserPreferencesRepository
import com.dullbluelab.voicewriter3.data.WriterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class KeyType {
    Number, Symbol, Mode, BackSpace,
    Uppercase, HeadUpper, Lowercase,
    Cancel, Space, Return,
    Left, Right, Dot,
    None
}
val INITIAL_KEY_POSITION = KeyType.None

val INITIAL_CONVERT_MODE = ConvertMode.Coder

class WriterViewModel(
    private val preferences: UserPreferencesRepository,
    private val repository: WriterRepository
) : ViewModel() {

    data class UiState(
        var sourceText: String = "",
        var inputText: String = "",
        var keyPosition: KeyType = INITIAL_KEY_POSITION,
        var convertMode: ConvertMode = INITIAL_CONVERT_MODE,
        var symbolPadOn: Boolean = false,
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class SettingUiState(
        var cursorChar: String = "",
        var symbolPadPriority: Boolean = true,
        val symbolList: List<SymbolTable> = listOf()
    )
    private val _settingUi = MutableStateFlow(SettingUiState())
    val settingUi: StateFlow<SettingUiState> = _settingUi.asStateFlow()

    val setting: StateFlow<UserPreferencesRepository.State> = preferences.setting.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferencesRepository.State()
    )

    private var sourceText: String = ""
    private var inputText: String = ""
    private var cursorPosition: Int = 0
    private var activity: MainActivity? = null
    private var launchSpoken: (() -> Unit)? = null
    var screenPosition: String = ""

    fun sourceText() = sourceText

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VoiceWriterApplication)
                WriterViewModel(application.userPreferencesRepository, application.container.writerRepository)
            }
        }
    }

    private fun updateUi() {
        val text = textInsert(sourceText, cursorPosition, setting.value.cursorChar)
        _uiState.update { state ->
            state.copy(
                sourceText = text,
                inputText = inputText,
            )
        }
    }

    // public
    fun isInitialized(): Boolean = (activity != null)

    // public
    fun setup(activity: MainActivity) {
        viewModelScope.launch {
            repository.initDatabase()
            setSettingUi()
            updateUi()
        }
        this.activity = activity
        launchSpoken = {
            this.activity?.let { it.launchSpeechRecognizer { list -> spoken(list) } }
        }
    }

    private fun setSettingUi(cursorChar: String = setting.value.cursorChar) {
        viewModelScope.launch {
            repository.getSymbolList { list ->
                _settingUi.update {
                    SettingUiState(
                        cursorChar = cursorChar,
                        symbolPadPriority = setting.value.symbolPadPriority,
                        symbolList = list
                    )
                }
            }
        }
    }

    // public
    fun changeSymbolToken(symbol: String, token: String) {
        viewModelScope.launch {
            repository.changeSymbolToken(symbol, token)
            setSettingUi()
        }
    }

    // public
    fun changeCursorChar(chars: String) {
        viewModelScope.launch {
            if (chars.isNotEmpty()) preferences.updateCursorChar(chars)
            setSettingUi(chars)
        }
    }

    // public
    fun clickText(offset: Int) {
        cursorPosition =
            if (offset > sourceText.length) sourceText.length else offset
        updateUi()
    }

    // public
    fun clickKey(type: KeyType) {
        when (type) {
            KeyType.Number, KeyType.Symbol,
            KeyType.Uppercase, KeyType.HeadUpper, KeyType.Lowercase -> {
                updateShiftKey(type)
                launchSpoken?.let { it() }
            }
            KeyType.Mode -> {
                updateShiftKey(KeyType.None)
                toggleMode()

            }
            KeyType.BackSpace -> {
                updateShiftKey(KeyType.None)
                backSpace()
                updateUi()
            }
            KeyType.Cancel -> {
                updateShiftKey(KeyType.None)
                inputText = ""
                updateUi()
            }
            KeyType.Return -> {
                updateShiftKey(KeyType.None)
                if (uiState.value.inputText.isNotEmpty()) input()
                else input("\n")
                updateUi()
            }
            KeyType.Space -> {
                updateShiftKey(KeyType.None)
                if (uiState.value.inputText.isNotEmpty()) input()
                input(" ")
                updateUi()
            }
            KeyType.Left -> {
                updateShiftKey(KeyType.None)
                if (cursorPosition > 0) {
                    cursorPosition --
                    updateUi()
                }
            }
            KeyType.Right -> {
                updateShiftKey(KeyType.None)
                if (cursorPosition < sourceText.length) {
                    cursorPosition ++
                    updateUi()
                }
            }
            KeyType.Dot -> {
                updateShiftKey(KeyType.None)
                if (uiState.value.inputText.isNotEmpty()) input()
                input(".")
                updateUi()
            }
            KeyType.None -> {
                updateShiftKey(KeyType.None)
            }
        }
    }

    private fun toggleMode() {
        val mode =
            if (_uiState.value.convertMode == ConvertMode.Coder) ConvertMode.Writer
            else ConvertMode.Coder
        _uiState.update { state ->
            state.copy(
                convertMode = mode
            )
        }
        repository.changeConvertMode(mode)
    }

    private fun updateShiftKey(type: KeyType) {
        _uiState.update { state ->
            state.copy(
                keyPosition = type
            )
        }
        repository.changeKeyPosition(type)
    }

    private fun input(text: String = inputText) {
        sourceText = textInsert(sourceText, cursorPosition, text)
        cursorPosition += text.length
        inputText = ""
    }

    private fun textInsert(text: String, position: Int, add: String): String {
        val length = text.length
        val newText =
            if (position == 0) {
                add + text
            }
            else if (position < length) {
                text.substring(0, position) + add + text.substring(position, length)
            }
            else {
                text + add
            }
        return newText
    }

    private fun backSpace() {
        if (inputText.isNotEmpty()) {
            val text = inputText
            if (text.length < 2) inputText = ""
            else inputText = text.substring(0, (text.length - 1))
        }
        else {
            if (cursorPosition > 0 && cursorPosition <= sourceText.length) {
                cursorPosition --
                sourceText = textRemoveAt(sourceText, cursorPosition)
            }
        }
    }

    private fun textRemoveAt(text: String, position: Int): String {
        val length = text.length
        if (length < 1) return text
        val newText = when {
            (position < 1) -> text
            (position == 1) -> text.substring(1, length)
            (position < (length - 1)) ->
                text.substring(0, position) + text.substring((position + 1), length)
            (position == (length - 1)) -> text.substring(0, position)
            else -> text
        }
        return newText
    }

    // public
    fun clickSymbol(chars: String) {
        inputText += chars
        updateUi()
    }

    private fun spoken(list: ArrayList<String>?) {
        repository.spoken(list, inputText) { convert ->
            inputText = convert
            updateUi()
        }
    }

    // public
    fun changeInputText(text: String) {
        inputText = text
        updateUi()
    }

    // public
    fun symbolPadOn(flag: Boolean, flagEnter: Boolean = true) {
        if (inputText.isNotEmpty() && flagEnter) input() else inputText = ""
        _uiState.update { state ->
            state.copy(
                symbolPadOn = flag
            )
        }
        updateUi()
    }
}