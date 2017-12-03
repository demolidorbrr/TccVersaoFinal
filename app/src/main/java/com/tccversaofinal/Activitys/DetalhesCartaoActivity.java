package com.tccversaofinal.Activitys;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tccversaofinal.Adaptadores.AdaptadorAnexos;
import com.tccversaofinal.Adaptadores.AdaptadorComentariosCartao;
import com.tccversaofinal.Adaptadores.AdaptadorProjetos;
import com.tccversaofinal.Adaptadores.UsuariosResponsaveisAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Constants;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class DetalhesCartaoActivity extends AppCompatActivity {

    int PICK_IMAGE_REQUEST = 1011;
    private TextView descricaoTarefa, caminhoCartao, dataEntrega;
    FloatingActionMenu menuFab;
    com.github.clans.fab.FloatingActionButton adicionarResponsavel, adicionarAnexo, adicionarDataEntrega, chatTarefa;
    Calendar mCurrentdata;
    int dia, mes, ano;
    private DatabaseReference firebase, firebase2, firebase3;
    private String dataEntregaFirebase;
    private ImageButton novoComentario;
    private EditText comentarioCartao;
    private String comentario;
    private Usuarios usuarios = new Usuarios();
    private FirebaseAuth autenticacao;
    private ArrayList<CartaoComentario> listComentario;
    private ArrayAdapter<CartaoComentario> adapter;
    private ListView listVComentarios, listaContatos, listaResponsaveis;
    private ArrayList<String> membros;
    private String[] vet;
    private ArrayList<Contato> membrosObjeto;
    private ArrayList<Contato> membrosResponsaveis;
    private ArrayAdapter<Contato> adaptadorUsuariosResponsaveis;
    private ArrayList<String> listas;
    private String nomeListaEditado = "";
    private LinearLayout layoutDescricaoCartao;
    private DatabaseReference mDatabaseReference;
    private ArrayList<Anexo> todosAnexos;
    private RecyclerView listaAnexos;
    private ValueEventListener valueNomeDasListas;
    List<Anexo> uploadList;
    private ArrayAdapter<Anexo> adapterAnexos;
    ListView listViewAnexos;
    private AlertDialog.Builder dialog;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_cartao);

        listaContatos = (ListView) findViewById(R.id.contatosDetalhesActivity);
        comentarioCartao = (EditText) findViewById(R.id.editComentarioCartao);
        novoComentario = (ImageButton) findViewById(R.id.icNovoComentario);
        dataEntrega = (TextView) findViewById(R.id.dataEntregaTarefa);
        descricaoTarefa = (TextView) findViewById(R.id.descricaoCartao);
        caminhoCartao = (TextView) findViewById(R.id.caminhoCartao);
        menuFab = (FloatingActionMenu) findViewById(R.id.menuFabDetalhesCartao);
        adicionarResponsavel = (FloatingActionButton) findViewById(R.id.addMembroFabDetalhesCartao);
        adicionarAnexo = (FloatingActionButton) findViewById(R.id.addAnexoFabDetalhesCartao);
        adicionarDataEntrega = (FloatingActionButton) findViewById(R.id.addDataEntregaDetalhesCartao);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        listComentario = new ArrayList<>();
        listVComentarios = (ListView) findViewById(R.id.lv_comentarios_cartao);
        membros = new ArrayList<>();
        membrosObjeto = new ArrayList<>();
        membrosResponsaveis = new ArrayList<>();
        listaResponsaveis = (ListView) findViewById(R.id.listaResponsaveisCartao);
        listas = new ArrayList<>();
        chatTarefa = (FloatingActionButton) findViewById(R.id.iniciarChatDaTarefa);
        layoutDescricaoCartao = (LinearLayout) findViewById(R.id.layoutDescricaoCartao);
        uploadList = new ArrayList<>();
        listViewAnexos = (ListView) findViewById(R.id.listViewAnexos);


        //  listaAnexos = (RecyclerView) findViewById(R.id.lv_anexos);
        // listaAnexos.setHasFixedSize(true);
        // listaAnexos.setLayoutManager(new LinearLayoutManager(this));

        todosAnexos = new ArrayList<>();

        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            autenticacao.signOut();
            Intent intent = new Intent(DetalhesCartaoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        menuFab.setClosedOnTouchOutside(true);


        mCurrentdata = Calendar.getInstance();
        dia = mCurrentdata.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentdata.get(Calendar.MONTH);
        ano = mCurrentdata.get(Calendar.YEAR);

        mes = mes + 1;


        //  dataEntrega.setText(dia+"/"+mes+"/"+ano);

        Preferencias preferencias = new Preferencias(DetalhesCartaoActivity.this);
        final String nomeTarefa = preferencias.getNomeTarefa();
        final String nomeLista = preferencias.getNomeLista();
        final String descricaoCartao = preferencias.getDescricaoTarefa();
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String idTarefa = preferencias.getidTarefa();
        final String nomeUsuario = preferencias.getNomeUsuario();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCartao);
        toolbar.setTitle(nomeTarefa);
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebase3 = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("listas");

        valueNomeDasListas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

                switch (nomeLista) {
                    case "fragmentA":
                        nomeListaEditado = vet[0];
                        break;
                    case "fragmentB":
                        nomeListaEditado = vet[1];
                        break;
                    case "fragmentC":
                        nomeListaEditado = vet[2];
                        break;
                    case "fragmentD":
                        nomeListaEditado = vet[3];

                        break;
                    case "fragmentE":
                        nomeListaEditado = vet[4];
                        break;
                    case "fragmentF":
                        nomeListaEditado = vet[5];
                        break;
                }
                caminhoCartao.setText("Tarefa " + nomeTarefa + " da lista " + nomeListaEditado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase2 = ConfiguracaoFirebase.getFirebase();


        firebase2.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("anexos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploadList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Anexo upload = postSnapshot.getValue(Anexo.class);
                    uploadList.add(upload);
                    System.out.println();
                }

                adapterAnexos = new AdaptadorAnexos(DetalhesCartaoActivity.this, uploadList);
                listViewAnexos.setAdapter(adapterAnexos);
                setListViewHeightBasedOnItems(listViewAnexos);
                adapterAnexos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        descricaoTarefa.setText(descricaoCartao);

        layoutDescricaoCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogLightTheme);
                v = View.inflate(DetalhesCartaoActivity.this, R.layout.dialog_editar_descricao_tarefa, null);
                final EditText novaDescricaoTarefa = (EditText) v.findViewById(R.id.editNovaDescricaoTarefa);
                novaDescricaoTarefa.setText(descricaoCartao);

                mBuilder.setView(v).setPositiveButton(v.getResources().getString(R.string.Adicionar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String descricao = novaDescricaoTarefa.getText().toString();
                        firebase = ConfiguracaoFirebase.getFirebase();
                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("descricao").setValue(descricao);
                        descricaoTarefa.setText(descricao);

                    }

            })
                    .setNegativeButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            final AlertDialog dialog = mBuilder.create();
                dialog.show();


            }
        });


        final String currentDate = DateFormat.getDateInstance().format(new Date());
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdf.format(date);

        usuarios.setId(autenticacao.getCurrentUser().getUid());
        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Tarefas t = dataSnapshot.getValue(Tarefas.class);
                    dataEntregaFirebase = t.getDataEntrega();
                    if (dataEntregaFirebase == null) {
                        dataEntrega.setText("Data de entrega não definida");
                    } else {
                        System.out.println(t.getDataEntrega());
                        dataEntrega.setText("Data de entrega: " + dataEntregaFirebase);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        firebase = ConfiguracaoFirebase.getFirebase();

        firebase.child("projetos").child(identificadorProjeto).child("contatos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                membros.clear();
                membrosObjeto.clear();
                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    Contato c = objSnapchot.getValue(Contato.class);
                    membrosObjeto.add(c);
                    membros.add(c.getNome());
                    System.out.println(c);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebase = ConfiguracaoFirebase.getFirebase();

        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("responsaveis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                membrosResponsaveis.clear();
                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    Contato c = objSnapchot.getValue(Contato.class);
                    membrosResponsaveis.add(c);
                    System.out.println(c);
                }
                adaptadorUsuariosResponsaveis = new UsuariosResponsaveisAdapter(DetalhesCartaoActivity.this, membrosResponsaveis);
                listaResponsaveis.setAdapter(adaptadorUsuariosResponsaveis);

                ViewGroup.LayoutParams params = listaResponsaveis.getLayoutParams();
                int tamanho = membrosResponsaveis.size();
                int a = 150;
                params.height = a * tamanho;
                listaResponsaveis.setLayoutParams(params);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adicionarResponsavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);

                // setup the alert builder
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetalhesCartaoActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = DetalhesCartaoActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.contatos_detalhes_cartao, null);
                dialogBuilder.setView(dialogView);

                final ListView listView = (ListView) dialogView.findViewById(R.id.contatosDetalhesActivity);


                vet = new String[membros.size()];
                for (int i = 0; i < membros.size(); i++)
                    vet[i] = membros.get(i);

                ArrayAdapter<String> adapterContatos = new ArrayAdapter<String>(DetalhesCartaoActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, vet);


                listView.setAdapter(adapterContatos);


                final AlertDialog alertDialog = dialogBuilder.create();
                Window window = alertDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER); // set alert dialog in center
                // window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); // set alert dialog in Bottom

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // ListView Clicked item index
                        int itemPosition = position;

                        // ListView Clicked item value
                        String itemValue = (String) listView.getItemAtPosition(position);

                        final Contato contato = membrosObjeto.get(position);

                        Contato contato10 = new Contato();

                        contato10.setNome(contato.getNome());
                        contato10.setIdentificadorUsuario(contato.getIdentificadorUsuario());
                        contato10.setEmail(contato.getEmail());

                        Historico historico = new Historico();
                        String key = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").push().getKey();
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
                        historico.setDescricao(nomeUsuario + " adicionou " + contato10.getNome() + " como responsavel em " + historico.getData() + " as " + historico.getHora());


                        firebase = ConfiguracaoFirebase.getFirebase();

                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("responsaveis").child(contato10.getIdentificadorUsuario()).setValue(contato10);

                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").child(historico.getId()).setValue(historico);

                        historico.setDescricao(nomeUsuario + " adicionou " + contato10.getNome() + " como responsavel na tarefa " + nomeTarefa+ " em " + historico.getData() + " as " + historico.getHora());
                        String key2 = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
                        historico.setId(key2);

                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                        //    Toast.makeText(DetalhesCartaoActivity.this, contato10.getIdentificadorUsuario(), Toast.LENGTH_SHORT).show();


                        // Show Alert
                        //  Toast.makeText(getApplicationContext(),
                        //          "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        //          .show();
                        alertDialog.hide();
                    }

                });

                // Cancel Button
                Button cancel_btn = (Button) dialogView.findViewById(R.id.buttoncancellist);
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });

                alertDialog.show();
            }
        });


        adicionarAnexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);

                      AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                 builder.setTitle(view.getResources().getString(R.string.choose_action_detalhes_cartao)).setItems(R.array.actions_array_detalhes_cartao, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = new Intent(DetalhesCartaoActivity.this, AnexarImagemActivity.class);
                        startActivity(intent);
                       }
                       if (which == 1) {
                            Intent intent = new Intent(DetalhesCartaoActivity.this, AnexarPDFActivity.class);
                            startActivity(intent);
                        }
                        if (which == 2) {
                            Intent intent = new Intent(DetalhesCartaoActivity.this, AnexarTextoActivity.class);
                            startActivity(intent);
                        }
                   }
                 })
                     .show();
                }

        });


        adicionarDataEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);
                final Tarefas tarefa = new Tarefas();
                tarefa.setDescricao(descricaoCartao);
                tarefa.setNome(nomeTarefa);
                tarefa.setId(idTarefa);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DetalhesCartaoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int ano, int mesDoAno, int diaDoMes) {
                        mesDoAno = mesDoAno + 1;
                        dataEntrega.setText("Data de entrega: " +diaDoMes + "/" + mesDoAno + "/" + ano);
                        tarefa.setDataEntrega(diaDoMes + "/" + mesDoAno + "/" + ano);
                        firebase = ConfiguracaoFirebase.getFirebase();
                        firebase.child("projetos")
                                .child(identificadorProjeto)
                                .child(nomeLista)
                                .child(idTarefa)
                                .child("dataEntrega")
                                .setValue(tarefa.getDataEntrega());

                        Historico historico = new Historico();
                        String key = firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").push().getKey();
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
                        historico.setDescricao(nomeUsuario + " alterou a data de entrega para " + tarefa.getDataEntrega() + " em " + historico.getData() + " as " + historico.getHora());

                        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").child(historico.getId()).setValue(historico);

                        historico.setDescricao(nomeUsuario + " alterou a data de entrega para " + tarefa.getDataEntrega()+ " na tarefa " + nomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

                        String key2 = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();

                        historico.setId(key2);
                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);
                    }
                }, ano, mes, dia);
                datePickerDialog.show();




            }
        });

        chatTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DetalhesCartaoActivity.this, ChatDetalhesCartaoActivity.class);
                startActivity(intent);
            }
        });

        novoComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comentarioCartao.getText().toString().equals("")) {
                    Toast.makeText(DetalhesCartaoActivity.this, "Digite um comentario", Toast.LENGTH_SHORT).show();
                } else {

                    comentario = comentarioCartao.getText().toString().trim();
                    CartaoComentario cartaoComentario = new CartaoComentario();
                    cartaoComentario.setDescricao(comentario);
                    String key = firebase.child("projetos")
                            .child(identificadorProjeto)
                            .child(nomeLista)
                            .child(idTarefa)
                            .child("comentarios")
                            .push()
                            .getKey();
                    cartaoComentario.setId(key);
                    //  cartaoComentario.setId((String.valueOf(UUID.randomUUID())));
                    cartaoComentario.setDataComentario(dateString);
                    cartaoComentario.setUsuario(usuarios);
                    cartaoComentario.setNomeUsuario(nomeUsuario);
                    cartaoComentario.setIdTarefa(idTarefa);

                    salvarComentario(cartaoComentario);
                    comentarioCartao.setText("");

                }
            }
        });


        firebase = ConfiguracaoFirebase.getFirebase();

        firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("comentarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listComentario.clear();
                for (DataSnapshot objSnapchot : dataSnapshot.getChildren()) {
                    CartaoComentario c = objSnapchot.getValue(CartaoComentario.class);
                    listComentario.add(c);
                    System.out.println(c);
                }

                adapter = new AdaptadorComentariosCartao(DetalhesCartaoActivity.this, listComentario);
                listVComentarios.setAdapter(adapter);
                setListViewHeightBasedOnItems(listVComentarios);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean salvarComentario(CartaoComentario cartaoComentario) {
        final String currentDate = DateFormat.getDateInstance().format(new Date());
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdf.format(date);

        Preferencias preferencias = new Preferencias(DetalhesCartaoActivity.this);
        String identificadorProjeto = preferencias.getIdProjeto();
        String nomeLista = preferencias.getNomeLista();
        String nomeTarefa = preferencias.getNomeTarefa();
        String idTarefa = preferencias.getidTarefa();
        String nomeUsuario = preferencias.getNomeUsuario();
        try {

            firebase = ConfiguracaoFirebase.getFirebase();

            firebase.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("comentarios").child(cartaoComentario.getId()).setValue(cartaoComentario);

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
            historico.setDescricao(nomeUsuario + " adicionou um comentario na tarefa " + nomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

            firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

            Toast.makeText(DetalhesCartaoActivity.this, "Comentario adicionado", Toast.LENGTH_SHORT).show();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {

                float px = 300 * (listView.getResources().getDisplayMetrics().density);

                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhes_cartao, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.historicoDetalhesCartaoMenu:
                Intent intent = new Intent(DetalhesCartaoActivity.this, HistoricoTarefaActivity.class);
                startActivity(intent);
                Toast.makeText(DetalhesCartaoActivity.this, "Historico Tarefa", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(DetalhesCartaoActivity.this, ListasActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //   Preferencias p = new Preferencias(DetalhesCartaoActivity.this);
        // String idProjeto = p.getIdProjeto();
        //String nomeLista = p.getNomeLista();
        //String idTarefa = p.getidTarefa();

        //  firebase2 = ConfiguracaoFirebase.getFirebase().child("projetos").child(idProjeto).child(nomeLista).child(idTarefa).child("anexos");

        // FirebaseRecyclerAdapter<Anexo, BlogViewHolder> firebaseRecycleAdapter = new FirebaseRecyclerAdapter<Anexo, BlogViewHolder>(

        //       Anexo.class,
        //       R.layout.blog_row,
        //        BlogViewHolder.class,
        //       firebase2


        //  ) {
        //      @Override
        //      protected void populateViewHolder(BlogViewHolder viewHolder, Anexo model, int position) {

        //        viewHolder.setTitle(model.getDescricao());
        //         viewHolder.setImage(getApplicationContext(),model.getUrl());

        //      }
        //   };

        //   listaAnexos.setAdapter(firebaseRecycleAdapter);


        firebase3.addValueEventListener(valueNomeDasListas);

    }

//    public static class BlogViewHolder extends RecyclerView.ViewHolder {

    //     View mView;


        //Constrcutor
    //     public BlogViewHolder(View itemView) {
    //           super(itemView);
    //          mView = itemView;
    //      }

    //     public void setTitle(String title) {
    //        TextView post_title = (TextView) mView.findViewById(R.id.post_title);
    //         post_title.setText(title);
    //     }

    //    public void setImage(Context ctx, String image) {
    //         ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            /*Glide.with(ctx)
                    .load(image)
                    .into(post_image);*/
    //       Picasso.with(ctx).load(image).into(post_image);

    //    }

    // }

    @Override
    protected void onStop() {
        super.onStop();
        firebase3.removeEventListener(valueNomeDasListas);

    }


}
