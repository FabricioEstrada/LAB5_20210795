package com.example.lab5_20210795;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        holder.tvCategoria.setText("Categoría: " + habit.getCategoria());

        holder.tvFrecuencia.setText("Frecuencia: Cada " + habit.getFrecuenciaHoras() + " horas");

        // Formatear fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaStr = sdf.format(habit.getFechaInicioMillis());
        holder.tvFechaInicio.setText("Inicio: " + fechaStr);
// Acción de eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Eliminar hábito")
                    .setMessage("¿Estás seguro de que quieres eliminar este hábito?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        habits.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, habits.size());

                        // Guardar lista actualizada
                        HabitStoragePrefs.guardarHabitos(context, habits);

                        // Mostrar mensaje si la lista queda vacía
                        if (context instanceof HabitsActivity && habits.isEmpty()) {
                            ((HabitsActivity) context).mostrarTextoNoHayHabitos();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvFrecuencia, tvFechaInicio, tvCategoria;
        Button btnEliminar;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHabit);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuencia);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
