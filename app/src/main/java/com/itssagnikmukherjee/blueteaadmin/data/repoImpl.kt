package com.itssagnikmukherjee.blueteaadmin.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.common.constants.Constants
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
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
                document.toObject(Category::class.java)?.copy(id = document.id) // Set the Firestore document ID
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
    override fun deleteCategory(categoryName: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading) // Send loading state

        try {
            // Query Firestore to find the document with the matching categoryName
            FirebaseFirestore.collection(Constants.CATEGORY)
                .whereEqualTo("categoryName", categoryName) // Query by categoryName
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // No document found with the given categoryName
                        trySend(ResultState.Error("Category not found"))
                    } else {
                        // Delete the first document (assuming category names are unique)
                        val document = querySnapshot.documents[0]
                        document.reference.delete()
                            .addOnSuccessListener {
                                trySend(ResultState.Success("Category deleted successfully"))
                            }
                            .addOnFailureListener { e ->
                                trySend(ResultState.Error("Failed to delete category: ${e.message}"))
                            }
                    }
                }
                .addOnFailureListener { e ->
                    trySend(ResultState.Error("Failed to query category: ${e.message}"))
                }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(ResultState.Error("Error: ${e.message}"))
        }

        awaitClose { close() }
    }


    //getting banner from
    override fun getBanners(): Flow<ResultState<List<Banner>>> = callbackFlow {
        trySend(ResultState.Loading)

        val documentId = "UzGlAlbnvIxVzzeiEonP" // Fixed document ID

        FirebaseFirestore.collection(Constants.BANNER)
            .document(documentId) // Fetch the single document
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val bannerImageUrls = document.get("bannerImageUrls") as? List<*>
                    val bannerName = document.getString("bannerName")

                    if (bannerImageUrls != null && bannerName != null) {
                        val imageUrls = bannerImageUrls.filterIsInstance<String>()
                        val banner = Banner(bannerName = bannerName, bannerImageUrls = imageUrls)
                        trySend(ResultState.Success(listOf(banner)))
                    } else {
                        trySend(ResultState.Error("Invalid banner data"))
                    }
                } else {
                    trySend(ResultState.Error("Banner document does not exist"))
                }
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message.toString()))
            }

        awaitClose { close() }
    }

    //adding banner to firebase
    override fun addBanner(banner: Banner): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val documentId = "UzGlAlbnvIxVzzeiEonP"

        FirebaseFirestore.collection(Constants.BANNER)
            .document(documentId)
            .set(banner)
            .addOnSuccessListener {
                trySend(ResultState.Success("Banner updated successfully"))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message.toString()))
            }

        awaitClose { close() }
    }

    //adding product to firebase
    override fun addProduct(product: Product): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.PRODUCT).add(product)
            .addOnSuccessListener {
                trySend(ResultState.Success("Product added successfully"))
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.message.toString()))
            }
        awaitClose{close()}
    }
}
