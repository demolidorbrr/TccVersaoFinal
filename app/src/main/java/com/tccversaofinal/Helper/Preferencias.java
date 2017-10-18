package com.tccversaofinal.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lucas on 15/10/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "whatsapp.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_IDENTIFICADOR_PROJETO = "identificadorProjeto";
    private final String CHAVE_NOME_TAREFA = "nometarefa";
    private final String CHAVE_ID_TAREFA = "idtarefa";
    private final String CHAVE_DESCRICAO_TAREFA = "descricaotarefa";
    private final String CHAVE_NOME_LISTA = "nomeLista";
    private final String CHAVE_NOME_USUARIO = "nomeUsuario";
    private final String CHAVE_NOME_PROJETO = "nomeProjeto";


    public Preferencias( Context contextoParametro){

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE );
        editor = preferences.edit();

    }

    public void salvarDados( String identificadorUsuario, String nomeUsuario ){

        editor.putString(CHAVE_NOME_USUARIO, nomeUsuario);
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.commit();

    }

    public String getIdentificadorUsuario(){
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }

    public String getNomeUsuario(){
        return preferences.getString(CHAVE_NOME_USUARIO, null);
    }

    public void salvarIdProjeto (String idProjeto){
        editor.putString(CHAVE_IDENTIFICADOR_PROJETO, idProjeto);
        editor.commit();
    }

    public String getIdProjeto(){
        return preferences.getString(CHAVE_IDENTIFICADOR_PROJETO, null);
    }
    public void salvarTarefa (String nomeTarefa, String descricaoTarefa, String nomeLista, String idTarefa){
        editor.putString(CHAVE_NOME_TAREFA, nomeTarefa);
        editor.putString(CHAVE_DESCRICAO_TAREFA, descricaoTarefa);
        editor.putString(CHAVE_NOME_LISTA, nomeLista);
        editor.putString(CHAVE_ID_TAREFA, idTarefa);
        editor.commit();
    }

    public String getNomeTarefa(){
        return preferences.getString(CHAVE_NOME_TAREFA, null);
    }
    public String getDescricaoTarefa(){
        return preferences.getString(CHAVE_DESCRICAO_TAREFA, null);
    }
    public String getNomeLista(){
        return preferences.getString(CHAVE_NOME_LISTA, null);
    }
    public String getidTarefa(){
        return preferences.getString(CHAVE_ID_TAREFA, null);
    }

    public void salvarNomeProjeto(String nomeProjeto){
        editor.putString(CHAVE_NOME_PROJETO, nomeProjeto);
        editor.commit();
    }

    public String getNomeProjeto(){
        return preferences.getString(CHAVE_NOME_PROJETO, null);
    }

}
