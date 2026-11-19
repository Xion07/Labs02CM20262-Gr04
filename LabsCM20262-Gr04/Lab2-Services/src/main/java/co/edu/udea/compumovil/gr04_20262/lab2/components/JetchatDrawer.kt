/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.udea.compumovil.gr04_20262.lab2.components

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.work.WorkInfo
import co.edu.udea.compumovil.gr04_20262.lab2.worker.WorkScheduler
import java.util.Date
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import co.edu.udea.compumovil.gr04_20262.lab2.R
import co.edu.udea.compumovil.gr04_20262.lab2.data.colleagueProfile
import co.edu.udea.compumovil.gr04_20262.lab2.data.meProfile
import co.edu.udea.compumovil.gr04_20262.lab2.navigation.Conversation
import co.edu.udea.compumovil.gr04_20262.lab2.navigation.Profile
import co.edu.udea.compumovil.gr04_20262.lab2.theme.JetchatTheme
import co.edu.udea.compumovil.gr04_20262.lab2.widget.WidgetReceiver

/**
 * Content of the navigation drawer.
 *
 * @param selectedKey the key of the screen currently displayed, used to highlight its item
 * @param onNavigate called with the key of the item the user tapped
 */
@Composable
fun JetchatDrawerContent(selectedKey: NavKey, onNavigate: (NavKey) -> Unit) {
    // Use windowInsetsTopHeight() to add a spacer which pushes the drawer content
    // below the status bar (y-axis)
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeader()
        DividerItem()
        DrawerItemHeader(stringResource(R.string.drawer_chats))
        ChatItem(Conversation("composers"), selectedKey, onNavigate)
        ChatItem(Conversation("droidcon-nyc"), selectedKey, onNavigate)
        DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader(stringResource(R.string.drawer_recent_profiles))
        ProfileItem(stringResource(R.string.drawer_me), meProfile.photo, Profile(meProfile.userId), selectedKey, onNavigate)
        ProfileItem("Taylor Brooks", colleagueProfile.photo, Profile(colleagueProfile.userId), selectedKey, onNavigate)
        DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader(stringResource(R.string.drawer_background))
        BackgroundServiceItem()
        if (widgetAddingIsSupported(LocalContext.current)) {
            DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
            DrawerItemHeader(stringResource(R.string.drawer_settings))
            WidgetDiscoverability()
        }
    }
}

@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = CenterVertically) {
        JetchatIcon(
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Icon(
            painter = painterResource(id = R.drawable.jetchat_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = CenterStart,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChatItem(key: Conversation, selectedKey: NavKey, onNavigate: (NavKey) -> Unit) {
    val selected = key == selectedKey
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = { onNavigate(key) }),
        verticalAlignment = CenterVertically,
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat),
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null,
        )
        Text(
            key.channel,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun ProfileItem(text: String, @DrawableRes profilePic: Int?, key: Profile, selectedKey: NavKey, onNavigate: (NavKey) -> Unit) {
    val background = if (key == selectedKey) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = { onNavigate(key) }),
        verticalAlignment = CenterVertically,
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(24.dp)
        if (profilePic != null) {
            Image(
                painter = painterResource(id = profilePic),
                modifier = paddingSizeModifier.then(Modifier.clip(CircleShape)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

@Composable
@Preview
fun DrawerPreview() {
    JetchatTheme {
        Surface {
            Column {
                JetchatDrawerContent(selectedKey = Conversation("composers"), onNavigate = {})
            }
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    JetchatTheme(isDarkTheme = true) {
        Surface {
            Column {
                JetchatDrawerContent(selectedKey = Conversation("composers"), onNavigate = {})
            }
        }
    }
}

/**
 * Lab 2: muestra en vivo el estado de los workers (tarea periódica y última frase pedida)
 * y permite pedir una frase al servicio REST en este momento.
 */
@Composable
private fun BackgroundServiceItem() {
    val context = LocalContext.current.applicationContext
    val periodic by remember { WorkScheduler.periodicInfo(context) }.collectAsState(initial = null)
    val now by remember { WorkScheduler.nowInfo(context) }.collectAsState(initial = null)
    val running = now?.state == WorkInfo.State.RUNNING || now?.state == WorkInfo.State.ENQUEUED
    Column(modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)) {
        Text(
            text = stringResource(R.string.worker_periodic, stateLabel(periodic?.state)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val next = periodic?.nextScheduleTimeMillis?.takeIf { it in 1 until Long.MAX_VALUE }
        if (next != null && periodic?.state == WorkInfo.State.ENQUEUED) {
            Text(
                text = stringResource(R.string.worker_next, DateFormat.getTimeFormat(context).format(Date(next))),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(R.string.worker_last, stateLabel(now?.state)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        FilledTonalButton(onClick = { WorkScheduler.syncNow(context) }, enabled = !running) {
            Text(stringResource(if (running) R.string.worker_running else R.string.worker_sync_now))
        }
    }
}

@Composable
private fun stateLabel(state: WorkInfo.State?): String = stringResource(
    when (state) {
        WorkInfo.State.ENQUEUED -> R.string.work_enqueued
        WorkInfo.State.RUNNING -> R.string.work_running
        WorkInfo.State.SUCCEEDED -> R.string.work_succeeded
        WorkInfo.State.FAILED -> R.string.work_failed
        WorkInfo.State.BLOCKED -> R.string.work_blocked
        WorkInfo.State.CANCELLED -> R.string.work_cancelled
        null -> R.string.work_none
    },
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WidgetDiscoverability() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                addWidgetToHomeScreen(context)
            }),
        verticalAlignment = CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.add_widget_to_home_page),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun addWidgetToHomeScreen(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val myProvider = ComponentName(context, WidgetReceiver::class.java)
    if (widgetAddingIsSupported(context)) {
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
private fun widgetAddingIsSupported(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported
}
