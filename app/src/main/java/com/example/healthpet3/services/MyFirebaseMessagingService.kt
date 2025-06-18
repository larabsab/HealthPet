package com.example.healthpet3.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.healthpet3.R
import com.example.healthpet3.ui.notifications.StatusNotificationsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "Nova atualização", it.body ?: "")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val tutorEmail = user?.email
        if (tutorEmail != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("tutors").document(tutorEmail)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    android.util.Log.d("FCM", "Token salvo com sucesso!")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("FCM", "Erro ao salvar token", e)
                }
        }
    }

    private fun sendRegistrationToServer(token: String) {
        // Pegue o email do tutor logado
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val tutorEmail = user?.email

        if (tutorEmail != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("tutors").document(tutorEmail)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    android.util.Log.d("FCM", "Token salvo com sucesso!")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("FCM", "Erro ao salvar token", e)
                }
        } else {
            android.util.Log.e("FCM", "Usuário não logado, não foi possível salvar o token")
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, StatusNotificationsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Status Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}