package com.example.lab5_20210795;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Recibir datos del hábito desde el intent
        String nombreHabit = intent.getStringExtra("nombre");
        String categoria = intent.getStringExtra("categoria");
        int habitId = intent.getIntExtra("habit_id", 1);

        // Definir canal según categoría
        String channelId = "";
        String mensaje = "¡Hoy es un gran día para tu hábito!";
        int icono = R.drawable.ic_launcher_foreground;

        switch (categoria) {
            case "Ejercicio":
                channelId = "canal_Ejercicio";
                mensaje = "¡Hora de moverte! 🏃‍♂️ ¡Sal a correr!";
                icono = R.drawable.ic_ejercicio;
                break;
            case "Alimentación":
                channelId = "canal_Alimentacion";
                mensaje = "¡Momento de comer bien! 🥗 Cuida tu cuerpo.";
                icono = R.drawable.ic_alimentacion;
                break;
            case "Sueño":
                channelId = "canal_Suenho";
                mensaje = "🛌 ¡Hora de dormir como un campeón!";
                icono = R.drawable.ic_suenho;
                break;
            case "Lectura":
                channelId = "canal_Lectura";
                mensaje = "📖 ¡Abre un libro y explora un nuevo mundo!";
                icono = R.drawable.ic_lectura;
                break;
        }
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, habitId, i, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icono)  // ícono representativo distinto
                .setContentTitle(nombreHabit)
                .setContentText(mensaje)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(habitId, builder.build());
        }
    }
}
