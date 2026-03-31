package com.example.praktam_2417051034.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background

@Composable
fun NotificationScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))
        NotificationItem("You spent Rp 25.000 on Makan Gacoan")
        NotificationItem("You spent Rp 15.000 on Transport Ojek")
        NotificationItem("Don't forget to track today's expenses")
    }
}
@Composable
fun NotificationItem(message: String){
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)){
            Text("🔔",
                style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
