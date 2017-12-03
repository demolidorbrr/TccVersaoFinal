package com.tccversaofinal.Activitys;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.MensagemAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Conversa;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Mensagem;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;
    private ClipboardManager clipManager;
    private ClipData clipData;

    // dados do destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    // dados do rementente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.mensagemChat);
        btMensagem = (ImageButton) findViewById(R.id.botaoEnviarMensagem);
        listView = (ListView) findViewById(R.id.lv_conversas_mensagens);


        // dados do usuário logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificadorUsuario();
        nomeUsuarioRemetente = preferencias.getNomeUsuario();
        final String identificadorProjeto = preferencias.getIdProjeto();


        Bundle extra = getIntent().getExtras();

        if( extra != null ){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64( emailDestinatario );
        }

        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        toolbar.setTitle(nomeUsuarioDestinatario);
        setSupportActionBar(toolbar);


        // Monta listview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter( adapter );


        // Recuperar mensagens do Firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("projetos")
                .child(identificadorProjeto)
                .child("mensagens")
                .child( idUsuarioRemetente )
                .child( idUsuarioDestinatario );

        // Cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                mensagens.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Mensagem mensagem = dados.getValue( Mensagem.class );
                    mensagens.add( mensagem );
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerMensagem );

        // Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = editMensagem.getText().toString();

                if( textoMensagem.isEmpty() ){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
                }else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

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

                    String key = firebase.child("projetos")
                            .child(identificadorProjeto)
                            .child("mensagens")
                            .child(idUsuarioRemetente)
                            .child(idUsuarioDestinatario)
                            .push()
                            .getKey();

                    mensagem.setIdMensagem(key);


                    // salvamos mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario , mensagem );
                    if( !retornoMensagemRemetente ){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {

                        // salvamos mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente , mensagem );
                        if( !retornoMensagemDestinatario ){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao enviar mensagem para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    // salvamos Conversa para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario( idUsuarioDestinatario );
                    conversa.setNome( nomeUsuarioDestinatario );
                    conversa.setMensagem( textoMensagem );
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if( !retornoConversaRemetente ){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        // salvamos Conversa para o Destinatario

                        conversa = new Conversa();
                        conversa.setIdUsuario( idUsuarioRemetente );
                        conversa.setNome( nomeUsuarioRemetente );
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa );
                        if( !retornoConversaDestinatario ){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao salvar conversa para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    editMensagem.setText("");


                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Mensagem mensagem = mensagens.get( position );
                String text = mensagem.getMensagem();
                clipData = ClipData.newPlainText("text", text);
                clipManager.setPrimaryClip(clipData);
                Toast.makeText(ConversaActivity.this, "Texto Copiado", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Mensagem mensagem = mensagens.get( position );

                final AlertDialog.Builder alerta = new AlertDialog.Builder(ConversaActivity.this);
                alerta.setTitle("Alerta");
                alerta.setMessage("Deseja Deletar?");
                alerta.setCancelable(true);
                alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alerta.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Mensagem mensagemClicada = new Mensagem();
                        mensagemClicada.setIdUsuario(mensagem.getIdUsuario());
                        mensagemClicada.setMensagem(mensagem.getMensagem());
                        mensagemClicada.setDataEnvio(mensagem.getDataEnvio());
                        mensagemClicada.setHoraEnvio(mensagem.getHoraEnvio());
                        mensagemClicada.setIdMensagem(mensagem.getIdMensagem());

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

                        int horaAtual2 = hour2min(historico.getHora());
                        int horaAtual3 = hour2min(mensagemClicada.getHoraEnvio())+ 2;

                        String hora_atual_hoje = String.valueOf(horaAtual2);
                        String hora_do_envio_da_mensagem = String.valueOf(horaAtual3);

                        boolean podeSerDeletado = false;

                        if(horaAtual2 > horaAtual3){
                            podeSerDeletado = false;
                        }else{
                            podeSerDeletado = true;
                        }

                        if(podeSerDeletado == false) {
                            Toast.makeText(ConversaActivity.this, "Mensagem so pode ser deletada ate 2 minutos apos envio", Toast.LENGTH_SHORT).show();
                        }
                        else if(historico.getData().equals(mensagemClicada.getDataEnvio()) && podeSerDeletado == true){

                            firebase = ConfiguracaoFirebase.getFirebase();

                            firebase.child("projetos")
                                    .child(identificadorProjeto)
                                    .child("mensagens")
                                    .child(idUsuarioRemetente)
                                    .child(idUsuarioDestinatario)
                                    .child(mensagemClicada.getIdMensagem())
                                    .removeValue();
                            Toast.makeText(ConversaActivity.this, "Mensagem apagada com sucesso", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alerta.show();


                return true;

            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        final String identificadorProjeto = preferencias.getIdProjeto();

        try {


            firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("mensagens");

            firebase.child( idRemetente )
                    .child( idDestinatario )
                    .child(mensagem.getIdMensagem())
                    .setValue( mensagem );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        final String identificadorProjeto = preferencias.getIdProjeto();

        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("conversas");
            firebase.child( idRemetente )
                    .child( idDestinatario )
                    .setValue( conversa );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public int hour2min(String hour) {
        try {
            Date date = new SimpleDateFormat("kk:mm").parse(hour);
            return (int) (TimeUnit.MILLISECONDS.toMinutes(date.getTime())-180);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
