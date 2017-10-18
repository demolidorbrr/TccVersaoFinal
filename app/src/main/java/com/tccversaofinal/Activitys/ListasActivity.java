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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Adaptadores.MyFragmentPagerAdapter;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;
import java.util.UUID;

public class ListasActivity extends AppCompatActivity {

    SharedPreferences sPreferences = null;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    com.github.clans.fab.FloatingActionButton adicionarLista, adicionarTarefa;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerListas;
    private ArrayList<String> listas;
    FloatingActionMenu menuFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        sPreferences = getSharedPreferences("firstRun", MODE_PRIVATE);
        listas = new ArrayList<>();

        menuFab = (FloatingActionMenu) findViewById(R.id.menuFabListas);
        menuFab.setClosedOnTouchOutside(true);

        adicionarLista = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addListaFab);
        adicionarTarefa = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addTarefaFab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Listas");
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        Preferencias preferencias = new Preferencias(ListasActivity.this);
        final String identificadorProjeto = preferencias.getIdProjeto();

        adicionarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                view = View.inflate(ListasActivity.this, R.layout.dialog_add_tarefa, null);
                final EditText nomeTarefa = (EditText) view.findViewById(R.id.edtDialogNomeTarefa);
                final EditText descricaoTarefa = (EditText) view.findViewById(R.id.edtDialogDescricaoTarefa);

                mBuilder.setView(view).setPositiveButton(view.getResources().getString(R.string.Adicionar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String edtNomeTarefa = nomeTarefa.getText().toString();
                        String edtDescricaoTarefa = descricaoTarefa.getText().toString();

                        Tarefas tarefas = new Tarefas();
                        tarefas.setNome(edtNomeTarefa);
                        tarefas.setDescricao(edtDescricaoTarefa);
                        tarefas.setId(String.valueOf(UUID.randomUUID()));

                        Preferencias preferencias = new Preferencias(ListasActivity.this);
                        String identificadorProjeto = preferencias.getIdProjeto();
                        firebase = ConfiguracaoFirebase.getFirebase();

                        firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefas.getId()).setValue(tarefas);

                        Toast.makeText(ListasActivity.this, "Tarefa Adicionado com Sucesso", Toast.LENGTH_SHORT).show();

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

        firebase = ConfiguracaoFirebase.getFirebase().child("projetos").child(identificadorProjeto).child("listas");

        valueEventListenerListas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                listas.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Lista quantidadeListas = dados.getValue( Lista.class );
                    listas.add( quantidadeListas.getNome() );
                    System.out.println(listas);
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
            case R.id.menu_gerenciar_listas:
                Intent intent = new Intent(ListasActivity.this, GerenciarListasActivity.class);
                startActivity(intent);
                Toast.makeText(ListasActivity.this, "Gerenciar Listas", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean salvarLista(Lista lista1) {
        try{
            Preferencias preferencias = new Preferencias(ListasActivity.this);
            final String identificadorProjeto = preferencias.getIdProjeto();

            firebase.child("projetos").child(identificadorProjeto).child("listas").child(lista1.getId()).setValue(lista1);
            //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

            Toast.makeText(ListasActivity.this, "Lista Adicionada com Sucesso", Toast.LENGTH_SHORT).show();

            return true;
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




