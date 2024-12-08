package data;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.ITarefa;
import model.Tarefa;

public class RepositorioTarefa implements ITarefa {
    private String arquivo = "tarefa.ser";

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<Tarefa> getAllTarefas() {
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            tarefas = (ArrayList<Tarefa>) ois.readObject();
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado. Criando um novo.");
            salvarTarefas(tarefas); // Cria o arquivo vazio
        } catch (EOFException ex) {
            System.out.println("Arquivo vazio. Nenhuma tarefa carregada.");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erro ao carregar tarefas: " + ex.getMessage());
        }
        return tarefas;
    }

    @Override
    public void criarTarefa(Tarefa tarefa) {
        ArrayList<Tarefa> tarefas = getAllTarefas();
        tarefas.add(tarefa);
        salvarTarefas(tarefas);
    }

    @Override
    public Tarefa lerTarefa(String nome) {
        ArrayList<Tarefa> tarefas = getAllTarefas();
        for (Tarefa t : tarefas) {
            if (t.getNome().equalsIgnoreCase(nome)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void atualizarTarefa(Tarefa tarefa) {
        ArrayList<Tarefa> tarefas = getAllTarefas();
        boolean tarefaAtualizada = false;

        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getId() == tarefa.getId()) {
                tarefas.set(i, tarefa); // Substitui a tarefa existente
                tarefaAtualizada = true;
                break;
            }
        }

        if (!tarefaAtualizada) {
            throw new RuntimeException("Tarefa não encontrada para atualização.");
        }

        salvarTarefas(tarefas); // Salva todas as tarefas no arquivo
    }

    @Override
    public void deletarTarefa(Tarefa tarefa) {
        ArrayList<Tarefa> tarefas = getAllTarefas();
        if (!tarefas.removeIf(t -> t.getId() == tarefa.getId())) {
            throw new RuntimeException("Tarefa não encontrada para exclusão.");
        }
        salvarTarefas(tarefas); // Salva todas as tarefas restantes no arquivo
    }

    private void salvarTarefas(ArrayList<Tarefa> tarefas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject(tarefas);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao salvar tarefas: " + ex.getMessage());
        }
    }
}