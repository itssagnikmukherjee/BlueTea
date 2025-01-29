package com.itssagnikmukherjee.blueteaadmin.data

import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.common.constants.Constants
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class repoImpl @Inject constructor(private val FirebaseFirestore: FirebaseFirestore) : Repo {

    override fun addCategory(category: Category): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        FirebaseFirestore.collection(Constants.CATEGORY).add(category)
            .addOnSuccessListener {
                trySend(ResultState.Success("Category added successfully"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }
    }
}
