package com.hirlu.boxvista.views.addbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.models.ObjectItem

@Composable
fun AddBoxView(
    state: AddBoxViewState,
    isAuthenticated: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddObject: (ObjectItem) -> Unit,
    onObjectStateChange: (Int, Boolean) -> Unit,
    onSave: () -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AddBoxContent(
        state = state,
        isAuthenticated = isAuthenticated,
        onNameChange = onNameChange,
        onDescriptionChange = onDescriptionChange,
        onAddObject = onAddObject,
        onObjectStateChange = onObjectStateChange,
        onSave = onSave,
        onOpenAuth = onOpenAuth,
        modifier = modifier,
    )
}

@Composable
private fun AddBoxContent(
    state: AddBoxViewState,
    isAuthenticated: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddObject: (ObjectItem) -> Unit,
    onObjectStateChange: (Int, Boolean) -> Unit,
    onSave: () -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var objectName by remember { mutableStateOf("") }
    val canSave = isAuthenticated && !state.isSaving

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Nueva caja",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        if (!isAuthenticated) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Inicia sesión o regístrate antes de guardar una caja.")
                        Button(onClick = onOpenAuth) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Text("Abrir login/registro", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = objectName,
                    onValueChange = { objectName = it },
                    label = { Text("Nuevo objeto") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = {
                        val trimmedName = objectName.trim()
                        if (trimmedName.isNotEmpty()) {
                            onAddObject(
                                ObjectItem(
                                    name = trimmedName,
                                    state = true,
                                    id = -1L,
                                    boxId = -1L,
                                )
                            )
                            objectName = ""
                        }
                    },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        }

        if (state.objects.isEmpty()) {
            item {
                Text(
                    text = "Añade al menos un objeto y déjalo activo para poder guardar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            itemsIndexed(state.objects) { index, objectItem ->
                ObjectCard(
                    name = objectItem.name,
                    state = objectItem.state,
                    onStateChange = { isActive -> onObjectStateChange(index, isActive) },
                )
            }
        }

        state.error?.let { error ->
            item {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        item {
            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                }
                Text("Guardar", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun ObjectCard(
    name: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = state,
                onCheckedChange = onStateChange,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBoxViewPreview() {
    AddBoxContent(
        state = AddBoxViewState(
            name = "Caja de prueba",
            description = "Descripción de prueba",
            objects = listOf(
                ObjectItem(id = 1L, name = "Objeto 1", state = true, boxId = 1L),
                ObjectItem(id = 2L, name = "Objeto 2", state = false, boxId = 1L),
            ),
        ),
        isAuthenticated = true,
        onNameChange = {},
        onDescriptionChange = {},
        onAddObject = {},
        onObjectStateChange = { _, _ -> },
        onSave = {},
        onOpenAuth = {},
    )
}
