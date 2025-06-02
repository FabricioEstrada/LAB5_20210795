package com.example.lab5_20210795;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionesActivity extends AppCompatActivity {

    private EditText etNombre, etMensaje, etHoras;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);

        etNombre = findViewById(R.id.etNombre);
        etMensaje = findViewById(R.id.etMensaje);
        etHoras = findViewById(R.id.etHoras);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre", "Usuario"));
        etMensaje.setText(prefs.getString("mensaje", "¡Hoy será un gran día!"));
        etHoras.setText(String.valueOf(prefs.getInt("horas", 6))); // por defecto 6 horas

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String mensaje = etMensaje.getText().toString();
            int horas = Integer.parseInt(etHoras.getText().toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombre", nombre);
            editor.putString("mensaje", mensaje);
            editor.putInt("horas", horas);
            editor.apply();

            programarNotificacionMotivacional(horas);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish(); // regresar a MainActivity
        });
    }

    private void programarNotificacionMotivacional(int horas) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiverMotivacional.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent); // Cancelar si existía

        long intervalo = horas * 60 * 1000L; // horas a milisegundos
        //long intervalo = 120 * 1000; // 1 minuto en milisegundos
        long primerDisparo = System.currentTimeMillis() + intervalo;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                primerDisparo,
                intervalo,
                pendingIntent
        );
    }
}
