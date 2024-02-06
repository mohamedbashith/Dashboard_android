package zuper.dev.android.dashboard.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import zuper.dev.android.dashboard.data.model.RouteScreen
import zuper.dev.android.dashboard.screens.DashboardScreen
import zuper.dev.android.dashboard.screens.JobsScreen
import zuper.dev.android.dashboard.viewmodel.JobViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val sharedViewModel = viewModel<JobViewModel>()

    val jobsList by sharedViewModel.initialJobList.collectAsState()
    val invoiceList by sharedViewModel.invoicesList.collectAsState()

    NavHost(
        navController = navController,
        startDestination = RouteScreen.DashboardScreen.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = { ExitTransition.None }) {
        composable(route = RouteScreen.DashboardScreen.route) {
            DashboardScreen(jobsList, invoiceList) {
                sharedViewModel.setJobList(it)
                navController.navigate(route = RouteScreen.JobsScreen.route)
            }
        }
        composable(route = RouteScreen.JobsScreen.route) {
            JobsScreen(sharedViewModel.jobsList.value, sharedViewModel) {
                navController.navigate(RouteScreen.DashboardScreen.route) {
                    popUpTo(RouteScreen.DashboardScreen.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}