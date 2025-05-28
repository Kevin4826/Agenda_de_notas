package com.example.proyecto_agenda.NotasImportantes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private RecyclerView recyclerViewNotasImportantes;
    private FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante> firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference misNotasImportantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_archivadas);

        setupActionBar();
        initFirebaseReferences();
        setupRecyclerView();
        setupFirebaseAdapter();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Notas Importantes");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initFirebaseReferences() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            misNotasImportantes = FirebaseDatabase.getInstance()
                    .getReference("Notas_Importantes")
                    .child(user.getUid());
        }
    }

    private void setupRecyclerView() {
        recyclerViewNotasImportantes = findViewById(R.id.RecyclerViewNotasImportantes);
        recyclerViewNotasImportantes.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewNotasImportantes.setLayoutManager(linearLayoutManager);
    }

    private void setupFirebaseAdapter() {
        FirebaseRecyclerOptions<Nota> options = new FirebaseRecyclerOptions.Builder<Nota>()
                .setQuery(misNotasImportantes, Nota.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Importante holder, int position, @NonNull Nota nota) {
                holder.setItemData(getApplicationContext(),
                        String.valueOf(nota.getId()),
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
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_nota_importante, parent, false);
                return new ViewHolder_Nota_Importante(view);
            }
        };

        recyclerViewNotasImportantes.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}