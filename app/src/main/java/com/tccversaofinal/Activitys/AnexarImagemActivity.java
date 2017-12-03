package com.tccversaofinal.Activitys;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AnexarImagemActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference firebase;
    private ImageView imageView;
    private EditText txtImageName;
    private Uri imgUri;
    private String idProjeto, nomeLista, idTarefa, nomeUsuario, idUsuario, nomeTarefa;
    private Toolbar toolbar;


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anexar_imagem);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        //   firebase = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        firebase = ConfiguracaoFirebase.getFirebase();

        imageView = (ImageView) findViewById(R.id.imageView);
        txtImageName = (EditText) findViewById(R.id.txtNomeDaImagem);

        Preferencias p = new Preferencias(AnexarImagemActivity.this);
        idProjeto = p.getIdProjeto();
        nomeLista = p.getNomeLista();
        idTarefa = p.getidTarefa();
        nomeUsuario = p.getNomeUsuario();
        idUsuario = p.getIdentificadorUsuario();
        nomeTarefa = p.getNomeTarefa();

        toolbar = (Toolbar) findViewById(R.id.toolbarAnexarImagem);
        toolbar.setTitle("Anexar Imagem");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

    }

    public void btnBrowse_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void btnUpload_Click(View v) {
        if (imgUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Carregando Imagem");
            dialog.show();

            //Get the storage reference
            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            //Add file to reference

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    //Dimiss dialog when success
                    dialog.dismiss();
                    //Display success toast msg
                    Toast.makeText(getApplicationContext(), "Imagem Carregada", Toast.LENGTH_SHORT).show();
                    //// ImageUpload imageUpload = new ImageUpload(txtImageName.getText().toString(), taskSnapshot.getDownloadUrl().toString());

                    String key = firebase.child("projetos").child(idProjeto).child(nomeLista).child(idTarefa).child("imagens").push().getKey();

                    Anexo imageUpload = new Anexo();
                    imageUpload.setDescricao(txtImageName.getText().toString().trim() + " - Imagem");
                    imageUpload.setUrl(taskSnapshot.getDownloadUrl().toString());
                    imageUpload.setId(key);
                    imageUpload.setNomeUsuario(nomeUsuario);
                    imageUpload.setIdUsuario(idUsuario);

                    //Save image info in to firebase database
                    //String uploadId = firebase.push().getKey();
                     firebase.child("projetos").child(idProjeto).child(nomeLista).child(idTarefa).child("anexos").child(imageUpload.getId()).setValue(imageUpload);
                    // firebase.child("anexos").child(imageUpload.getId()).setValue(imageUpload);

                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    final String dateString = sdf.format(date);
                    Historico historico = new Historico();
                    String key2 = firebase.child("projetos").child(idProjeto).child(nomeLista).child(idTarefa).child("historico").push().getKey();
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
                    historico.setDescricao(nomeUsuario + " anexou a imagem " + imageUpload.getDescricao() + " em " + historico.getData() + " as " + historico.getHora());

                    firebase.child("projetos").child(idProjeto).child(nomeLista).child(idTarefa).child("historico").child(historico.getId()).setValue(historico);

                    historico.setDescricao(nomeUsuario + " anexou um arquivo na tarefa " + nomeTarefa + " em " + historico.getData() + " as " + historico.getHora());

                    String key3 = firebase.child("projetos").child(idProjeto).child("historico").push().getKey();

                    historico.setId(key3);
                    firebase.child("projetos").child(idProjeto).child("historico").child(historico.getId()).setValue(historico);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dimiss dialog when error
                            dialog.dismiss();
                            //Display err toast msg
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Carregando " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Por favor selecione a imagem", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnShowListImage_Click(View v) {
        // Intent i = new Intent(MainActivity.this, ImageListActivity.class);
        // startActivity(i);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(AnexarImagemActivity.this, DetalhesCartaoActivity.class);
        startActivity(intent);
        finish();
    }


}
