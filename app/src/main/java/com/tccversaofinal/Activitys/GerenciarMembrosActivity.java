package com.tccversaofinal.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorGerenciarMembros;
import com.tccversaofinal.Adaptadores.AdaptadorHistoricoTarefa;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

public class GerenciarMembrosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private DatabaseReference firebase;
    private ArrayList<Contato> todosContatos;
    private ArrayAdapter<Contato> adapterContatos;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseAuth autenticacao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_membros);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            autenticacao.signOut();
            Intent intent = new Intent(GerenciarMembrosActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        listView = (ListView) findViewById(R.id.lista_gerenciar_membros);
        firebase = ConfiguracaoFirebase.getFirebase();
        todosContatos = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbarGerenciarMembros);
        toolbar.setTitle("Membros");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        Preferencias preferencias = new Preferencias(GerenciarMembrosActivity.this);
        String identificadorProjeto = preferencias.getIdProjeto();

        adapterContatos = new AdaptadorGerenciarMembros(GerenciarMembrosActivity.this, todosContatos);
        listView.setAdapter( adapterContatos );

        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("contatos");

        // Cria listener para mensagens
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                todosContatos.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Contato c = dados.getValue( Contato.class );
                    todosContatos.add( c );
                }

                adapterContatos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerContatos );



    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }
}
