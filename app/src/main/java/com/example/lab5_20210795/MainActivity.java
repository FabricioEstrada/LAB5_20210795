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
import java.util.List;

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

        List<Habit> listaHabitos = HabitStoragePrefs.cargarHabitos(this);

        if (!listaHabitos.isEmpty()) {
            for (Habit habit : listaHabitos) {
                NotificationHelper.programarNotificacion(this, habit);
            }
        }

        tvSaludo.setText("¡Hola, " + nombre + "!");
        tvMensaje.setText(mensaje);

        createNotificationChannelMotivacion();
        createNotificationChannels();
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
            startActivity(new Intent(MainActivity.this, HabitsActivity.class));
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

    public void createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            String[] categorias = {"Ejercicio", "Alimentacion", "Suenho", "Lectura"};
            int[] importancias = {
                    NotificationManager.IMPORTANCE_HIGH,
                    NotificationManager.IMPORTANCE_DEFAULT,
                    NotificationManager.IMPORTANCE_LOW,
                    NotificationManager.IMPORTANCE_DEFAULT
            };

            for (int i = 0; i < categorias.length; i++) {
                String id = "canal_" + categorias[i];
                String name = "Canal de " + categorias[i];
                String descripcion = "Canal para hábitos de tipo " + categorias[i];

                NotificationChannel channel = new NotificationChannel(id, name, importancias[i]);
                channel.setDescription(descripcion);
                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            }

            askNotificationPermission();  // Luego de crear canales, pedimos permisos si aplica
        }
    }

    public void askNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    101);
        }
    }

    public void createNotificationChannelMotivacion() {
        //android.os.Build.VERSION_CODES.O == 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Canal Motivacion",
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
        int horas = prefs.getInt("horas", 1);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mensaje", mensaje); // se asegura que esté guardado
        editor.putInt("horas", horas);
        editor.apply();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiverMotivacional.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long intervalo = horas * 60 * 1000L;
        long primerDisparo = System.currentTimeMillis() + intervalo;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                primerDisparo,
                intervalo,
                pendingIntent
        );
    }
}
