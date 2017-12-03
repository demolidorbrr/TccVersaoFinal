package com.tccversaofinal.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.tccversaofinal.R;

public class AjudaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        toolbar = (Toolbar) findViewById(R.id.toolbarAjuda);

        toolbar.setTitle("Sobre o aplicativo");
        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
        toolbar.setNavigationIcon(R.mipmap.ic_voltar_menor_branco);
        setSupportActionBar(toolbar);

    }
}
