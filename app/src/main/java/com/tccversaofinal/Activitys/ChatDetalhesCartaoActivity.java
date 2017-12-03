package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.AdaptadorMensagensDetalhesCartao;
import com.tccversaofinal.Adaptadores.AdaptadorMensagensEmGrupo;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
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

public class ChatDetalhesCartaoActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private String identificadorUsuario, identificadorProjeto, nomeUsuarioOrigem, hora_atual_vizualizacao;
    private EditText editMensagem;
    private ImageButton botaoEnviarMensagem;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerMensagem;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detalhes_cartao);

        toolbar = (Toolbar) findViewById(R.id.tb_chat_detalhes_cartao);
        editMensagem = (EditText) findViewById(R.id.mensagemChatDetalhesCartao);
        botaoEnviarMensagem = (ImageButton) findViewById(R.id.botaoEnviarMensagemDetalhesCartao);
        listView = (ListView) findViewById(R.id.lv_chatDetalhesTarefa);

        pDialog = new ProgressDialog(ChatDetalhesCartaoActivity.this);
        pDialog.setMessage("Enviando..");

        Preferencias preferencias = new Preferencias(ChatDetalhesCartaoActivity.this);
        String nomeProjeto = preferencias.getNomeProjeto();
        identificadorUsuario = preferencias.getIdentificadorUsuario();
        identificadorProjeto = preferencias.getIdProjeto();
        nomeUsuarioOrigem = preferencias.getNomeUsuario();
        String nomeTarefa = preferencias.getNomeTarefa();
        String nomeLista = preferencias.getNomeLista();
        String idTarefa = preferencias.getidTarefa();

        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        toolbar.setTitle(nomeTarefa);
        setSupportActionBar(toolbar);

        mensagens = new ArrayList<>();
        adapter = new AdaptadorMensagensDetalhesCartao(ChatDetalhesCartaoActivity.this, mensagens);
        listView.setAdapter(adapter);


        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("mensagenstarefa");

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
                Preferencias preferencias = new Preferencias(ChatDetalhesCartaoActivity.this);
                String nomeLista = preferencias.getNomeLista();
                String idTarefa = preferencias.getidTarefa();

                pDialog.show();

                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ChatDetalhesCartaoActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }else {

                    Mensagem mensagem = new Mensagem();
                    String key = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("mensagenstarefa").push().getKey();
                    mensagem.setIdMensagem(key);
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
                        Toast.makeText(ChatDetalhesCartaoActivity.this, "Problema ao enviar mensagem, Tente novamente!", Toast.LENGTH_SHORT).show();
                    }

                    editMensagem.setText("");

                }
            }
        });

        //   listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        //    @Override
        //    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //        Toast.makeText(ChatDetalhesCartaoActivity.this, "Deletar?", Toast.LENGTH_SHORT).show();
        //        return false;
        //    }
        //});
        }

    private boolean salvarMensagem(final Mensagem mensagem, final String idUsuario) {
        try{
            Preferencias preferencias = new Preferencias(ChatDetalhesCartaoActivity.this);
            String nomeLista = preferencias.getNomeLista();
            String idTarefa = preferencias.getidTarefa();

            firebase = ConfiguracaoFirebase.getFirebase();

            firebase = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("mensagenstarefa").child(mensagem.getIdMensagem());

            firebase.setValue(mensagem);


            pDialog.dismiss();

            Toast.makeText(ChatDetalhesCartaoActivity.this, "Mensagem Enviada", Toast.LENGTH_SHORT).show();
            return true;

        }catch (Exception e){
            pDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(ChatDetalhesCartaoActivity.this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
