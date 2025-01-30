package com.dullbluelab.voicewriter3.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScreenHome(
    viewModel: WriterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            AnnotatedString(uiState.sourceText),
            //onClick = { viewModel.clickText(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        )
        if (uiState.symbolPadOn) {
            BottomSymbolPad(
                model = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
        }
        else {
            BottomPanel(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
        }
    }
}

@Composable
fun BottomPanel(
    viewModel: WriterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val setting by viewModel.setting.collectAsState()
    val buttonMod = Modifier

    val returnText = if (uiState.inputText.isNotEmpty()) "enter" else "return"

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = uiState.inputText,
                onValueChange = { viewModel.changeInputText(it) },
                singleLine = true,
                modifier = Modifier
                    .weight(3f)
                    .horizontalScroll(rememberScrollState())
            )
            ShiftKey(
                type = KeyType.Mode,
                text = uiState.convertMode.name,
                onClick = { viewModel.clickKey(KeyType.Mode) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ShiftKey(
                type = KeyType.Left,
                text = "<",
                onClick = { viewModel.clickKey(KeyType.Left) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Number,
                text = "123",
                onClick = { viewModel.clickKey(KeyType.Number) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Symbol,
                text = "+/*",
                onClick = {
                    if (setting.symbolPadPriority) viewModel.symbolPadOn(true)
                    else viewModel.clickKey(KeyType.Symbol)
                },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Right,
                text = ">",
                onClick = { viewModel.clickKey(KeyType.Right) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ShiftKey(
                type = KeyType.Uppercase,
                text = "ABC",
                onClick = { viewModel.clickKey(KeyType.Uppercase) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.HeadUpper,
                text = "Abc",
                onClick = { viewModel.clickKey(KeyType.HeadUpper) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Lowercase,
                text = "abc",
                onClick = { viewModel.clickKey(KeyType.Lowercase) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.BackSpace,
                text = "BS",
                onClick = { viewModel.clickKey(KeyType.BackSpace) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ShiftKey(
                type = KeyType.Cancel,
                text = "cancel",
                onClick = { viewModel.clickKey(KeyType.Cancel) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Space,
                text = "space",
                onClick = { viewModel.clickKey(KeyType.Space) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
            ShiftKey(
                type = KeyType.Dot,
                text = ".",
                onClick = { viewModel.clickKey(KeyType.Dot) },
                uiState = uiState,
                modifier = buttonMod.weight(0.7f))
            ShiftKey(
                type = KeyType.Return,
                text = returnText,
                onClick = { viewModel.clickKey(KeyType.Return) },
                uiState = uiState,
                modifier = buttonMod.weight(1f)
            )
        }
    }
}

@Composable
fun ShiftKey(
    type: KeyType,
    text: String,
    onClick: () -> Unit,
    uiState: WriterViewModel.UiState,
    modifier: Modifier
) {
    val background =
        if (type == uiState.keyPosition) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.primaryContainer
    val color =
        if (type == uiState.keyPosition) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary

    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color
        ),
        shape = RoundedCornerShape(4.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp
        ),
        modifier = modifier
            .padding(2.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(0.dp)
        )
    }
}
