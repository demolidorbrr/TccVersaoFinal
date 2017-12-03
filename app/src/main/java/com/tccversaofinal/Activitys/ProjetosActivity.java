package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorProjetos;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Entidades.Projetos;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjetosActivity extends AppCompatActivity {

    private DatabaseReference firebase, databaseReference, firebase2;
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerView;
    com.github.clans.fab.FloatingActionButton adicionarProjeto;
    private FloatingActionMenu menuFabProjetos;
    private ArrayList<Projetos> listProjetos;
    private ArrayList<String> listProjetosId;
    private Usuarios usuarios = new Usuarios();
    private ProgressDialog pDialog;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener valueEventListenerMembros, valueEventListenerProjetos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);

        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarprojetos);
        toolbar.setTitle("Projetos");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        inicializaFirebase();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        adicionarProjeto = (FloatingActionButton) findViewById(R.id.addProjetoFab);

        menuFabProjetos = (FloatingActionMenu) findViewById(R.id.menuFabProjetos);
        menuFabProjetos.setClosedOnTouchOutside(true);

        String email = autenticacao.getCurrentUser().getEmail();
        final String identificadorUsuario = Base64Custom.codificarBase64(email);

        listProjetos = new ArrayList<>();
        listProjetosId = new ArrayList<>();

        usuarios.setId(identificadorUsuario);

        adicionarProjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjetosActivity.this, AddProjetoActivity.class);
                startActivity(intent);
            }
        });


        firebase = ConfiguracaoFirebase.getFirebase().child("membroprojeto");

        valueEventListenerMembros = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                listProjetosId.clear();

                // Recupera mensagens

                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    MembroProjeto mp = objSnapchot.getValue(MembroProjeto.class);
                    System.out.println(mp.getUsuarioID()+" = "+identificadorUsuario);
                    if (mp.getUsuarioID().equals(identificadorUsuario)){
                        listProjetosId.add(mp.getProjetoID());
                        System.out.println("id adicionado ...");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //    firebase.addValueEventListener( valueEventListenerMembros );


        firebase = ConfiguracaoFirebase.getFirebase().child("projetos");

        valueEventListenerProjetos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                listProjetos.clear();

                // Recupera mensagens

                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    Projetos p = objSnapchot.getValue(Projetos.class);
                    System.out.println(p.getId()+" contido: "+listProjetosId.contains(p.getId()));
                    if (listProjetosId.contains(p.getId())){
                        listProjetos.add(p);
                        System.out.println("projeto adicionado ...");
                    }
                }
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager recyce = new GridLayoutManager(ProjetosActivity.this,2);

                recyclerView.setLayoutManager(recyce);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

            firebase.addValueEventListener( valueEventListenerProjetos );

        recyclerView.setAdapter(new AdaptadorProjetos(listProjetos, this));



    }

    private void inicializaFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            autenticacao.signOut();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase = ConfiguracaoFirebase.getFirebase().child("membroprojeto");
        firebase.addValueEventListener(valueEventListenerMembros);
        firebase = ConfiguracaoFirebase.getFirebase().child("projetos");
        firebase.addValueEventListener(valueEventListenerProjetos);

    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMembros);
        firebase.removeEventListener(valueEventListenerProjetos);


    }

}
