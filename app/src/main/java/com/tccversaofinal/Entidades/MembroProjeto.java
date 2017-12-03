package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 15/10/2017.
 */

public class MembroProjeto {

    private String dataAtribuicao;
    private String projetoID;
    private String usuarioID;
    private String id;

    public MembroProjeto() {

    }

    public String getDataAtribuicao() {
        return dataAtribuicao;
    }

    public void setDataAtribuicao(String dataAtribuicao) {
        this.dataAtribuicao = dataAtribuicao;
    }

    public String getProjetoID() {
        return projetoID;
    }

    public void setProjetoID(String projetoID) {
        this.projetoID = projetoID;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Membro Projeto{" +
                "Usuario id='" + usuarioID + '\'' +
                ", Projeto ID='" + projetoID + '\'' +
                ", Data ='" + dataAtribuicao + '\'' +
                '}';

    }

}