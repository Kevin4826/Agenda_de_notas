package com.example.proyecto_agenda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText CorreoLogin, PassLogin;
    private Button Btn_Logeo;
    private TextView UsuarioNuevoTXT;
    private ProgressDialog progressDialog;

    private AuthService authService;
    private LoginValidator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);

        setupActionBar();
        initViews();

        validator = new LoginValidator();
        authService = new FirebaseAuthService();

        Btn_Logeo.setOnClickListener(v -> validarDatos());
        UsuarioNuevoTXT.setOnClickListener(v -> startActivity(new Intent(Login.this, Registro.class)));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        CorreoLogin = findViewById(R.id.CorreoLogin);
        PassLogin = findViewById(R.id.PassLogin);
        Btn_Logeo = findViewById(R.id.Btn_Logeo);
        UsuarioNuevoTXT = findViewById(R.id.UsuarioNuevoTXT);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void validarDatos() {
        String email = CorreoLogin.getText().toString();
        String password = PassLogin.getText().toString();

        if (!validator.isValidEmail(email)) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
        } else if (!validator.isValidPassword(password)) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else {
            iniciarSesion(email, password);
        }
    }

    private void iniciarSesion(String email, String password) {
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        authService.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                progressDialog.dismiss();
                startActivity(new Intent(Login.this, MenuPrincipal.class));
                Toast.makeText(Login.this, "Bienvenido(a): " + user.getEmail(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // ================== COMPONENTES INTERNOS ==================

    interface AuthService {
        void login(String email, String password, AuthCallback callback);
    }

    interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    class FirebaseAuthService implements AuthService {
        private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        @Override
        public void login(String email, String password, final AuthCallback callback) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess(firebaseAuth.getCurrentUser());
                        } else {
                            callback.onFailure("Correo o contraseña inválidos.");
                        }
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }
    }

    class LoginValidator {
        public boolean isValidEmail(String email) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        public boolean isValidPassword(String password) {
            return !TextUtils.isEmpty(password);
        }
    }
}