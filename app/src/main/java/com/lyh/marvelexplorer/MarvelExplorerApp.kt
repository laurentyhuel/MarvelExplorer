package com.lyh.marvelexplorer

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.lyh.marvelexplorer.data.di.getDataModule
import com.lyh.marvelexplorer.domain.di.domainModule
import com.lyh.marvelexplorer.feature.character.di.featureCharacterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import com.lyh.marvelexplorer.feature.core.R

class MarvelExplorerApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@MarvelExplorerApp)
            modules(
                getDataModule(BuildConfig.DEBUG),
                domainModule,
                featureCharacterModule
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .placeholder(R.drawable.ic_no_image)
            .error(R.drawable.ic_no_image)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}
