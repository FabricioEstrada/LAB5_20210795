package com.example.lab5_20210795;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_FILENAME = "imagen_guardada.png";
    private static final String channelId = "canal_motivacional";


    private TextView tvSaludo, tvMensaje;
    private ImageView ivImagen;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvMensaje = findViewById(R.id.tvMensaje);
        ivImagen = findViewById(R.id.ivImagen);

        // Leer de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");
        String mensaje = prefs.getString("mensaje", "¡Hoy será un gran día!");

        tvSaludo.setText("¡Hola, " + nombre + "!");
        tvMensaje.setText(mensaje);

        createNotificationChannel();
        programarNotificacionMotivacionalPorDefecto();

        // Inicializar el nuevo launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            guardarImagenEnInternalStorage(imageUri);
                        }
                    }
                }
        );

        // Cargar imagen guardada (si existe)
        cargarImagenDesdeAlmacenamiento();

        // Abrir galería al tocar la imagen
        ivImagen.setOnClickListener(v -> seleccionarImagenDesdeGaleria());

        findViewById(R.id.btnHabitos).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HabitosActivity.class));
        });

        findViewById(R.id.btnConfiguraciones).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ConfiguracionesActivity.class));
        });
    }

    private void seleccionarImagenDesdeGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void guardarImagenEnInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            //En este punto se declara donde se guardara la imagen (en el internal storage)
            File file = new File(getFilesDir(), IMAGE_FILENAME);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            // Mostrar la imagen guardada
            ivImagen.setImageURI(Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarImagenDesdeAlmacenamiento() {
        File file = new File(getFilesDir(), IMAGE_FILENAME);
        if (file.exists()) {
            ivImagen.setImageURI(Uri.fromFile(file));
        }
    }

    public void createNotificationChannel() {
        //android.os.Build.VERSION_CODES.O == 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Canal notificaciones default",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones con prioridad default");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            askPermission();
        }
    }
    public void askPermission(){
        //android.os.Build.VERSION_CODES.TIRAMISU == 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }
    }



    private void programarNotificacionMotivacionalPorDefecto() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // Si no hay datos previos, se usarán los valores por defecto
        String mensaje = prefs.getString("mensaje", "¡Hoy será un gran día!");
        int horas = prefs.getInt("horas", 6);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mensaje", mensaje); // se asegura que esté guardado
        editor.putInt("horas", horas);
        editor.apply();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificacionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //long intervalo = horas * 60 * 60 * 1000L;
        long intervalo = 20 * 1000;
        long primerDisparo = System.currentTimeMillis() + intervalo;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                primerDisparo,
                intervalo,
                pendingIntent
        );
    }
}
