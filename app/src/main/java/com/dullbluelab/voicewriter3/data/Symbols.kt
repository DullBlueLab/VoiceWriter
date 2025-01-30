package com.dullbluelab.voicewriter3.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn

class Symbols(private val dao: SymbolDao) {

    enum class Key {
        symbol_at, symbol_double, symbol_sharp, symbol_dollar, symbol_percent,
        symbol_and, symbol_quote, symbol_bracket, symbol_minus, symbol_plus,
        symbol_multi, symbol_slash, symbol_equal, symbol_att, symbol_semi,
        symbol_colon, symbol_small, symbol_large, symbol_quest, symbol_under,
        symbol_brace, symbol_square
    }

    companion object {
        const val CLOSE_TOKEN = "close"
        const val ENUM_TEXT = "!\"#$%&\'()-^\\=~|@`;:,.\\_+*/[]{}"
    }

    class Item(
        val key: Key,
        val symbol: String,
        var token: String,
        var closeBrace: String = ""
    )

    fun getKey(name: String): Key {
        return Key.valueOf(name)
    }

    // public
    suspend fun getItemByToken(token: String, success: (SymbolTable?) -> Unit) {
        dao.getByToken(token).collect { item ->
            success(item)
        }
    }

    // public
    suspend fun getItem(key: Key, success: (SymbolTable?) -> Unit) {
        dao.getByKey(key.name).collect { item ->
            success(item)
        }
    }

    // public
    suspend fun changeToken(symbol: String, token: String) {
        dao.getBySymbol(symbol).collect { item ->
            dao.update(item.copy(token = token))
        }
    }

    suspend fun setDefault() {
        for (item in defaults) {
            val table = SymbolTable(0, item.key.name, item.symbol, item.token, item.closeBrace)
            dao.insert(table)
        }
    }
}

// default value
private val defaults: List<Symbols.Item> = listOf(
    Symbols.Item(Symbols.Key.symbol_at, "!", "at"),
    Symbols.Item(Symbols.Key.symbol_double, "\"", "double"),
    Symbols.Item(Symbols.Key.symbol_sharp, "#", "sharp"),
    Symbols.Item(Symbols.Key.symbol_dollar, "$", "dollar"),
    Symbols.Item(Symbols.Key.symbol_percent, "%", "percent"),

    Symbols.Item(Symbols.Key.symbol_and, "&", "and"),
    Symbols.Item(Symbols.Key.symbol_quote, "\'", "quote"),
    Symbols.Item(Symbols.Key.symbol_bracket, "(", "bracket", ")"),
    Symbols.Item(Symbols.Key.symbol_minus, "-", "minus"),
    Symbols.Item(Symbols.Key.symbol_plus, "+", "plus"),

    Symbols.Item(Symbols.Key.symbol_multi, "*", "multi"),
    Symbols.Item(Symbols.Key.symbol_slash, "/", "slash"),
    Symbols.Item(Symbols.Key.symbol_equal, "=", "equal"),
    Symbols.Item(Symbols.Key.symbol_att, "@", "att"),
    Symbols.Item(Symbols.Key.symbol_semi, ";", "semi"),

    Symbols.Item(Symbols.Key.symbol_colon, ":", "colon"),
    Symbols.Item(Symbols.Key.symbol_small, "<", "small"),
    Symbols.Item(Symbols.Key.symbol_large, ">", "large"),
    Symbols.Item(Symbols.Key.symbol_quest, "?", "quest"),
    Symbols.Item(Symbols.Key.symbol_under, "_", "under"),

    Symbols.Item(Symbols.Key.symbol_brace, "{", "brace", "}"),
    Symbols.Item(Symbols.Key.symbol_square, "[", "square", "]"),
)
