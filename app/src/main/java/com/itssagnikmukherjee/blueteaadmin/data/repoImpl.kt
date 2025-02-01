package com.itssagnikmukherjee.blueteaadmin.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.common.constants.Constants
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.text.get

class repoImpl @Inject constructor(private val FirebaseFirestore: FirebaseFirestore) : Repo {

    // getting categories from firebase
    override fun getCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.CATEGORY).get().addOnSuccessListener {
            val categories = it.documents.mapNotNull { document ->
                document.toObject(Category::class.java)
            }
            trySend(ResultState.Success(categories))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose { close() }
    }

    //adding categories to firebase
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

    //delete category from firebase
    override fun deleteCategory(categoryId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        FirebaseFirestore.collection(Constants.CATEGORY)
            .document(categoryId)
            .delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Category deleted successfully"))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message.toString()))
            }

        awaitClose { close() }
    }

    //getting banner from
    override fun getBanners(): Flow<ResultState<List<Banner>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.BANNER).get()
            .addOnSuccessListener { querySnapshot ->
                val banners = querySnapshot.documents.mapNotNull { document ->
                    val bannerImageUrls = document.get("bannerImageUrls") as? List<*>
                    val bannerName = document.getString("bannerName")

                    if (bannerImageUrls != null && bannerName != null) {
                        val imageUrls = bannerImageUrls.filterIsInstance<String>()
                        Banner(bannerName = bannerName, bannerImageUrls = imageUrls)
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

    //adding banner to firebase
    override fun addBanner(banner: Banner): Flow<ResultState<String>> =callbackFlow{
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.BANNER).add(banner)
            .addOnSuccessListener {
                trySend(ResultState.Success("Banner added successfully"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }
        awaitClose{close()}
    }

    //delete banner from firebase
    override fun deleteBanner(bannerName: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        FirebaseFirestore.collection(Constants.BANNER)
            .document(bannerName) // Use bannerName as the document ID
            .delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Banner deleted successfully"))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message.toString()))
            }

        awaitClose { close() }
    }
}
