package com.dullbluelab.voicewriter3.data

import com.dullbluelab.voicewriter3.ui.KeyType
import com.dullbluelab.voicewriter3.unit.ConvertMode
import com.dullbluelab.voicewriter3.unit.WordMaker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WriterRepository(
    private val database: SymbolDatabase
) {
    val symbols: Symbols = Symbols(database.symbolDao())
    private val wordMaker: WordMaker = WordMaker(symbols)

    suspend fun initDatabase() {
        withContext(Dispatchers.Default) {
            if (database.symbolDao().count() <= 0) {
                symbols.setDefault()
            }
        }
    }

    suspend fun getSymbolList(success: (List<SymbolTable>) -> Unit) {
        database.symbolDao().getAll().collect { list ->
            success(list)
        }
    }

    fun changeConvertMode(mode: ConvertMode) {
        wordMaker.convertMode = mode
    }

    fun changeKeyPosition(type: KeyType) {
        wordMaker.keyPosition = type
    }

    suspend fun changeSymbolToken(symbol: String, token: String) {
        symbols.changeToken(symbol, token)
    }

    fun spoken(list: ArrayList<String>?, inputText: String, result: (String) -> Unit) {
        wordMaker.spoken(list, inputText, result)
    }
}