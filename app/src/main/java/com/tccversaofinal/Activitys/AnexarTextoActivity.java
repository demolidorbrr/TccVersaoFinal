package com.tccversaofinal.Activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Helper.Constants;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AnexarTextoActivity extends AppCompatActivity implements View.OnClickListener{

    //this is the pic pdf code  used in the file chooser
    final static int PICK_TEXT_CODE = 23423;

    //These are the views
    TextView textViewStatus;
    EditText editTextoNome;
    ProgressBar progressBar;
    private Toolbar toolbar;



    //Firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anexar_texto);

        editTextoNome = (EditText) findViewById(R.id.txtNomeDoArquivoTexto);
        textViewStatus = (TextView) findViewById(R.id.anexarTextoStatus);

        progressBar = (ProgressBar) findViewById(R.id.progressbar2);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.btnUploadArquivoTexto).setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbarAnexarTexto);
        toolbar.setTitle("Anexar Texto");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

    }

    //This function will get the pdf from the storage
    private void getTXT() {

        //for greater than lolipop versions we need permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        if (TextUtils.isEmpty(editTextoNome.getText().toString())) {
            editTextoNome.setError("Por favor digite o nome do arquivo");
            return;
        }
        //creating an intent for a file chooser
        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione o Arquivo"), PICK_TEXT_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user chooses the file
        if (requestCode == PICK_TEXT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                uploadFile(data.getData());
            } else {
                Toast.makeText(this, "Nenhum arquivo escolhido", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis());
        sRef.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Preferencias preferencias = new Preferencias(AnexarTextoActivity.this);
                String identificadorProjeto = preferencias.getIdProjeto();
                String nomeLista = preferencias.getNomeLista();
                String idTarefa = preferencias.getidTarefa();
                String nomeUsuario = preferencias.getNomeUsuario();
                String nomeTarefa = preferencias.getNomeTarefa();

                progressBar.setVisibility(View.GONE);
                textViewStatus.setText("Arquivo carregado com sucesso");


                Anexo uploads = new Anexo();
                uploads.setDescricao(editTextoNome.getText().toString().trim()  + " - Texto");
                uploads.setUrl(taskSnapshot.getDownloadUrl().toString());
                String key = mDatabaseReference.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("anexos").push().getKey();
                uploads.setId(key);
                //  mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(uploads);
                mDatabaseReference.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("anexos").child(uploads.getId()).setValue(uploads);
                //mDatabaseReference.child("anexos").setValue(uploads);



                final String currentDate = DateFormat.getDateInstance().format(new Date());
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final String dateString = sdf.format(date);
                Historico historico = new Historico();
                String key2 = mDatabaseReference.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").push().getKey();
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
                historico.setDescricao(nomeUsuario + " anexou um arquivo texto em " + historico.getData() + " as " + historico.getHora());

                mDatabaseReference.child("projetos").child(identificadorProjeto).child(nomeLista).child(idTarefa).child("historico").child(historico.getId()).setValue(historico);

                historico.setDescricao(nomeUsuario + " anexou um arquivo na tarefa " + nomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

                String key3 = mDatabaseReference.child("projetos").child(identificadorProjeto).child("historico").push().getKey();

                historico.setId(key3);
                mDatabaseReference.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {



                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                textViewStatus.setText((int) progress + "% Uploading...");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUploadArquivoTexto:
                getTXT();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(AnexarTextoActivity.this, DetalhesCartaoActivity.class);
        startActivity(intent);
        finish();
    }

}
