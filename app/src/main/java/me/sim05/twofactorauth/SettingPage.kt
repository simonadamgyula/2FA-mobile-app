package me.sim05.twofactorauth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Switch
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
    data class ClickSettings(
        val title: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) :
        Setting()

    data class ToggleSettings(
        val title: String,
        val icon: ImageVector,
        val onClick: () -> Unit,
        val checked: Boolean,
        val key: String
    ) : Setting()
}

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    onThemeChange: (String) -> Unit
) {
    val context = LocalContext.current
    var openedDialog by remember { mutableStateOf<Int?>(null) }
    val sharedPreferences = remember { PreferenceManager(context) }

    var showNextToken by remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                "showNextToken",
                false
            )
        )
    }
    var hiddenTokens by remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                "hiddenTokens",
                false
            )
        )
    }

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
        Setting.ToggleSettings(
            "Show next token",
            Icons.Default.Info,
            onClick = {
                showNextToken = !showNextToken
                sharedPreferences.toggleBoolean("showNextToken")
            },
            checked = showNextToken,
            key = "showNextToken"
        ),
        Setting.ToggleSettings(
            "Hidden tokens",
            Icons.Default.Info,
            onClick = {
                hiddenTokens = !hiddenTokens
                sharedPreferences.toggleBoolean("hiddenTokens")
            },
            checked = hiddenTokens,
            key = "hiddenTokens"
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
                    sharedPreferences = sharedPreferences,
                    onThemeChange = onThemeChange
                )
            }
        }
    }
}

@Composable
fun SettingsList(
    modifier: Modifier = Modifier,
    settings: List<Setting>,
) {
    Column(modifier = modifier) {
        settings.forEach { setting ->
            when (setting) {
                is Setting.ClickSettings -> {
                    SettingElement(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, start = 8.dp),
                        title = setting.title,
                        icon = setting.icon,
                        onClick = setting.onClick
                    )
                }

                is Setting.ToggleSettings -> {
                    ToggleSettingElement(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, start = 8.dp),
                        title = setting.title,
                        icon = setting.icon,
                        onClick = setting.onClick,
                        isChecked = setting.checked
                    )
                }
            }
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
fun ToggleSettingElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isChecked: Boolean
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.padding(16.dp))
        Text(title)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = { onClick() }
        )
    }
}

@Composable
fun ListTypeDialog(onDismissRequest: () -> Unit, sharedPreferences: PreferenceManager) {
    val listOptions = listOf("Default", "Compact")
    val (selectedOption, onOptionSelect) = remember {
        mutableStateOf(
            sharedPreferences.getData(
                "listStyle",
                "followsystem"
            )
        )
    }
    Log.v("selectedOption", selectedOption)

    fun onSelect(option: String) {
        val parsedOption = option.lowercase().replace(" ", "")

        onOptionSelect(parsedOption)
        sharedPreferences.saveData("listStyle", parsedOption)
    }

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
                            selected = (option.lowercase().replace(" ", "") == selectedOption),
                            onClick = {
                                onSelect(option)
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onSelect(option)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDialog(
    onDismissRequest: () -> Unit,
    sharedPreferences: PreferenceManager,
    onThemeChange: (String) -> Unit
) {
    val listOptions = listOf("Follow system", "Light", "Dark")
    val (selectedOption, onOptionSelect) = remember {
        mutableStateOf(
            sharedPreferences.getData(
                "theme",
                "followsystem"
            )
        )
    }
    Log.v("selectedOption", selectedOption)

    fun onSelect(option: String) {
        val parsedOption = option.lowercase().replace(" ", "")

        onOptionSelect(parsedOption)
        sharedPreferences.saveData("theme", parsedOption)
        onThemeChange(parsedOption)
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
                            selected = (option.lowercase().replace(" ", "") == selectedOption),
                            onClick = {
                                onSelect(option)
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onSelect(option)
                                }
                        )
                    }
                }
            }
        }
    }
}