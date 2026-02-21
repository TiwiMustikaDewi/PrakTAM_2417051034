package com.example.praktam_2417051034

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.praktam_2417051034.ui.theme.PrakTAM_2417051034Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051034Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        nama = "Tiwi Mustika Dewi",
                        npm = "2417051034",
                        ideProject = "ExpenseLite - Daily Expense Tracker",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(nama: String, npm: String, ideProject: String, modifier: Modifier = Modifier) {
    Text(
        text = "Halo, saya $nama dengan NPM $npm siap belajar Compose!\n" +
                "Ide Project saya adalah $ideProject",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051034Theme {
        Greeting(nama ="Tiwi Mustika Dewi", npm = "2417051034", ideProject = "ExpenseLite - Daily Expense Tracker")
    }
}