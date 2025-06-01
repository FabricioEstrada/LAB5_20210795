package com.example.lab5_20210795;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HabitsActivity extends AppCompatActivity {

    private RecyclerView rvHabits;
    private Button btnAgregarHabit;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    private static final int REQUEST_CODE_NUEVO_HABITO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        rvHabits = findViewById(R.id.rvHabits);
        btnAgregarHabit = findViewById(R.id.btnAgregarHabit);

        habitList = new ArrayList<>();
        // Aquí podrías cargar hábitos guardados en base de datos o SharedPreferences

        habitAdapter = new HabitAdapter(this, habitList);
        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        rvHabits.setAdapter(habitAdapter);

        btnAgregarHabit.setOnClickListener(v -> {
            Intent intent = new Intent(HabitsActivity.this, NuevoHabitActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NUEVO_HABITO);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_NUEVO_HABITO && resultCode == RESULT_OK && data != null){
            Habit nuevoHabit = (Habit) data.getSerializableExtra("nuevo_habit");
            habitList.add(nuevoHabit);
            habitAdapter.notifyItemInserted(habitList.size() -1);
        }
    }
}