package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun NavbarComposable(modifier: Modifier = Modifier) {

    val navItemList = listOf<NavItem>(
        NavItem(icon = Icons.Default.Home, text = "Home"),
        NavItem(icon = Icons.Default.Favorite, text = "Favorite"),
        NavItem(icon = Icons.Default.ShoppingCart, text = "Cart"),
        NavItem(icon = Icons.Default.Person, text = "Profile")
    )

    Box(modifier = Modifier
        .height(140.dp).fillMaxWidth()
        .clip(
            RoundedCornerShape(20.dp)
        ),
        contentAlignment = Alignment.Center){
        Box(
            modifier = Modifier.clip(RoundedCornerShape(60.dp))
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(.7f).background(Color.White)
                    .clip(RoundedCornerShape(20.dp)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(navItemList.size) { item ->
                    Column {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = navItemList[item].icon,
                                contentDescription = navItemList[item].text,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val icon: ImageVector,
    val text: String
)

