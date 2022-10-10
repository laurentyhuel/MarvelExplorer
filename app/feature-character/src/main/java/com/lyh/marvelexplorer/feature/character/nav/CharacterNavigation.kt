package com.lyh.marvelexplorer.feature.character.nav

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lyh.marvelexplorer.feature.character.detail.CharacterRoute
import com.lyh.marvelexplorer.feature.character.list.CharacterListRoute
import com.lyh.marvelexplorer.feature.core.NavigationDestination

object CharacterListNavigation : NavigationDestination {
    override val route = "characters_route"
    override val destination = "characters_destination"
}


object CharacterDestination : NavigationDestination {
    const val characterIdArg = "characterId"
    override val route = "character_route/{$characterIdArg}"
    override val destination = "character_destination"

    /**
     * Creates destination route for an characterId that could include special characters
     */
    fun createNavigationRoute(characterIdArg: String): String {
        val encodedId = Uri.encode(characterIdArg)
        return "character_route/$encodedId"
    }

    /**
     * Returns the characterId from a [NavBackStackEntry] after an character destination navigation call
     */
    fun fromNavArgs(entry: NavBackStackEntry): String {
        val encodedId = entry.arguments!!.getInt(characterIdArg)
        return Uri.decode(encodedId.toString())
    }
}

fun NavGraphBuilder.characterGraph(
    navigateToCharacter: (String) -> Unit,
    onBackClick: () -> Unit,
) {

    composable(route = CharacterListNavigation.route) {
        CharacterListRoute(onNavigateToCharacter = navigateToCharacter)
    }
    composable(
        route = CharacterDestination.route,
        arguments = listOf(
            navArgument(CharacterDestination.characterIdArg) { type = NavType.IntType }
        )
    ) {
        CharacterRoute(
            onBackClick = onBackClick)
    }    
}
