package com.example.praktam_2417051034

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.praktam_2417051034.ui.DashboardScreen
import com.example.praktam_2417051034.ui.theme.PrakTAM_2417051034Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2417051034Theme {
                DashboardScreen()
            }
        }
    }
}
