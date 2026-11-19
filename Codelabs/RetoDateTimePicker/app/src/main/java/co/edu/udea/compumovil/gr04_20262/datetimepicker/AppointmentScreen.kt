package co.edu.udea.compumovil.gr04_20262.datetimepicker

import android.text.format.DateFormat
import android.widget.Toast
import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr04_20262.datetimepicker.ui.theme.DateTimePickerTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Formatea millis (UTC, como entrega el DatePicker) con formato según idioma, sin desfase de zona. */
private fun formatDate(millis: Long): String =
    java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

/** True si la fecha (millis UTC del picker) + hora local ya pasó respecto a ahora. */
private fun isPastDateTime(dateMillis: Long, hour: Int, minute: Int): Boolean {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = dateMillis }
    val chosen = Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), hour, minute, 0)
    }
    return chosen.timeInMillis < System.currentTimeMillis()
}

/** Formatea hora/minuto según 12 h o 24 h del teléfono. */
private fun formatTime(hour: Int, minute: Int, is24h: Boolean): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    val pattern = if (is24h) "HH:mm" else "hh:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(cal.time)
}

/** Solo permite hoy y fechas futuras (el picker trabaja en UTC). */
@OptIn(ExperimentalMaterial3Api::class)
private object FutureDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Medianoche UTC del día local actual
        val local = Calendar.getInstance()
        val todayUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
        return utcTimeMillis >= todayUtc
    }

    override fun isSelectableYear(year: Int): Boolean =
        year >= Calendar.getInstance().get(Calendar.YEAR)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen() {
    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)

    // Estado que sobrevive a la rotación (null = sin selección)
    var dateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var hour by rememberSaveable { mutableStateOf<Int?>(null) }
    var minute by rememberSaveable { mutableStateOf<Int?>(null) }
    var showDate by rememberSaveable { mutableStateOf(false) }
    var showTime by rememberSaveable { mutableStateOf(false) }

    val dateText = dateMillis?.let { formatDate(it) }.orEmpty()
    val h = hour
    val m = minute
    val timeText = if (h != null && m != null) formatTime(h, m, is24h) else ""
    val dm = dateMillis
    // Error si la cita es hoy y la hora ya pasó
    val timePast = dm != null && h != null && m != null && isPastDateTime(dm, h, m)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReadOnlyField(
                value = dateText,
                label = stringResource(R.string.date_label),
                placeholder = stringResource(R.string.select_date),
                trailing = {
                    IconButton(onClick = { showDate = true }) {
                        Icon(Icons.Default.DateRange, stringResource(R.string.open_calendar))
                    }
                },
                onClick = { showDate = true }
            )
            ReadOnlyField(
                value = timeText,
                label = stringResource(R.string.time_label),
                placeholder = stringResource(R.string.select_time),
                trailing = {
                    IconButton(onClick = { showTime = true }) {
                        Icon(painterResource(R.drawable.ic_schedule), stringResource(R.string.open_clock))
                    }
                },
                errorText = if (timePast) stringResource(R.string.time_past_error) else null,
                onClick = { showTime = true }
            )

            // Tarjeta resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        stringResource(R.string.summary_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (dateText.isEmpty() && timeText.isEmpty()) {
                        Text(stringResource(R.string.summary_empty))
                    } else {
                        if (dateText.isNotEmpty()) Text(stringResource(R.string.summary_date, dateText))
                        if (timeText.isNotEmpty()) Text(stringResource(R.string.summary_time, timeText))
                    }
                }
            }

            Button(
                onClick = {
                    if (!timePast) Toast.makeText(
                        context,
                        context.getString(R.string.confirmed, dateText, timeText),
                        Toast.LENGTH_LONG
                    ).show()
                },
                enabled = dateText.isNotEmpty() && timeText.isNotEmpty() && !timePast,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.confirm)) }
        }
    }

    if (showDate) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateMillis,
            selectableDates = FutureDates
        )
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateMillis = pickerState.selectedDateMillis
                        showDate = false
                    },
                    enabled = pickerState.selectedDateMillis != null
                ) { Text(stringResource(R.string.accept)) }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) { DatePicker(state = pickerState) }
    }

    if (showTime) {
        val now = Calendar.getInstance()
        val timeState = rememberTimePickerState(
            initialHour = h ?: now.get(Calendar.HOUR_OF_DAY),
            initialMinute = m ?: now.get(Calendar.MINUTE),
            is24Hour = is24h
        )
        TimePickerDialog(
            state = timeState,
            onDismiss = { showTime = false },
            onConfirm = {
                hour = timeState.hour
                minute = timeState.minute
                showTime = false
            }
        )
    }
}

/** Campo de solo lectura que abre su diálogo al tocarlo. */
@Composable
private fun ReadOnlyField(
    value: String,
    label: String,
    placeholder: String,
    trailing: @Composable () -> Unit,
    errorText: String? = null,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    LaunchedEffect(pressed) { if (pressed) onClick() }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        trailingIcon = trailing,
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        interactionSource = source,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun AppointmentScreenPreview() {
    DateTimePickerTheme { AppointmentScreen() }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppointmentScreenDarkPreview() {
    DateTimePickerTheme { AppointmentScreen() }
}
