package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Lucas on 22/11/2017.
 */

public class AdaptadorGerenciarMembros extends ArrayAdapter<Contato> {

    private Context context;
    private ArrayList<Contato> todosContatos;
    private ArrayList<MembroProjeto> todosNosMembroProjeto;
    private DatabaseReference firebase;
    private AlertDialog alerta;

    public AdaptadorGerenciarMembros(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.todosContatos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        firebase = ConfiguracaoFirebase.getFirebase();

        Preferencias preferencias = new Preferencias(context);
        final String usuarioLogado = preferencias.getIdentificadorUsuario();
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String nomeUsuarioLogado = preferencias.getNomeUsuario();


        todosNosMembroProjeto = new ArrayList<>();

        // Verifica se a lista está vazia
        if( todosContatos != null ){

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.item_membro_gerenciar, parent, false);

            // recupera elemento para exibição
            final TextView nomeUsuario = (TextView) view.findViewById(R.id.nome_membro_projeto_gerenciar);
            ImageView deletarUsuario = (ImageView) view.findViewById(R.id.botaoDeletarMembro);

            final Contato contato = todosContatos.get( position );
            nomeUsuario.setText( contato.getNome());

            deletarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Contato contatoADeletar = new Contato();
                    contatoADeletar.setNome(contato.getNome());
                    contatoADeletar.setIdentificadorUsuario(contato.getIdentificadorUsuario());
                    contatoADeletar.setEmail(contato.getEmail());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Deletar Usuario");
                    builder.setMessage("Deseja deletar o usuario " + contatoADeletar.getNome() + " ? ");
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(usuarioLogado.equals(contatoADeletar.getIdentificadorUsuario())){
                                Toast.makeText(context, "Nao foi possivel deletar", Toast.LENGTH_SHORT).show();
                            }else{

                                firebase.child("projetos").child(identificadorProjeto).child("contatos").child(contatoADeletar.getIdentificadorUsuario()).removeValue();

                                //apagar o membro projeto
                                firebase.child("membroprojeto").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        todosNosMembroProjeto.clear();
                                        for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                            MembroProjeto membro = dados.getValue( MembroProjeto.class );
                                            todosNosMembroProjeto.add(membro);
                                            if(membro.getUsuarioID().equals(contatoADeletar.getIdentificadorUsuario())){
                                                firebase.child("membroprojeto").child(membro.getId()).removeValue();

                                            }
                                        }

                                        }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                final String currentDate = DateFormat.getDateInstance().format(new Date());
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                final String dateString = sdf.format(date);
                                final Historico historico = new Historico();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
                                historico.setId(key);
                                historico.setData(dateString);
                                SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
                                dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
                                Date data = new Date();
                                java.util.Calendar cal = java.util.Calendar.getInstance();
                                cal.setTime(data);
                                Date data_atual = cal.getTime();
                                final String hora_atual = dateFormat_hora.format(data_atual);

                                historico.setHora(hora_atual);
                                historico.setDescricao(nomeUsuarioLogado + " removeu o usuario " + contatoADeletar.getNome() + " do projeto em " + historico.getData() + " as " + historico.getHora());

                                firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                Toast.makeText(context, "Membro removido com sucesso", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                    builder.setNegativeButton("NAO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

                    alerta = builder.create();
                    //Exibe
                    alerta.show();

                }
            });
        }

        return view;

    }
}