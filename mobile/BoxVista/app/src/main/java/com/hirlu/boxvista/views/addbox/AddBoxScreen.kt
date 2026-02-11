package com.hirlu.boxvista.views.addbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.views.homescreen.HomeScreenViewModel

data class EditableObject(
    val id: Long,
    val name: String,
    val state: Boolean,
)

@Composable
fun AddBoxScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    onBoxCreated: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    var boxName by remember { mutableStateOf("") }
    var boxDescription by remember { mutableStateOf("") }
    var newObjectName by remember { mutableStateOf("") }
    val objects = remember { mutableStateListOf<EditableObject>() }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.actionError) {
        if (state.actionError != null) {
            localError = state.actionError
            viewModel.clearActionError()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Crear caja", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = boxName,
            onValueChange = { boxName = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = boxDescription,
            onValueChange = { boxDescription = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newObjectName,
                onValueChange = { newObjectName = it },
                label = { Text("Nuevo objeto") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Button(onClick = {
                if (newObjectName.isNotBlank()) {
                    objects.add(
                        EditableObject(
                            id = System.currentTimeMillis() + objects.size,
                            name = newObjectName.trim(),
                            state = true
                        )
                    )
                    newObjectName = ""
                }
            }) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Objetos", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            itemsIndexed(objects, key = { _, item -> item.id }) { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = item.state,
                                onCheckedChange = { checked ->
                                    objects[index] = item.copy(state = checked)
                                }
                            )
                            Text(item.name)
                        }
                        Button(onClick = { objects.removeAt(index) }) {
                            Text("Quitar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (localError != null) {
            Text(localError!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                localError = when {
                    boxName.isBlank() -> "El nombre de la caja no puede estar vacío"
                    objects.none { it.state } -> "Debe haber al menos un objeto activo en la caja"
                    else -> null
                }

                if (localError == null) {
                    val payload = objects.mapIndexed { idx, obj ->
                        ObjectItem(
                            id = idx.toLong() + 1,
                            name = obj.name,
                            state = obj.state,
                            boxId = 0L
                        )
                    }
                    viewModel.createBox(boxName.trim(), boxDescription.trim(), payload) {
                        boxName = ""
                        boxDescription = ""
                        objects.clear()
                        newObjectName = ""
                        onBoxCreated()
                    }
                }
            },
            enabled = !state.isSaving
        ) {
            Text(if (state.isSaving) "Guardando..." else "Guardar caja")
        }
    }
}
