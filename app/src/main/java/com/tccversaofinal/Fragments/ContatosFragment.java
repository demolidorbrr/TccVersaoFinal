package com.tccversaofinal.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Activitys.ConversaActivity;
import com.tccversaofinal.Activitys.ListasActivity;
import com.tccversaofinal.Adaptadores.ContatoAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 21/10/2017.
 */

public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseAuth autenticacao;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();


        //Instânciar objetos
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Monta listview e adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos
        );*/

        adapter = new ContatoAdapter(getActivity(), contatos );
        listView.setAdapter( adapter );

        //Recuperar contatos do firebase
        Preferencias preferencias = new Preferencias(getActivity());
        final String identificadorUsuarioLogado = preferencias.getIdentificadorUsuario();
        final String identificadorProjeto = preferencias.getIdProjeto();

        // firebase = ConfiguracaoFirebase.getFirebase().child("contatos");
        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("contatos");


        //Listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpar lista
                contatos.clear();

                //Listar contatos
                for (DataSnapshot dados: dataSnapshot.getChildren() ){
                    Contato contato = dados.getValue( Contato.class );
                    if(contato.getIdentificadorUsuario().equals(identificadorUsuarioLogado)){

                    }else{
                        contatos.add( contato );

                    }

                    }

                adapter.notifyDataSetChanged();

                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  Intent intent = new Intent(getActivity(), ConversaActivity.class);

                // recupera dados a serem passados
                Contato contato = contatos.get(position);

                // enviando dados para conversa activity
                intent.putExtra("nome", contato.getNome() );
                intent.putExtra("email", contato.getEmail() );

                startActivity(intent);

            }
        });

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener( valueEventListenerContatos );
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener( valueEventListenerContatos );
    }

}
