package com.example.proyecto_agenda.AgregarNota;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_agenda.Objetos.AppDatabase;
import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.Objetos.NotaDao;
import com.example.proyecto_agenda.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agregar_Nota extends AppCompatActivity {

    private static final String TAG = "Agregar_Nota";

    // Variables UI agrupadas en una clase para respetar SRP
    private static class VistaNota {
        TextView uidUsuario, correoUsuario, fechaHoraActual, fechaNota, estado;
        EditText titulo, descripcion;
        Button btnCalendario;

        VistaNota(AppCompatActivity activity) {
            uidUsuario = activity.findViewById(R.id.Uid_Usuario);
            correoUsuario = activity.findViewById(R.id.Correo_Usuario);
            fechaHoraActual = activity.findViewById(R.id.Fecha_Hora_Actual);
            fechaNota = activity.findViewById(R.id.Fecha);
            estado = activity.findViewById(R.id.Estado);
            titulo = activity.findViewById(R.id.Titulo);
            descripcion = activity.findViewById(R.id.Descripcion);
            btnCalendario = activity.findViewById(R.id.Btn_Calendario);
        }
    }

    private VistaNota vista;
    private NotaDao notaDao;

    private int dia, mes, anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);

        configurarActionBar();
        inicializarComponentes();
        cargarDatosIniciales();
        configurarCalendario();
    }

    private void configurarActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void inicializarComponentes() {
        Log.d(TAG, "Inicializando componentes");
        vista = new VistaNota(this);
        notaDao = AppDatabase.getInstance(this).notaDao();
    }

    private void cargarDatosIniciales() {
        Log.d(TAG, "Cargando datos iniciales");
        String uid = getIntent().getStringExtra("Uid");
        String correo = getIntent().getStringExtra("Correo");

        vista.uidUsuario.setText(uid != null ? uid : "");
        vista.correoUsuario.setText(correo != null ? correo : "");
        vista.fechaHoraActual.setText(obtenerFechaHoraActual());
    }

    private String obtenerFechaHoraActual() {
        return new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a", Locale.getDefault()).format(System.currentTimeMillis());
    }

    private void configurarCalendario() {
        vista.btnCalendario.setOnClickListener(v -> mostrarSelectorFecha());
    }

    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String fechaFormateada = formatearFecha(dayOfMonth, month + 1, year);
                    vista.fechaNota.setText(fechaFormateada);
                }, anio, mes, dia);
        datePickerDialog.show();
    }

    private String formatearFecha(int dia, int mes, int anio) {
        String diaFormateado = dia < 10 ? "0" + dia : String.valueOf(dia);
        String mesFormateado = mes < 10 ? "0" + mes : String.valueOf(mes);
        return diaFormateado + "/" + mesFormateado + "/" + anio;
    }

    private boolean validarCampos() {
        return !vista.uidUsuario.getText().toString().isEmpty()
                && !vista.correoUsuario.getText().toString().isEmpty()
                && !vista.fechaHoraActual.getText().toString().isEmpty()
                && !vista.titulo.getText().toString().isEmpty()
                && !vista.descripcion.getText().toString().isEmpty()
                && !vista.fechaNota.getText().toString().isEmpty()
                && !vista.estado.getText().toString().isEmpty();
    }

    private Nota construirNota() {
        return new Nota(
                vista.uidUsuario.getText().toString(),
                vista.correoUsuario.getText().toString(),
                vista.fechaHoraActual.getText().toString(),
                vista.titulo.getText().toString(),
                vista.descripcion.getText().toString(),
                vista.fechaNota.getText().toString(),
                vista.estado.getText().toString()
        );
    }

    private void agregarNota() {
        Log.d(TAG, "Intentando agregar nota");
        if (!validarCampos()) {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Nota nota = construirNota();

        // Inserción en base de datos en hilo separado (DIP: abstraer acceso a datos)
        new Thread(() -> {
            long id = notaDao.insert(nota);
            nota.setId((int) id);

            runOnUiThread(() -> {
                Toast.makeText(this, "Se agregó la nota exitosamente", Toast.LENGTH_SHORT).show();
                getIntent().putExtra("id_nota", nota.getId());
                onBackPressed();
            });
        }).start();
    }

    // Menú y navegación

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_agregar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Agregar_Nota_BD) {
            agregarNota();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}