package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorMensagensEmGrupo;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Conversa;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Entidades.Mensagem;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ConversaEmGrupoActivity extends AppCompatActivity {


    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private String identificadorUsuario, identificadorProjeto, nomeUsuarioOrigem, hora_atual_vizualizacao;
    private EditText editMensagem;
    private ImageButton botaoEnviarMensagem;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private List<String> listMembrosId;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerMensagem;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa_em_grupo);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa_grupo);
        editMensagem = (EditText) findViewById(R.id.mensagemChatEmGrupo);
        botaoEnviarMensagem = (ImageButton) findViewById(R.id.botaoEnviarMensagemEmGrupo);
        listView = (ListView) findViewById(R.id.lv_conversasEmGrupo);

        pDialog = new ProgressDialog(ConversaEmGrupoActivity.this);
        pDialog.setMessage("Enviando..");

        Preferencias preferencias = new Preferencias(ConversaEmGrupoActivity.this);
        String nomeProjeto = preferencias.getNomeProjeto();
        identificadorUsuario = preferencias.getIdentificadorUsuario();
        identificadorProjeto = preferencias.getIdProjeto();
        nomeUsuarioOrigem = preferencias.getNomeUsuario();


        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        toolbar.setTitle(nomeProjeto);
        setSupportActionBar(toolbar);

        mensagens = new ArrayList<>();
        adapter = new AdaptadorMensagensEmGrupo(ConversaEmGrupoActivity.this, mensagens);
        listMembrosId = new ArrayList<String>();
        listView.setAdapter(adapter);

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("projetos")
                .child(identificadorProjeto)
                .child("mensagensProjeto")
                .child(identificadorUsuario);

        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mensagens.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Mensagem mensagem2 = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem2);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);

        botaoEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.show();

                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaEmGrupoActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }else {

                    Mensagem mensagem = new Mensagem();
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setUsuarioOrigem(identificadorUsuario);
                    mensagem.setNomeUsuarioOrigem(nomeUsuarioOrigem);

                    SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
                    dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
                    Date data = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(data);
                    Date data_atual = cal.getTime();
                    String hora_atual = dateFormat_hora.format(data_atual);

                    mensagem.setHoraEnvio(hora_atual);

                    Date data1 = new Date();
                    SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");
                    String dataFormatada = formatar.format(data1);

                    mensagem.setDataEnvio(dataFormatada);



                    boolean retorno = salvarMensagem(mensagem, identificadorUsuario);

                    if(!retorno){
                        Toast.makeText(ConversaEmGrupoActivity.this, "Problema ao enviar mensagem, Tente novamente!", Toast.LENGTH_SHORT).show();
                    }

                    editMensagem.setText("");

                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ConversaEmGrupoActivity.this, "Deletar?", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }
    private boolean salvarMensagem(final Mensagem mensagem, final String idUsuario) {
        try{
            firebase = ConfiguracaoFirebase.getFirebase();

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();

            databaseReference.child("membroprojeto").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                        MembroProjeto mp = objSnapchot.getValue(MembroProjeto.class);
                        //   System.out.println(mp.getUsuarioID()+" = "+identificadorUsuario);
                        if (mp.getProjetoID().equals(identificadorProjeto)){
                            //  listMembrosId.add(mp.getUsuarioID());
                            firebase.child("projetos")
                                    .child(identificadorProjeto)
                                    .child("mensagensProjeto")
                                    .child(mp.getUsuarioID())
                                    .push()
                                    .setValue(mensagem);
                            // System.out.println("id adicionado ...");
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //    firebase.child("projetos")
            //          .child(identificadorProjeto)
            //           .child("mensagens")
            //         .child(idUsuario)
            //         .push()
            //        .setValue(mensagem);

            pDialog.dismiss();

            Toast.makeText(ConversaEmGrupoActivity.this, "Mensagem Enviada", Toast.LENGTH_SHORT).show();
            return true;

        }catch (Exception e){
            pDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(ConversaEmGrupoActivity.this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
