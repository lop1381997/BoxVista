package com.hirlu.boxvista.views.homescreen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.models.ObjectItem

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