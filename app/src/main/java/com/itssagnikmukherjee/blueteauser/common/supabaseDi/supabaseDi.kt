package com.itssagnikmukherjee.blueteauser.common.supabaseDi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
            supabaseUrl = com.itssagnikmukherjee.blueteauser.BuildConfig.SUPABASE_URL,
            supabaseKey = com.itssagnikmukherjee.blueteauser.BuildConfig.SUPABASE_KEY
        ){
            install(Storage)
        }
    }

}