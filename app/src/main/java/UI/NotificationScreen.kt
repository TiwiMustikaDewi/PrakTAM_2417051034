package com.example.praktam_2417051034.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.Card

@Composable
fun NotificationScreen(){
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Text(
            text = "Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ){
        Row(modifier = Modifier.padding(16.dp)){
            Text("🔔")
            Spacer(modifier = Modifier.width(10.dp))
            Text(message)
        }
    }
}
