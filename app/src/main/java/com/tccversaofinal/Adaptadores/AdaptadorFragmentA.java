package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 18/10/2017.
 */

public class AdaptadorFragmentA extends ArrayAdapter<Tarefas> {
    private ArrayList<Tarefas> tarefas;
    private Context context;
    private LinearLayout leftColor;
    private DatabaseReference firebase;


    public AdaptadorFragmentA(Context c, ArrayList<Tarefas> objects) {
        super(c, 0, objects);
        this.context = c;
        this.tarefas = objects;
        firebase = ConfiguracaoFirebase.getFirebase();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        // leftColor = view.findViewById(R.id.card_left_color);


        // Verifica se a lista está preenchida
        //  if( tarefas != null ){

        // inicializar objeto para montagem da view
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        // Monta view a partir do xml
        view = inflater.inflate(R.layout.item_lista, parent, false);

        // recupera elemento para exibição
        TextView nome = (TextView) view.findViewById(R.id.card_nome);

        final Tarefas tarefa = tarefas.get(position);
        nome.setText(tarefa.getNome());

        final Tarefas t = new Tarefas();
        t.setNome(tarefa.getNome());
        t.setDescricao(tarefa.getDescricao());
        t.setId(tarefa.getId());

        return view;

        //      }{

    }
}

