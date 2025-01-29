package com.itssagnikmukherjee.blueteaadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModels @Inject constructor(private val repo: Repo) : ViewModel() {

    private val _addCategory = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategory.asStateFlow()

    fun addCategory(category: Category) {
        viewModelScope.launch{
            repo.addCategory(category).collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _addCategory.value = AddCategoryState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _addCategory.value = AddCategoryState(data = it.data)
                        }
                    is ResultState.Error -> {
                        _addCategory.value = AddCategoryState(error = it.error)
                    }
                }
            }
        }
    }
}

data class AddCategoryState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String = ""
)