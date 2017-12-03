package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 29/10/2017.
 */

public class CartaoComentario {
    private String id;
    private String descricao;
    private String dataComentario;
    private Usuarios usuario;
    private String nomeUsuario;
    private String idTarefa;

    public CartaoComentario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataComentario() {
        return dataComentario;
    }

    public void setDataComentario(String dataComentario) {
        this.dataComentario = dataComentario;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(String idTarefa) {
        this.idTarefa = idTarefa;
    }

    @Override
    public String toString() {
        return "CartaoComentario{" +
                "id='" + id + '\'' +
                ", descricao='" + descricao + '\'' +
                ", Data ='" + dataComentario + '\'' +
                '}';

    }
}
