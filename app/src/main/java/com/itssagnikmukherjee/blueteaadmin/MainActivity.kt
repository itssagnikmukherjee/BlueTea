package com.itssagnikmukherjee.blueteaadmin

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.AddCategoryScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.AddProductScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.OrdersScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.ShimmerScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner.AddBannerScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.BlueTeaAdminTheme
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueTeaAdminTheme {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )

                var currentScreen by remember { mutableStateOf("AddProductScreen") }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        Box(modifier = Modifier.clip(RoundedCornerShape(30.dp))) {
                            NavigationDrawerContent { item ->
                                scope.launch { drawerState.close() }
                                currentScreen = when (item) {
                                    "Modify Categories" -> "AddCategoryScreen"
                                    "Banner Settings" -> "AddBannerScreen"
                                    "Manage Products" -> "AddProductScreen"
                                    "Manage Orders" -> "OrdersScreen"
                                    else -> currentScreen
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            FloatingTopBar(
                                scrollBehavior = scrollBehavior,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {

                            when (currentScreen) {
                                "AddBannerScreen" -> AddBannerScreen()
                                "AddCategoryScreen" -> AddCategoryScreen()
                                "AddProductScreen" -> AddProductScreen()
                                "OrdersScreen" -> OrdersScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingTopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior, onMenuClick: () -> Unit) {
    val insets = WindowInsets.systemBars
        .only(WindowInsetsSides.Top)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .windowInsetsPadding(insets)
    ) {
        TopAppBar(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(primaryBlack),
            scrollBehavior = scrollBehavior,
            title = {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "BaazarIO",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.bars),
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.clickable{
                            onMenuClick()
                        }.size(40.dp).padding(horizontal = 10.dp)
                    )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            actions = {

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bell_solid),
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                    Text("3", fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = primaryBlack, modifier = Modifier.padding(horizontal = 6.dp))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .background(Color.White)
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp)),
    ) {

        Spacer(Modifier.height(80.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.clip(CircleShape).height(150.dp).width(150.dp)) {
                    Image(
                        painter = painterResource(R.drawable.tea),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)
                    )
                }
                Text(
                    text = "Blue Tea",
                    style = MaterialTheme.typography.headlineMedium,
                    color = primaryBlack,
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(50.dp))

        Column {
            NavigationDrawerItem(
                label = { Text("Categories", style = MaterialTheme.typography.bodyLarge, color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Modify Categories") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.categories), contentDescription = null, modifier = Modifier.size(20.dp), tint = primaryBlack) }
            )
            NavigationDrawerItem(
                label = { Text("Banner Settings", style = MaterialTheme.typography.bodyLarge, color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Banner Settings") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.banner), contentDescription = null, modifier = Modifier.size(20.dp), tint = primaryBlack) }
            )
            NavigationDrawerItem(
                label = { Text("Manage Products", style = MaterialTheme.typography.bodyLarge, color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Manage Products") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.box), contentDescription = null, modifier = Modifier.size(20.dp), tint = primaryBlack) }
            )
            NavigationDrawerItem(
                label = { Text("Manage Orders", style = MaterialTheme.typography.bodyLarge, color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Manage Orders") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.clipboard), contentDescription = null, modifier = Modifier.size(20.dp), tint = primaryBlack) }
            )
        }

        Spacer(Modifier.height(80.dp))
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationDrawerItem(
                label = { Text("Store", style = MaterialTheme.typography.bodyLarge, color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Store") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.store_solid), contentDescription = null, modifier = Modifier.size(20.dp) , tint = primaryBlack) }
            )
            NavigationDrawerItem(
                label = { Text("Settings", style = MaterialTheme.typography.bodyLarge , color = primaryBlack) },
                selected = false,
                onClick = { onItemClick("Settings") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                icon = { Icon(painter = painterResource(R.drawable.gear_solid), contentDescription = null, modifier = Modifier.size(20.dp) , tint = primaryBlack) }
            )
        }
    }
}