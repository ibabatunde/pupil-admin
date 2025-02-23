package com.okediran.administrator.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilResponse
import com.okediran.administrator.data.models.ResultState
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.okediran.administrator.R
import com.okediran.administrator.presentation.theme.NewGlobeBlue
import com.okediran.administrator.presentation.viewmodels.PupilViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PupilListScreen(
    pupils: StateFlow<ResultState<PupilResponse>>,
    onPupilClick: (Int) -> Unit,
    onAddPupilClick: () -> Unit,
    viewModel: PupilViewModel // Add ViewModel as a parameter
) {
    val pupilsState by pupils.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrator") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = NewGlobeBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPupilClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Pupil")
            }
        }
    ) { paddingValues ->
        when (val state = pupilsState) {
            is ResultState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ResultState.Success -> {
                val pupilList = state.data.items

                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    itemsIndexed(pupilList) { index, pupil ->
                        PupilItem(pupil, onClick = { onPupilClick(pupil.pupilId!!) })
                        if (index < pupilList.lastIndex) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }

            is ResultState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.error}", color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchPupils() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is ResultState.Idle, is ResultState.Empty  -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No Data Available")
                    }
                    Button(onClick = { viewModel.fetchPupils() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun PupilItem(pupil: Pupil, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = pupil.image,
            contentDescription = "Pupil Image",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_default_avatar)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = pupil.name, fontWeight = FontWeight.Bold)
            Text(text = pupil.country, color = Color.Gray)
        }
    }
}

