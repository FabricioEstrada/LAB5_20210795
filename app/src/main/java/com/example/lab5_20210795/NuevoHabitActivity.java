package com.example.lab5_20210795;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class NuevoHabitActivity extends AppCompatActivity {

    private EditText etNombre, etFrecuencia, etFechaHoraInicio;
    private Spinner spinnerCategoria;
    private Button btnGuardar;

    private long fechaInicioMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_habit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombre = findViewById(R.id.etNombreHabit);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etFechaHoraInicio = findViewById(R.id.etFechaHoraInicio);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        btnGuardar = findViewById(R.id.btnGuardarHabit);

        // Configurar spinner con categorías
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias_habitos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        // Cuando toquen el campo fecha/hora mostrar el selector
        etFechaHoraInicio.setOnClickListener(v -> mostrarSelectorFechaYHora());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String categoria = spinnerCategoria.getSelectedItem().toString();
            String freqStr = etFrecuencia.getText().toString().trim();

            if (nombre.isEmpty() || freqStr.isEmpty() || fechaInicioMillis == 0) {
                // Puedes mostrar un Toast o mensaje que falta info
                return;
            }

            int frecuencia = Integer.parseInt(freqStr);

            Habit habit = new Habit(nombre, categoria, frecuencia, fechaInicioMillis);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("nuevo_habit", habit);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void mostrarSelectorFechaYHora() {
        final Calendar calendario = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendario.set(Calendar.MINUTE, minute);
                calendario.set(Calendar.SECOND, 0);
                fechaInicioMillis = calendario.getTimeInMillis();
                etFechaHoraInicio.setText(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", fechaInicioMillis));
            };
            new TimePickerDialog(NuevoHabitActivity.this, timeSetListener,
                    calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show();
        };

        new DatePickerDialog(NuevoHabitActivity.this, dateSetListener,
                calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }
}
