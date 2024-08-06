package me.sim05.twofactorauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwoFactorAuthTheme {
                TwoFactorAuthApp();
            }
        }
    }
}

enum class Pages {
    Main,
    Settings
}

@Composable
fun TwoFactorAuthApp(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Pages.Main.name,
    ) {
        composable(route = Pages.Main.name) {
            MainPage()
        }
        composable(route = Pages.Settings.name) {
            val context = LocalContext.current;
            SettingPage()
        }
    }
}


