package com.example.proyecto_agenda.Detalle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_agenda.Objetos.AppDatabase;
import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Detalle_Nota extends AppCompatActivity {

    private static final String TAG = "Detalle_Nota";
    Button botonImportante;
    TextView idNotaDetalle, uidUsuarioDetalle, correoUsuarioDetalle, tituloDetalle, descripcionDetalle,
            fechaRegistroDetalle, fechaNotaDetalle, estadoDetalle;

    String idNota, uidUsuario, correoUsuario, fechaRegistro, titulo, descripcion, fechaNota, estado;

    boolean comprobarNotaImportante = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_nota);
        Log.d(TAG, "onCreate");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Detalle de nota");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        inicializarVistas();
        Log.d(TAG, "Vistas inicializadas");
        recuperarDatos();
        Log.d(TAG, "Datos recuperados");
        setearDatosRecuperados();
        Log.d(TAG, "Datos recuperados establecidos en las vistas");
        verificarNotaImportante();
        Log.d(TAG, "Nota importante verificada");

        botonImportante.setOnClickListener(v -> {
            if (comprobarNotaImportante) {
                eliminarNotaImportante();
            } else {
                agregarNotaImportante();
            }
        });
    }

    private void inicializarVistas() {
        Log.d(TAG, "Inicializando vistas");
        idNotaDetalle = findViewById(R.id.Id_nota_Detalle);
        uidUsuarioDetalle = findViewById(R.id.Uid_usuario_Detalle);
        correoUsuarioDetalle = findViewById(R.id.Correo_usuario_Detalle);
        descripcionDetalle = findViewById(R.id.Descripcion_Detalle);
        tituloDetalle = findViewById(R.id.Titulo_Detalle);
        fechaRegistroDetalle = findViewById(R.id.Fecha_Registro_Detalle);
        fechaNotaDetalle = findViewById(R.id.Fecha_Nota_Detalle);
        estadoDetalle = findViewById(R.id.Estado_Detalle);
        botonImportante = findViewById(R.id.Boton_Importante);
    }

    private void recuperarDatos() {
        Log.d(TAG, "Recuperando datos");
        Bundle intent = getIntent().getExtras();

        if (intent != null) {
            uidUsuario = intent.getString("uid_usuario");
            correoUsuario = intent.getString("correo_usuario");
            fechaRegistro = intent.getString("fecha_registro");
            titulo = intent.getString("titulo");
            descripcion = intent.getString("descripcion");
            fechaNota = intent.getString("fecha_nota");
            estado = intent.getString("estado");
            idNota = intent.getString("id_nota");

            // Verificación de uidUsuario
            if (uidUsuario == null) {
                Log.e(TAG, "UID de usuario es nulo después de recuperar datos del Intent");
                Toast.makeText(this, "Error: UID de usuario nulo", Toast.LENGTH_SHORT).show();
                finish(); // Finalizar actividad si UID de usuario es nulo
            }
        } else {
            Log.e(TAG, "No se recibieron datos del intent");
            Toast.makeText(this, "Error al recibir datos del intent", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar actividad si no hay datos recibidos
        }
    }

    private void setearDatosRecuperados() {
        Log.d(TAG, "Seteando datos recuperados en las vistas");
        idNotaDetalle.setText(idNota);
        uidUsuarioDetalle.setText(uidUsuario);
        correoUsuarioDetalle.setText(correoUsuario);
        descripcionDetalle.setText(descripcion);
        tituloDetalle.setText(titulo);
        fechaRegistroDetalle.setText(fechaRegistro);
        fechaNotaDetalle.setText(fechaNota);
        estadoDetalle.setText(estado);
    }

    private void agregarNotaImportante() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Comenzando a agregar nota importante");

                // Verificar si la nota ya existe como importante
                List<Nota> notasUsuario = AppDatabase.getInstance(getApplicationContext())
                        .notaDao().getNotasByUsuario(uidUsuario);
                for (Nota nota : notasUsuario) {
                    if (String.valueOf(nota.getId()).equals(idNota)) {
                        showToast("La nota ya es importante");
                        Log.d(TAG, "La nota ya es importante");
                        return; // Salir del método si la nota ya es importante
                    }
                }

                // Agregar la nota como importante en la base de datos local (Room)
                Nota nota = new Nota(
                        uidUsuario,
                        correoUsuario,
                        fechaRegistro,
                        titulo,
                        descripcion,
                        fechaNota,
                        estado
                );
                long idNota = AppDatabase.getInstance(getApplicationContext()).notaDao().insert(nota);
                nota.setId((int) idNota);

                // Guardar la nota como importante en Firebase
                DatabaseReference notasImportantesRef = FirebaseDatabase.getInstance().getReference("Notas_Importantes").child(uidUsuario);
                notasImportantesRef.child(String.valueOf(nota.getId())).setValue(nota);

                runOnUiThread(() -> {
                    Toast.makeText(Detalle_Nota.this, "Se ha añadido a notas importantes", Toast.LENGTH_SHORT).show();
                    comprobarNotaImportante = true;
                    actualizarBotonImportante();
                    Log.d(TAG, "Nota importante añadida correctamente");
                });
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error al agregar nota importante", e);
            }

        }).start();
    }

    private void eliminarNotaImportante() {
        Log.d(TAG, "Eliminando nota importante");
        new Thread(() -> {
            try {
                if (idNota == null) {
                    showToast("Error: ID de nota nulo");
                    return; // Salir del método si el ID de la nota es nulo
                }

                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                int idNotaInt = Integer.parseInt(idNota);
                db.notaDao().deleteNotaById(idNotaInt);

                runOnUiThread(() -> {
                    Toast.makeText(Detalle_Nota.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                    comprobarNotaImportante = false;
                    actualizarBotonImportante();
                });
            } catch (NumberFormatException e) {
                showError("Error al convertir ID de nota a entero", e);
            } catch (Exception e) {
                showError("Error al eliminar nota importante", e);
            }
        }).start();
    }

    private void verificarNotaImportante() {
        Log.d(TAG, "Verificando nota importante");
        if (uidUsuario == null || idNota == null) {
            Log.e(TAG, "UID de usuario o ID de nota nulos");
            return;
        }

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                Nota nota = db.notaDao().getNotaById(Integer.parseInt(idNota));

                if (nota != null && nota.getUidUsuario().equals(uidUsuario)) {
                    comprobarNotaImportante = true;
                    Log.d(TAG, "La nota es importante");
                } else {
                    comprobarNotaImportante = false;
                    Log.d(TAG, "La nota no es importante");
                }

                runOnUiThread(this::actualizarBotonImportante);
            } catch (NumberFormatException e) {
                runOnUiThread(() -> showError("Error al convertir ID de nota a entero", e));
            } catch (Exception e) {
                runOnUiThread(() -> showError("Error al verificar nota importante", e));
            }
        }).start();
    }

    private void actualizarBotonImportante() {
        if (botonImportante != null) {
            runOnUiThread(() -> {
                if (comprobarNotaImportante) {
                    botonImportante.setText("Eliminar de importantes");
                } else {
                    botonImportante.setText("Agregar a importantes");
                }
            });
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(Detalle_Nota.this, message, Toast.LENGTH_SHORT).show());
    }

    private void showError(String message, Exception e) {
        Log.e(TAG, message + ": " + e.getMessage());
        showToast(message);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "Volviendo hacia atrás");
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
