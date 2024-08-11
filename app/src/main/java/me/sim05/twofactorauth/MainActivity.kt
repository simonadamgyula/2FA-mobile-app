package me.sim05.twofactorauth

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
            TwoFactorAuthApp(context = this)
        }
    }
}

enum class Pages {
    Home,
    Settings,
    Add,
    Edit,
}

enum class SettingsPages {
    About,
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun TwoFactorAuthApp(navController: NavHostController = rememberNavController(), context: Context) {
    val sharedPreferences = remember { PreferenceManager(context) }
    var theme by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getData(
                "theme",
                "followsystem"
            )
        )
    }

    val isDarkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    TwoFactorAuthTheme(
        darkTheme = isDarkTheme
    ) {
        NavHost(
            navController = navController,
            startDestination = Pages.Home.name,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(
                route = Pages.Home.name,

                ) {
                HomePage(navController = navController)
            }
            composable(route = Pages.Settings.name,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ) {
                SettingPage(navController = navController, onThemeChange = { theme = it })
            }
            composable(route = Pages.Add.name,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Up
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.Down
                    )
                }
            ) {
                AddServicePage(navController = navController)
            }
            composable(
                route = Pages.Edit.name,
                enterTransition = {
                    expandIn(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    )
                },
                exitTransition = {
                    shrinkOut(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    )
                }
            ) {
                EditServicePage(navController = navController)
            }

            composable(
                route = SettingsPages.About.name,
                enterTransition = {
                    expandIn(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    )
                },
                exitTransition = {
                    shrinkOut(
                        animationSpec = tween(
                            300, easing = LinearEasing
                        )
                    )
                }
            ) {
                AboutPage(navController = navController)
            }
        }
    }
}



