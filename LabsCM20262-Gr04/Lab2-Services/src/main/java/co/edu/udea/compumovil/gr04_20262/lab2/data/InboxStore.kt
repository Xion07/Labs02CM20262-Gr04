package co.edu.udea.compumovil.gr04_20262.lab2.data

import android.content.Context
import co.edu.udea.compumovil.gr04_20262.lab2.conversation.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Bandeja de mensajes recibidos por el worker (frases del servicio REST).
 * Se persiste en SharedPreferences para que el worker (segundo plano) y la UI compartan datos,
 * y se expone como [StateFlow] para que la conversación se actualice en vivo.
 */
object InboxStore {
    private const val PREFS = "inbox"
    private const val KEY = "items"
    private const val MAX_ITEMS = 50
    const val BOT_AUTHOR = "Quote Bot"

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private var loaded = false

    @Synchronized
    fun init(context: Context) {
        if (loaded) return
        loaded = true
        val raw = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return
        val list = runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                Message(
                    author = o.getString("author"),
                    content = o.getString("content"),
                    timestamp = o.getString("timestamp"),
                )
            }
        }.getOrDefault(emptyList())
        _messages.value = list
    }

    /** Agrega un mensaje nuevo (el más reciente primero) y lo persiste. */
    @Synchronized
    fun add(context: Context, author: String, content: String) {
        init(context)
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val msg = Message(author = author, content = content, timestamp = time)
        val updated = (listOf(msg) + _messages.value).take(MAX_ITEMS)
        _messages.value = updated
        val arr = JSONArray()
        updated.forEach {
            arr.put(
                JSONObject()
                    .put("author", it.author)
                    .put("content", it.content)
                    .put("timestamp", it.timestamp),
            )
        }
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }
}
