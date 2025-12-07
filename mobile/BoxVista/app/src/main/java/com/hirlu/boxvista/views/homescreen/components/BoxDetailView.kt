package com.hirlu.boxvista.views.homescreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.models.Box

@Composable
fun BoxDetailModal(box: Box, onDismiss: () -> Unit) {
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
            Text("📦 ${box.name}", modifier = Modifier.padding(bottom = 8.dp))

            if (box.description.isNotEmpty()) {
                Text(box.description, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))
            }

            if (box.objects.isNotEmpty()) {
                Text("🔧 Objetos en esta caja:", modifier = Modifier.padding(bottom = 8.dp))
                box.objects.forEach { obj ->
                    Text("• ${obj.name} ${if (obj.state) "✅" else "❌"}", modifier = Modifier.padding(4.dp))
                }
            } else {
                Text("Esta caja está vacía", modifier = Modifier.padding(bottom = 16.dp))
            }

            Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                Text("Cerrar")
            }
        }
    }
}