package com.example.lab5_20210795;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habits;
    private Context context;

    public HabitAdapter(Context context, List<Habit> habits) {
        this.context = context;
        this.habits = habits;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.tvNombre.setText(habit.getNombre());

        // Configurar Spinner para la categoría
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categorias_habitos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerCategoria.setAdapter(adapter);

        // Seleccionar categoría correspondiente
        int spinnerPosition = adapter.getPosition(habit.getCategoria());
        if (spinnerPosition >= 0) {
            holder.spinnerCategoria.setSelection(spinnerPosition);
        }

        holder.tvFrecuencia.setText("Frecuencia: Cada " + habit.getFrecuenciaHoras() + " horas");

        // Formatear fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaStr = sdf.format(habit.getFechaInicioMillis());
        holder.tvFechaInicio.setText("Inicio: " + fechaStr);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvFrecuencia, tvFechaInicio;
        Spinner spinnerCategoria;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHabit);
            spinnerCategoria = itemView.findViewById(R.id.spinnerCategoria);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuencia);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);
        }
    }
}
