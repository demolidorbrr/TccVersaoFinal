package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Entidades.Projetos;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class AddProjetoActivity extends AppCompatActivity {

    private EditText nomeProjeto, descricaoProjeto;
    private Button botaoAdicionarProjeto, botaoCancelar;
    private Projetos projetos;
    private DatabaseReference firebase;
    private FirebaseAuth autenticacao;
    private Usuarios usuarios = new Usuarios();
    private ProgressDialog pDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_projeto);

        toolbar = (Toolbar) findViewById(R.id.toolbarAddProjeto);
        toolbar.setTitle("Adicionar Projeto ");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializaComponentes();
        eventoClicks();
        String email = autenticacao.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);
        usuarios.setId(identificadorUsuario);

    }

    private void eventoClicks() {
        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddProjetoActivity.this, ProjetosActivity.class);
                startActivity(i);
                finish();
            }
        });

        botaoAdicionarProjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(AddProjetoActivity.this);
                pDialog.setMessage("Adicionando..");
                pDialog.show();
                if(nomeProjeto.getText().toString().equals("")
                        || descricaoProjeto.getText().toString().equals("")){
                    pDialog.dismiss();
                    Toast.makeText(AddProjetoActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    firebase = ConfiguracaoFirebase.getFirebase();
                    projetos = new Projetos();
                    projetos.setNome(nomeProjeto.getText().toString());
                    projetos.setDescricao(descricaoProjeto.getText().toString());
                    projetos.setUsuarios(usuarios);
                    String key = firebase.child("projetos").push().getKey();
                    projetos.setId(key);
                    //    projetos.setId(String.valueOf(UUID.randomUUID()));
                    salvarProjeto(projetos);

                    Intent i = new Intent(AddProjetoActivity.this, ProjetosActivity.class);

                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private boolean salvarProjeto(Projetos projetos) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebase = ConfiguracaoFirebase.getFirebase();
        try{

            String email = autenticacao.getCurrentUser().getEmail();

            String identificadorUsuario = Base64Custom.codificarBase64(email);

            firebase.child("projetos").child(projetos.getId()).setValue(projetos);
            // firebase.child("projetos").setValue(projetos);

            //firebase.child("membroprojeto").child(identificadorUsuario).child(projetos.getId()).setValue(projetos);


            Preferencias preferencias2 = new Preferencias(AddProjetoActivity.this);
            String nomeUsuario = preferencias2.getNomeUsuario();

            final String currentDate = DateFormat.getDateInstance().format(new Date());
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final String dateString = sdf.format(date);
            Historico historico = new Historico();
            String key12 = firebase.child("projetos").child(projetos.getId()).child("historico").push().getKey();
            historico.setId(key12);
            historico.setData(dateString);
            SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
            dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
            Date data = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(data);
            Date data_atual = cal.getTime();
            String hora_atual = dateFormat_hora.format(data_atual);
            historico.setHora(hora_atual);
            historico.setDescricao(nomeUsuario + " criou o projeto " + projetos.getNome() + " em " + historico.getData() + " as " + historico.getHora());

            firebase.child("projetos").child(projetos.getId()).child("historico").child(historico.getId()).setValue(historico);


            MembroProjeto membro = new MembroProjeto();
            membro.setUsuarioID(usuarios.getId());
            membro.setProjetoID(projetos.getId());


            String key10 = firebase.child("projetos").child("membroprojeto").push().getKey();
             String id = key10;

            membro.setId(id);
            //      String id = projetos.getId();

            firebase.child("membroprojeto").child(id).setValue(membro);

            firebase = ConfiguracaoFirebase.getFirebase();
            Lista lista1 = new Lista();
            lista1.setNome("A Iniciar");
            String key = firebase.child("projetos")
                    .child(projetos.getId())
                    .child("listas")
                    .push()
                    .getKey();
            lista1.setId(key);

            Lista lista2 = new Lista();
            lista2.setNome("Em Andamento");
            String key2 = firebase.child("projetos")
                    .child(projetos.getId())
                    .child("listas")
                    .push()
                    .getKey();
            lista2.setId(key2);

            Lista lista3 = new Lista();
            lista3.setNome("Pausado");
            String key3 = firebase.child("projetos")
                    .child(projetos.getId())
                    .child("listas")
                    .push()
                    .getKey();
            lista3.setId(key3);

            Lista lista4 = new Lista();
            lista4.setNome("Concluido");
            String key4 = firebase.child("projetos")
                    .child(projetos.getId())
                    .child("listas")
                    .push()
                    .getKey();
            lista4.setId(key4);

            salvarLista(lista1);
            salvarLista2(lista2);
            salvarLista3(lista3);
            salvarLista3(lista4);


            usuarios.setEmail(autenticacao.getCurrentUser().getEmail());
            Preferencias preferencias = new Preferencias(AddProjetoActivity.this);
            String nomeUsuarioLogado = preferencias.getNomeUsuario();
            usuarios.setNome(nomeUsuarioLogado);

            Contato contato = new Contato();
            contato.setEmail(usuarios.getEmail());
            contato.setNome(usuarios.getNome());
            contato.setIdentificadorUsuario(usuarios.getId());

            firebase = ConfiguracaoFirebase.getFirebase();
            //    firebase.child("contatos").child(usuarios.getId()).setValue(contato);
            firebase.child("projetos").child(projetos.getId()).child("contatos").child(usuarios.getId()).setValue(contato);


            pDialog.dismiss();
            Toast.makeText(AddProjetoActivity.this, "Projeto Adicionado com Sucesso", Toast.LENGTH_SHORT).show();


            return true;

        }catch (Exception e){
            Toast.makeText(AddProjetoActivity.this, "Não foi possivel Adicionar", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarLista(Lista lista1) {
        try{
            firebase.child("projetos").child(projetos.getId()).child("listas").child(lista1.getId()).setValue(lista1);
                //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

                return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean salvarLista2(Lista lista2) {
        try{
            firebase.child("projetos").child(projetos.getId()).child("listas").child(lista2.getId()).setValue(lista2);
            //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean salvarLista3(Lista lista3) {
        try{
            firebase.child("projetos").child(projetos.getId()).child("listas").child(lista3.getId()).setValue(lista3);
            //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean salvarLista4(Lista lista4) {
        try{
            firebase.child("projetos").child(projetos.getId()).child("listas").child(lista4.getId()).setValue(lista4);
            //  firebase.child("listas").child(lista1.getId()).setValue(lista1);

            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    private void inicializaComponentes() {
        nomeProjeto = (EditText) findViewById(R.id.edtNomeProjeto);
        descricaoProjeto = (EditText) findViewById(R.id.edtDescricaoProjeto);
        botaoAdicionarProjeto = (Button) findViewById(R.id.btnAdicionarProjeto);
        botaoCancelar = (Button) findViewById(R.id.btnCancelarNovoProjeto);
    }

}
