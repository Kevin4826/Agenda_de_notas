package com.example.proyecto_agenda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_agenda.AgregarNota.Agregar_Nota;
import com.example.proyecto_agenda.ListarNotas.Listar_Notas;
import com.example.proyecto_agenda.NotasImportantes.Notas_Importantes;
import com.example.proyecto_agenda.Perfil.Perfil_Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    private static final String TAG = "MenuPrincipal";
    private static final String PREFS_NAME = "UserPrefs";

    Button AgregarNotas, ListarNotas, Importantes, Perfil, AcercaDe, CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView UidPrincipal, nombresPrincipal, correoPrincipal;
    ProgressBar progressBarDatos;

    DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Proyecto Agenda");
        }

        UidPrincipal = findViewById(R.id.UidPrincipal);
        nombresPrincipal = findViewById(R.id.nombresPrincipal);
        correoPrincipal = findViewById(R.id.correoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Importantes = findViewById(R.id.Importantes);
        Perfil = findViewById(R.id.Perfil);
        AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "AgregarNotas button clicked");
                String uid_usuario = UidPrincipal.getText().toString();
                String correo_usuario = correoPrincipal.getText().toString();
                Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);
            }
        });

        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ListarNotas button clicked");
                String uid_usuario = UidPrincipal.getText().toString();
                Intent intent = new Intent(MenuPrincipal.this, Listar_Notas.class);
                intent.putExtra("Uid", uid_usuario);
                startActivity(intent);
                Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });


        Importantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Importantes button clicked");
                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });

        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Perfil button clicked");
                startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
                Toast.makeText(MenuPrincipal.this, "Perfil Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "AcercaDe button clicked");
                Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
            }
        });

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CerrarSesion button clicked");
                SalirAplicacion();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart called");
        comprobarInicioSesion();
        super.onStart();
    }

    private void comprobarInicioSesion() {
        if (user != null) {
            Log.d(TAG, "User is signed in");
            if (isNetworkAvailable()) {
                cargaDeDatos();
            } else {
                cargaDeDatosLocal();
            }
        } else {
            Log.d(TAG, "User is not signed in, redirecting to MainActivity");
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    private void cargaDeDatos() {
        Log.d(TAG, "Loading user data from Firebase");
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User data found");
                    progressBarDatos.setVisibility(View.GONE);
                    UidPrincipal.setVisibility(View.VISIBLE);
                    nombresPrincipal.setVisibility(View.VISIBLE);
                    correoPrincipal.setVisibility(View.VISIBLE);

                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String correo = "" + snapshot.child("correo").getValue();

                    UidPrincipal.setText(uid);
                    nombresPrincipal.setText(nombres);
                    correoPrincipal.setText(correo);

                    guardarDatosLocalmente(uid, nombres, correo);

                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    Importantes.setEnabled(true);
                    Perfil.setEnabled(true);
                    AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);
                } else {
                    Log.d(TAG, "User data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading user data: " + error.getMessage());
            }
        });
    }

    private void guardarDatosLocalmente(String uid, String nombres, String correo) {
        Log.d(TAG, "Saving user data locally");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", uid);
        editor.putString("nombres", nombres);
        editor.putString("correo", correo);
        editor.apply();
    }

    private void cargaDeDatosLocal() {
        Log.d(TAG, "Loading user data from SharedPreferences");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);
        String nombres = sharedPreferences.getString("nombres", null);
        String correo = sharedPreferences.getString("correo", null);

        if (uid != null && nombres != null && correo != null) {
            progressBarDatos.setVisibility(View.GONE);
            UidPrincipal.setVisibility(View.VISIBLE);
            nombresPrincipal.setVisibility(View.VISIBLE);
            correoPrincipal.setVisibility(View.VISIBLE);

            UidPrincipal.setText(uid);
            nombresPrincipal.setText(nombres);
            correoPrincipal.setText(correo);

            AgregarNotas.setEnabled(true);
            ListarNotas.setEnabled(true);
            Importantes.setEnabled(true);
            Perfil.setEnabled(true);
            AcercaDe.setEnabled(true);
            CerrarSesion.setEnabled(true);
        } else {
            Log.d(TAG, "No user data found locally");
            Toast.makeText(this, "No hay datos disponibles sin conexión", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void SalirAplicacion() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "Signing out");
            firebaseAuth.signOut();
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            Toast.makeText(this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "No internet connection, cannot sign out");
            Toast.makeText(this, "No hay conexión a internet. No se puede cerrar sesión.", Toast.LENGTH_SHORT).show();
        }
    }
}


















//package com.example.proyecto_agenda;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.proyecto_agenda.AgregarNota.Agregar_Nota;
//import com.example.proyecto_agenda.ListarNotas.Listar_Notas;
//import com.example.proyecto_agenda.NotasImportantes.Notas_Importantes;
//import com.example.proyecto_agenda.Perfil.Perfil_Usuario;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class MenuPrincipal extends AppCompatActivity {
//
//    private static final String TAG = "MenuPrincipal";
//
//    Button AgregarNotas, ListarNotas, Importantes, Perfil, AcercaDe, CerrarSesion;
//    FirebaseAuth firebaseAuth;
//    FirebaseUser user;
//
//    TextView UidPrincipal, nombresPrincipal, correoPrincipal;
//    ProgressBar progressBarDatos;
//
//    DatabaseReference Usuarios;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_menu_principal);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle("Proyecto Agenda");
//        }
//
//        UidPrincipal = findViewById(R.id.UidPrincipal);
//        nombresPrincipal = findViewById(R.id.nombresPrincipal);
//        correoPrincipal = findViewById(R.id.correoPrincipal);
//        progressBarDatos = findViewById(R.id.progressBarDatos);
//
//        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
//
//        AgregarNotas = findViewById(R.id.AgregarNotas);
//        ListarNotas = findViewById(R.id.ListarNotas);
//        Importantes = findViewById(R.id.Importantes);
//        Perfil = findViewById(R.id.Perfil);
//        AcercaDe = findViewById(R.id.AcercaDe);
//        CerrarSesion = findViewById(R.id.CerrarSesion);
//        firebaseAuth = FirebaseAuth.getInstance();
//        user = firebaseAuth.getCurrentUser();
//
//        AgregarNotas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "AgregarNotas button clicked");
//                String uid_usuario = UidPrincipal.getText().toString();
//                String correo_usuario = correoPrincipal.getText().toString();
//                Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
//                intent.putExtra("Uid", uid_usuario);
//                intent.putExtra("Correo", correo_usuario);
//                startActivity(intent);
//            }
//        });
//
//        ListarNotas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "ListarNotas button clicked");
//                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
//                Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        Importantes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Importantes button clicked");
//                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
//                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        Perfil.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Perfil button clicked");
//                startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
//                Toast.makeText(MenuPrincipal.this, "Perfil Usuario", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        AcercaDe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "AcercaDe button clicked");
//                Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        CerrarSesion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "CerrarSesion button clicked");
//                SalirAplicacion();
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        Log.d(TAG, "onStart called");
//        comprobarInicioSesion();
//        super.onStart();
//    }
//
//    private void comprobarInicioSesion() {
//        if (user != null) {
//            Log.d(TAG, "User is signed in");
//            cargaDeDatos();
//        } else {
//            Log.d(TAG, "User is not signed in, redirecting to MainActivity");
//            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
//            finish();
//        }
//    }
//
//    private void cargaDeDatos() {
//        Log.d(TAG, "Loading user data");
//        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Log.d(TAG, "User data found");
//                    progressBarDatos.setVisibility(View.GONE);
//                    UidPrincipal.setVisibility(View.VISIBLE);
//                    nombresPrincipal.setVisibility(View.VISIBLE);
//                    correoPrincipal.setVisibility(View.VISIBLE);
//
//                    String uid = "" + snapshot.child("uid").getValue();
//                    String nombres = "" + snapshot.child("nombres").getValue();
//                    String correo = "" + snapshot.child("correo").getValue();
//
//                    UidPrincipal.setText(uid);
//                    nombresPrincipal.setText(nombres);
//                    correoPrincipal.setText(correo);
//
//                    AgregarNotas.setEnabled(true);
//                    ListarNotas.setEnabled(true);
//                    Importantes.setEnabled(true);
//                    Perfil.setEnabled(true);
//                    AcercaDe.setEnabled(true);
//                    CerrarSesion.setEnabled(true);
//                } else {
//                    Log.d(TAG, "User data not found");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Error loading user data: " + error.getMessage());
//            }
//        });
//    }
//
//    private void SalirAplicacion() {
//        Log.d(TAG, "Signing out");
//        firebaseAuth.signOut();
//        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
//        Toast.makeText(this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
//    }
//}