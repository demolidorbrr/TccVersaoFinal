package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Activitys.GerenciarListasActivity;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 18/10/2017.
 */

public class ListasAdapter extends ArrayAdapter<Lista> {

    private Context context;
    private ArrayList<Lista> listas;
    private DatabaseReference firebase;
    private ArrayList<Tarefas> todasTarefas;
    private AlertDialog alerta;
    private ArrayList<Historico> todosHistorico;
    private ArrayList<Contato> todosResponsaveis;
    private ArrayList<Anexo> todosAnexos;
    private ArrayList<CartaoComentario> todosComentarios;


    public ListasAdapter(Context c, ArrayList<Lista> objects) {
        super(c, 0, objects);
        this.context = c;
        this.listas = objects;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está preenchida
        if( listas != null ){

            // Recupera dados do usuario remetente
            //  Preferencias preferencias = new Preferencias(context);
            //   String idUsuarioRementente = preferencias.getIdentificador();

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Recupera mensagem
            Lista list = listas.get( position );
            todasTarefas = new ArrayList<>();
            todosComentarios = new ArrayList<>();
            todosHistorico = new ArrayList<>();
            todosResponsaveis = new ArrayList<>();
            todosAnexos = new ArrayList<>();


            // Monta view a partir do xml
            view = inflater.inflate(R.layout.item_lista_gerenciador, parent, false);

            // Recupera elemento para exibição
            Button botaoDeletarLista = view.findViewById(R.id.botaoDeletarListas);
            TextView tituloLista = (TextView) view.findViewById(R.id.titulo_lista);
            tituloLista.setText( list.getNome() );
            Preferencias preferencias = new Preferencias(context);
            final String identificadorProjeto = preferencias.getIdProjeto();

        }

        return view;

    }

}
