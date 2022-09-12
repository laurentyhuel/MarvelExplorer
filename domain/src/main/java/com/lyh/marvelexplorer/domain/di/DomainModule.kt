package com.lyh.marvelexplorer.domain.di

import com.lyh.marvelexplorer.domain.CharacterUseCase
import com.lyh.marvelexplorer.domain.SquadCharacterUseCase
import org.koin.dsl.module

/**
 * Koin module for data-remote
 */
val domainModule = module {
    single { CharacterUseCase(get()) }
    single { SquadCharacterUseCase(get()) }
}