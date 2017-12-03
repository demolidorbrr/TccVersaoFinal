package com.tccversaofinal.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorFragmentA;
import com.tccversaofinal.Adaptadores.AdaptadorFragmentC;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 17/10/2017.
 */

public class FragmentC extends android.support.v4.app.Fragment{
    private ListView listView;
    private ArrayAdapter<Tarefas> adapter;
    private ArrayList<Tarefas> tarefas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerTarefas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listas, container, false);

        tarefas = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_tarefasAIniciar);

        Preferencias preferencias = new Preferencias(getContext());
        String identificadorProjeto = preferencias.getIdProjeto();
        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("fragmentC");

        valueEventListenerTarefas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tarefas.clear();

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Tarefas tarefa = dados.getValue( Tarefas.class );
                    tarefas.add(tarefa);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        adapter = new AdaptadorFragmentC(getActivity(), tarefas );
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerTarefas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerTarefas);
    }

}
