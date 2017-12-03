package com.tccversaofinal.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tccversaofinal.Activitys.DetalhesCartaoActivity;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;
import com.tccversaofinal.Entidades.Anexo;
import com.tccversaofinal.Entidades.CartaoComentario;
import com.tccversaofinal.Entidades.Contato;
import com.tccversaofinal.Entidades.Historico;
import com.tccversaofinal.Entidades.Lista;
import com.tccversaofinal.Entidades.Tarefas;
import com.tccversaofinal.Helper.Preferencias;
import com.tccversaofinal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Lucas on 19/10/2017.
 */

public class AdaptadorFragmentE extends ArrayAdapter<Tarefas> {
    private ArrayList<Tarefas> tarefas;
    private Context context;
    private LinearLayout leftColor;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerListas2;
    private ArrayList<CharSequence> listas;
    private CharSequence[] vet2;
    private ArrayList<CartaoComentario> todosComentarios;
    private ArrayList<Historico> todosHistorico;
    private ArrayList<Contato> todosResponsaveis;
    private ArrayList<Anexo> todosAnexos;

    public AdaptadorFragmentE(Context c, ArrayList<Tarefas> objects) {
        super(c, 0, objects);
        this.context = c;
        this.tarefas = objects;
        firebase = ConfiguracaoFirebase.getFirebase();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        listas = new ArrayList<>();
        todosComentarios = new ArrayList<>();
        todosHistorico = new ArrayList<>();
        todosResponsaveis = new ArrayList<>();
        todosAnexos = new ArrayList<>();

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        // Monta view a partir do xml
        view = inflater.inflate(R.layout.item_lista, parent, false);

        // recupera elemento para exibição
        TextView nome = (TextView) view.findViewById(R.id.card_nome);
        TextView dataEntrega = (TextView) view.findViewById(R.id.card_data);


        final Tarefas tarefa = tarefas.get(position);
        nome.setText(tarefa.getNome());
        if(tarefa.getDataEntrega() == null){
            dataEntrega.setText("Data de entrega nao definida");
        }else{
            dataEntrega.setText("Data para entrega: " + tarefa.getDataEntrega());
        }

        final Tarefas t = new Tarefas();
        t.setNome(tarefa.getNome());
        t.setDescricao(tarefa.getDescricao());
        t.setId(tarefa.getId());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext(), t.getDescricao(), Toast.LENGTH_SHORT).show();
                //   Toast.makeText(getContext(), "Clicou"+ tarefas.get(position), Toast.LENGTH_SHORT).show();
                Preferencias preferencias = new Preferencias(getContext());
                preferencias.salvarTarefa(t.getNome(), t.getDescricao(), "fragmentE", t.getId());

                Intent intent = new Intent(getContext(), DetalhesCartaoActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();

            }
        });
        Preferencias preferencias = new Preferencias(getContext());
        final String identificadorProjeto = preferencias.getIdProjeto();
        final String nomeUsuario = preferencias.getNomeUsuario();


        firebase.child("projetos").child(identificadorProjeto).child("listas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listas.clear();
                for ( DataSnapshot dados: dataSnapshot.getChildren()){
                    Lista quantidadeListas = dados.getValue( Lista.class );
                    listas.add( quantidadeListas.getNome() );
                    System.out.println(listas);
                }
                vet2 = new CharSequence[listas.size()];
                for(int i=0; i<listas.size(); i++)
                    vet2[i] = listas.get(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final String currentDate = DateFormat.getDateInstance().format(new Date());
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdf.format(date);
        final Historico historico = new Historico();
        String key = firebase.child("projetos").child(identificadorProjeto).child("historico").push().getKey();
        historico.setId(key);
        historico.setData(dateString);
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
        dateFormat_hora.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
        Date data = new Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        final String hora_atual = dateFormat_hora.format(data_atual);




        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                builder.setTitle(view.getResources().getString(R.string.choose_action)).setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        if(which == 0) {
                            //Mover Cartão
                            final int[] categoriaAMover = {0};
                            // CharSequence[] charSequence = {view.getResources().getString(R.string.Ainiciar),
                            //      view.getResources().getString(R.string.EmAndamento),
                            //      view.getResources().getString(R.string.Pausado),
                            //      view.getResources().getString(R.string.Concluido)};


                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle(view.getResources().getString(R.string.choose_category_dialog))
                                    .setSingleChoiceItems(vet2, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Select category
                                            categoriaAMover[0] = which;
                                        }
                                    })
                                    .setPositiveButton(view.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Move card to selected category
                                            Preferencias preferencias = new Preferencias(getContext());
                                            final String identificadorProjeto = preferencias.getIdProjeto();

                                            switch (categoriaAMover[0]) {
                                                case 0:
                                                    firebase = ConfiguracaoFirebase.getFirebase();
                                                    Tarefas t2 = new Tarefas();
                                                    t2.setNome(tarefa.getNome());
                                                    t2.setDescricao(tarefa.getDescricao());
                                                    t2.setId(tarefa.getId());

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentA")
                                                            .child(tarefa.getId())
                                                            .setValue(tarefa);

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosComentarios.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                                                todosComentarios.add(c1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosHistorico.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Historico h1 = dados.getValue( Historico.class );
                                                                todosHistorico.add(h1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosResponsaveis.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Contato contato = dados.getValue( Contato.class );
                                                                todosResponsaveis.add(contato);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosAnexos.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Anexo a = dados.getValue( Anexo.class );
                                                                todosAnexos.add(a);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentA").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentE")
                                                            .child(tarefa.getId())
                                                            .removeValue();

                                                    historico.setHora(hora_atual);
                                                    historico.setDescricao(nomeUsuario + " moveu a tarefa " + tarefa.getNome() + " da lista "+ vet2[4] + " para a lista " + vet2[0] + " em " + historico.getData() + " as " + historico.getHora());
                                                    firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                                    Toast.makeText(getContext(), "Movido com sucesso", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 1:
                                                    firebase = ConfiguracaoFirebase.getFirebase();
                                                    t2 = new Tarefas();
                                                    t2.setNome(tarefa.getNome());
                                                    t2.setDescricao(tarefa.getDescricao());
                                                    t2.setId(tarefa.getId());

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentB")
                                                            .child(tarefa.getId())
                                                            .setValue(tarefa);

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosComentarios.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                                                todosComentarios.add(c1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosHistorico.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Historico h1 = dados.getValue( Historico.class );
                                                                todosHistorico.add(h1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosResponsaveis.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Contato contato = dados.getValue( Contato.class );
                                                                todosResponsaveis.add(contato);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosAnexos.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Anexo a = dados.getValue( Anexo.class );
                                                                todosAnexos.add(a);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentB").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentE")
                                                            .child(tarefa.getId())
                                                            .removeValue();

                                                    historico.setHora(hora_atual);
                                                    historico.setDescricao(nomeUsuario + " moveu a tarefa " + tarefa.getNome() + " da lista "+ vet2[4] + " para a lista " + vet2[1] + " em " + historico.getData() + " as " + historico.getHora());
                                                    firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                                    Toast.makeText(getContext(), "Movido com sucesso", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 2:
                                                    firebase = ConfiguracaoFirebase.getFirebase();
                                                    t2 = new Tarefas();
                                                    t2.setNome(tarefa.getNome());
                                                    t2.setDescricao(tarefa.getDescricao());
                                                    t2.setId(tarefa.getId());

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentC")
                                                            .child(tarefa.getId())
                                                            .setValue(tarefa);

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosComentarios.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                                                todosComentarios.add(c1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosHistorico.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Historico h1 = dados.getValue( Historico.class );
                                                                todosHistorico.add(h1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosResponsaveis.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Contato contato = dados.getValue( Contato.class );
                                                                todosResponsaveis.add(contato);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosAnexos.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Anexo a = dados.getValue( Anexo.class );
                                                                todosAnexos.add(a);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentC").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentE")
                                                            .child(tarefa.getId())
                                                            .removeValue();

                                                    historico.setHora(hora_atual);
                                                    historico.setDescricao(nomeUsuario + " moveu a tarefa " + tarefa.getNome() + " da lista "+ vet2[4] + " para a lista " + vet2[2] + " em " + historico.getData() + " as " + historico.getHora());
                                                    firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                                    Toast.makeText(getContext(), "Movido com sucesso", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 3:
                                                    firebase = ConfiguracaoFirebase.getFirebase();
                                                    t2 = new Tarefas();
                                                    t2.setNome(tarefa.getNome());
                                                    t2.setDescricao(tarefa.getDescricao());
                                                    t2.setId(tarefa.getId());

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentD")
                                                            .child(tarefa.getId())
                                                            .setValue(tarefa);

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosComentarios.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                                                todosComentarios.add(c1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosHistorico.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Historico h1 = dados.getValue( Historico.class );
                                                                todosHistorico.add(h1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosResponsaveis.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Contato contato = dados.getValue( Contato.class );
                                                                todosResponsaveis.add(contato);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosAnexos.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Anexo a = dados.getValue( Anexo.class );
                                                                todosAnexos.add(a);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentD").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentE")
                                                            .child(tarefa.getId())
                                                            .removeValue();

                                                    historico.setHora(hora_atual);
                                                    historico.setDescricao(nomeUsuario + " moveu a tarefa " + tarefa.getNome() + " da lista "+ vet2[4] + " para a lista " + vet2[3] + " em " + historico.getData() + " as " + historico.getHora());
                                                    firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                                    Toast.makeText(getContext(), "Movido com sucesso", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 4:
                                                    Toast.makeText(getContext(), "Sem Mudança", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 5:
                                                    firebase = ConfiguracaoFirebase.getFirebase();
                                                    t2 = new Tarefas();
                                                    t2.setNome(tarefa.getNome());
                                                    t2.setDescricao(tarefa.getDescricao());
                                                    t2.setId(tarefa.getId());

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentF")
                                                            .child(tarefa.getId())
                                                            .setValue(tarefa);

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosComentarios.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                                                todosComentarios.add(c1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosHistorico.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Historico h1 = dados.getValue( Historico.class );
                                                                todosHistorico.add(h1);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosResponsaveis.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Contato contato = dados.getValue( Contato.class );
                                                                todosResponsaveis.add(contato);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase = ConfiguracaoFirebase.getFirebase();


                                                    firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // Limpar mensagens
                                                            todosAnexos.clear();
                                                            // Recupera mensagens
                                                            for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                                                Anexo a = dados.getValue( Anexo.class );
                                                                todosAnexos.add(a);
                                                                firebase.child("projetos").child(identificadorProjeto).child("fragmentF").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    firebase.child("projetos")
                                                            .child(identificadorProjeto)
                                                            .child("fragmentE")
                                                            .child(tarefa.getId())
                                                            .removeValue();

                                                    historico.setHora(hora_atual);
                                                    historico.setDescricao(nomeUsuario + " moveu a tarefa " + tarefa.getNome() + " da lista "+ vet2[4] + " para a lista " + vet2[5] + " em " + historico.getData() + " as " + historico.getHora());
                                                    firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                                    Toast.makeText(getContext(), "Movido com sucesso", Toast.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    })
                                    .setNegativeButton(view.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Cancel dialog
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();


                        } else if(which == 1) {
                            //Editar Cartão
                            Preferencias preferencias = new Preferencias(getContext());
                            final String identificadorProjeto = preferencias.getIdProjeto();
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogLightTheme);
                            final View view = inflater.inflate(R.layout.dialog_editar_cartao, null);

                            mBuilder.setView(view).setPositiveButton(view.getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tarefas tarefa = tarefas.get(position);

                                    final Tarefas t2 = new Tarefas();
                                    t2.setNome(tarefa.getNome());
                                    t2.setDescricao(tarefa.getDescricao());
                                    t2.setId(tarefa.getId());

                                    EditText cartaoNomeNovo = (EditText) view.findViewById(R.id.edtDialogDescricaoCartao);
                                    String cartaoNomeNovo2 = cartaoNomeNovo.getText().toString();

                                    if(cartaoNomeNovo2.equals("")){
                                        Toast.makeText(getContext(), "Digite um novo nome", Toast.LENGTH_LONG).show();

                                    }else {


                                        t2.setNome(cartaoNomeNovo2);

                                        firebase = ConfiguracaoFirebase.getFirebase();
                                        firebase.child("projetos")
                                                .child(identificadorProjeto)
                                                .child("fragmentE")
                                                .child(t2.getId())
                                                .setValue(t2);

                                        historico.setHora(hora_atual);
                                        historico.setDescricao(nomeUsuario + " alterou o nome da tarefa " + tarefa.getNome() + " para " + cartaoNomeNovo2 +  " em " + historico.getData() + " as " + historico.getHora());
                                        firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                                        //    firebase.child("tarefas").child(t2.getId()).setValue(t2);

                                        Toast.makeText(getContext(), "Tarefa Alterada com Sucesso", Toast.LENGTH_LONG).show();

                                    }
                                }
                            })
                                    .setNegativeButton(view.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog dialog2 = mBuilder.create();
                            dialog2.show();


                        } else if(which == 2) {
                            //Mover Para Arquivo
                            Preferencias preferencias = new Preferencias(getContext());
                            final String identificadorProjeto = preferencias.getIdProjeto();
                            Tarefas t2 = new Tarefas();
                            t2 = new Tarefas();
                            t2.setNome(tarefa.getNome());
                            t2.setDescricao(tarefa.getDescricao());
                            t2.setId(tarefa.getId());
                            firebase = ConfiguracaoFirebase.getFirebase();
                            firebase.child("arquivo").child(tarefa.getId()).setValue(tarefa);

                            firebase = ConfiguracaoFirebase.getFirebase();


                            firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Limpar mensagens
                                    todosComentarios.clear();
                                    // Recupera mensagens
                                    for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                        CartaoComentario c1 = dados.getValue( CartaoComentario.class );
                                        todosComentarios.add(c1);
                                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("comentarios").child(c1.getId()).setValue(c1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firebase = ConfiguracaoFirebase.getFirebase();


                            firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("historico").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Limpar mensagens
                                    todosHistorico.clear();
                                    // Recupera mensagens
                                    for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                        Historico h1 = dados.getValue( Historico.class );
                                        todosHistorico.add(h1);
                                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("historico").child(h1.getId()).setValue(h1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firebase = ConfiguracaoFirebase.getFirebase();


                            firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("responsaveis").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Limpar mensagens
                                    todosResponsaveis.clear();
                                    // Recupera mensagens
                                    for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                        Contato contato = dados.getValue( Contato.class );
                                        todosResponsaveis.add(contato);
                                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("responsaveis").child(contato.getIdentificadorUsuario()).setValue(contato);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firebase = ConfiguracaoFirebase.getFirebase();


                            firebase.child("projetos").child(identificadorProjeto).child("fragmentE").child(tarefa.getId()).child("anexos").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Limpar mensagens
                                    todosAnexos.clear();
                                    // Recupera mensagens
                                    for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                                        Anexo a = dados.getValue( Anexo.class );
                                        todosAnexos.add(a);
                                        firebase.child("projetos").child(identificadorProjeto).child("arquivo").child(tarefa.getId()).child("anexos").child(a.getId()).setValue(a);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firebase.child("projetos")
                                    .child(identificadorProjeto)
                                    .child("fragmentE")
                                    .child(tarefa.getId())
                                    .removeValue();
                            //  firebase.child("tarefas").child(tarefa.getId()).removeValue();
                            // firebase.child("tarefasEmAndamento").child(tarefa.getId()).removeValue();
                            //firebase.child("tarefasPausadas").child(tarefa.getId()).removeValue();
                            //firebase.child("tarefasConcluidas").child(tarefa.getId()).removeValue();
                            Toast.makeText(getContext(), "Movido para Arquivo", Toast.LENGTH_LONG).show();
                        }

                        else if(which == 3) {
                            //Deletar Cartão
                            Preferencias preferencias = new Preferencias(getContext());
                            String identificadorProjeto = preferencias.getIdProjeto();

                            firebase = ConfiguracaoFirebase.getFirebase();
                            firebase.child("projetos")
                                    .child(identificadorProjeto)
                                    .child("fragmentE")
                                    .child(tarefa.getId())
                                    .removeValue();

                            historico.setHora(hora_atual);
                            historico.setDescricao(nomeUsuario + " deletou a tarefa " + tarefa.getNome() + " em " + historico.getData() + " as " + historico.getHora());
                            firebase.child("projetos").child(identificadorProjeto).child("historico").child(historico.getId()).setValue(historico);

                            Toast.makeText(getContext(), "Cartão Deletado", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .show();
                return true;
            }
        });

        return view;
    }
}