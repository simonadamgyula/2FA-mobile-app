package me.sim05.twofactorauth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import me.sim05.twofactorauth.ui.components.BottomNavigationBar

sealed class Setting {
    data class ClickSettings(val title: String, val icon: ImageVector, val onClick: () -> Unit) :
        Setting()

    data class ToggleSetting(
        val title: String,
        val icon: ImageVector,
        val onClick: () -> Unit,
        val isChecked: Boolean
    ) : Setting()
}

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    var openedDialog by remember { mutableStateOf<Int?>(null) }
    val sharedPreferences = remember { PreferenceManager(context) }

    val settings: List<Setting> = listOf(
        Setting.ClickSettings(
            "List style",
            Icons.Default.Info,
            onClick = {
                openedDialog = 0
            }
        ),
        Setting.ClickSettings(
            "Theme",
            Icons.AutoMirrored.Filled.List,
            onClick = {
                openedDialog = 1
            }
        ),
        Setting.ToggleSetting(
            "Show next token",
            Icons.Default.Info,
            onClick = {},
            isChecked = false
        ),
        Setting.ClickSettings(
            "Hidden tokens",
            Icons.Default.Info,
            onClick = {}
        ),
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        SettingsList(
            modifier = Modifier.padding(innerPadding),
            settings = settings,
        )
        when (openedDialog) {
            0 -> {
                ListTypeDialog(
                    onDismissRequest = { openedDialog = null },
                    sharedPreferences = sharedPreferences
                )
            }

            1 -> {
                ThemeDialog(
                    onDismissRequest = { openedDialog = null },
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

@Composable
fun SettingsList(modifier: Modifier = Modifier, settings: List<Setting>) {
    Column(modifier = modifier) {
        settings.forEach { setting ->
            SettingElement(
                modifier = Modifier.fillMaxWidth(),
                title = setting.title,
                icon = setting.icon,
                onClick = setting.onClick
            )
        }
    }
}

@Composable
fun SettingElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.padding(16.dp))
        Text(title)
    }
}

@Composable
fun ListTypeDialog(onDismissRequest: () -> Unit, sharedPreferences: PreferenceManager) {
    val listOptions = listOf("Default", "Compact")
    val (selectedOption, onOptionSelect) = remember {
        mutableStateOf(
            sharedPreferences.getData(
                "listStyle",
                "Follow system"
            )
        )
    }
    Log.v("selectedOption", selectedOption)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Select list style",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                )
                listOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                onOptionSelect(option)
                                sharedPreferences.saveData("listStyle", option.lowercase())
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDialog(onDismissRequest: () -> Unit, sharedPreferences: PreferenceManager) {
    val listOptions = listOf("Follow system", "Light", "Dark")
    val (selectedOption, onOptionSelect) = remember {
        mutableStateOf(
            sharedPreferences.getData(
                "theme",
                "Follow system"
            )
        )
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Select theme",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                )
                listOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                onOptionSelect(option)
                                sharedPreferences.saveData("theme", option.lowercase())
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}