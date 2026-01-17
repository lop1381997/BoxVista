package com.hirlu.boxvista.views.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.views.homescreen.components.BoxDetailModal
import com.hirlu.boxvista.views.homescreen.components.HomeScreenBoxView
import com.hirlu.boxvista.views.homescreen.components.HomeScreenBoxViewObjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    viewModel: HomeScreenViewModel = viewModel())
{
    val state by viewModel.state.collectAsState()
    //show detail box
    var selectedBox by remember { mutableStateOf<Box?>(null) }
    var showDetail by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.loadBoxes() }

    if (showDetail && selectedBox != null){
        ModalBottomSheet(
            onDismissRequest = {
                showDetail = false
                selectedBox = null
            },
            sheetState = sheetState
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
    
    when {
        state.isLoading -> {
            Text("Cargando…")
        }

        state.error != null -> {
            Column {
                Text("Error: ${state.error}")
                Button(onClick = viewModel::retry) { Text("Reintentar") }
            }
        }

        state.isEmpty -> {
            Text("No hay cajas todavía.")
        }

        else -> {
            // Use items() with the list directly for safe and efficient list handling
            // This avoids index-based access issues during recomposition
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título de cajas
                item {
                    Text("📦 Cajas disponibles:")
                    Spacer(modifier = Modifier.padding(4.dp))
                }

                // Listado de cajas
                state.boxes.forEach { box ->
                    item {
                        HomeScreenBoxView(
                            box = box,
                            onClick = {
                                selectedBox = box
                                showDetail = true
                            }
                        )
                    }
                }

                // Espaciador entre secciones
                item {
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                // Título de objetos
                item {
                    Text("🔧 Objetos disponibles:")
                    Spacer(modifier = Modifier.padding(4.dp))
                }

                // Listado de objetos
                state.boxes.forEach { box ->
                    if (box.objects.isNotEmpty()) {
                        item { HomeScreenBoxViewObjects(box.objects) }
                    }
                }
            }
        }
    }

}


// Previews
@Preview(showBackground = true)
@Composable
fun HomeScreenBoxViewPreview() {
    val sampleBox = Box(
        id = 1,
        name = "Caja de repuestos",
        description = "Contiene piezas de repuesto para impresoras y ordenadores.",
        objects = mutableListOf(
            ObjectItem(
                id = 1,
                name = "Tóner HP 17A",
                state = true,
                boxId = 1
            ),
            ObjectItem(
                id = 2,
                name = "Cable de alimentación universal",
                state = true,
                boxId = 1
            )
        )
    )

    HomeScreenBoxView(box = sampleBox)
}

@Preview(showBackground = true)
@Composable
fun HomeScreenBoxViewEmptyPreview() {
    val emptyBox = Box(
        id = 2,
        name = "Caja vacía",
        description = "Esta caja no contiene objetos",
        objects = mutableListOf()
    )

    HomeScreenBoxView(box = emptyBox)
}

@Preview(showBackground = true)
@Composable
fun HomeScreenBoxViewNoDescriptionPreview() {
    val boxNoDescription = Box(
        id = 3,
        name = "Caja sin descripción",
        description = "",
        objects = mutableListOf(
            ObjectItem(
                id = 15,
                name = "Drill",
                state = false,
                boxId = 3
            )
        )
    )

    HomeScreenBoxView(box = boxNoDescription)
}

@Preview(showBackground = true, name = "HomeScreen - Cargando")
@Composable
fun HomeScreenViewLoadingPreview() {
    // Simulamos el estado de carga mostrando solo el texto
    Text("Cargando…")
}

@Preview(showBackground = true, name = "HomeScreen - Error")
@Composable
fun HomeScreenViewErrorPreview() {
    // Simulamos el estado de error
    Column {
        Text("Error: No se pudieron cargar las cajas")
        Button(onClick = { /* No action in preview */ }) {
            Text("Reintentar")
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Vacío")
@Composable
fun HomeScreenViewEmptyPreview() {
    // Simulamos el estado vacío
    Text("No hay cajas todavía.")
}

@Preview(showBackground = true, name = "HomeScreen - Con datos")
@Composable
fun HomeScreenViewWithDataPreview() {
    // Simulamos el estado con datos usando una sola LazyColumn
    val boxes = listOf(
        Box(
            id = 1,
            name = "Caja de repuestos",
            description = "Contiene piezas de repuesto para impresoras y ordenadores.",
            objects = mutableListOf(
                ObjectItem(
                    id = 1,
                    name = "Tóner HP 17A",
                    state = true,
                    boxId = 1
                ),
                ObjectItem(
                    id = 2,
                    name = "Cable de alimentación universal",
                    state = true,
                    boxId = 1
                )
            )
        ),
        Box(
            id = 2,
            name = "Caja de herramientas",
            description = "Herramientas diversas para mantenimiento",
            objects = mutableListOf(
                ObjectItem(
                    id = 15,
                    name = "Drill",
                    state = false,
                    boxId = 2
                ),
                ObjectItem(
                    id = 16,
                    name = "Hammer",
                    state = true,
                    boxId = 2
                )
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("📦 Cajas disponibles:")
            Spacer(modifier = Modifier.padding(4.dp))
        }

        boxes.forEach { box ->
            item { HomeScreenBoxView(box) }
        }

        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }

        item {
            Text("🔧 Objetos disponibles:")
            Spacer(modifier = Modifier.padding(4.dp))
        }

        boxes.forEach { box ->
            if (box.objects.isNotEmpty()) {
                item { HomeScreenBoxViewObjects(box.objects) }
            }
        }
    }
}
