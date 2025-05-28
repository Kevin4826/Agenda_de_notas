package com.example.proyecto_agenda.ListarNotas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.ActualizarNota.Actualizar_Nota;
import com.example.proyecto_agenda.Detalle.Detalle_Nota;
import com.example.proyecto_agenda.Objetos.AppDatabase;
import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.example.proyecto_agenda.ViewHolder.ViewHolder_Nota;

import java.util.List;

public class Listar_Notas extends AppCompatActivity {

    private static final String TAG = "Listar_Notas";

    private RecyclerView recyclerViewNotas;
    private NotaAdapter notaAdapter;
    private Dialog dialog;
    private String uidUsuario;
    private String correoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_notas);

        setupActionBar();
        initViews();
        retrieveIntentData();

        if (uidUsuario != null) {
            loadNotas();
        } else {
            showErrorUidNull();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mis Notas");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        recyclerViewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerViewNotas.setHasFixedSize(true);
        recyclerViewNotas.setLayoutManager(createLinearLayoutManager());
        dialog = new Dialog(this);
    }

    private LinearLayoutManager createLinearLayoutManager() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        return manager;
    }

    private void retrieveIntentData() {
        uidUsuario = getIntent().getStringExtra("Uid");
        correoUsuario = getIntent().getStringExtra("Correo");
        Log.d(TAG, "UID Usuario: " + uidUsuario);
        Log.d(TAG, "Correo Usuario: " + correoUsuario);
    }

    private void showErrorUidNull() {
        Log.e(TAG, "UID del usuario es nulo. No se pueden cargar las notas.");
        Toast.makeText(this, "Error: UID del usuario no disponible.", Toast.LENGTH_SHORT).show();
    }

    private void loadNotas() {
        new LoadNotasTask().execute();
    }

    private class LoadNotasTask extends AsyncTask<Void, Void, List<Nota>> {
        @Override
        protected List<Nota> doInBackground(Void... voids) {
            Log.d(TAG, "Cargando notas para UID: " + uidUsuario);
            return AppDatabase.getInstance(getApplicationContext()).notaDao().getNotasByUsuario(uidUsuario);
        }

        @Override
        protected void onPostExecute(List<Nota> notas) {
            super.onPostExecute(notas);
            Log.d(TAG, "Notas cargadas. Cantidad: " + notas.size());
            notaAdapter = new NotaAdapter(notas);
            recyclerViewNotas.setAdapter(notaAdapter);
        }
    }

    private void showDialogOptions(final Nota nota) {
        Log.d(TAG, "Mostrando opciones para la nota: " + nota.getTitulo());

        dialog.setContentView(R.layout.dialogo_opciones);
        Button btnEliminar = dialog.findViewById(R.id.CD_Eliminar);
        Button btnActualizar = dialog.findViewById(R.id.CD_Actualizar);

        btnEliminar.setOnClickListener(v -> {
            Log.d(TAG, "Eliminar nota: " + nota.getTitulo());
            confirmarEliminarNota(nota.getId());
            dialog.dismiss();
        });

        btnActualizar.setOnClickListener(v -> {
            Log.d(TAG, "Actualizar nota: " + nota.getTitulo());
            abrirActualizarNota(nota);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void confirmarEliminarNota(int idNota) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Nota")
                .setMessage("¿Desea eliminar la nota?")
                .setPositiveButton("Sí", (dialog, which) -> new DeleteNotaTask(idNota).execute())
                .setNegativeButton("No", (dialog, which) -> {
                    Toast.makeText(this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Eliminación cancelada por el usuario");
                })
                .create()
                .show();
    }

    private class DeleteNotaTask extends AsyncTask<Void, Void, Void> {
        private final int idNota;

        DeleteNotaTask(int idNota) {
            this.idNota = idNota;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase.getInstance(getApplicationContext()).notaDao().deleteNotaById(idNota);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(Listar_Notas.this, "Nota Eliminada", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Nota eliminada exitosamente");
            loadNotas();
        }
    }

    private void abrirActualizarNota(Nota nota) {
        Intent intent = new Intent(this, Actualizar_Nota.class);
        intent.putExtra("nota", nota);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uidUsuario != null) {
            loadNotas();
        }
    }

    // Adaptador con Interface para Clicks, siguiendo ISP y SRP
    private class NotaAdapter extends RecyclerView.Adapter<ViewHolder_Nota> {
        private final List<Nota> notaList;

        NotaAdapter(List<Nota> notaList) {
            this.notaList = notaList;
        }

        @NonNull
        @Override
        public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
            return new ViewHolder_Nota(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder_Nota holder, int position) {
            Nota nota = notaList.get(position);
            Log.d(TAG, "Posición " + position + ", ID Nota: " + nota.getId());

            holder.SetearDatos(
                    getApplicationContext(),
                    String.valueOf(nota.getId()),
                    nota.getUidUsuario(),
                    nota.getCorreoUsuario(),
                    nota.getFechaHoraActual(),
                    nota.getTitulo(),
                    nota.getDescripcion(),
                    nota.getFechaNota(),
                    nota.getEstado()
            );

            holder.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, "Nota seleccionada: " + nota.getTitulo());
                    abrirDetalleNota(nota);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    Log.d(TAG, "Nota seleccionada para opciones: " + nota.getTitulo());
                    showDialogOptions(nota);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notaList.size();
        }
    }

    private void abrirDetalleNota(Nota nota) {
        Intent intent = new Intent(this, Detalle_Nota.class);
        intent.putExtra("id", String.valueOf(nota.getId()));
        intent.putExtra("uid_usuario", nota.getUidUsuario());
        intent.putExtra("correo_usuario", nota.getCorreoUsuario());
        intent.putExtra("fecha_registro", nota.getFechaHoraActual());
        intent.putExtra("titulo", nota.getTitulo());
        intent.putExtra("descripcion", nota.getDescripcion());
        intent.putExtra("fecha_nota", nota.getFechaNota());
        intent.putExtra("estado", nota.getEstado());
        startActivity(intent);
    }
}