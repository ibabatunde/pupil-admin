package com.okediran.administrator.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.okediran.administrator.presentation.ui.screens.CreatePupilScreen
import com.okediran.administrator.presentation.ui.screens.PupilDetailsScreen
import com.okediran.administrator.presentation.ui.screens.PupilListScreen
import com.okediran.administrator.presentation.viewmodels.PupilViewModel

// Navigation Destinations
sealed class Screen(val route: String) {
    object PupilList : Screen("pupil_list")
    object CreatePupil : Screen("create_pupil")
    object PupilDetails : Screen("pupil_details/{pupilId}") {
        fun createRoute(pupilId: Int) = "pupil_details/$pupilId"
    }
}

@Composable
fun PupilNavGraph(viewModel: PupilViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.PupilList.route) {
        composable(Screen.PupilList.route) {
            PupilListScreen(
                pupils = viewModel.pupilsState,
                onPupilClick = { navController.navigate(Screen.PupilDetails.createRoute(it)) },
                onAddPupilClick = { navController.navigate(Screen.CreatePupil.route) },
                viewModel
            )
        }
        composable(Screen.CreatePupil.route) {
            CreatePupilScreen(
                navController,
                createPupil = { name, country, imageUri ->
                    viewModel.createPupil(name, country, imageUri)
                },
                creationState = viewModel.creationState
            )
        }
        composable(Screen.PupilDetails.route) { backStackEntry ->
            val pupilId = backStackEntry.arguments?.getString("pupilId")?.toIntOrNull()
            pupilId?.let { PupilDetailsScreen(navController, it) }
        }
    }
}
