package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Mensagem;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 15/11/2017.
 */

public class AdaptadorMensagensDetalhesCartao extends ArrayAdapter<Mensagem>{

    Context context;
    private ArrayList<Mensagem> mensagens;

    private DatabaseReference firebase;
    private String identificadorUsuario, identificadorProjeto, nomeLista, idTarefa;


    public AdaptadorMensagensDetalhesCartao(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        firebase = ConfiguracaoFirebase.getFirebase();

        Preferencias preferencias = new Preferencias(context);
        identificadorUsuario = preferencias.getIdentificadorUsuario();
        identificadorProjeto = preferencias.getIdProjeto();
        nomeLista = preferencias.getNomeLista();
        idTarefa = preferencias.getidTarefa();


        if(mensagens !=null){

            Preferencias preferencias3 = new Preferencias(context);
            String idUsuarioRemetente = preferencias3.getIdentificadorUsuario();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            Mensagem mensagem = mensagens.get(position);

            if(idUsuarioRemetente.equals(mensagem.getUsuarioOrigem())){

                view = inflater.inflate(R.layout.item_mensagem_direita_conversa_grupo, parent, false);
                TextView horaMensagem = (TextView) view.findViewById(R.id.hora_vizualizacao_conversa_grupo);
                horaMensagem.setText(mensagem.getDataEnvio() + " às " + mensagem.getHoraEnvio());


            }else{

                view = inflater.inflate(R.layout.item_mensagem_esquerda_conversa_grupo, parent, false);

                TextView usuarioMensagem = (TextView) view.findViewById(R.id.tv_nome_usuario_chat_conversa_grupo);
                usuarioMensagem.setText(mensagem.getNomeUsuarioOrigem());

                TextView horaMensagem = (TextView) view.findViewById(R.id.hora_envio_mensagem_conversa_grupo);
                horaMensagem.setText(mensagem.getDataEnvio() + " às " + mensagem.getHoraEnvio());

            }

            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem_conversa_grupo);
            textoMensagem.setText(mensagem.getMensagem());

        }

        return view;
    }

}
