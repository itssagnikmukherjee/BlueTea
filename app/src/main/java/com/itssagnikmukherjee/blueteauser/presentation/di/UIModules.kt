package com.itssagnikmukherjee.blueteauser.presentation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteauser.data.repoImpl.repoImpl
import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UIModules {

    @Provides
    fun provideRepo(FirebaseFirestore: FirebaseFirestore, firebaseAuth: FirebaseAuth): Repo {
        return repoImpl(FirebaseFirestore,firebaseAuth)
    }

}