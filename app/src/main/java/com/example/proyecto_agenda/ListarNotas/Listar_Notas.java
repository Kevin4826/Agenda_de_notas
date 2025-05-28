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

    RecyclerView recyclerviewNotas;
    LinearLayoutManager linearLayoutManager;
    NotaAdapter notaAdapter;
    Dialog dialog;
    String uidUsuario;  // Variable para almacenar el UID del usuario actual
    String correoUsuario; // Variable para almacenar el correo del usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_notas);

        Log.d(TAG, "onCreate: Iniciando actividad Listar_Notas");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis Notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerviewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerviewNotas.setHasFixedSize(true);

        dialog = new Dialog(Listar_Notas.this);

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);

        // Obtén el UID y correo del usuario desde el Intent
        uidUsuario = getIntent().getStringExtra("Uid");
        correoUsuario = getIntent().getStringExtra("Correo");
        Log.d(TAG, "UID Usuario: " + uidUsuario);
        Log.d(TAG, "Correo Usuario: " + correoUsuario);

        // Verifica que el UID del usuario no sea nulo antes de cargar las notas
        if (uidUsuario != null) {
            // Cargar notas desde la base de datos Room usando uidUsuario
            loadNotas();
        } else {
            Log.e(TAG, "UID del usuario es nulo. No se pueden cargar las notas.");
            // Manejar el caso de UID nulo, por ejemplo, mostrar un mensaje de error al usuario
            Toast.makeText(this, "Error: UID del usuario no disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotas() {
        class LoadNotasTask extends AsyncTask<Void, Void, List<Nota>> {
            @Override
            protected List<Nota> doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground: Cargando notas para UID: " + uidUsuario);
                return AppDatabase.getInstance(getApplicationContext()).notaDao().getNotasByUsuario(uidUsuario);
            }

            @Override
            protected void onPostExecute(List<Nota> notas) {
                super.onPostExecute(notas);
                Log.d(TAG, "onPostExecute: Notas cargadas. Cantidad: " + notas.size());
                notaAdapter = new NotaAdapter(notas);
                recyclerviewNotas.setAdapter(notaAdapter);
            }
        }

        new LoadNotasTask().execute();
    }

    private void showDialogOptions(Nota nota) {
        Log.d(TAG, "showDialogOptions: Mostrando opciones para la nota: " + nota.getTitulo());

        Button CD_Eliminar, CD_Actualizar;
        dialog.setContentView(R.layout.dialogo_opciones);
        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Eliminar nota: " + nota.getTitulo());
                eliminarNota(nota.getId());
                dialog.dismiss();
            }
        });

        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Actualizar nota: " + nota.getTitulo());
                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                intent.putExtra("nota", nota); // Aquí se pasa el objeto Nota completo
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void eliminarNota(int idNota) {
        Log.d(TAG, "eliminarNota: Eliminando nota con id: " + idNota);

        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Eliminar Nota");
        builder.setMessage("¿Desea eliminar la nota?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                class DeleteNotaTask extends AsyncTask<Void, Void, Void> {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AppDatabase.getInstance(getApplicationContext()).notaDao().deleteNotaById(idNota);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(Listar_Notas.this, "Nota Eliminada", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onDataChange: Nota eliminada exitosamente");
                        loadNotas();
                    }
                }

                new DeleteNotaTask().execute();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Toast.makeText(Listar_Notas.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Eliminación de nota cancelada por el usuario");
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotas();
    }

    private class NotaAdapter extends RecyclerView.Adapter<ViewHolder_Nota> {
        private List<Nota> notaList;

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
            Log.d(TAG, "onBindViewHolder: Posición " + position + ", ID Nota: " + nota.getId());
            holder.SetearDatos(
                    getApplicationContext(),
                    String.valueOf(nota.getId()), // Asegúrate de pasar el id como String
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
                    Log.d(TAG, "onItemClick: Nota seleccionada: " + nota.getTitulo());

                    Intent intent = new Intent(Listar_Notas.this, Detalle_Nota.class);
                    intent.putExtra("id", String.valueOf(nota.getId())); // Asegúrate de que este valor no sea nulo y es un String
                    intent.putExtra("uid_usuario", nota.getUidUsuario());
                    intent.putExtra("correo_usuario", nota.getCorreoUsuario());
                    intent.putExtra("fecha_registro", nota.getFechaHoraActual());
                    intent.putExtra("titulo", nota.getTitulo());
                    intent.putExtra("descripcion", nota.getDescripcion());
                    intent.putExtra("fecha_nota", nota.getFechaNota());
                    intent.putExtra("estado", nota.getEstado());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    Log.d(TAG, "onItemLongClick: Nota seleccionada para opciones: " + nota.getTitulo());
                    showDialogOptions(nota);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notaList.size();
        }
    }
}