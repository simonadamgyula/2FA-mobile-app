package me.sim05.twofactorauth.widget

import android.content.Context
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
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import me.sim05.twofactorauth.ServicesApplication
import me.sim05.twofactorauth.data.Service

class AuthWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            AuthContent()
        }
    }

    @Composable
    private fun AuthContent() {
        val servicesRepository = remember { ServicesApplication().container.servicesRepository }
        val services by servicesRepository.getAllServicesStream().collectAsState(listOf())
        var expandedId by remember { mutableStateOf<Int?>(null) }

        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                services.forEach {
                    if (it.id == expandedId) {
                        ExpandedServiceContent(it)
                    } else {
                        ServiceContent(it)
                    }
                }
            }
        }
    }

    @Composable
    private fun ServiceContent(service: Service, onClick: (Int) -> Unit) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .clickable(
                    onClick = actionRunCallback<ExpandAction>()
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
    private val serviceKey = ActionParameters.Key<String>(
        "service"
    )

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val serviceId = parameters[serviceKey] ?: return

        val widget = AuthWidget()
        widget.update(context, glanceId)
    }
}