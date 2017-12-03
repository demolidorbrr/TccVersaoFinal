package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Lucas on 12/11/2017.
 */

public class UsuariosResponsaveisAdapter extends ArrayAdapter<Contato>{
    private Context context;
    private List<Contato> responsaveis;
    private DatabaseReference firebase;
    private AlertDialog alerta;

    public UsuariosResponsaveisAdapter(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.responsaveis = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        // Verifica se a lista está preenchida
        if( responsaveis != null ){

            // Recupera dados do usuario remetente
            //  Preferencias preferencias = new Preferencias(context);
            //   String idUsuarioRementente = preferencias.getIdentificador();

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Recupera mensagem
            final Contato responsavel = responsaveis.get( position );
            Preferencias preferencias = new Preferencias(context);
            final String identificadorProjeto = preferencias.getIdProjeto();
            final String nomeLista = preferencias.getNomeLista();
            final String IdTarefa = preferencias.getidTarefa();
            final String nomeUsuarioLogado = preferencias.getNomeUsuario();
            final String nomeTarefa = preferencias.getNomeTarefa();

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.item_responsaveis, parent, false);

            // Recupera elemento para exibição
            TextView tituloLista = (TextView) view.findViewById(R.id.titulo_lista_responsaveis);
            ImageView deletarResponsavel = (ImageView) view.findViewById(R.id.botaoDeletarResponsavel);

            tituloLista.setText( responsavel.getNome() );

            deletarResponsavel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebase = ConfiguracaoFirebase.getFirebase();
                    Contato responsavelFirebase = responsaveis.get(position);
                    final Contato ContatoPosicao = new Contato();
                    ContatoPosicao.setNome(responsavelFirebase.getNome());
                    ContatoPosicao.setIdentificadorUsuario(responsavelFirebase.getIdentificadorUsuario());
                    ContatoPosicao.setNome(responsavelFirebase.getNome());

                    final String posicao = String.valueOf(position);


                    //Cria o gerador do AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //define o titulo
                    builder.setTitle("Deletar Responsavel");
                    //define a mensagem
                    builder.setMessage("Deseja realmente deletar o responsavel?");
                    //define um botão como positivo
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            final String currentDate = DateFormat.getDateInstance().format(new Date());
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            final String dateString = sdf.format(date);
                            Historico historico = new Historico();
                            String key = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(IdTarefa).child("historico").push().getKey();
                            historico.setId(key);
                            historico.setData(dateString);
                            SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
                            dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
                            Date data = new Date();
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.setTime(data);
                            Date data_atual = cal.getTime();
                            String hora_atual = dateFormat_hora.format(data_atual);
                            historico.setHora(hora_atual);
                            historico.setDescricao(nomeUsuarioLogado + " removeu " + ContatoPosicao.getNome() + " como responsavel em " + historico.getData() + "as" + historico.getHora());

                            firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(IdTarefa).child("historico").child(historico.getId()).setValue(historico);

                            historico.setDescricao(nomeUsuarioLogado + " removeu " + ContatoPosicao.getNome() + " como responsavel na tarefa " + nomeTarefa+ " em " + historico.getData() + " as " + historico.getHora());

                            firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                            firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(IdTarefa).child("responsaveis").child(ContatoPosicao.getIdentificadorUsuario()).removeValue();


                        }
                    });
                    //define um botão como negativo.
                    builder.setNegativeButton("NAO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();

                }
            });

        }

        return view;

    }

}
