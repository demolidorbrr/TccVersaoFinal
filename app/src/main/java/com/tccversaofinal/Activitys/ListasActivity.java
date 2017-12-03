package com.tccversaofinal.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.MyFragmentPagerAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class ListasActivity extends AppCompatActivity {

    SharedPreferences sPreferences = null;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    com.github.clans.fab.FloatingActionButton adicionarLista, adicionarTarefa, adicionarMembro, iniciarChat;
    private DatabaseReference firebase, firebase2;
    private ValueEventListener valueEventListenerListas, valueEventListenerDemaisUsuarios;
    private ArrayList<String> listas;
    FloatingActionMenu menuFab;
    private String identificadorMembro;
    private FirebaseAuth autenticacao;
    private TextView dataEntregaDaTarefa;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            autenticacao.signOut();
            Intent intent = new Intent(ListasActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        sPreferences = getSharedPreferences("firstRun", MODE_PRIVATE);
        listas = new ArrayList<>();

        menuFab = (FloatingActionMenu) findViewById(R.id.menuFabListas);
        menuFab.setClosedOnTouchOutside(true);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();



        dataEntregaDaTarefa = (TextView) findViewById(R.id.card_data);
        adicionarLista = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addListaFab);
        adicionarTarefa = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addTarefaFab);
        adicionarMembro = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addMembroFab);
        iniciarChat = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.iniciarChatFab);


        Preferencias preferencias = new Preferencias(ListasActivity.this);
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String nomeProjeto = preferencias.getNomeProjeto();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarListas);
        toolbar.setTitle(nomeProjeto);
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);


        //mTabLayout.setBackgroundColor(getResources().getColor(R.color.laranja));



        adicionarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                view = View.inflate(ListasActivity.this, R.layout.dialog_add_tarefa, null);
                final EditText nomeTarefa = (EditText) view.findViewById(R.id.edtDialogNomeTarefa);
                final EditText descricaoTarefa = (EditText) view.findViewById(R.id.edtDialogDescricaoTarefa);

                final int posicao = mTabLayout.getSelectedTabPosition();


                mBuilder.setView(view).setPositiveButton(view.getResources().getString(R.string.Adicionar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(nomeTarefa.getText().toString().equals("") || descricaoTarefa.getText().toString().equals("")){
                            Toast.makeText(ListasActivity.this, "Preencha todos os dados", Toast.LENGTH_SHORT).show();

                        }else {
                            String edtNomeTarefa = nomeTarefa.getText().toString();
                            String edtDescricaoTarefa = descricaoTarefa.getText().toString();

                            Tarefas tarefas = new Tarefas();
                            tarefas.setNome(edtNomeTarefa);
                            tarefas.setDescricao(edtDescricaoTarefa);
                            tarefas.setId(String.valueOf(UUID.randomUUID()));

                            Preferencias preferencias = new Preferencias(ListasActivity.this);
                            String identificadorProjeto = preferencias.getIdProjeto();
                            String nomeUsuario = preferencias.getNomeUsuario();

                            if(posicao == 0){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentA").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }else if(posicao == 1){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentB").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }else if(posicao == 2){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentC").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }else if(posicao == 3){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentD").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }else if(posicao == 4){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentE").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }else if(posicao == 5){
                                firebase = ConfiguracaoFirebase.getFirebase();
                                String key = firebase.child("projetos").child(identificadorProjeto).child("fragmentF").push().getKey();
                                tarefas.setId(key);
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").child(tarefas.getId()).setValue(tarefas);
                                Toast.makeText(ListasActivity.this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                            }

                            final String currentDate = DateFormat.getDateInstance().format(new Date());
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            final String dateString = sdf.format(date);
                            Historico historico = new Historico();
                            String key = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
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
                            historico.setDescricao(nomeUsuario + " adicionou a tarefa " + edtNomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

                            firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                        }
                    }
                })
                        .setNegativeButton(view.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        adicionarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);

                firebase = ConfiguracaoFirebase.getFirebase();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListasActivity.this);


                //Configurações do Dialog
                alertDialog.setTitle("Nova Lista");
                alertDialog.setMessage("Nome da Lista");
                alertDialog.setCancelable(false);

                final EditText editText = new EditText(ListasActivity.this);
                alertDialog.setView( editText );



                alertDialog.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(editText.getText().toString().equals("")){
                            Toast.makeText(ListasActivity.this, "Digite um nome para a lista", Toast.LENGTH_SHORT).show();
                        }else{
                        Lista lista1 = new Lista();
                        lista1.setNome(editText.getText().toString());
                        String key = firebase.child("projetos")
                                             .child(identificadorProjeto)
                                             .child("listas")
                                             .push()
                                             .getKey();
                        //  String key = firebase.child("listas").push().getKey();
                        lista1.setId(key);

                        salvarLista(lista1);
                        }
                    }

                });
                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.create();
                alertDialog.show();

            }

        });

        adicionarMembro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                view = View.inflate(ListasActivity.this, R.layout.dialog_adicionar_usuario, null);
                final EditText emailUsuario = (EditText) view.findViewById(R.id.edtDialogEmailUsuario);

                mBuilder.setView(view).setPositiveButton(view.getResources().getString(R.string.Adicionar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String emailMembro = emailUsuario.getText().toString().trim();
                        identificadorMembro = Base64Custom.codificarBase64(emailMembro);
                            if(identificadorMembro.equals("")){
                            Toast.makeText(ListasActivity.this, "Favor digitar o email", Toast.LENGTH_SHORT).show();
                        }
                        else {
                                firebase = ConfiguracaoFirebase.getFirebase();

                                firebase = firebase.child("usuarios").child(identificadorMembro);

                                firebase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getValue() != null) {

                                            Usuarios usuarioMembro = dataSnapshot.getValue(Usuarios.class);

                                            String nomeUsuarioContato = usuarioMembro.getNome();

                                            Preferencias preferencias = new Preferencias(ListasActivity.this);
                                            String identificadorUsuarioLogado = preferencias.getIdentificadorUsuario();
                                            String nomeUsuario = preferencias.getNomeUsuario();

                                            Preferencias preferencias2 = new Preferencias(ListasActivity.this);
                                            String idProjeto = preferencias2.getIdProjeto();

                                            firebase = ConfiguracaoFirebase.getFirebase();

                                            //     String id = String.valueOf(UUID.randomUUID());
                                            String key = firebase.child("membroprojeto").push().getKey();
                                            String id = key;
                                            firebase = firebase.child("membroprojeto").child(id);

                                            //  firebase = firebase.child("projetos").child(idProjeto).child("membrosprojeto").child(identificadorUsuarioLogado);

                                            MembroProjeto membro = new MembroProjeto();
                                            membro.setProjetoID(idProjeto);
                                            membro.setUsuarioID(identificadorMembro);
                                            membro.setId(id);

                                            firebase.setValue(membro);


                                            Contato contato = new Contato();
                                            contato.setIdentificadorUsuario(identificadorMembro);

                                            contato.setEmail(emailMembro);
                                            contato.setNome(nomeUsuarioContato);

                                            firebase = ConfiguracaoFirebase.getFirebase();

                                            //  firebase.child("contatos").child(contato.getIdentificadorUsuario()).setValue(contato);
                                            firebase.child("projetos").child(identificadorProjeto).child("contatos").child(contato.getIdentificadorUsuario()).setValue(contato);

                                            final String currentDate = DateFormat.getDateInstance().format(new Date());
                                            long date = System.currentTimeMillis();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            final String dateString = sdf.format(date);
                                            Historico historico = new Historico();
                                            String key2 = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
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
                                            historico.setDescricao(nomeUsuario + " adicionou o usuario " + nomeUsuarioContato + " como membro do projeto em " + historico.getData() + " as " + historico.getHora());

                                            firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                            Toast.makeText(ListasActivity.this, "Usuario adicionado com sucesso", Toast.LENGTH_SHORT).show();


                                        } else {
                                            Toast.makeText(ListasActivity.this, "Usuario não possui cadastro", Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                    }
                })
                        .setNegativeButton(view.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        iniciarChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListasActivity.this, ChatActivity.class);
                startActivity(intent);
                Toast.makeText(ListasActivity.this, "Chat Iniciado", Toast.LENGTH_SHORT).show();

            }
        });



        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("listas");

        valueEventListenerListas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    // Limpar mensagens
                listas.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    if( dataSnapshot.getValue() != null ) {
                        Lista quantidadeListas = dados.getValue( Lista.class );
                        listas.add( quantidadeListas.getNome() );
                        System.out.println(listas);
                    }
                }

                String[] vet = new String[listas.size()];
                for(int i=0; i<listas.size(); i++)
                    vet[i] = listas.get(i);

                mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), vet));

                mTabLayout.setupWithViewPager(mViewPager);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //   Toast.makeText(MainActivity.this, tamanho, Toast.LENGTH_SHORT).show();
        firebase.addValueEventListener( valueEventListenerListas );


        //   ArrayList<String> tabs = new ArrayList<>();
        //tabs.add("A Iniciar");
        //tabs.add("Em Andamento");
        //tabs.add("Pausado");
        //tabs.add("Concluido");


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listas, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_menu_gerenciar_listas:
                Intent intent = new Intent(ListasActivity.this, GerenciarListasActivity.class);
                startActivity(intent);
                Toast.makeText(ListasActivity.this, "Gerenciar Listas", Toast.LENGTH_SHORT).show();
                break;
            case R.id.historicoProjeto:
                Intent intent2 = new Intent(ListasActivity.this, HistoricoProjetoActivity.class);
                startActivity(intent2);
                Toast.makeText(ListasActivity.this, "Historico", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_menu_membros_projeto:
                Intent intent3 = new Intent(ListasActivity.this, GerenciarMembrosActivity.class);
                startActivity(intent3);
                Toast.makeText(ListasActivity.this, "Gerenciar Membros", Toast.LENGTH_SHORT).show();
                break;
            case R.id.arquivoProjeto:
                Intent intent6 = new Intent(ListasActivity.this, ArquivoActivity.class);
                startActivity(intent6);
                Toast.makeText(ListasActivity.this, "Tarefas Arquivadas", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ajuda:
                Intent intent4 = new Intent(ListasActivity.this, AjudaActivity.class);
                startActivity(intent4);
                Toast.makeText(ListasActivity.this, "Sobre o aplicativo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                autenticacao.signOut();
                Intent intent5 = new Intent(ListasActivity.this, LoginActivity.class);
                startActivity(intent5);
                finish();
                Toast.makeText(ListasActivity.this, "Saiu", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean salvarLista(Lista lista1) {
        try{
            Preferencias preferencias = new Preferencias(ListasActivity.this);
            final String identificadorProjeto = preferencias.getIdProjeto();
            String nomeUsuario = preferencias.getNomeUsuario();

            if(listas.size() >=6){
                Toast.makeText(ListasActivity.this, "Número maximo de listas atingido", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                firebase.child("projetos").child(identificadorProjeto).child("listas").child(lista1.getId()).setValue(lista1);
                //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

                final String currentDate = DateFormat.getDateInstance().format(new Date());
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final String dateString = sdf.format(date);
                Historico historico = new Historico();
                String key = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
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
                historico.setDescricao(nomeUsuario + " adicionou a lista " + lista1.getNome() + " em " + historico.getData() + " as " + historico.getHora());

                firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                Toast.makeText(ListasActivity.this, "Lista Adicionada com Sucesso", Toast.LENGTH_SHORT).show();

                return true;
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerListas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerListas);
    }
    }




