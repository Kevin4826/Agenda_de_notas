package com.example.proyecto_agenda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pantalla_De_Carga extends AppCompatActivity {

    private static final String TAG = "Pantalla_De_Carga";

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_de_carga);

        firebaseAuth = FirebaseAuth.getInstance();

        int Tiempo = 3000;

        Log.d(TAG, "Pantalla de carga iniciada, tiempo de espera: " + Tiempo + "ms");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Tiempo de espera completado");
                VerificarUsuario();
            }
        }, Tiempo);
    }

    private void VerificarUsuario() {
        Log.d(TAG, "Verificando usuario");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Log.d(TAG, "No se encontró usuario, redirigiendo a MainActivity");
            startActivity(new Intent(Pantalla_De_Carga.this, MainActivity.class));
            finish();
        } else {
            Log.d(TAG, "Usuario encontrado, redirigiendo a MenuPrincipal");
            startActivity(new Intent(Pantalla_De_Carga.this, MenuPrincipal.class));
            finish();
        }
    }
}
