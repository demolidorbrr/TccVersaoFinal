package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.tccversaofinal.Activitys.DetalhesCartaoActivity;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Lucas on 02/12/2017.
 */

public class AdaptadorAnexos extends ArrayAdapter<Anexo> {

    private List<Anexo> listaAnexos;
    private Context context;
    private LinearLayout leftColor;
    private DatabaseReference firebase;
    private TextView descricaoComentario, feitoPor;
    private FirebaseAuth autenticacao;
    private Usuarios usuarios = new Usuarios();
    private LinearLayout cardAnexo;
    private AlertDialog.Builder dialog;


    public AdaptadorAnexos(Context c, List<Anexo> objects) {
        super(c, 0, objects);
        this.context = c;
        this.listaAnexos = objects;
        firebase = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarios.setId(autenticacao.getCurrentUser().getUid());
        usuarios.setNome(autenticacao.getCurrentUser().getUid());
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = convertView;


        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);



        // Monta view a partir do xml
        view = inflater.inflate(R.layout.blog_row, parent, false);

        Preferencias preferencias = new Preferencias(context);
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String nomeLista = preferencias.getNomeLista();
        final String idTarefa = preferencias.getidTarefa();
        final String nomeUsuario = preferencias.getNomeUsuario();
        final String nomeTarefa = preferencias.getNomeTarefa();


        final Anexo anexo = listaAnexos.get(position);


        TextView tituloAnexo = (TextView) view.findViewById(R.id.post_title);
        tituloAnexo.setText(anexo.getDescricao());
        String url = anexo.getUrl();

          cardAnexo = (LinearLayout) view.findViewById(R.id.cartaoAnexo);

        cardAnexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(anexo.getUrl()));
                context.startActivity(intent);
            }
        });

        cardAnexo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Remover Anexo");
                dialog.setMessage("Deseja remover o anexo clicado?");
                dialog.setCancelable(true);
                dialog.setNegativeButton("NAO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                //configurar positivo
                dialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebase = ConfiguracaoFirebase.getFirebase();
                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("anexos").child(anexo.getId()).removeValue();
                        Toast.makeText(context, "Anexo deletado com sucesso", Toast.LENGTH_SHORT).show();

                        final String currentDate = DateFormat.getDateInstance().format(new Date());
                        long date = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        final String dateString = sdf.format(date);
                        Historico historico = new Historico();
                        String key2 = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").push().getKey();
                        historico.setId(key2);
                        historico.setData(dateString);
                        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
                        dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
                        Date data = new Date();
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(data);
                        Date data_atual = cal.getTime();
                        String hora_atual = dateFormat_hora.format(data_atual);
                        historico.setHora(hora_atual);
                        historico.setDescricao(nomeUsuario + " excluiu o anexo " + anexo.getDescricao() + " em " + historico.getData() + " as " + historico.getHora());

                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").child(historico.getId()).setValue(historico);
                        //mDatabaseReference.child("anexos").setValue(uploads);

                        historico.setDescricao(nomeUsuario + " excluiu o anexo " + anexo.getDescricao() + " na tarefa " + nomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

                        String key3 = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();

                        historico.setId(key3);
                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                    }
                });

                dialog.create();
                dialog.show();
                return true;
            }
        });

        return view;
    }
}