package me.sim05.twofactorauth.widget

import android.content.Context
import androidx.compose.animation.expandIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import me.sim05.twofactorauth.ServicesApplication
import me.sim05.twofactorauth.data.AppDataContainer
import me.sim05.twofactorauth.data.Service
import java.io.File

object AuthWidget : GlanceAppWidget() {
    val KEY_WIDGET_EXPANDED = "expanded"
    val expandedActionKey = ActionParameters.Key<String>(
        KEY_WIDGET_EXPANDED
    )
    val expandedKey = intPreferencesKey(KEY_WIDGET_EXPANDED)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val expanded = currentState(key = expandedKey)
            AuthContent()
        }
    }

    @Composable
    private fun AuthContent() {
        val context = LocalContext.current
        val servicesApplication = remember { ServicesApplication() }
        servicesApplication.container = AppDataContainer(context)
        val servicesRepository = remember { servicesApplication.container.servicesRepository }
        val services by servicesRepository.getAllServicesStream().collectAsState(listOf())
        val expandedId = currentState<Int>()

        LazyColumn(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Services", modifier = GlanceModifier.padding(12.dp))
            }
            items(services) { service ->
                if (service.id == expandedId) {
                    ExpandedServiceContent(service)
                } else {
                    ServiceContent(service)
                }
            }
        }
    }

    @Composable
    private fun ServiceContent(service: Service) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .clickable(
                    onClick = actionRunCallback<ExpandAction>(actionParametersOf(expandedActionKey to service.id.toString()))
                ),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Info, contentDescription = "Service log")
            Text(text = service.name, modifier = GlanceModifier.padding(12.dp))
            Spacer(modifier = GlanceModifier.defaultWeight())
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }

    @Composable
    private fun ExpandedServiceContent(service: Service) {
        Row(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Info, contentDescription = "Service log")
            Column {
                Text(text = service.name, modifier = GlanceModifier.padding(12.dp))
                Text(text = "000 000", modifier = GlanceModifier.padding(12.dp))
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

class ExpandAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val serviceId = parameters[AuthWidget.expandedActionKey] ?: return

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {  preferences ->
            preferences.toMutablePreferences().apply {
                this[AuthWidget.expandedKey] = serviceId.toInt()
            }
        }
        AuthWidget.update(context, glanceId)
    }
}