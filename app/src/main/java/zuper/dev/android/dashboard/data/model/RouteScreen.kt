package zuper.dev.android.dashboard.data.model

sealed class RouteScreen(val route: String) {
    object DashboardScreen : RouteScreen(route = "dashboardScreen")
    object JobsScreen : RouteScreen(route = "jobsScreen")
}
