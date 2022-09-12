package com.lyh.marvelexplorer.feature.character.di

import com.lyh.marvelexplorer.feature.character.detail.CharacterViewModel
import com.lyh.marvelexplorer.feature.character.list.CharacterListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureCharacterModule = module {
    viewModel { CharacterViewModel(get(), get()) }
    viewModel { CharacterListViewModel(get(), get()) }
}