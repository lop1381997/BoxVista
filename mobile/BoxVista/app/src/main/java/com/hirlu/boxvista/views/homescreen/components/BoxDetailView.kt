package com.hirlu.boxvista.views.homescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem

data class EditableBoxObject(
    val id: Long,
    val name: String,
    val state: Boolean
)

@Composable
fun BoxDetailModal(
    box: Box,
    isSaving: Boolean,
    actionError: String?,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onToggleObject: (ObjectItem, Boolean) -> Unit,
    onSaveEdition: (String, String, List<EditableBoxObject>) -> Unit
) {
    var isEditing by remember(box.id) { mutableStateOf(false) }
    var boxName by remember(box.id) { mutableStateOf(box.name) }
    var boxDescription by remember(box.id) { mutableStateOf(box.description) }
    val editableObjects = remember(box.id) {
        mutableStateListOf<EditableBoxObject>().apply {
            addAll(box.objects.map { EditableBoxObject(id = it.id, name = it.name, state = it.state) })
        }
    }
    var newObjectName by remember(box.id) { mutableStateOf("") }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isEditing) {
                Text("📦 ${box.name}", modifier = Modifier.padding(bottom = 8.dp))

                if (box.description.isNotEmpty()) {
                    Text(
                        box.description,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (box.objects.isNotEmpty()) {
                    Text("🔧 Objetos en esta caja:", modifier = Modifier.padding(bottom = 8.dp))
                    box.objects.forEach { obj ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(obj.name)
                            Checkbox(
                                checked = obj.state,
                                onCheckedChange = { checked -> onToggleObject(obj, checked) }
                            )
                        }
                    }
                } else {
                    Text("Esta caja está vacía", modifier = Modifier.padding(bottom = 16.dp))
                }

                if (actionError != null) {
                    Text(actionError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onDelete, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("Borrar")
                    }
                    Button(onClick = { isEditing = true }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("Editar")
                    }
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("Cerrar")
                    }
                }
            } else {
                Text("Editar caja", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = boxName,
                    onValueChange = { boxName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = boxDescription,
                    onValueChange = { boxDescription = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            editableObjects.add(
                                EditableBoxObject(
                                    id = System.currentTimeMillis() + editableObjects.size,
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    itemsIndexed(editableObjects, key = { _, item -> item.id }) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.state,
                                    onCheckedChange = { checked ->
                                        editableObjects[index] = item.copy(state = checked)
                                    }
                                )
                                Text(item.name)
                            }
                            Button(onClick = { editableObjects.removeAt(index) }) {
                                Text("Quitar")
                            }
                        }
                    }
                }

                if (actionError != null) {
                    Text(actionError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        onSaveEdition(boxName, boxDescription, editableObjects.toList())
                    }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text(if (isSaving) "Guardando..." else "Guardar")
                    }
                    Button(onClick = { isEditing = false }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
