package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 16/11/2017.
 */

public class Historico {

    private String id;
    private String data;
    private String descricao;
    private String hora;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Historico() {

    }
}
