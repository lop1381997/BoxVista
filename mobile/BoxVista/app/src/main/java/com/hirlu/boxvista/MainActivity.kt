package com.hirlu.boxvista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.auth.AuthRepository
import com.hirlu.boxvista.auth.SecureTokenStorage
import com.hirlu.boxvista.auth.TokenStorage
import com.hirlu.boxvista.ui.theme.BoxVistaTheme
import com.hirlu.boxvista.views.addbox.AddBoxView
import com.hirlu.boxvista.views.addbox.AddBoxViewModel
import com.hirlu.boxvista.views.auth.AuthScreen
import com.hirlu.boxvista.views.homescreen.HomeScreenView
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NetworkManager.init(BuildConfig.API_BASE_URL)
        val tokenStorage = SecureTokenStorage(applicationContext)
        NetworkManager.setAuthTokenProvider { tokenStorage.getToken() }
        val authRepository = AuthRepository(tokenStorage)
        setContent {
            TabScreen(authRepository = authRepository)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabScreen(authRepository: AuthRepository) {
    BoxVistaTheme {
        val tabItems: List<Pair<String, ImageVector>> = listOf(
            "Home" to Icons.Filled.Home,
            "Settings" to Icons.Filled.Settings,
            "Add Box" to Icons.Filled.Add
        )

        var selectedTabIndex: Int by remember { mutableIntStateOf(0) }
        var sessionVersion: Int by remember { mutableIntStateOf(0) }
        var isAuthenticated: Boolean by remember { mutableStateOf(authRepository.isAuthenticated()) }
        val pagerState = rememberPagerState(pageCount = { tabItems.size })
        val scope = rememberCoroutineScope()
        val addBoxViewModel: AddBoxViewModel = viewModel()
        val addBoxState by addBoxViewModel.state.collectAsState()

        fun selectTab(index: Int) {
            selectedTabIndex = index
            scope.launch { pagerState.animateScrollToPage(index) }
        }

        LaunchedEffect(pagerState.currentPage) {
            selectedTabIndex = pagerState.currentPage
        }

        Scaffold(
            bottomBar = {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, (title, icon) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectTab(index) },
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
                    0 -> HomeScreenView(
                        sessionVersion = sessionVersion,
                        onOpenAuth = { selectTab(1) },
                    )
                    1 -> AuthScreen(
                        authRepository = authRepository,
                        isAuthenticated = isAuthenticated,
                        onSessionChanged = { authenticated ->
                            isAuthenticated = authenticated
                            sessionVersion += 1
                        },
                    )
                    2 -> AddBoxView(
                        state = addBoxState,
                        isAuthenticated = isAuthenticated,
                        onNameChange = addBoxViewModel::onNameChange,
                        onDescriptionChange = addBoxViewModel::descriptionChange,
                        onAddObject = addBoxViewModel::addObject,
                        onObjectStateChange = addBoxViewModel::updateObjectState,
                        onSave = {
                            addBoxViewModel.createNewBox {
                                sessionVersion += 1
                                selectTab(0)
                            }
                        },
                        onOpenAuth = { selectTab(1) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabScreenPreview() {
    TabScreen(authRepository = AuthRepository(PreviewTokenStorage()))
}

private class PreviewTokenStorage : TokenStorage {
    private var token: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun clearToken() {
        token = null
    }
}
