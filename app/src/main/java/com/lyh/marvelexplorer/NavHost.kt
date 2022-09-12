package com.lyh.marvelexplorer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lyh.marvelexplorer.feature.character.nav.CharacterDestination
import com.lyh.marvelexplorer.feature.character.nav.CharacterListNavigation
import com.lyh.marvelexplorer.feature.character.nav.characterGraph
import com.lyh.marvelexplorer.feature.core.NavigationDestination

@Composable
fun NavHost(
    navController: NavHostController,
    onNavigateToDestination: (NavigationDestination, String) -> Unit,
    startDestination: String = CharacterListNavigation.route,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        characterGraph(
            navigateToCharacter = {
                onNavigateToDestination(
                    CharacterDestination, CharacterDestination.createNavigationRoute(it)
                )
            },
            onBackClick = onBackClick
        )
    }
}