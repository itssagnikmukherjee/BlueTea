package com.itssagnikmukherjee.blueteauser.presentation.screens.components
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

@Composable
fun NavbarComposable(navController: NavController, userId: String) {

    val navItemList = listOf(
        NavItem(icon = R.drawable.home, text = "Home"),
        NavItem(icon = R.drawable.heart_solid, text = "Favorite"),
        NavItem(icon = R.drawable.cart_filled, text = "Cart"),
        NavItem(icon = R.drawable.profile, text = "Profile")
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
                    .fillMaxWidth(0.9f)
                    .background(CustomColors.blackest)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(15.dp,10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(navItemList.size) { item ->
                    val isSelected = selectedNavItem == item

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else CustomColors.darkGray,
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
                                    painter = painterResource(navItemList[item].icon),
                                    contentDescription = navItemList[item].text,
                                    modifier = Modifier
                                        .size((24).dp),
                                    tint = iconColor
                                )
                            }

                            AnimatedVisibility(visible = isSelected) {
                                Text(
                                    text = navItemList[item].text,
                                    color = iconColor,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Medium
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
    val icon: Int,
    val text: String
)

