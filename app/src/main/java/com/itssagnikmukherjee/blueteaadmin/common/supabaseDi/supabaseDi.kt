package com.itssagnikmukherjee.blueteaadmin.common.supabaseDi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object supabaseDi {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://iebhahjyatpcvwhwbicr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImllYmhhaGp5YXRwY3Z3aHdiaWNyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzgxMzk5NzIsImV4cCI6MjA1MzcxNTk3Mn0.zJW7W_V45kM3_zgU-DMloK1R8qMff2CJkiGPkibdc08"
        ){
            install(Storage)
        }
    }

}