package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SectionType
import com.example.ui.InspectionViewModel
import com.example.ui.components.CarTheme
import com.example.ui.screens.*

sealed interface AppScreen {
    object Home : AppScreen
    object VehicleInfo : AppScreen
    object InspectionHub : AppScreen
    data class InspectionSection(val section: SectionType) : AppScreen
    object Report : AppScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: InspectionViewModel = viewModel()
            val isDark = viewModel.isDarkMode
            val currentLang = viewModel.currentLanguage

            // Force dynamic layout direction for native RTL support in Arabic & Tunisian
            val layoutDirection = if (currentLang == "ar" || currentLang == "tn") LayoutDirection.Rtl else LayoutDirection.Ltr

            CarTheme(isDark = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppNavigation(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(viewModel: InspectionViewModel) {
    var screenStack by remember { mutableStateOf<List<AppScreen>>(listOf(AppScreen.Home)) }
    val currentScreen = screenStack.last()

    fun navigateTo(screen: AppScreen) {
        screenStack = screenStack + screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun navigateHome() {
        screenStack = listOf(AppScreen.Home)
    }

    // Capture physical android back button gracefully
    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState is AppScreen.Home) {
                // Popping back to home -> slide out or fade out
                slideInHorizontally { width -> -width } + fadeIn() with
                        slideOutHorizontally { width -> width } + fadeOut()
            } else {
                // Advancing -> slide inside or fade inside
                slideInHorizontally { width -> width } + fadeIn() with
                        slideOutHorizontally { width -> -width } + fadeOut()
            }
        }
    ) { screen ->
        when (screen) {
            is AppScreen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onStartNewInspection = { navigateTo(AppScreen.VehicleInfo) },
                    onViewInspection = { inspect ->
                        navigateTo(AppScreen.Report)
                    }
                )
            }
            is AppScreen.VehicleInfo -> {
                VehicleInfoScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    onNavigateToHub = { navigateTo(AppScreen.InspectionHub) }
                )
            }
            is AppScreen.InspectionHub -> {
                InspectionHubScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    onNavigateToSection = { sectionType ->
                        navigateTo(AppScreen.InspectionSection(sectionType))
                    },
                    onNavigateToReport = { navigateTo(AppScreen.Report) }
                )
            }
            is AppScreen.InspectionSection -> {
                InspectionSectionScreen(
                    viewModel = viewModel,
                    sectionType = screen.section,
                    onBack = { navigateBack() }
                )
            }
            is AppScreen.Report -> {
                ReportScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    onDone = { navigateHome() }
                )
            }
        }
    }
}
