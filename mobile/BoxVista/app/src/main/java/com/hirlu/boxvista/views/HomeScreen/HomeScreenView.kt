package com.hirlu.boxvista.views.HomeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hirlu.boxvista.views.home.HomeScreenViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.Models.Box

@Composable
fun HomeScreenView(
    viewModel: HomeScreenViewModel = viewModel())
{
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadBoxes() }

    when {
        state.isLoading -> Text("Cargando…")
        state.error != null -> Column {
            Text("Error: ${state.error}")
            Button(onClick = viewModel::retry) { Text("Reintentar") }
        }
        state.isEmpty -> Text("No hay cajas todavía.")
        else -> LazyColumn {
            state.boxes.forEach { box ->
                item { HomeScreenBoxView(box)}
            }
        }
    }
}
@Composable
fun HomeScreenBoxView(box : Box){
    Card (){
        Column {
            Text("📦 ${box.name}")
            if(box.description.isNotEmpty()){
                Text(box.description)
            }
            if(box.objects.isNotEmpty()){
                Text("Contiene:")
                box.objects.forEach { obj ->
                    Text("   - 📄 ${obj.name}")
                }
            } else {
                Text("   (vacío)")
            }
        }
    }
}
