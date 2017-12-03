package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 21/10/2017.
 */

public class Mensagem {

    private String idMensagem;
    private String idUsuario;
    private String mensagem;
    private String dataEnvio;
    private String horaEnvio;
    private String usuarioOrigem;
    private Usuarios usuarioDestino;
    private String nomeUsuarioOrigem;


    public Mensagem() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getHoraEnvio() {
        return horaEnvio;
    }

    public void setHoraEnvio(String horaEnvio) {
        this.horaEnvio = horaEnvio;
    }

    public String getIdMensagem() {
        return idMensagem;
    }

    public void setIdMensagem(String idMensagem) {
        this.idMensagem = idMensagem;
    }

    public String getUsuarioOrigem() {
        return usuarioOrigem;
    }

    public void setUsuarioOrigem(String usuarioOrigem) {
        this.usuarioOrigem = usuarioOrigem;
    }

    public Usuarios getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(Usuarios usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }

    public String getNomeUsuarioOrigem() {
        return nomeUsuarioOrigem;
    }

    public void setNomeUsuarioOrigem(String nomeUsuarioOrigem) {
        this.nomeUsuarioOrigem = nomeUsuarioOrigem;
    }
}
