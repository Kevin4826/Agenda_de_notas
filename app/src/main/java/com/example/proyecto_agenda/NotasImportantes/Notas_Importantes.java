package com.example.proyecto_agenda.NotasImportantes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.example.proyecto_agenda.ViewHolder.ViewHolder_Nota_Importante;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notas_Importantes extends AppCompatActivity {

    RecyclerView recyclerViewNotasImportantes;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference misNotasImportantes;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> firebaseRecyclerOptions;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_archivadas);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Notas Importantes");
        }

        recyclerViewNotasImportantes = findViewById(R.id.RecyclerViewNotasImportantes);
        recyclerViewNotasImportantes.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        misNotasImportantes = firebaseDatabase.getReference("Notas_Importantes").child(user.getUid());

        listarNotasImportantes();
    }

    private void listarNotasImportantes() {
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Nota>()
                .setQuery(misNotasImportantes, Nota.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Importante viewHolder, int position, @NonNull Nota nota) {
                viewHolder.setItemData(getApplicationContext(),
                        String.valueOf(nota.getId()), // Convertir el ID a String
                        nota.getUidUsuario(),
                        nota.getCorreoUsuario(),
                        nota.getFechaHoraActual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFechaNota(),
                        nota.getEstado());
            }

            @NonNull
            @Override
            public ViewHolder_Nota_Importante onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_importante, parent, false);
                return new ViewHolder_Nota_Importante(view);
            }
        };

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewNotasImportantes.setLayoutManager(linearLayoutManager);
        recyclerViewNotasImportantes.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

}
