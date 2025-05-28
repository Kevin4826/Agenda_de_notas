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
    TextView Uid_Usuario, Correo_Usuario, Fecha_Hora_Actual, Fecha, Estado;
    EditText Titulo, Descripcion;
    Button Btn_Calendario;

    int dia, mes, anio;

    private NotaDao notaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);
        Log.d(TAG, "onCreate");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        notaDao = AppDatabase.getInstance(this).notaDao();

        InicializarVariables();
        Log.d(TAG, "Variables inicializadas");
        ObtenerDatos();
        Log.d(TAG, "Datos obtenidos");
        Obtener_Fecha_Hora_Actual();
        Log.d(TAG, "Fecha y hora actual obtenidas");

        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Seleccionar fecha del calendario");

                final Calendar calendario = Calendar.getInstance();

                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                        String diaFormateado, mesFormateado;

                        //Obtener el dia
                        if (DiaSeleccionado < 10) {
                            diaFormateado = "0" + DiaSeleccionado;
                        } else {
                            diaFormateado = String.valueOf(DiaSeleccionado);
                        }
                        //Obtener el mes
                        int Mes = MesSeleccionado + 1;
                        if (Mes < 10) {
                            mesFormateado = "0" + Mes;
                        } else {
                            mesFormateado = String.valueOf(Mes);
                        }

                        //Setear fecha en TextView
                        Fecha.setText(diaFormateado + "/" + mesFormateado + "/" + AnioSeleccionado);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    private void InicializarVariables() {
        Log.d(TAG, "Inicializando variables");
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_Usuario = findViewById(R.id.Correo_Usuario);
        Fecha_Hora_Actual = findViewById(R.id.Fecha_Hora_Actual);
        Fecha = findViewById(R.id.Fecha);
        Estado = findViewById(R.id.Estado);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        Btn_Calendario = findViewById(R.id.Btn_Calendario);
    }

    private void ObtenerDatos() {
        Log.d(TAG, "Obteniendo datos");
        String uid_recuperado = getIntent().getStringExtra("Uid");
        String correo_recuperado = getIntent().getStringExtra("Correo");

        Uid_Usuario.setText(uid_recuperado);
        Correo_Usuario.setText(correo_recuperado);
    }

    private void Obtener_Fecha_Hora_Actual() {
        Log.d(TAG, "Obteniendo fecha y hora actual");
        String Fecha_hora_registro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a", Locale.getDefault()).format(System.currentTimeMillis());
        Fecha_Hora_Actual.setText(Fecha_hora_registro);
    }

    private void Agregar_Nota() {
        Log.d(TAG, "Agregando nota");
        //Obtener los datos
        String uidUsuario = Uid_Usuario.getText().toString();
        String correoUsuario = Correo_Usuario.getText().toString();
        String fechaHoraActual = Fecha_Hora_Actual.getText().toString();
        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String fechaNota = Fecha.getText().toString();
        String estado = Estado.getText().toString();

        //Validar los datos
        if (!uidUsuario.isEmpty() && !correoUsuario.isEmpty() && !fechaHoraActual.isEmpty() &&
                !titulo.isEmpty() && !descripcion.isEmpty() && !fechaNota.isEmpty() && !estado.isEmpty()) {
            Nota nota = new Nota(
                    uidUsuario,
                    correoUsuario,
                    fechaHoraActual,
                    titulo,
                    descripcion,
                    fechaNota,
                    estado);

            // Insertar nota en la base de datos
            new Thread(() -> {
                long idNota = notaDao.insert(nota);
                nota.setId((int) idNota);

                runOnUiThread(() -> {
                    Toast.makeText(Agregar_Nota.this, "Se agregó la nota exitosamente", Toast.LENGTH_SHORT).show();
                    getIntent().putExtra("id_nota", nota.getId());
                    onBackPressed();
                });
            }).start();
        } else {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Creando menú");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Item de menú seleccionado");
        if (item.getItemId() == R.id.Agregar_Nota_BD) {
            Agregar_Nota();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            // Manejar el botón de retroceso
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "Volviendo hacia atrás");
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finalizar la actividad para evitar duplicados al volver
        finish();
    }
}



















//package com.example.proyecto_agenda.AgregarNota;
//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.proyecto_agenda.Objetos.Nota;
//import com.example.proyecto_agenda.R;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//public class Agregar_Nota extends AppCompatActivity {
//
//    private static final String TAG = "Agregar_Nota";
//    TextView Uid_Usuario, Correo_Usuario, Fecha_Hora_Actual, Fecha, Estado;
//    EditText Titulo, Descripcion;
//    Button Btn_Calendario;
//
//    int dia, mes, anio;
//
//    DatabaseReference BD_Firebase;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_agregar_nota);
//        Log.d(TAG, "onCreate");
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("");
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        InicializarVariables();
//        Log.d(TAG, "Variables inicializadas");
//        ObtenerDatos();
//        Log.d(TAG, "Datos obtenidos");
//        Obtener_Fecha_Hora_Actual();
//        Log.d(TAG, "Fecha y hora actual obtenidas");
//
//        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Seleccionar fecha del calendario");
//
//                final Calendar calendario = Calendar.getInstance();
//
//                dia = calendario.get(Calendar.DAY_OF_MONTH);
//                mes = calendario.get(Calendar.MONTH);
//                anio = calendario.get(Calendar.YEAR);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {
//
//                        String diaFormateado, mesFormateado;
//
//                        //Obtener el dia
//                        if (DiaSeleccionado < 10) {
//                            diaFormateado = "0" + String.valueOf(DiaSeleccionado);
//                        } else {
//                            diaFormateado = String.valueOf(DiaSeleccionado);
//                        }
//                        //Obtener el mes
//                        int Mes = MesSeleccionado + 1;
//                        if (Mes < 10) {
//                            mesFormateado = "0" + String.valueOf(Mes);
//                        } else {
//                            mesFormateado = String.valueOf(Mes);
//                        }
//
//                        //Setear fecha en TextView
//                        Fecha.setText(diaFormateado + "/" + mesFormateado + "/" + AnioSeleccionado);
//                    }
//                }, anio, mes, dia);
//                datePickerDialog.show();
//            }
//        });
//    }
//
//    private void InicializarVariables() {
//        Log.d(TAG, "Inicializando variables");
//        Uid_Usuario = findViewById(R.id.Uid_Usuario);
//        Correo_Usuario = findViewById(R.id.Correo_Usuario);
//        Fecha_Hora_Actual = findViewById(R.id.Fecha_Hora_Actual);
//        Fecha = findViewById(R.id.Fecha);
//        Estado = findViewById(R.id.Estado);
//
//        Titulo = findViewById(R.id.Titulo);
//        Descripcion = findViewById(R.id.Descripcion);
//        Btn_Calendario = findViewById(R.id.Btn_Calendario);
//
//        BD_Firebase = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void ObtenerDatos() {
//        Log.d(TAG, "Obteniendo datos");
//        String uid_recuperado = getIntent().getStringExtra("Uid");
//        String correo_recuperado = getIntent().getStringExtra("Correo");
//
//        Uid_Usuario.setText(uid_recuperado);
//        Correo_Usuario.setText(correo_recuperado);
//    }
//
//    private void Obtener_Fecha_Hora_Actual() {
//        Log.d(TAG, "Obteniendo fecha y hora actual");
//        String Fecha_hora_registro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a", Locale.getDefault()).format(System.currentTimeMillis());
//        Fecha_Hora_Actual.setText(Fecha_hora_registro);
//    }
//
//    private void Agregar_Nota() {
//        Log.d(TAG, "Agregando nota");
//        //Obtener los datos
//        String uid_usuario = Uid_Usuario.getText().toString();
//        String correo_usuario = Correo_Usuario.getText().toString();
//        String fecha_hora_actual = Fecha_Hora_Actual.getText().toString();
//        String titulo = Titulo.getText().toString();
//        String descripcion = Descripcion.getText().toString();
//        String fecha = Fecha.getText().toString();
//        String estado = Estado.getText().toString();
//        String id_nota = BD_Firebase.push().getKey();
//
//        //Validar los datos
//        if (!uid_usuario.equals("") && !correo_usuario.equals("") && !fecha_hora_actual.equals("") &&
//                !titulo.equals("") && !descripcion.equals("") && !fecha.equals("") && !estado.equals("")) {
//            Nota nota = new Nota(id_nota,
//                    uid_usuario,
//                    correo_usuario,
//                    fecha_hora_actual,
//                    titulo,
//                    descripcion,
//                    fecha,
//                    estado);
//            String Nombre_BD = "Notas_Publicadas";
//
//            BD_Firebase.child(Nombre_BD).child(id_nota).setValue(nota);
//
//            Toast.makeText(this, "Se agregó la nota exitosamente", Toast.LENGTH_SHORT).show();
//            onBackPressed();
//        } else {
//            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.d(TAG, "Creando menú");
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_agregar, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d(TAG, "Item de menú seleccionado");
//        if (item.getItemId() == R.id.Agregar_Nota_BD) {
//            Agregar_Nota();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        Log.d(TAG, "Volviendo hacia atrás");
//        onBackPressed();
//        return super.onSupportNavigateUp();
//    }
//}
