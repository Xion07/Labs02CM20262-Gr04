package co.edu.udea.compumovil.gr04_20262.lab2.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.edu.udea.compumovil.gr04_20262.lab2.data.InboxStore
import co.edu.udea.compumovil.gr04_20262.lab2.data.remote.ApiClient
import co.edu.udea.compumovil.gr04_20262.lab2.widget.JetChatWidget
import kotlinx.coroutines.delay

/**
 * Worker de una sola vez que responde el mensaje que el usuario acaba de enviar.
 *
 * Funcionalidad de Jetchat "enviar mensajes" hecha con WorkManager: la respuesta se
 * pide al servicio REST (comentario aleatorio de dummyjson con su autor) en segundo plano,
 * así que llega aunque el usuario cierre la app justo después de enviar.
 */
class ReplyWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        val sent = inputData.getString(KEY_TEXT).orEmpty()
        // Simula que el otro miembro "está escribiendo"
        delay(TYPING_DELAY_MS)
        val comment = ApiClient.api.comment(pickId(sent))
        val author = comment.user.fullName.ifBlank { comment.user.username.ifBlank { InboxStore.BOT_AUTHOR } }
        InboxStore.add(context, author, comment.body)
        JetChatWidget().updateAll(context)
        Result.success(workDataOf(KEY_AUTHOR to author))
    } catch (e: Exception) {
        if (runAttemptCount < 3) Result.retry() else Result.failure()
    }

    companion object {
        const val KEY_TEXT = "text"
        const val KEY_AUTHOR = "author"
        private const val TYPING_DELAY_MS = 1_500L
        private const val COMMENTS = 340

        /** El mismo mensaje siempre trae la misma respuesta; mensajes distintos, respuestas distintas. */
        fun pickId(text: String): Int = Math.floorMod(text.hashCode(), COMMENTS) + 1
    }
}
