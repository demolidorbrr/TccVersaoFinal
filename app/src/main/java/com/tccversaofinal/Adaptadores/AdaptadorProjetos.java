package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.Activitys.ArquivoActivity;
import com.tccversaofinal.Activitys.ListasActivity;
import com.tccversaofinal.Entidades.Projetos;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 15/10/2017.
 */

public class AdaptadorProjetos extends RecyclerView.Adapter<AdaptadorProjetos.MyHoder> {

    private ArrayList<Projetos> listProjetos;
    Context context;
    private AlertDialog alerta;
    private DatabaseReference firebase;


    public AdaptadorProjetos(ArrayList<Projetos> listProjetos, Context context) {
        this.listProjetos = listProjetos;
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_projetos, parent, false);
        //MyHoder myHoder = new MyHoder(view);
        // return myHoder;

        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        Projetos projetos = listProjetos.get(position);
        holder.nome.setText(projetos.getNome());
        holder.descricao.setText(projetos.getDescricao());
    }

    @Override
    public int getItemCount() {
        return listProjetos.size();
    }


    public class MyHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nome, descricao;
        private ItemClickListener itemClickListener;

        public MyHoder(View view) {
            super(view);
            view.setOnClickListener(this);
            nome = (TextView) view.findViewById(R.id.txtViewNome);
            descricao = (TextView) view.findViewById(R.id.txtViewDescricao);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Arquivar Projeto");
                    builder.setMessage("Deseja arquivar o projeto selecionado? ");
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Deseja arquivar o projeto?", Toast.LENGTH_SHORT).show();
                        }
                        });

                builder.setNegativeButton("NAO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                        alerta = builder.create();
                        //Exibe
                alerta.show();
                    return true;
                }
            });
        }


        @Override
        public void onClick(View view) {

            Projetos projetos = listProjetos.get(getAdapterPosition());
            Tarefas t = new Tarefas();
            t.setId(projetos.getId());
            t.setNome(projetos.getNome());
            String id = t.getId();
            String nome = t.getNome();

            Preferencias preferencias2 = new Preferencias(context);
            preferencias2.salvarNomeProjeto(nome);

            //    if (itemClickListener != null) {
            //    itemClickListener.onItemClick(getAdapterPosition());
            // }
            //   view.setOnClickListener(new View.OnClickListener() {
            //      @Override
            //      public void onClick(View view) {

            Preferencias preferencias = new Preferencias(context);
            preferencias.salvarIdProjeto(id);

            Toast.makeText(context, nome, Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(context, ListasActivity.class);
                context.startActivity(intent);
        }
        //      });

        // }

        public void setOnItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


    }
    public interface ItemClickListener {

        void onItemClick(int position);
    }
}
