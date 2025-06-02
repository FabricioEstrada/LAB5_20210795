package com.example.lab5_20210795;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
    public static void programarNotificacion(Context context, Habit habit) {
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("nombre", habit.getNombre());
        intent.putExtra("categoria", habit.getCategoria());
        intent.putExtra("mensaje", "Realizar " + habit.getNombre());

        // Agrega el ID único como extra
        intent.putExtra("habit_id", habit.getIdUnico());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                habit.getIdUnico(), // Usa un ID único por hábito para distinguir las notificaciones
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long tiempoInicial = habit.getFechaInicioMillis();  // desde cuándo inicia
        long intervalo = habit.getFrecuenciaHoras() * 60 * 1000;  // en milisegundos

        // Repetir periódicamente
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                tiempoInicial,
                intervalo,
                pendingIntent
        );
    }
}
