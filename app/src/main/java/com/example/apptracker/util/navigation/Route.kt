package com.example.apptracker.util.navigation

sealed class Route(val path: String, val argumentlessPath: String = path, val transitionType: NavigationTransitionType = NavigationTransitionType.Slide) {
    object Apps: Route("/apps", transitionType = NavigationTransitionType.Fade)
    object AddApp: Route("/apps/add/{packageName}", "/apps/add/")
    object EditApp: Route("/apps/edit/{packageName}", "/apps/edit/")
    object Stats: Route("/stats", transitionType = NavigationTransitionType.Fade)
    object UsageStats: Route("/stats/usage/{selectedBar}","/stats/usage/")
    object AllTimeUsageStats: Route("/stats/usage/all")
    object More: Route("/more", transitionType = NavigationTransitionType.Fade)
    object AddApps: Route("/more/appSelector")
    object Settings: Route("/more/settings")
    object Categories: Route("/more/categories")
    object Appearance: Route("/more/settings/appearance")
    object General: Route("/more/settings/general")
    object PackageUsagePermission: Route("/packageUsagePermission")
}