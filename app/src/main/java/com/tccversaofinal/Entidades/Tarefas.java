package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 15/10/2017.
 */

public class Tarefas {

    private String id;
    private String nome;
    private String descricao;
    private String dataEntrega;

    public Tarefas() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
        this.dataEntrega = dataEntrega;
    }


    @Override
    public String toString() {
        return "Tarefas{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';

    }
}
