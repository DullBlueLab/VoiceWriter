package com.dullbluelab.voicewriter3.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dullbluelab.voicewriter3.R
import com.dullbluelab.voicewriter3.data.SymbolTable

@Composable
fun ScreenSetting(
    viewModel: WriterViewModel,
    modifier: Modifier = Modifier
) {
    val setting by viewModel.settingUi.collectAsState()
    val symbolList = setting.symbolList

    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp, 32.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_cursor_char),
                modifier = Modifier.weight(0.6f)
            )
            TextField(
                value = setting.cursorChar,
                onValueChange = { viewModel.changeCursorChar(it) },
                singleLine = true,
                modifier = Modifier.weight(0.4f)
            )
        }
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = stringResource(id = R.string.label_setting_symbol),
            modifier = modifier.padding(0.dp, 8.dp)
        )
        for (item in symbolList) {
            PlaceSymbolSet(viewModel, item, Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun PlaceSymbolSet(viewModel: WriterViewModel, item: SymbolTable, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = item.token,
            onValueChange = { viewModel.changeSymbolToken(item.symbol, it) },
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = "is " + item.symbol,
            modifier = Modifier.padding(16.dp)
        )
    }
}
