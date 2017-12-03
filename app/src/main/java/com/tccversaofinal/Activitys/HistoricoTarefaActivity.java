package com.tccversaofinal.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorComentariosCartao;
import com.tccversaofinal.Adaptadores.AdaptadorHistoricoTarefa;
import com.tccversaofinal.Adaptadores.ListasAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

public class HistoricoTarefaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private DatabaseReference firebase;
    private ArrayList<Historico> todosHistoricos;
    private ArrayAdapter<Historico> adapterHistorico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_tarefa);

        listView = (ListView) findViewById(R.id.lista_historico);
        firebase = ConfiguracaoFirebase.getFirebase();
        todosHistoricos = new ArrayList<>();
        adapterHistorico = new AdaptadorHistoricoTarefa(HistoricoTarefaActivity.this, todosHistoricos);
        listView.setAdapter( adapterHistorico );

        Preferencias preferencias = new Preferencias(HistoricoTarefaActivity.this);
        String nomeTarefa = preferencias.getNomeTarefa();
        String identificadorProjeto = preferencias.getIdProjeto();
        String nomeLista = preferencias.getNomeLista();
        String idTarefa = preferencias.getidTarefa();


        toolbar = (Toolbar) findViewById(R.id.toolbarHistoricoTarefa);
        toolbar.setTitle("Historico " + nomeTarefa);
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").addValueEventListener(new ValueEventListener() {
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
}
