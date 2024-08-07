package me.sim05.twofactorauth

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import me.sim05.twofactorauth.ui.AppViewModelProvider
import me.sim05.twofactorauth.ui.BottomNavigationBar
import me.sim05.twofactorauth.ui.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme

@Composable
fun AddServicePage(
    navController: NavController,
    viewModel: ServiceEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }) { contentPadding ->
        ServiceForm(modifier = Modifier.padding(contentPadding));
    }
}

@Composable
fun ServiceForm(modifier: Modifier = Modifier) {
    var serviceName: String by remember { mutableStateOf("") }
    var username: String by remember { mutableStateOf("") }
    var token: String by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serviceName,
            onValueChange = { serviceName = it },
            label = "Service name: ",
            leadingIcon = Icons.Filled.Star,
        )
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            label = "Username: ",
            leadingIcon = Icons.Filled.Person,
        )
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = token,
            onValueChange = { token = it },
            label = "Token: ",
            leadingIcon = Icons.Filled.Lock,
        )
        Spacer(modifier = Modifier.fillMaxHeight())
        Button(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.create))
        }
    }
}

@Composable
fun CardTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector
) {
    Card(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
    }

}

@Preview
@Composable
fun AddServicePagePreview() {
    TwoFactorAuthTheme {
        Scaffold(bottomBar = {
            BottomNavigationBar(rememberNavController())
        }) { contentPadding ->
            ServiceForm(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth()
                    .padding(10.dp)
            );
        }
    }
}