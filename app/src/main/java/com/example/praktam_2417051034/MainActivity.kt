package com.example.praktam_2417051034

import Model.Food
import Model.FoodSource
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
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image


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
    val food = FoodSource.dummyFood[0]

    Column(modifier = Modifier.fillMaxSize().padding(all = 30.dp)){
        Image(
            painter = painterResource(id = food.ImageRes),
            contentDescription = food.nama,
            modifier = Modifier.size(200.dp),
            contentScale = Crop
        )
        Text(text = "Nama: ${food.nama}")
        Text(text = "Deskripsi: ${food.deskripsi}")
        Text(text = "Harga: ${food.harga}")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051034Theme {
        Greeting(nama ="Tiwi Mustika Dewi", npm = "2417051034", ideProject = "ExpenseLite - Daily Expense Tracker")
    }
}