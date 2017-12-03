package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.Activitys.DetalhesCartaoActivity;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.List;

/**
 * Created by Lucas on 29/10/2017.
 */

public class AdaptadorComentariosCartao extends ArrayAdapter<CartaoComentario> {

    private List<CartaoComentario> comentarios;
    private Context context;
    private LinearLayout leftColor;
    private DatabaseReference firebase;
    private TextView descricaoComentario, feitoPor;
    private FirebaseAuth autenticacao;
    private Usuarios usuarios = new Usuarios();
    private CardView card;

    public AdaptadorComentariosCartao(Context c, List<CartaoComentario> objects) {
        super(c, 0, objects);
        this.context = c;
        this.comentarios = objects;
        firebase = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarios.setId(autenticacao.getCurrentUser().getUid());
        usuarios.setNome(autenticacao.getCurrentUser().getUid());
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = convertView;

        Preferencias preferencias = new Preferencias(context);
        final String idUsuarioLogado = preferencias.getIdentificadorUsuario();
        final String nomeUsuario = preferencias.getNomeUsuario();
        final String idProjeto = preferencias.getIdProjeto();
        final String listaNome = preferencias.getNomeLista();
        final String idTarefa = preferencias.getidTarefa();


        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);



        // Monta view a partir do xml
        view = inflater.inflate(R.layout.item_lista_comentario, parent, false);

        descricaoComentario = (TextView) view.findViewById(R.id.card_nome_comentario);
        feitoPor = (TextView) view.findViewById(R.id.card_feito_por_comentario);
        card = (CardView) view.findViewById(R.id.card_comentario);

        final CartaoComentario comentario = comentarios.get(position);



        descricaoComentario.setText(comentario.getDescricao());
        feitoPor.setText("Por " + comentario.getNomeUsuario() + " em " + comentario.getDataComentario());


        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensagem = "Deseja apagar o comentario?";

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                dialog.setTitle( "Atenção" )
                        .setIcon(R.drawable.ic_alert)
                        .setMessage(mensagem)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialoginterface, int i) {
          dialoginterface.cancel();
          }})
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                if(nomeUsuario.equals(comentario.getNomeUsuario())) {
                                    firebase = ConfiguracaoFirebase.getFirebase();
                                    firebase.child("projetos").child(idProjeto).child(listaNome).child(idTarefa).child("comentarios").child(comentario.getId()).removeValue();


                                    Toast.makeText(context, "Comentario removido com sucesso", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Seomente usuario que fez o comentario pode excluir", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();

            }
        });

        return view;

    }
}
