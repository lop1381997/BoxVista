package com.hirlu.boxvista.views.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.views.homescreen.components.BoxDetailModal
import com.hirlu.boxvista.views.homescreen.components.EditableBoxObject
import com.hirlu.boxvista.views.homescreen.components.HomeScreenBoxView
import com.hirlu.boxvista.views.homescreen.components.HomeScreenBoxViewObjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    refreshTrigger: Int = 0,
    viewModel: HomeScreenViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedBox by remember { mutableStateOf<Box?>(null) }
    var showDetail by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(refreshTrigger) { viewModel.loadBoxes() }

    if (showDeleteConfirm && selectedBox != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Seguro que quieres borrar la caja? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(onClick = {
                    val box = selectedBox ?: return@Button
                    viewModel.deleteBox(box) {
                        showDeleteConfirm = false
                        showDetail = false
                        selectedBox = null
                    }
                }) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDetail && selectedBox != null) {
        val liveBox = state.boxes.firstOrNull { it.id == selectedBox?.id } ?: selectedBox!!
        ModalBottomSheet(
            onDismissRequest = {
                showDetail = false
                selectedBox = null
            },
            sheetState = sheetState
        ) {
            BoxDetailModal(
                box = liveBox,
                isSaving = state.isSaving,
                actionError = state.actionError,
                onDismiss = {
                    showDetail = false
                    selectedBox = null
                },
                onDelete = { showDeleteConfirm = true },
                onToggleObject = { obj, checked ->
                    val boxId = liveBox.id ?: return@BoxDetailModal
                    viewModel.updateObjectState(boxId = boxId, objectItem = obj, newState = checked)
                },
                onSaveEdition = { name, description, objects ->
                    val localError = validateEditPayload(name, objects)
                    if (localError != null) {
                        viewModel.setActionError(localError)
                        return@BoxDetailModal
                    }
                    val updated = liveBox.copy(
                        name = name.trim(),
                        description = description.trim(),
                        objects = objects.mapIndexed { idx, item ->
                            ObjectItem(
                                id = item.id,
                                name = item.name,
                                state = item.state,
                                boxId = liveBox.id ?: idx.toLong()
                            )
                        }.toMutableList()
                    )
                    viewModel.updateBox(updated)
                }
            )
        }
    }

    when {
        state.isLoading -> Text("Cargando…")
        state.error != null -> {
            Column {
                Text("Error: ${state.error}")
                Button(onClick = viewModel::retry) { Text("Reintentar") }
            }
        }

        state.isEmpty -> Text("No hay cajas todavía.")

        else -> {
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

                item {
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                item {
                    Text("🔧 Objetos disponibles:")
                    Spacer(modifier = Modifier.padding(4.dp))
                }

                state.boxes.forEach { box ->
                    if (box.objects.isNotEmpty()) {
                        item { HomeScreenBoxViewObjects(box.objects) }
                    }
                }
            }
        }
    }
}

private fun validateEditPayload(name: String, objects: List<EditableBoxObject>): String? {
    return when {
        name.isBlank() -> "El nombre de la caja no puede estar vacío"
        objects.none { it.state } -> "Debe haber al menos un objeto activo en la caja"
        else -> null
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewSimple() {
    Text("Home")
}
