package com.okediran.administrator.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.okediran.administrator.R
import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilRequest
import com.okediran.administrator.data.models.ResultState
import com.okediran.administrator.presentation.theme.NewGlobeBlue
import com.okediran.administrator.presentation.viewmodels.PupilViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PupilDetailsScreen(
    navController: NavController,
    pupilId: Int,
    viewModel: PupilViewModel = hiltViewModel()
) {
    val pupilState by viewModel.pupilState.collectAsState()
    val pupilUpdateState by viewModel.updateState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    // Fetch pupil when screen loads
    LaunchedEffect(Unit) {
        viewModel.getPupilById(pupilId)
    }

    when (pupilState) {
        is ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val pupil = (pupilState as ResultState.Success<Pupil>).data
            name = pupil.name
            country = pupil.country

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(pupil.name) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = NewGlobeBlue,
                            titleContentColor = Color.White
                        ),
                        actions = {
                            IconButton(onClick = { showEditDialog = true }) { Text("✏️") }
                            IconButton(onClick = { showDeleteDialog = true }) { Text("🗑️") }
                        })
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = pupil.image,
                        contentDescription = "Pupil Image",
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_default_avatar)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = pupil.name, fontWeight = FontWeight.Bold)
                    Text(text = pupil.country, color = Color.Gray)
                }
            }

            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Pupil") },
                    text = {
                        Column {
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") })
                            TextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country") })
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val updatedPupil = PupilRequest(
                                country = country,
                                name = name,
                                image = pupil.image,
                                latitude = pupil.latitude,
                                longitude = pupil.longitude
                            )
                            viewModel.updatePupil(pupil.pupilId!!, updatedPupil)
                            showEditDialog = false
                        }) {
                            Text("Updated")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showEditDialog = false }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Pupil") },
                    text = { Text("Are you sure you want to delete this pupil?") },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.deletePupil(pupil.pupilId!!) {
                                navController.popBackStack()
                            }
                            showDeleteDialog = false
                        }) {
                            Text("Deleted")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }

        is ResultState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error loading pupil details", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        }

        else -> {}
    }
}
