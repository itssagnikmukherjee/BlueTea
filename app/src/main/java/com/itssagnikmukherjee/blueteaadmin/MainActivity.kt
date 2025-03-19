package com.itssagnikmukherjee.blueteaadmin

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.AddCategoryScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.AddProductScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.OrdersScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner.AddBannerScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.BlueTeaAdminTheme
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

                // State to manage the current screen
                var currentScreen by remember { mutableStateOf("OrdersScreen") }

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
                            TopBar(
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
                            // Display the selected screen based on the state
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
fun TopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior, onMenuClick: () -> Unit) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = "BazaarIO",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        actions = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                modifier = modifier.padding(horizontal = 10.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Profile",
                modifier = modifier.padding(horizontal = 10.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    TopBar(
        scrollBehavior = scrollBehavior,
        onMenuClick = {}
    )
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "BazaarIO",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }

        Column {
            // Drawer Items
            NavigationDrawerItem(
                label = { Text("Modify Categories", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Modify Categories") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text("Banner Settings", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Banner Settings") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text("Manage Products", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Manage Products") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text("Manage Orders", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Manage Orders") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) }
            )
        }
        Spacer(modifier = Modifier.height(250.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            NavigationDrawerItem(
                label = { Text("Profile", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Manage Orders") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text("Settings", style = MaterialTheme.typography.bodyLarge) },
                selected = false,
                onClick = { onItemClick("Manage Orders") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.White
                ),
                modifier = Modifier.padding(8.dp),
                icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) }
            )
        }
    }
}
