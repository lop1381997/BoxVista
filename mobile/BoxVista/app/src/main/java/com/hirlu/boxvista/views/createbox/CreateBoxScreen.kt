package com.hirlu.boxvista.views.createbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

private val defaultBoxTypes = listOf("Electrónica", "Herramientas", "Documentos", "Repuestos")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoxScreen(
    viewModel: CreateBoxViewModel = viewModel(),
    boxTypes: List<String> = defaultBoxTypes
) {
    val state by viewModel.state.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear nueva caja")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = state.selectedType,
                onValueChange = {},
                label = { Text("Tipo de caja") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                boxTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            viewModel.onTypeSelected(type)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (state.error != null) {
            Text("Error: ${state.error}")
        }

        Button(
            onClick = viewModel::createBox,
            enabled = !state.isLoading && state.selectedType.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isLoading) "Creando..." else "Crear")
        }
    }

    if (state.createdBoxId != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissCreatedDialog,
            title = { Text("Caja creada") },
            text = { Text("UUID/ID de la caja: ${state.createdBoxId}") },
            confirmButton = {
                TextButton(onClick = viewModel::dismissCreatedDialog) {
                    Text("Aceptar")
                }
            }
        )
    }
}
