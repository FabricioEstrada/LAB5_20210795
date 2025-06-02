package com.example.lab5_20210795;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HabitsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NUEVO_HABITO = 1;

    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> listaHabitos;
    private TextView tvNoHayHabitos;

    // Aquí declaras el launcher para recibir resultado
    private ActivityResultLauncher<Intent> nuevoHabitLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        tvNoHayHabitos = findViewById(R.id.NoHayHabitos);
        recyclerView = findViewById(R.id.rvHabits);

        // Cargar los hábitos guardados
        listaHabitos = HabitStoragePrefs.cargarHabitos(this); // O HabitStorageJson, depende de tu implementación

        if (listaHabitos.isEmpty()) {
            tvNoHayHabitos.setVisibility(View.VISIBLE);
        } else {
            tvNoHayHabitos.setVisibility(View.GONE);
        }

        habitAdapter = new HabitAdapter(this, listaHabitos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        // Inicializas el launcher
        nuevoHabitLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Habit nuevoHabit = (Habit) result.getData().getSerializableExtra("nuevo_habit");
                        if (nuevoHabit != null) {
                            listaHabitos.add(nuevoHabit);
                            habitAdapter.notifyItemInserted(listaHabitos.size() - 1);
                            tvNoHayHabitos.setVisibility(View.GONE); // Ocultar el texto
                            // Aquí también podrías guardar la lista actualizada en JSON o donde sea
                        }
                    }
                }
        );

        Button btnAgregarHabit = findViewById(R.id.btnAgregarHabit);
        btnAgregarHabit.setOnClickListener(v -> {
            Intent intent = new Intent(HabitsActivity.this, NuevoHabitActivity.class);
            nuevoHabitLauncher.launch(intent);  // Aquí usas el launcher en lugar de startActivityForResult
        });
    }
    public void mostrarTextoNoHayHabitos() {
        TextView tvNoHayHabitos = findViewById(R.id.NoHayHabitos);
        tvNoHayHabitos.setVisibility(View.VISIBLE);
    }

}
