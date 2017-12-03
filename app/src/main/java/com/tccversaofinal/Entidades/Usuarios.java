package com.tccversaofinal.Entidades;

import com.google.firebase.database.DatabaseReference;
import com.tccversaofinal.DAO.ConfiguracaoFirebase;

/**
 * Created by Lucas on 15/10/2017.
 */

public class Usuarios {
    private String id;
    private String Nome;
    private String email;
    private String senha;

    public Usuarios() {
    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child(String.valueOf(getId())).setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "Usuario id='" + id + '\'' +
                ", Nome='" + Nome + '\'' +
                ", Email ='" + email + '\'' +
                '}';

    }
}
