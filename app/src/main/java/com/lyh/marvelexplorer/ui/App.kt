package com.lyh.marvelexplorer.ui

import androidx.compose.runtime.Composable
import com.lyh.marvelexplorer.NavHost

@Composable
fun App(
    appState: AppState = rememberAppState()
) {
    AppTheme {
        NavHost(
            navController = appState.navController,
            onBackClick = appState::onBackClick,
            onNavigateToDestination = appState::navigate,
        )
    }

}