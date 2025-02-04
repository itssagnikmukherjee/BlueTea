package com.itssagnikmukherjee.blueteauser.data

import android.util.Log
import androidx.compose.runtime.internal.StabilityInferred
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.common.constants.Constants
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.models.Product
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

    override fun getBanners(): Flow<ResultState<List<Banner>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.BANNER).get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FirestoreData", "Document: ${querySnapshot.documents}")
                val banners = querySnapshot.documents.mapNotNull { document ->
                    val bannerImageUrls = document.get("bannerImageUrls") as? List<*>
                    val bannerName = document.getString("bannerName")

                    if (bannerImageUrls != null && bannerName != null) {
                        val imageUrls = bannerImageUrls.filterIsInstance<String>()
                        Banner(imageUrls, bannerName)
                    } else {
                        null
                    }
                }
                trySend(ResultState.Success(banners))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message.toString()))
            }
        awaitClose { close() }
    }

    override fun getProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.PRODUCT).get().addOnSuccessListener {
            val products = it.documents.mapNotNull {
                it.toObject(Product::class.java)
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose {
            close()
        }
        }
}
