package com.lyh.marvelexplorer.feature.character.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.lyh.marvelexplorer.domain.model.CharacterModel
import com.lyh.marvelexplorer.feature.character.mapper.toUi
import com.lyh.marvelexplorer.feature.character.model.CharacterUi
import com.lyh.marvelexplorer.feature.character.model.SquadCharacterUi
import com.lyh.marvelexplorer.feature.core.*
import com.lyh.marvelexplorer.feature.core.R
import com.lyh.marvelexplorer.feature.core.ui.AppTopBar
import com.lyh.marvelexplorer.feature.core.ui.ErrorComponent
import com.lyh.marvelexplorer.feature.core.ui.LoadingComponent
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CharacterListRoute(
    modifier: Modifier = Modifier,
    onNavigateToCharacter: (characterId: String) -> Unit,
    viewModel: CharacterListViewModel = getViewModel()
) {

    val lazyPagingItems = viewModel.characters.collectAsLazyPagingItems()
    val squadCharactersResource by viewModel.squadCharacters.collectAsStateWithLifecycle()
    CharacterListScreen(
        modifier = modifier,
        lazyPagingItems = lazyPagingItems,
        squadCharactersResource = squadCharactersResource,
        retrySquadCharacters = {},//viewModel::triggerCharacters,
        retryCharacters = {},//viewModel::triggerCharacters,
        onNavigateToCharacter = onNavigateToCharacter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<CharacterModel>,
    squadCharactersResource: Resource<List<SquadCharacterUi>>,
    retrySquadCharacters: () -> Unit,
    retryCharacters: () -> Unit,
    onNavigateToCharacter: (characterId: String) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = stringResource(id = R.string.app_name), onBackClick = null) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SquadCharacterList(
                squadCharactersResource = squadCharactersResource,
                retry = retrySquadCharacters,
                onNavigateToCharacter = onNavigateToCharacter
            )
            CharacterList(
                lazyPagingItems = lazyPagingItems,
                retry = retryCharacters,
                onNavigateToCharacter = onNavigateToCharacter)
        }
    }
}

@Composable
private fun SquadCharacterList(
    modifier: Modifier = Modifier,
    squadCharactersResource: Resource<List<SquadCharacterUi>>,
    retry: () -> Unit,
    onNavigateToCharacter: (characterId: String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {

        when (squadCharactersResource) {
            is ResourceError -> ErrorComponent(
                errorText = squadCharactersResource.errorMessage.getMessage(
                    LocalContext.current
                ),
                retry = null,
                modifier = Modifier.padding(10.dp)
            )
            is ResourceLoading -> LoadingComponent(
                loadingText = stringResource(id = R.string.loading_data),
                modifier = Modifier.padding(10.dp)
            )
            is ResourceSuccess -> {
                if(squadCharactersResource.data.isEmpty()) {
                    Text(
                        text = stringResource(id = com.lyh.marvelexplorer.feature.character.R.string.my_squad_empty),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(10.dp))
                } else {
                    LazyRow(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        items(squadCharactersResource.data) {
                            SquadCharacterItem(
                                squadCharacter = it,
                                onNavigateToCharacter = onNavigateToCharacter
                            )
                        }
                    }
                    Text(
                        text = stringResource(id = com.lyh.marvelexplorer.feature.character.R.string.my_squad),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 10.dp, )
                    )
                }
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SquadCharacterItem(
    modifier: Modifier = Modifier,
    squadCharacter: SquadCharacterUi,
    onNavigateToCharacter: (characterId: String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { onNavigateToCharacter(squadCharacter.id.toString()) },
        modifier = modifier.width(90.dp)
    ) {
        Column {
            AsyncImage(
                model = squadCharacter.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(5.dp)
                    .clip(CircleShape)
            )
            Text(
                text = squadCharacter.name,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )
        }

        
    }
}


@Composable
private fun CharacterList(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<CharacterModel>,
    retry: () -> Unit,
    onNavigateToCharacter: (characterId: String) -> Unit
) {
    when (lazyPagingItems.loadState.refresh) {
        is LoadState.Error -> {
            Timber.e((lazyPagingItems.loadState.refresh as LoadState.Error).error)
            ErrorComponent(
                errorText = stringResource(id = R.string.fetch_exception),
                retry = retry
            )
        }
        is LoadState.Loading -> LoadingComponent(loadingText = stringResource(id = R.string.loading_data))
        is LoadState.NotLoading -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                itemsIndexed(lazyPagingItems) { _, item ->
                    item?.let {
                        CharacterItemCard(
                            characterUi = item.toUi(),
                            onClick = onNavigateToCharacter
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterItemCard(
    modifier: Modifier = Modifier,
    characterUi: CharacterUi,
    onClick: (characterId: String) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = CardDefaults.elevatedShape,
        onClick = { onClick(characterUi.id.toString()) },
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {

            AsyncImage(
                model = characterUi.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Text(
                characterUi.name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
    }

}