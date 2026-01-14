package com.hirlu.boxvista.views.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.models.Box as BoxModel
import com.hirlu.boxvista.ui.theme.BoxVistaBlue
import com.hirlu.boxvista.ui.theme.BoxVistaDarkBackground
import com.hirlu.boxvista.ui.theme.BoxVistaDarkSurface
import com.hirlu.boxvista.ui.theme.BoxVistaGray
import com.hirlu.boxvista.ui.theme.BoxVistaGreen
import com.hirlu.boxvista.ui.theme.BoxVistaRed
import com.hirlu.boxvista.ui.theme.BoxVistaWhite
import com.hirlu.boxvista.ui.theme.BoxVistaYellow
import com.hirlu.boxvista.views.homescreen.components.BoxDetailModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    viewModel: HomeScreenViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedBox by remember { mutableStateOf<BoxModel?>(null) }
    var showDetail by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) { viewModel.loadBoxes() }

    if (showDetail && selectedBox != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showDetail = false
                selectedBox = null
            },
            sheetState = sheetState,
            containerColor = BoxVistaDarkSurface
        ) {
            BoxDetailModal(
                box = selectedBox!!,
                onDismiss = {
                    showDetail = false
                    selectedBox = null
                }
            )
        }
    }

    Scaffold(
        containerColor = BoxVistaDarkBackground,
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))
            FilterChips()
            Spacer(modifier = Modifier.height(24.dp))
            StatsSection()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Recent Scans",
                color = BoxVistaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            when {
                state.isLoading -> Text("Loading...", color = BoxVistaWhite)
                state.error != null -> {
                    Text("Error: ${state.error}", color = BoxVistaRed)
                    Button(onClick = viewModel::retry) { Text("Retry") }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.boxes) { box ->
                            RecentScanCard(box = box, onClick = {
                                selectedBox = box
                                showDetail = true
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BoxVistaBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = BoxVistaWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Good Morning,", color = BoxVistaGray, fontSize = 12.sp)
                Text("Alex", color = BoxVistaWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = BoxVistaWhite
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search Box ID, RFID, or Item...", color = BoxVistaGray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BoxVistaGray) },
        trailingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = BoxVistaBlue) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BoxVistaDarkSurface,
            unfocusedContainerColor = BoxVistaDarkSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = BoxVistaBlue
        )
    )
}

@Composable
fun FilterChips() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FilterChip("All Filters", true) }
        item { FilterChip("Warehouse A", false) }
        item { FilterChip("Discrepancies", false, isWarning = true) }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, isWarning: Boolean = false) {
    val backgroundColor = if (isSelected) BoxVistaBlue.copy(alpha = 0.2f) else BoxVistaDarkSurface
    val textColor = if (isSelected) BoxVistaBlue else if (isWarning) BoxVistaYellow else BoxVistaGray
    val borderColor = if (isSelected) BoxVistaBlue else if (isWarning) BoxVistaYellow else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isWarning) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = BoxVistaYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            number = "45",
            label = "Scanned Today",
            color = BoxVistaBlue,
            icon = Icons.Rounded.QrCodeScanner
        )
        StatCard(
            modifier = Modifier.weight(1f),
            number = "3",
            label = "Action Required",
            color = BoxVistaRed,
            icon = Icons.Rounded.Warning
        )
        StatCard(
            modifier = Modifier.weight(1f),
            number = "12",
            label = "Pending Check",
            color = BoxVistaYellow,
            icon = Icons.Rounded.Inventory
        )
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, number: String, label: String, color: Color, icon: ImageVector) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BoxVistaDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 Text(number, color = BoxVistaWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                 Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = BoxVistaGray, fontSize = 10.sp, lineHeight = 12.sp)
        }
    }
}

@Composable
fun RecentScanCard(box: BoxModel, onClick: () -> Unit) {
    // Mock data for UI fidelity
    val isMismatch = (box.id?.rem(2) == 0L)
    val statusColor = if (isMismatch) BoxVistaRed else BoxVistaGreen
    val statusText = if (isMismatch) "MISMATCH" else "VERIFIED"
    val icon = if (isMismatch) Icons.Rounded.Warning else Icons.Rounded.CheckCircle
    val location = "Zone ${('A'..'C').random()} - Shelf ${(1..5).random()}"
    val time = "10:${(10..59).random()} AM"

    Card(
        colors = CardDefaults.cardColors(containerColor = BoxVistaDarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#BX-${box.id ?: "000"}",
                    color = BoxVistaWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = time,
                    color = BoxVistaGray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = BoxVistaGray, modifier = Modifier.size(12.dp)) // Placeholder icon
                Spacer(modifier = Modifier.width(4.dp))
                Text(location, color = BoxVistaGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(24.dp)
                        .background(BoxVistaDarkBackground, RoundedCornerShape(4.dp))) // Placeholder image
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(box.name, color = BoxVistaGray, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = BoxVistaDarkBackground,
        contentColor = BoxVistaGray
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BoxVistaBlue,
                selectedTextColor = BoxVistaBlue,
                indicatorColor = Color.Transparent,
                unselectedIconColor = BoxVistaGray,
                unselectedTextColor = BoxVistaGray
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Rounded.Inventory, contentDescription = "Inventory") },
            label = { Text("Inventory", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = BoxVistaGray, unselectedTextColor = BoxVistaGray)
        )
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            Button(
                onClick = {},
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = BoxVistaBlue),
                modifier = Modifier.size(56.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Scan", tint = BoxVistaWhite)
            }
        }
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Rounded.Warning, contentDescription = "Alerts") },
            label = { Text("Alerts", fontSize = 10.sp) },
             colors = NavigationBarItemDefaults.colors(unselectedIconColor = BoxVistaGray, unselectedTextColor = BoxVistaGray)
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings", fontSize = 10.sp) },
             colors = NavigationBarItemDefaults.colors(unselectedIconColor = BoxVistaGray, unselectedTextColor = BoxVistaGray)
        )
    }
}
