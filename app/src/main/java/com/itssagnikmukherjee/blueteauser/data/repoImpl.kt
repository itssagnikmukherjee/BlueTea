package com.itssagnikmukherjee.blueteauser.data

import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.common.constants.Constants
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class repoImpl @Inject constructor(private val FirebaseFirestore: FirebaseFirestore) : Repo {

    // getting categories from firebase
    override fun getCategories(): Flow<ResultState<List<Category>>> =callbackFlow{
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.CATEGORY).get().addOnSuccessListener {
            val categories = it.documents.mapNotNull {
                it.toObject(Category::class.java)
            }
            trySend(ResultState.Success(categories))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose {
            close()
        }
    }


}
