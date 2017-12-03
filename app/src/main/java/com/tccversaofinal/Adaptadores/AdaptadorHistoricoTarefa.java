package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 17/11/2017.
 */

public class AdaptadorHistoricoTarefa extends ArrayAdapter<Historico> {

    private Context context;
    private ArrayList<Historico> todosHistorico;
    private DatabaseReference firebase;
    private AlertDialog alerta;

    public AdaptadorHistoricoTarefa(Context c, ArrayList<Historico> objects) {
        super(c, 0, objects);
        this.context = c;
        this.todosHistorico = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if( todosHistorico != null ){

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.item_historico, parent, false);

            // recupera elemento para exibição
            TextView descricaoHistorico = (TextView) view.findViewById(R.id.textHistoricoTarefa);

            Historico historico = todosHistorico.get( position );
            descricaoHistorico.setText( historico.getDescricao());

        }

        return view;

    }


}
