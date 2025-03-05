package com.itssagnikmukherjee.blueteaadmin.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.common.constants.Constants
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.models.OrderDetails
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.domain.models.UserData
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import com.itssagnikmukherjee.blueteaadmin.domain.usecases.getProductsFromFirebaseUsecase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.text.get

class repoImpl @Inject constructor(
    private val FirebaseFirestore: FirebaseFirestore,
    ) : Repo {

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

    override fun getProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.PRODUCT).get().addOnSuccessListener {
            val products = it.documents.mapNotNull {
                it.toObject(Product::class.java)?.copy(productId = it.id)
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun getUserDetails(userId: String): Flow<ResultState<UserData>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.USERS).get().addOnSuccessListener {
            val users = it.documents.mapNotNull { it.toObject(UserData::class.java)?.copy(userId = it.id) }
            val user = users.find { it.userId == userId }
            if (user != null) {
                trySend(ResultState.Success(user))
            } else {
                trySend(ResultState.Error("User not found"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose{close()}
    }

    override fun getOrders(): Flow<ResultState<List<OrderDetails>>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseFirestore.collection(Constants.ORDERS).get().addOnSuccessListener {
            val orders = it.documents.mapNotNull {
                it.toObject(OrderDetails::class.java)?.copy(orderId = it.id)
            }
            trySend(ResultState.Success(orders))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun updateOrderStatus(orderId: String, newStatus: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading) // Emit loading state

        // Access Firestore and update the status
        FirebaseFirestore.collection(Constants.USERS)
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the current orderedItems map
                    val orderedItems = document.get("orderedItems") as? Map<String, Map<String, Any>> ?: emptyMap()

                    // Update the status for each order in orderedItems
                    val updatedOrderedItems = orderedItems.mapValues { (_, orderDetails) ->
                        orderDetails.toMutableMap().apply {
                            put("status", newStatus)

                            when (newStatus) {
                                "In Transit" -> {
                                    put("transitTime", System.currentTimeMillis())
                                }
                                "Delivered" -> {
                                    put("deliveredTime", System.currentTimeMillis())
                                }
                            }

                        }
                    }

                    // Update the Firestore document with the new orderedItems
                    FirebaseFirestore
                        .collection(Constants.USERS)
                        .document(orderId)
                        .update("orderedItems", updatedOrderedItems)
                        .addOnSuccessListener {
                            trySend(ResultState.Success("Status updated successfully")) // Emit success state
                            close() // Close the flow
                        }
                        .addOnFailureListener { e ->
                            trySend(ResultState.Error(e.localizedMessage ?: "Failed to update status")) // Emit error state
                            close() // Close the flow
                        }
                } else {
                    trySend(ResultState.Error("User document not found")) // Emit error state
                    close() // Close the flow
                }
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Failed to fetch user document")) // Emit error state
                close() // Close the flow
            }

        // Close the flow when the coroutine scope is cancelled
        awaitClose { close() }
    }
}
