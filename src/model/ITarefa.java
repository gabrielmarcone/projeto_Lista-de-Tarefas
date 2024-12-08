package model;

import java.util.ArrayList;

public interface ITarefa {
    public ArrayList<Tarefa> getAllTarefas();
    public void criarTarefa (Tarefa tarefa);
    public Tarefa lerTarefa(String nome);
    public void atualizarTarefa(Tarefa tarefa);
    public void deletarTarefa(Tarefa Ttrefa);
}
