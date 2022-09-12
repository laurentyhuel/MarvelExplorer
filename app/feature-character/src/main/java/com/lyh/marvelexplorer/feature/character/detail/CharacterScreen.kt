package com.lyh.marvelexplorer.feature.character.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.lyh.marvelexplorer.feature.character.R
import com.lyh.marvelexplorer.feature.character.model.CharacterUi
import com.lyh.marvelexplorer.feature.core.Resource
import com.lyh.marvelexplorer.feature.core.ResourceError
import com.lyh.marvelexplorer.feature.core.ResourceLoading
import com.lyh.marvelexplorer.feature.core.ResourceSuccess
import com.lyh.marvelexplorer.feature.core.ui.AppTopBar
import com.lyh.marvelexplorer.feature.core.ui.ErrorComponent
import com.lyh.marvelexplorer.feature.core.ui.LoadingComponent
import org.koin.androidx.compose.getViewModel
import com.lyh.marvelexplorer.feature.core.R as RCore

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CharacterRoute(
    modifier: Modifier = Modifier,
    id: String,
    viewModel: CharacterViewModel = getViewModel(),
    onBackClick: () -> Unit,
) {
    LaunchedEffect(id) {
        viewModel.setCharacterId(id.toInt())
    }
    val characterResource by viewModel.character.collectAsStateWithLifecycle()
    val presentInSquad by viewModel.isCharacterPresentInSquad.collectAsStateWithLifecycle()

    CharacterScreen(
        modifier = modifier,
        characterResource = characterResource,
        presentInSquad = presentInSquad,
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    modifier: Modifier = Modifier,
    characterResource: Resource<CharacterUi>,
    presentInSquad: Boolean?,
    viewModel: CharacterViewModel,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(id = RCore.string.app_name),
                onBackClick = onBackClick
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (characterResource) {
                is ResourceError -> ErrorComponent(
                    errorText = characterResource.errorMessage.getMessage(
                        LocalContext.current
                    ),
                    retry = null,
                )
                is ResourceLoading -> LoadingComponent(loadingText = stringResource(id = RCore.string.loading_data))
                is ResourceSuccess -> CharacterDetail(
                    character = characterResource.data,
                    presentInSquad = presentInSquad,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetail(
    modifier: Modifier = Modifier,
    character: CharacterUi,
    presentInSquad: Boolean?,
    viewModel: CharacterViewModel
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { CharacterFooter(viewModel, presentInSquad, character) },
        topBar = { CharacterHeader(character) }
    ) { innerPadding ->
        Text(
            text = getCharacterDescription(character),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 20.dp,
                    end = 20.dp
                )
        )
    }
}

@Composable
private fun CharacterHeader(character: CharacterUi) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = character.thumbnailUrl,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            text = character.name,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

@Composable
private fun CharacterFooter(
    viewModel: CharacterViewModel,
    presentInSquad: Boolean?,
    character: CharacterUi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        when (presentInSquad) {
            true -> Button(onClick = { viewModel.fireSquadCharacter(character) }) {
                Text(text = stringResource(id = R.string.quad_fire))
            }
            false -> Button(onClick = { viewModel.recruitSquadCharacter(character) }) {
                Text(text = stringResource(id = R.string.squad_recruit))
            }
            null -> {
                //nothing to do
            }
        }
    }
}

@Composable
private fun getCharacterDescription(character: CharacterUi) =
    if (character.description.isNullOrBlank())
        stringResource(id = R.string.missing_description)
    else
        character.description