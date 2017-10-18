package com.tccversaofinal.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
