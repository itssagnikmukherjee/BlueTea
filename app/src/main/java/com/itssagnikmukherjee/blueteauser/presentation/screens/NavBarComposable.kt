package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes

@Composable
fun NavbarComposable(navController: NavController, userId: String) {

    val navItemList = listOf(
        NavItem(icon = Icons.Default.Home, text = "Home"),
        NavItem(icon = Icons.Default.Favorite, text = "Favorite"),
        NavItem(icon = Icons.Default.ShoppingCart, text = "Cart"),
        NavItem(icon = Icons.Default.Person, text = "Profile")
    )
    var selectedNavItem by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.clip(RoundedCornerShape(60.dp))) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color.White)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(15.dp,10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(navItemList.size) { item ->
                    val isSelected = selectedNavItem == item

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) Color.Blue else Color.Black,
                        animationSpec = tween(durationMillis = 300)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(5.dp,0.dp)
                        ) {
                            IconButton(onClick = {
                                selectedNavItem = item

                                val currentDestination = navController.currentBackStackEntry?.destination?.route
                                val targetRoute = when (navItemList[item].text) {
                                    "Home" -> Routes.HomeScreen
                                    "Favorite" -> Routes.WishListScreen(userId)
                                    "Cart" -> Routes.CartScreen(userId)
                                    "Profile" -> Routes.ProfileScreen(userId)
                                    else -> null
                                }

                                if (targetRoute != null && currentDestination != targetRoute) {
                                    navController.navigate(targetRoute) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            })
                            {
                                Icon(
                                    imageVector = navItemList[item].icon,
                                    contentDescription = navItemList[item].text,
                                    modifier = Modifier
                                        .size((30).dp),
                                    tint = iconColor
                                )
                            }

                            AnimatedVisibility(visible = isSelected) {
                                Text(
                                    text = navItemList[item].text,
                                    color = iconColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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

