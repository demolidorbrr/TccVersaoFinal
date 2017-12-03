package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Projetos;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

public class LoginActivity extends AppCompatActivity {

    private Button botaoLogar;
    private TextView txtIrParaCadastro, txtPerdeuSenha;
    private FirebaseAuth autenticacao;
    private ProgressDialog pDialog;
    private EditText emailLogin, senhaLogin;
    private Usuarios usuarios;
    private ValueEventListener valueEventListenerUsuario;
    private DatabaseReference firebase;
    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        botaoLogar = (Button) findViewById(R.id.btnLogar);
        txtIrParaCadastro = (TextView) findViewById(R.id.txtIrParaCadastro);
        txtPerdeuSenha = (TextView) findViewById(R.id.txtPerdeuSenha);
        emailLogin = (EditText) findViewById(R.id.edtEmailLogin);
        senhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);

        verificarUsuarioLogado();


        txtIrParaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtPerdeuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

                alertDialog.setTitle("Resetar Senha");
                alertDialog.setMessage("Digite o Email Cadastrado");
                alertDialog.setCancelable(false);

                final EditText editEmail = new EditText(LoginActivity.this);
                alertDialog.setView( editEmail );

                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pDialog = new ProgressDialog(LoginActivity.this);
                        pDialog.setMessage("Enviando..");
                        pDialog.show();
                        String email = editEmail.getText().toString().trim();
                        resetSenha(email);
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

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Logando..");
                pDialog.show();

                if(!emailLogin.getText().toString().equals("") && !senhaLogin.getText().toString().equals("")){
                    usuarios = new Usuarios();
                    usuarios.setEmail(emailLogin.getText().toString());
                    usuarios.setSenha(senhaLogin.getText().toString());
                    validarLogin();

                }else {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuarios.getEmail(), usuarios.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuarios.getEmail());


                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorUsuarioLogado);

                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Usuarios usuarioRecuperado = dataSnapshot.getValue(Usuarios.class);

                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.salvarDados(identificadorUsuarioLogado, usuarioRecuperado.getNome(), usuarioRecuperado.getEmail());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);

                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Logado com Sucesso", Toast.LENGTH_SHORT).show();
                    emailLogin.setText("");
                    senhaLogin.setText("");

                    abrirTelaPrincipal();
                } else{
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "E-Mail e/ou Senha não conferem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetSenha(String email) {
        autenticacao.sendPasswordResetEmail(email)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pDialog.dismiss();
                            alert("Um e-mail foi enviado para altera sua senha");
                        }else{
                            pDialog.dismiss();
                            alert("E-mail não registrado");
                        }
                    }
                });
    }

    private void alert(String s) {
        Toast.makeText(LoginActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, ProjetosActivity.class);
        startActivity( intent );
    }
    private void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
             abrirTelaPrincipal();
        }
    }

}
