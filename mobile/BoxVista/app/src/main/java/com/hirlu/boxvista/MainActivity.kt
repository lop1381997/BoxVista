package com.hirlu.boxvista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.services.AuthService
import com.hirlu.boxvista.services.SecureTokenStore
import com.hirlu.boxvista.ui.theme.BoxVistaTheme
import com.hirlu.boxvista.views.createbox.CreateBoxScreen
import com.hirlu.boxvista.views.homescreen.HomeScreenView
import com.hirlu.boxvista.views.login.LoginScreen
import com.hirlu.boxvista.views.login.LoginViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NetworkManager.init()
        setContent {
            TabScreen()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabScreen() {
    BoxVistaTheme {
        val tabItems: List<Pair<String, ImageVector>> = listOf(
            "Home" to Icons.Filled.Home,
            "Crear caja" to Icons.Filled.Add,
            "Settings" to Icons.Filled.Settings
        )

        var selectedTabIndex: Int by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState(pageCount = { tabItems.size })
        val scope = rememberCoroutineScope()
        Scaffold(
            bottomBar = {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, (title, icon) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = { Text(title) },
                            icon = { Icon(icon, contentDescription = null) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(paddingValues)
            ) { page ->
                when (page) {
                    0 -> HomeScreenView()
                    1 -> CreateBoxScreen()
                    2 -> SettingsScreen()
                }
            }
        }
    }
}







@Composable
fun FavoritesScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Favorites Screen Content")
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(
                    authService = AuthService(),
                    tokenStore = SecureTokenStore(context.applicationContext)
                ) as T
            }
        }
    )

    LoginScreen(viewModel = loginViewModel)
}




@Preview(showBackground = true)
@Composable
fun TabScreenPreview() {
    TabScreen()

}
