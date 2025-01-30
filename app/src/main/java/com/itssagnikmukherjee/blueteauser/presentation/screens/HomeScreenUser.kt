package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.presentation.GetCategoryState
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels


@Composable
fun HomeScreenUser(modifier: Modifier = Modifier, viewmodel: ViewModels = hiltViewModel()) {
    val state = viewmodel.getCategoryState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewmodel.getCategories()
    }

    LazyRow() {
        items(state.value.data.size){
            items->
            CategoryItem(state as MutableState<GetCategoryState>,items)
        }
    }

}

@Composable
fun CategoryItem(state: MutableState<GetCategoryState>, it: Int) {
    Column(modifier = Modifier.padding(10.dp)){
        AsyncImage("${state.value.data[it]?.imageUrl}","category_img",
            modifier = Modifier.size(90.dp)
                .clip(RoundedCornerShape(50))
                .wrapContentSize(Alignment.Center,false),
        )
        Text("${state.value.data[it]?.categoryName}",
            modifier = Modifier.padding(5.dp).align(Alignment.CenterHorizontally)
        )
    }
}