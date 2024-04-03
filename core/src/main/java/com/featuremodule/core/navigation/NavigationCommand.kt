package com.featuremodule.core.navigation

sealed interface NavigationCommand {
    data class Forward(val route: String) : NavigationCommand
    data object PopBack : NavigationCommand
    data class PopBackWithArguments<T>(val args: Map<String, T>) : NavigationCommand
    data class OpenNavBarDestination(val route: String) : NavigationCommand
}