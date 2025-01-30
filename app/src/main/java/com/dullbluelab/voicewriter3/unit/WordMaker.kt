package com.dullbluelab.voicewriter3.unit

import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.dullbluelab.voicewriter3.data.Symbols
import com.dullbluelab.voicewriter3.ui.INITIAL_CONVERT_MODE
import com.dullbluelab.voicewriter3.ui.INITIAL_KEY_POSITION
import com.dullbluelab.voicewriter3.ui.KeyType

enum class ConvertMode {
    Writer, Coder
}

class WordMaker(private val symbols: Symbols) {

    var keyPosition: KeyType = INITIAL_KEY_POSITION
    var convertMode: ConvertMode = INITIAL_CONVERT_MODE
    var closeBracketStack: String = ""

    fun spoken(list: ArrayList<String>?, inputText: String, result: (String) -> Unit) {
        val scope =  CoroutineScope(Job() + Dispatchers.Default)
        scope.launch {
            var convert = inputText
            val text = list?.get(0)
            text?.let {
                val words = text.split(' ')
                val lastWord = words.last()
                words.forEach { word ->
                    when (keyPosition) {
                        KeyType.Uppercase -> {
                            if (convert.isNotEmpty()) {
                                convert +=
                                    if (convertMode == ConvertMode.Writer) " "
                                    else "_"
                            }
                            convertText(word, KeyType.Uppercase) { text ->
                                convert += text
                                if (word == lastWord) result(convert)
                            }
                        }

                        KeyType.HeadUpper -> {
                            if (convertMode == ConvertMode.Writer) {
                                if (convert.isNotEmpty()) convert += " "
                            }
                            convertText(word, KeyType.HeadUpper) { text ->
                                convert += text
                                if (word == lastWord) result(convert)
                            }
                        }

                        KeyType.Lowercase -> {
                            if (convertMode == ConvertMode.Writer) {
                                if (convert.isNotEmpty()) convert += " "
                                convert += word
                            } else {
                                val type =
                                    if (convert.isNotEmpty()) KeyType.HeadUpper else KeyType.Lowercase
                                convertText(word, type) { text ->
                                    convert += text
                                    if (word == lastWord) result(convert)
                                }
                            }
                        }

                        else -> {
                            convertText(word, keyPosition) { text ->
                                convert += text
                                if (word == lastWord) result(convert)
                            }
                        }
                    }
                }
            } ?: result(convert)
        }
    }

    private suspend fun convertText(text: String, type: KeyType, result: (String) -> Unit) {
        if (text.isDigitsOnly()) {
            result(text)
        }
        else if (text.isNotEmpty()){
            if (type == KeyType.Symbol) {
                convertSymbol(text) { symbol ->
                    result(symbol)
                }
            }
            else {
                val newText = when (type) {
                    KeyType.Lowercase -> {
                        text.lowercase()
                    }
                    KeyType.HeadUpper -> {
                        if (text.length > 1)
                            text.substring(0, 1).uppercase() + text.substring(1, text.length).lowercase()
                        else
                            text.uppercase()
                    }
                    KeyType.Uppercase -> {
                        text.uppercase()
                    }
                    else -> text
                }
                result(newText)
            }
        }
    }

    private suspend fun convertSymbol(text: String, result: (String) -> Unit) {
        var symbol = ""
        if (Symbols.ENUM_TEXT.contains(text)) {
            symbol = text
        }
        else if (text == Symbols.CLOSE_TOKEN) {
            if (closeBracketStack.isNotEmpty()) {
                val len = closeBracketStack.length
                val pos = len - 1
                symbol = closeBracketStack.substring(pos, len)
                closeBracketStack = if (pos > 0) closeBracketStack.substring(0, pos) else ""
                result(symbol)
            }
        }
        else {
            symbols.getItemByToken(text.lowercase()) { set ->
                set?.let {
                    symbol = set.symbol
                    if (set.closeBrace.isNotEmpty()) closeBracketStack += set.closeBrace
                    result(symbol)
                }
            }
        }
    }
}
