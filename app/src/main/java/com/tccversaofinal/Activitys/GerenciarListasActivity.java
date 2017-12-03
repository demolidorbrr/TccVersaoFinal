package com.tccversaofinal.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.ListasAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Fragments.FragmentA;
import com.tccversaofinal.Fragments.FragmentB;
import com.tccversaofinal.Fragments.FragmentC;
import com.tccversaofinal.Fragments.FragmentD;
import com.tccversaofinal.Fragments.FragmentE;
import com.tccversaofinal.Fragments.FragmentF;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class GerenciarListasActivity extends AppCompatActivity {

    private ListView listaListas;
    private DatabaseReference firebase;
    private ArrayList<Lista> listas;
    private ArrayList<Tarefas> todasTarefas;
    private ArrayAdapter<Lista> adapter;
    private ValueEventListener valueEventListenerListas;
    private Toolbar toolbar;
    private Button deletarLista;
    private ArrayList<Lista> todasListas;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_listas);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            autenticacao.signOut();
            Intent intent = new Intent(GerenciarListasActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        listaListas = (ListView) findViewById(R.id.lv_listas);
        toolbar = (Toolbar) findViewById(R.id.toolbarGerenciarListas);
        deletarLista = (Button) findViewById(R.id.botaoDeletarListas);

        toolbar.setTitle("Gerenciar Listas");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        // Monta listview e adapter
        todasListas = new ArrayList<>();
        listas = new ArrayList<>();
        todasTarefas = new ArrayList<>();
        adapter = new ListasAdapter(GerenciarListasActivity.this, listas);
        listaListas.setAdapter( adapter );

        firebase = ConfiguracaoFirebase.getFirebase();

        Preferencias preferencias = new Preferencias(GerenciarListasActivity.this);
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String nomeUsuario = preferencias.getNomeUsuario();


        // Recuperar Listas do Firebase
        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("listas");

        // Cria listener para mensagens
        valueEventListenerListas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                listas.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Lista list = dados.getValue( Lista.class );
                    listas.add( list );
                    System.out.println(list);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerListas );


        listaListas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);

                //    Preferencias preferencias = new Preferencias(getContext());
                //  final String identificadorProjeto = preferencias.getIdProjeto();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                view = inflater.inflate(R.layout.dialog_editar_lista, null);

                final View finalView = view;
                mBuilder.setView(view).setPositiveButton(view.getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Lista listinha = listas.get(position);
                        Lista l2 = new Lista();
                        l2.setNome(listinha.getNome());
                        l2.setId(listinha.getId());

                        firebase = ConfiguracaoFirebase.getFirebase();

                        EditText cartaoNomeNovo = (EditText) finalView.findViewById(R.id.edtDialogListaNome);
                        String cartaoNomeNovo2 = cartaoNomeNovo.getText().toString();
                        l2.setNome(cartaoNomeNovo2);

                        //     Toast.makeText(GerenciarListasActivity.this, l2.getNome(), Toast.LENGTH_LONG).show();


                        //  firebase.child("listas").setValue(l2);

                        firebase.child("projetos").child(identificadorProjeto).child("listas").child(l2.getId()).setValue(l2);

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
                        historico.setDescricao(nomeUsuario + " alterou o nome da lista " + listinha.getNome() + " para "+ l2.getNome() + " em " + historico.getData() + " as " + historico.getHora());

                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);


                        Toast.makeText(GerenciarListasActivity.this, "Tarefa Alterada com Sucesso", Toast.LENGTH_LONG).show();


                    }
                })
                        .setNegativeButton(view.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                final AlertDialog dialog2 = mBuilder.create();
                dialog2.show();
            }
        });


        deletarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebase = ConfiguracaoFirebase.getFirebase();

                //
                firebase.child("projetos").child(identificadorProjeto).child("listas").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                            Lista lista = dados.getValue( Lista.class );
                            todasListas.add( lista);
                        }
                        Lista ultima = todasListas.get(todasListas.size() - 1);

                        switch (todasListas.size() - 1){
                            case 0:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").removeValue();
                                break;
                            case 1:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").removeValue();
                                break;
                            case 2:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").removeValue();
                                break;
                            case 3:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").removeValue();
                                break;
                            case 4:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentE").removeValue();
                                break;
                            case 5:
                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").removeValue();
                                break;

                        }
                        firebase.child("projetos").child(identificadorProjeto).child("listas").child(ultima.getId()).removeValue();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
    @Override
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
