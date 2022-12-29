package com.example.apptracker.util.navigation

sealed class Route(val path: String) {
    object Apps: Route("/apps")
    object More: Route("/more")
    object AddApps: Route("/more/appSelector")
    object Settings: Route("/more/settings")
    object Categories: Route("/more/categories")
    object Appearance: Route("/more/settings/appearance")
    object PackageUsagePermission: Route("/packageUsagePermission")
}