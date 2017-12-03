package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Usuarios;
import com.tccversaofinal.Helper.Base64Custom;
import com.tccversaofinal.Helper.Conexao;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

public class CadastroActivity extends AppCompatActivity {

    private Button btnCancelar, btnRegistrarUsuario;
    private EditText nomeCadastro, emailCadastro, senhaCadastro, senha2Cadastro;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    static final String TAG = CadastroActivity.class.getSimpleName();
    private Usuarios usuarios;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        if (!Conexao.verificaConexao(this)) {
            Conexao.initToast(this, "Você não tem conexão com internet");
            finish();
        }

        pDialog = new ProgressDialog(CadastroActivity.this);
        pDialog.setMessage("Cadastrando..");


        btnCancelar = (Button) findViewById(R.id.btnCancelarCadastro);
        btnRegistrarUsuario = (Button) findViewById(R.id.btnGravar);
        nomeCadastro = (EditText) findViewById(R.id.edtCadNome);
        emailCadastro = (EditText) findViewById(R.id.edtCadEmail);
        senhaCadastro = (EditText) findViewById(R.id.edtCadSenha);
        senha2Cadastro = (EditText) findViewById(R.id.edtCadSenha2);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.show();
                if(nomeCadastro.getText().toString().equals("")
                        || emailCadastro.getText().toString().equals("")
                        || senhaCadastro.getText().toString().equals("")
                        || senha2Cadastro.getText().toString().equals("")){
                    pDialog.dismiss();
                    Toast.makeText(CadastroActivity.this, "Preecha todos os campos", Toast.LENGTH_SHORT).show();
                }else {
                    usuarios = new Usuarios();
                    usuarios.setNome(nomeCadastro.getText().toString());
                    usuarios.setEmail(emailCadastro.getText().toString());
                    usuarios.setSenha(senhaCadastro.getText().toString());
                    if (usuarios.getSenha().equals(senha2Cadastro.getText().toString())) {
                        cadastrarUsuario();
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(CadastroActivity.this, "Senhas não conferem", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuarios.getEmail(),
                usuarios.getSenha()).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Preferencias preferencias = new Preferencias(CadastroActivity.this);
                    String identificadorUsuarioLogado = Base64Custom.codificarBase64(usuarios.getEmail());

                    preferencias.salvarDados(identificadorUsuarioLogado, usuarios.getNome(), usuarios.getEmail());
                    pDialog.dismiss();
                    Toast.makeText(CadastroActivity.this, "Usuario cadastrado com Sucesso!", Toast.LENGTH_SHORT).show();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuarios.getEmail());
                    //FirebaseUser usuarioFirebase = task.getResult().getUser();
                    usuarios.setId( identificadorUsuario );
                    usuarios.salvar();

                    Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        pDialog.dismiss();
                        erroExcecao = "Digite uma senha mais forte contendo no minimo 8 caracteras de letras e numeros";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        pDialog.dismiss();
                        erroExcecao = "Email digitado invalido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        pDialog.dismiss();
                        erroExcecao = "Email ja cadastrado";
                    } catch (FirebaseAuthInvalidUserException e) {
                        pDialog.dismiss();
                        erroExcecao = "Usuario Invalido";
                    } catch (FirebaseNetworkException e) {
                        pDialog.dismiss();
                        erroExcecao = "Sem Conexão";
                    } catch (Exception e) {
                        pDialog.dismiss();
                        erroExcecao = "Erro ao efetuar Cadastro";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, erroExcecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
