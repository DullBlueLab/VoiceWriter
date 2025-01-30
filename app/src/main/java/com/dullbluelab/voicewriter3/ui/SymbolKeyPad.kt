package com.dullbluelab.voicewriter3.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSymbolPad(
    model: WriterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by model.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier.weight(1.5f).fillMaxWidth()
        ) {
            TextField(
                value = uiState.inputText,
                onValueChange = { model.changeInputText(it) },
                singleLine = true,
                modifier = Modifier
                    .weight(2f)
                    .horizontalScroll(rememberScrollState())
            )
            Button(
                onClick = { model.symbolPadOn(false, false) },
                modifier = Modifier.weight(1f).padding(4.dp)
            ) {
                Text(
                    text = "cancel"
                )
            }
            Button(
                onClick = { model.symbolPadOn(false) },
                modifier = Modifier.weight(1f).padding(4.dp)
            ) {
                Text(
                    text = "enter"
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            SymbolKey(
                chars = "#",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "$",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "%",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "&",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "\'",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "\"",
                model = model,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            SymbolKey(
                chars = "!",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "@",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "^",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "~",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "|",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "`",
                model = model,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            SymbolKey(
                chars = "(",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ")",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "{",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "}",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "[",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "]",
                model = model,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            SymbolKey(
                chars = "+",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "*",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ";",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ":",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "<",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ">",
                model = model,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            SymbolKey(
                chars = "-",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "=",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "/",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = "\\",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ",",
                model = model,
                modifier = Modifier.weight(1f)
            )
            SymbolKey(
                chars = ".",
                model = model,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SymbolKey(
    chars: String,
    model: WriterViewModel,
    modifier: Modifier
) {
    val background = MaterialTheme.colorScheme.primaryContainer
    val color = MaterialTheme.colorScheme.primary

    Button(
        onClick = { model.clickSymbol(chars) },
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color
        ),
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            text = chars
        )
    }
}