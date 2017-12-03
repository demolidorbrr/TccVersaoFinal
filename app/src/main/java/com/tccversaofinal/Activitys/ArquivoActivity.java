package com.tccversaofinal.Activitys;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ArquivoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Preferencias preferencias;
    FirebaseListAdapter adapterArquivo;
    private ListView listView;
    private DatabaseReference firebase, firebase2;
    private AlertDialog alerta;
    private ArrayList<Historico> todosHistorico;
    private ArrayList<Contato> todosResponsaveis;
    private ArrayList<Anexo> todosAnexos;
    private ArrayList<CartaoComentario> todosComentarios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arquivo);

        listView = (ListView) findViewById(R.id.lista_arquivo);
        todosComentarios = new ArrayList<>();
        todosHistorico = new ArrayList<>();
        todosResponsaveis = new ArrayList<>();
        todosAnexos = new ArrayList<>();


        toolbar = (Toolbar) findViewById(R.id.toolbarArquivo);
        toolbar.setTitle("Arquivo");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        preferencias = new Preferencias(ArquivoActivity.this);
        String identificadorProjeto = preferencias.getIdProjeto();
        firebase2 = ConfiguracaoFirebase.getFirebase();

        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("arquivo");


        listaItens();

    }

    private void listaItens() {
        adapterArquivo = new FirebaseListAdapter<Tarefas>(this,Tarefas.class,R.layout.item_arquivo,firebase.orderByChild("id")){

            @Override
            protected void populateView(View v, Tarefas model, int position) {

                  final TextView txtNome = (TextView) v.findViewById(R.id.txtNomeTarefaArquivada);
                  final TextView txtDescricao = (TextView) v.findViewById(R.id.txtDescricaoTarefaArquivada);

                 txtNome.setText(model.getNome());
                 txtDescricao.setText(model.getDescricao());
            }
        };
        listView.setAdapter(adapterArquivo);
        adapterArquivo.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Tarefas model = (Tarefas) parent.getItemAtPosition(position);
                preferencias = new Preferencias(ArquivoActivity.this);
                final String identificadorProjeto = preferencias.getIdProjeto();
                final String nomeUsuarioLogado = preferencias.getNomeUsuario();

                AlertDialog.Builder builder = new AlertDialog.Builder(ArquivoActivity.this);
                builder.setTitle("Resgatar Tarefa");
                builder.setMessage("Deseja resgatar a tarefa selecionada? ");
                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Tarefas tarefa = new Tarefas();
                        tarefa.setId(model.getId());
                        tarefa.setNome(model.getNome());
                        tarefa.setDescricao(model.getDescricao());
                        tarefa.setDataEntrega(model.getDataEntrega());
                        firebase2.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).setValue(tarefa);

                        firebase = ConfiguracaoFirebase.getFirebase();

                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Limpar mensagens
                                todosComentarios.clear();
                                // Recupera mensagens
                                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                    CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                    todosComentarios.add(c1);
                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        firebase = ConfiguracaoFirebase.getFirebase();


                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Limpar mensagens
                                todosHistorico.clear();
                                // Recupera mensagens
                                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                    Historico h1 = dados.getValue( Historico.class );
                                    todosHistorico.add(h1);
                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        firebase = ConfiguracaoFirebase.getFirebase();


                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Limpar mensagens
                                todosResponsaveis.clear();
                                // Recupera mensagens
                                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                    Contato contato = dados.getValue( Contato.class );
                                    todosResponsaveis.add(contato);
                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        firebase = ConfiguracaoFirebase.getFirebase();


                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Limpar mensagens
                                todosAnexos.clear();
                                // Recupera mensagens
                                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                    Anexo a = dados.getValue( Anexo.class );
                                    todosAnexos.add(a);
                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        firebase = ConfiguracaoFirebase.getFirebase();


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
                        historico.setDescricao(nomeUsuarioLogado + " resgatou a tarefa " + tarefa.getNome() + " em " + historico.getData() + " as " + historico.getHora());

                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);


                        firebase2.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).removeValue();


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
}
