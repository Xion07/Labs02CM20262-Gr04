package co.edu.udea.compumovil.gr04_20262.lab2.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkInfo
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Programa las tareas recurrentes en segundo plano. */
object WorkScheduler {
    const val PERIODIC = "quote_periodic"
    const val NOW = "quote_now"
    const val REPLY = "reply"

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedule(context: Context) {
        val wm = WorkManager.getInstance(context)
        // Tarea recurrente: cada 15 min (mínimo permitido por Android)
        wm.enqueueUniquePeriodicWork(
            PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<QuoteWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build(),
        )
        // Primera frase inmediata al abrir la app
        wm.enqueueUniqueWork(
            NOW,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<QuoteWorker>().setConstraints(constraints).build(),
        )
    }

    /** Pide una frase ya mismo (botón del menú lateral). */
    fun syncNow(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            NOW,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<QuoteWorker>().setConstraints(constraints).build(),
        )
    }

    /** Encola la respuesta al mensaje [text]; si hay varios seguidos se responden en orden. */
    fun reply(context: Context, text: String) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            REPLY,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            OneTimeWorkRequestBuilder<ReplyWorker>()
                .setConstraints(constraints)
                .setInputData(workDataOf(ReplyWorker.KEY_TEXT to text))
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build(),
        )
    }

    /** Estado de la tarea periódica, para mostrarlo en la interfaz. */
    fun periodicInfo(context: Context): Flow<WorkInfo?> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(PERIODIC).map { it.firstOrNull() }

    /** Estado de la última frase pedida "ahora". */
    fun nowInfo(context: Context): Flow<WorkInfo?> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(NOW).map { it.firstOrNull() }
}
