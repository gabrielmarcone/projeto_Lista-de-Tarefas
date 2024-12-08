package model;

import java.io.Serializable;

public class Tarefa implements Serializable {
    //Atributos
    private int id;
    private static int cont;
    private String nome;
    private String descricao;
    private String categoria;
    private boolean isImportante;
    private boolean isFinalizada;

    //Construtor
    public Tarefa(String nome, String descricao, String categoria, boolean isImportante) {
        this.id = ++cont;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.isImportante = isImportante;
        this.isFinalizada = false; //As tarefas sempre são criadas como não finalizadas
    }

    //Getters e Setters
    public int getId() {
        return id;
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
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public boolean isImportante() {
        return isImportante;
    }
    public void setImportante(boolean importante) {
        this.isImportante = importante;
    }
    public boolean isFinalizada() {
        return isFinalizada;
    }
    public void setFinalizada(boolean finalizada) {
        this.isFinalizada = finalizada;
    }

    //Métodos
    @Override
    public String toString() {
        return "Tarefa:" + "\n" +
        " - Nome: " + getNome() + "\n" +
        " - Descrição: " + getDescricao() + "\n" +
        " - Categoria: " + getCategoria() + "\n";
    }
}