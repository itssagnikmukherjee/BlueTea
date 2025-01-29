package com.itssagnikmukherjee.blueteaadmin.common.supabaseDi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object supabaseDi {
    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = com.itssagnikmukherjee.blueteaadmin.BuildConfig.SUPABASE_URL,
            supabaseKey = com.itssagnikmukherjee.blueteaadmin.BuildConfig.SUPABASE_KEY
        ){
            install(Storage)
        }
    }

}