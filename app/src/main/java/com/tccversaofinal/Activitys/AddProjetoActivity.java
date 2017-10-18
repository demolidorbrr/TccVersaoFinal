package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.MembroProjeto;
import com.tccversaofinal.Entidades.Projetos;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.R;

import java.util.UUID;

public class AddProjetoActivity extends AppCompatActivity {

    private EditText nomeProjeto, descricaoProjeto;
    private Button botaoAdicionarProjeto, botaoCancelar;
    private Projetos projetos;
    private DatabaseReference firebase;
    private FirebaseAuth autenticacao;
    private Usuarios usuarios = new Usuarios();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_projeto);

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

                    projetos = new Projetos();
                    projetos.setNome(nomeProjeto.getText().toString());
                    projetos.setDescricao(descricaoProjeto.getText().toString());
                    projetos.setUsuarios(usuarios);
                    projetos.setId(String.valueOf(UUID.randomUUID()));


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

            MembroProjeto membro = new MembroProjeto();
            membro.setUsuarioID(usuarios.getId());
            membro.setProjetoID(projetos.getId());

            String id = String.valueOf(UUID.randomUUID());
            //      String id = projetos.getId();

            firebase.child("membroprojeto").child(id).setValue(membro);


            pDialog.dismiss();
            Toast.makeText(AddProjetoActivity.this, "Projeto Adicionado com Sucesso", Toast.LENGTH_SHORT).show();


            return true;

        }catch (Exception e){
            Toast.makeText(AddProjetoActivity.this, "Não foi possivel Adicionar", Toast.LENGTH_SHORT).show();
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
