package com.example.praktam_2417051034.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment

@Composable
fun NotesScreen() {

    var notes by remember { mutableStateOf(listOf<String>()) }
    var newNote by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "")
                }
                Text(
                    text = "My Notes",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notes.isEmpty()) {
                Text("Belum ada catatan...")
            } else {
                LazyColumn {
                    items(notes) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                note,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Tambah Catatan")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },

            title = { Text("Tambah Catatan") },

            text = {
                TextField(
                    value = newNote,
                    onValueChange = { newNote = it },
                    placeholder = { Text("Tulis catatan...") }
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(2000)

                            if (newNote.isNotBlank()) {
                                notes = notes + newNote
                                newNote = ""

                                showDialog = false

                                snackbarHostState.showSnackbar(
                                    "Catatan berhasil ditambahkan!"
                                )
                            }

                            isLoading = false
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menyimpan...")
                    } else {
                        Text("Simpan")
                    }
                }
            },

            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}