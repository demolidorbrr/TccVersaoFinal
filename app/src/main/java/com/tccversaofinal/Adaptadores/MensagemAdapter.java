package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tccversaofinal.Entidades.Mensagem;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 25/10/2017.
 */

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está preenchida
        if( mensagens != null ){

            // Recupera dados do usuario remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRementente = preferencias.getIdentificadorUsuario();

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Recupera mensagem
            Mensagem mensagem = mensagens.get( position );

            // Monta view a partir do xml
            if(idUsuarioRementente.equals( mensagem.getIdUsuario() )  ){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
                TextView dataeHoraEnvio = view.findViewById(R.id.hora_vizualizacao);
                dataeHoraEnvio.setText(mensagem.getDataEnvio() + " às " + mensagem.getHoraEnvio());
            }else {
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
                TextView dataEHoraMensagem = view.findViewById(R.id.data_e_hora_envio_mensagem);
                dataEHoraMensagem.setText(mensagem.getDataEnvio() + " às " + mensagem.getHoraEnvio());

            }



            // Recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText( mensagem.getMensagem() );


        }

        return view;

    }


}
