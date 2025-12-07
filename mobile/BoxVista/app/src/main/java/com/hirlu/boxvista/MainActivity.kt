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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.ui.theme.BoxVistaTheme
import com.hirlu.boxvista.views.homescreen.HomeScreenView
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
            "Settings" to Icons.Filled.Settings,
            "Add Box" to Icons.Filled.Add
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
                    1 -> FavoritesScreen()
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
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings Screen Content")
    }
}




@Preview(showBackground = true)
@Composable
fun TabScreenPreview() {
    TabScreen()

}
