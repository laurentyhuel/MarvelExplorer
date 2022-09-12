package com.lyh.marvelexplorer.data.di

import com.lyh.marvelexplorer.data.CharacterPagingSource
import com.lyh.marvelexplorer.data.CharacterRepository
import com.lyh.marvelexplorer.data.SquadCharacterRepository
import com.lyh.marvelexplorer.data.core.AppDispatchers
import com.lyh.marvelexplorer.data.local.di.dataLocalModule
import com.lyh.marvelexplorer.data.remote.di.getDataRemoteModule
import com.lyh.marvelexplorer.domain.repository.ICharacterRepository
import com.lyh.marvelexplorer.domain.repository.ISquadCharacterRepository
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/**
 * Koin module for data
 */
fun getDataModule(isDebugEnabled: Boolean) = module {
    includes(getDataRemoteModule(isDebugEnabled), dataLocalModule)

    single { CharacterPagingSource(get()) }
    single<ICharacterRepository> { CharacterRepository(get(), get(), get()) }
    single<ISquadCharacterRepository> { SquadCharacterRepository(get()) }
    single { AppDispatchers(Dispatchers.Main, Dispatchers.IO) }
}
