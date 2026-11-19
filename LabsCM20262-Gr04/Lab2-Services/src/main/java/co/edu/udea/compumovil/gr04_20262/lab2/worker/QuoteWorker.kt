package co.edu.udea.compumovil.gr04_20262.lab2.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.edu.udea.compumovil.gr04_20262.lab2.NavActivity
import co.edu.udea.compumovil.gr04_20262.lab2.R
import co.edu.udea.compumovil.gr04_20262.lab2.data.InboxStore
import co.edu.udea.compumovil.gr04_20262.lab2.data.remote.ApiClient
import co.edu.udea.compumovil.gr04_20262.lab2.widget.JetChatWidget

/**
 * Worker (corrutina) que corre en segundo plano de forma recurrente:
 *  1. Consume el servicio REST (Retrofit) y obtiene una frase.
 *  2. La publica como mensaje nuevo en el chat (funcionalidad "recibir mensajes").
 *  3. Actualiza el widget Glance de mensajes sin leer (funcionalidad "widget").
 *  4. Muestra una notificación.
 */
class QuoteWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        val quote = ApiClient.api.randomQuote()
        val text = "\"${quote.quote}\" — ${quote.author}"
        InboxStore.add(context, InboxStore.BOT_AUTHOR, text)
        JetChatWidget().updateAll(context)
        notify(text)
        Result.success()
    } catch (e: Exception) {
        // Sin red o servicio caído: WorkManager reintenta con backoff
        if (runAttemptCount < 3) Result.retry() else Result.failure()
    }

    private fun notify(text: String) {
        val granted = Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            Intent(context, NavActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_jetchat)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "quotes"
        const val NOTIF_ID = 1001
    }
}
