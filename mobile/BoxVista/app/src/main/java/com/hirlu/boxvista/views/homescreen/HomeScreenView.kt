package com.hirlu.boxvista.views.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem

@Composable
fun HomeScreenView(
    viewModel: HomeScreenViewModel = viewModel())
{
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadBoxes() }

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
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.boxes.forEach { box ->
                    item { HomeScreenBoxViewBox(box) }
                }

            }

            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    state.boxes.forEach { box ->
                        if (box.objects.isNotEmpty()) {
                             HomeScreenBoxViewObjects(box.objects)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun HomeScreenBoxViewBox(box : Box){

    Card (
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ){
        Text("📦 ${box.name}",
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterHorizontally)
        )
        if(box.description.isNotEmpty()){
            Text(box.description,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

    }

}

@Composable
fun HomeScreenBoxViewObjects(items: List<ObjectItem> = listOf()){
    Card (
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ){
        items.forEach {
            Text("🔧 ${it.name}",
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

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

    HomeScreenBoxViewBox(box = sampleBox)
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

    HomeScreenBoxViewBox(box = emptyBox)
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

    HomeScreenBoxViewBox(box = boxNoDescription)
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
    // Simulamos el estado con datos usando LazyColumn
    LazyColumn {
        item {
            HomeScreenBoxViewBox(
                box = Box(
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
            )
        }
        item {
            HomeScreenBoxViewBox(
                box = Box(
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
        }
        item {
            HomeScreenBoxViewBox(
                box = Box(
                    id = 3,
                    name = "Caja vacía",
                    description = "Esta caja está vacía",
                    objects = mutableListOf()
                )
            )
        }
    }
}
