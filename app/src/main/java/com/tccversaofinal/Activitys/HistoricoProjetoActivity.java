package com.tccversaofinal.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorHistoricoTarefa;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

public class HistoricoProjetoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private DatabaseReference firebase;
    private ArrayList<Historico> todosHistoricos;
    private ArrayAdapter<Historico> adapterHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_projeto);

        listView = (ListView) findViewById(R.id.lista_historico_projeto);
        firebase = ConfiguracaoFirebase.getFirebase();
        todosHistoricos = new ArrayList<>();
        adapterHistorico = new AdaptadorHistoricoTarefa(HistoricoProjetoActivity.this, todosHistoricos);
        listView.setAdapter( adapterHistorico );


        toolbar = (Toolbar) findViewById(R.id.toolbarHistoricoProjeto);
        toolbar.setTitle("Historico Projeto");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        Preferencias preferencias = new Preferencias(HistoricoProjetoActivity.this);
        String identificadorProjeto = preferencias.getIdProjeto();

        firebase.child("projetos").child(identificadorProjeto).child("historico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todosHistoricos.clear();
                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    Historico h = objSnapchot.getValue(Historico.class);
                    todosHistoricos.add(h);
                    System.out.println(h);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(HistoricoProjetoActivity.this, ListasActivity.class);
        startActivity(intent);
        finish();
    }
}
