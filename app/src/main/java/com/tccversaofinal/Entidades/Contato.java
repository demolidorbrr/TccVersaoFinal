package com.tccversaofinal.Entidades;

/**
 * Created by Lucas on 21/10/2017.
 */

public class Contato {

    private String identificadorUsuario;
    private String nome;
    private String email;

    public Contato() {
    }

    public String getIdentificadorUsuario() {
        return identificadorUsuario;
    }

    public void setIdentificadorUsuario(String identificadorUsuario) {
        this.identificadorUsuario = identificadorUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Contato{" +
                "id='" + identificadorUsuario + '\'' +
                ", Nome='" + nome + '\'' +
                ", Email ='" + email + '\'' +
                '}';

    }
}
