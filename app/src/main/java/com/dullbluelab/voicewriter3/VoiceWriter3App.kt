package com.dullbluelab.voicewriter3

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dullbluelab.voicewriter3.ui.ScreenHome
import com.dullbluelab.voicewriter3.ui.ScreenSetting
import com.dullbluelab.voicewriter3.ui.WriterViewModel

enum class VoiceWriterScreen {
    Home, Setting
}

@Composable
fun VoiceWriter3App(
    activity: MainActivity,
    modifier: Modifier = Modifier,
    viewModel: WriterViewModel = viewModel(factory = WriterViewModel.Factory)
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    if (!viewModel.isInitialized()) viewModel.setup(activity)
    viewModel.screenPosition = backStackEntry?.destination?.route ?: ""

    Scaffold(
        topBar = {
            VoiceWriterAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = backStackEntry?.destination?.route ?: VoiceWriterScreen.Home.name,
                navigateUp = { navController.navigateUp() },
                onShare = {
                    val text = viewModel.sourceText()
                    if (text.isNotEmpty()) activity.shareEditedText(text)
                },
                onSettings = { navController.navigate(VoiceWriterScreen.Setting.name) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = VoiceWriterScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = VoiceWriterScreen.Home.name) {
                ScreenHome(
                    viewModel = viewModel
                )
            }
            composable(route = VoiceWriterScreen.Setting.name) {
                ScreenSetting(
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceWriterAppBar(
    canNavigateBack: Boolean,
    currentScreen: String,
    navigateUp: () -> Unit,
    onShare: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when(currentScreen) {
        VoiceWriterScreen.Home.name -> stringResource(id = R.string.app_name)
        VoiceWriterScreen.Setting.name -> stringResource(id = R.string.title_setting)
        else -> stringResource(id = R.string.app_name)
    }

    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
            }
        },
        actions = {
            if (currentScreen == VoiceWriterScreen.Home.name) {
                IconButton(
                    onClick = { onShare() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(id = R.string.button_send)
                    )
                }
                IconButton(
                    onClick = {
                        onSettings()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.button_setting)
                    )
                }
            }
        }
    )
}