package me.sim05.twofactorauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwoFactorAuthTheme {
                TwoFactorAuthApp()
            }
        }
    }
}

enum class Pages {
    Home,
    Settings,
    Add,
    Edit,
}

@Composable
fun TwoFactorAuthApp(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Pages.Home.name,
    ) {
        composable(route = Pages.Home.name) {
            HomePage(navController = navController)
        }
        composable(route = Pages.Settings.name) {
            SettingPage(navController = navController)
        }
        composable(route = Pages.Add.name) {
            AddServicePage(navController = navController)
        }
        composable(route = Pages.Edit.name, arguments = listOf(navArgument("serviceDetails") { defaultValue = null })) {
            val serviceDetails = navController.previousBackStackEntry?.savedStateHandle?.get<ServiceDetails>("SERVICE")
            EditServicePage(navController = navController, serviceDetails = serviceDetails)
        }
    }
}


