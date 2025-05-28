package com.example.proyecto_agenda.ActualizarNota;

import android.app.DatePickerDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_agenda.AgregarNota.Agregar_Nota;
import com.example.proyecto_agenda.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Actualizar_Nota extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Actualizar_Nota";

    TextView Id_nota_A, Uid_Usuario_A, Correo_Usuario_A, Fecha_registro_A,Fecha_A, Estado_A, Estado_nuevo;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A;

    String id_nota_R, uid_usuario_R, correo_usuario_R, fecha_registro_R, titulo_R, descripcion_R, fecha_R, estado_R;

    ImageView Tarea_Finalizada, Tarea_No_Finalizada;

    Spinner Spinner_estado;

    int dia,mes,anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actualizar_nota);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar nota");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVistas();
        RecuperarDatos();
        SetearDatos();
        ComprobarEstadoNota();
        Spinner_Estado();

        Btn_Calendario_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarFecha();
            }
        });
    }

    private void InicializarVistas(){
        Id_nota_A = findViewById(R.id.Id_nota_A);
        Uid_Usuario_A=findViewById(R.id.Uid_Usuario_A);
        Correo_Usuario_A= findViewById(R.id.Correo_Usuario_A);
        Fecha_registro_A= findViewById(R.id.Fecha_registro_A);
        Fecha_A= findViewById(R.id.Fecha_A);
        Estado_A= findViewById(R.id.Estado_A);
        Titulo_A= findViewById(R.id.Titulo_A);
        Descripcion_A= findViewById(R.id.Descripcion_A);
        Btn_Calendario_A= findViewById(R.id.Btn_Calendario_A);

        Tarea_Finalizada = findViewById(R.id.Tarea_Finalizada);
        Tarea_No_Finalizada=findViewById(R.id.Tarea_No_Finalizada);
        Spinner_estado=findViewById(R.id.Spinner_estado);
        Estado_nuevo= findViewById(R.id.Estado_nuevo);
    }

    private void RecuperarDatos(){
        Bundle intent = getIntent().getExtras();

        id_nota_R= intent.getString("id_nota");
        uid_usuario_R= intent.getString("uid_usuario");
        correo_usuario_R= intent.getString("correo_usuario");
        fecha_registro_R= intent.getString("fecha_registro");
        titulo_R= intent.getString("titulo");
        descripcion_R= intent.getString("descripcion");
        fecha_R= intent.getString("fecha_nota");
        estado_R= intent.getString("estado");

        Log.d(TAG, "Datos recuperados exitosamente");
    }

    private void SetearDatos(){
        Id_nota_A.setText(id_nota_R);
        Uid_Usuario_A.setText(uid_usuario_R);
        Correo_Usuario_A.setText(correo_usuario_R);
        Fecha_registro_A.setText(fecha_registro_R);
        Titulo_A.setText(titulo_R);
        Descripcion_A.setText(titulo_R);
        Descripcion_A.setText(descripcion_R);
        Fecha_A.setText(fecha_R);
        Estado_A.setText(estado_R) ;

        Log.d(TAG, "Datos establecidos en las vistas");
    }

    private void ComprobarEstadoNota(){
        String estado_nota = Estado_A.getText().toString();
        if (estado_nota.equals("No finalizado")){
            Tarea_No_Finalizada.setVisibility(View.VISIBLE);
            Log.d(TAG, "Estado de la nota: No finalizado");
        }
        if(estado_nota.equals("Finalizado")){
            Tarea_Finalizada.setVisibility(View.VISIBLE);
            Log.d(TAG, "Estado de la nota: Finalizado");
        }
    }

    private void SeleccionarFecha(){
        final Calendar calendario = Calendar.getInstance();

        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Actualizar_Nota.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                String diaFormateado, mesFormateado;

                //Obtener el dia
                if (DiaSeleccionado <10){
                    diaFormateado = "0"+String.valueOf(DiaSeleccionado);
                    //Para que se muestre un 0 delante de la fecha del día
                }else {
                    diaFormateado = String.valueOf(DiaSeleccionado);
                }
                //Obtener el mes
                int Mes =MesSeleccionado+1;
                if (Mes < 10){
                    mesFormateado = "0"+String.valueOf(Mes);
                    //Igual le agreag 0 al mes si es menor que 10
                }else {
                    mesFormateado = String.valueOf(Mes);
                }

                //Setear fecha en TextView
                Fecha_A.setText(diaFormateado+ "/"+mesFormateado + "/"+ AnioSeleccionado);

                Log.d(TAG, "Fecha seleccionada: " + diaFormateado + "/" + mesFormateado + "/" + AnioSeleccionado);
            }
        }
                ,anio,mes,dia);
        datePickerDialog.show();
    }

    private void Spinner_Estado(){
        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,
                R.array.Estado_nota, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_estado.setAdapter(adapter);
        Spinner_estado.setOnItemSelectedListener(this);

        Log.d(TAG, "Spinner de estado configurado");
    }

    private void ActualizaNotaBD(){
        String tituloActualizar= Titulo_A.getText().toString();
        String descripcionActualizar= Descripcion_A.getText().toString();
        String fechaActualizar= Fecha_A.getText().toString();
        String estadoActualizar= Estado_nuevo.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("Notas_Publicadas");

        //Consulta
        Query query = databaseReference.orderByChild("id_nota").equalTo(id_nota_R);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("titulo").setValue(tituloActualizar);
                    ds.getRef().child("descripcion").setValue(descripcionActualizar);
                    ds.getRef().child("fecha_nota").setValue(fechaActualizar);
                    ds.getRef().child("estado").setValue(estadoActualizar);
                }

                Toast.makeText(Actualizar_Nota.this, "Nota actualizada con éxito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al actualizar la nota: " + error.getMessage());
            }
        });

        Log.d(TAG, "Actualización de la nota en la base de datos");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String ESTADO_ACTUAL = Estado_A.getText().toString();

        String Posicion_1 = adapterView.getItemAtPosition(1).toString();

        String estado_seleccionado = adapterView.getItemAtPosition(i).toString();
        Estado_nuevo.setText(estado_seleccionado);

        if (ESTADO_ACTUAL.equals("Finaliado")){
            Estado_nuevo.setText(Posicion_1);
        }

        Log.d(TAG, "Estado seleccionado: " + estado_seleccionado);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "Ningún estado seleccionado");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Actualizar_Nota_BD)
        {
            ActualizaNotaBD();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
