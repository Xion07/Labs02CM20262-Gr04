package co.edu.udea.compumovil.gr04_20262.teclado

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr04_20262.teclado.ui.theme.TecladoTheme
import kotlinx.coroutines.launch

/** Un campo con su explicación de KeyboardOptions debajo. */
@Composable
private fun CampoTeclado(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    explicacion: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    error: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
) {
    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            trailingIcon = trailing,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            explicacion,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen() {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var monto by rememberSaveable { mutableStateOf("") }
    var web by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var claveVisible by rememberSaveable { mutableStateOf(false) }
    var comentario by rememberSaveable { mutableStateOf("") }
    var busqueda by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var intentoEnvio by rememberSaveable { mutableStateOf(false) }
    var resumen by rememberSaveable { mutableStateOf<String?>(null) }

    val focus = LocalFocusManager.current
    val teclado = LocalSoftwareKeyboardController.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val obligatorio = stringResource(R.string.err_required)
    val correoInvalido = stringResource(R.string.err_email)
    val claveCorta = stringResource(R.string.err_password)
    val lNombre = stringResource(R.string.f_name)
    val lCorreo = stringResource(R.string.f_email)
    val lTelefono = stringResource(R.string.f_phone)
    val lEdad = stringResource(R.string.f_age)
    val lMonto = stringResource(R.string.f_amount)
    val lWeb = stringResource(R.string.f_web)
    val lCiudad = stringResource(R.string.f_city)
    val msgBusqueda =stringResource(R.string.search_snackbar, busqueda)

    // Validación simple; los errores se muestran tras el primer intento de envío
    val errNombre = if (nombre.isBlank()) obligatorio else null
    val errCorreo = when {
        correo.isBlank() -> obligatorio
        !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> correoInvalido
        else -> null
    }
    val errTelefono = if (telefono.isBlank()) obligatorio else null
    val errClave = if (clave.length < 6) claveCorta else null
    val hayErrores = listOf(errNombre, errCorreo, errTelefono, errClave).any { it != null }

    // Next mueve el foco al siguiente campo
    val next = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title)) }) },
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            CampoTeclado(
                nombre, { nombre = it }, stringResource(R.string.f_name), stringResource(R.string.h_name),
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next,
                ),
                next, error = if (intentoEnvio) errNombre else null,
            )
            CampoTeclado(
                correo, { correo = it.trim() }, stringResource(R.string.f_email), stringResource(R.string.h_email),
                KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                next, error = if (intentoEnvio) errCorreo else null,
            )
            CampoTeclado(
                telefono, { v -> telefono = v.filterIndexed { i, c -> c.isDigit() || (i == 0 && c == '+') } }, stringResource(R.string.f_phone),
                stringResource(R.string.h_phone),
                KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                next, error = if (intentoEnvio) errTelefono else null,
            )
            CampoTeclado(
                edad, { v -> edad = v.filter { it.isDigit() }.take(3) }, stringResource(R.string.f_age),
                stringResource(R.string.h_age),
                KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                next,
            )
            CampoTeclado(
                monto,
                { v ->
                    // Dígitos y un único separador decimal (',' se normaliza a '.')
                    val limpio = v.replace(',', '.').filter { it.isDigit() || it == '.' }
                    if (limpio.count { it == '.' } <= 1) monto = limpio
                },
                stringResource(R.string.f_amount), stringResource(R.string.h_amount),
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                next,
            )
            CampoTeclado(
                web, { web = it.trim() }, stringResource(R.string.f_web), stringResource(R.string.h_web),
                KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
                next,
            )
            CampoTeclado(
                clave, { clave = it }, stringResource(R.string.f_password), stringResource(R.string.h_password),
                KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                next, error = if (intentoEnvio) errClave else null,
                visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailing = {
                    // Botón de ojo (texto, sin depender de iconos extendidos)
                    TextButton(onClick = { claveVisible = !claveVisible }) {
                        Text(stringResource(if (claveVisible) R.string.hide else R.string.show))
                    }
                },
            )
            CampoTeclado(
                comentario, { comentario = it }, stringResource(R.string.f_comment), stringResource(R.string.h_comment),
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default,
                ),
                KeyboardActions.Default, singleLine = false, minLines = 3,
            )
            CampoTeclado(
                busqueda, { busqueda = it }, stringResource(R.string.f_search), stringResource(R.string.h_search),
                KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                KeyboardActions(onSearch = {
                    teclado?.hide()
                    scope.launch { snackbar.showSnackbar(msgBusqueda) }
                }),
            )
            CampoTeclado(
                ciudad, { ciudad = it }, stringResource(R.string.f_city), stringResource(R.string.h_city),
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                KeyboardActions(onDone = {
                    teclado?.hide()
                    focus.clearFocus()
                }),
            )
            Button(
                onClick = {
                    intentoEnvio = true
                    focus.clearFocus()
                    if (!hayErrores) {
                        resumen = listOf(
                            lNombre to nombre, lCorreo to correo, lTelefono to telefono, lEdad to edad,
                            lMonto to monto, lWeb to web, lCiudad to ciudad,
                        ).filter { it.second.isNotBlank() }
                            .joinToString("\n") { "${it.first}: ${it.second}" }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.submit)) }
        }
    }

    resumen?.let { texto ->
        AlertDialog(
            onDismissRequest = { resumen = null },
            title = { Text(stringResource(R.string.summary_title)) },
            text = { Text(texto) },
            confirmButton = { TextButton(onClick = { resumen = null }) { Text(stringResource(R.string.ok)) } },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistroPreview() {
    TecladoTheme { RegistroScreen() }
}
