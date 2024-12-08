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

public class RepositorioTarefa  implements ITarefa {
    private String arquivo = "tarefa.ser";

    @Override
    public ArrayList<Tarefa> getAllTarefas() {
        ArrayList<Tarefa> tarefas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo)) {
            if (fis.available() > 0) {
                ObjectInputStream ois = null;
                try {
                    while (fis.available() > 0) {
                        ois = new ObjectInputStream(fis);
                        Tarefa t = (Tarefa) ois.readObject();
                        tarefas.add(t);
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (FileNotFoundException ex) {
            // Arquivo não existe, cria vazio
            try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                System.out.println("Arquivo criado vazio.");
            } catch (IOException ioe) {
                throw new RuntimeException("Erro ao criar arquivo vazio: " + ioe.getMessage());
            }
        } catch (EOFException e) {
            // Arquivo está vazio, retorna lista vazia
            System.out.println("Arquivo vazio. Nenhuma tarefa carregada.");
        } catch (IOException ex) {
            System.out.println("Problema ao carregar o arquivo: " + ex.getMessage());
        }
        return tarefas;
    }

    @Override
    public void criarTarefa(Tarefa tarefa) {
        try (FileOutputStream fos = new FileOutputStream(arquivo, true)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tarefa);
            oos.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao salvar a tarefa");
        } catch (IOException ioe){
            throw new RuntimeException("Erro ao salvar a tarefa");
        }
    }

    @Override
    public Tarefa lerTarefa(String nome) {
        ArrayList <Tarefa> tarefas = getAllTarefas();
        Tarefa tarefa = null;
        for (Tarefa t: tarefas) {
            if (t.getNome().equalsIgnoreCase(nome)) {
                tarefa = t;
                break;
            }
        }
        return tarefa;
    }

    public void atualizarArquivoTarefa(ArrayList<Tarefa> tarefas) {
        ObjectOutputStream oos = null;
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            for (Tarefa t : tarefas) {
                oos = new ObjectOutputStream(fos);
                oos.writeObject(t);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar o arquivo: " + e.getMessage());
        }
    }

    @Override
    public void atualizarTarefa(Tarefa tarefa) {
        deletarTarefa(tarefa);
        criarTarefa(tarefa);
        /*ArrayList <Tarefa> tarefas;
        boolean achou=false;
        tarefas = (ArrayList<Tarefa>) getAllTarefas();
        Tarefa t = lerTarefa(tarefa.getId());
        for (int i=0; i < tarefas.size(); i++){
             if (tarefa.getId() == tarefas.get(i).getId()){
                 tarefas.remove(i);
                 tarefas.add(tarefa);
                 atualizarArquivoTarefa(tarefas);
                 achou = true;
                 break;
             }
        }
        if (!achou){
             throw new RuntimeException("Tarefa não encontrada");
        } */
    }

    @Override
    public void deletarTarefa(Tarefa tarefa) {
        ArrayList<Tarefa> tarefas = getAllTarefas();
        boolean removeu = false;
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefa.getId() == tarefas.get(i).getId()) {
                tarefas.remove(i);
                removeu = true;
                break;
            }
        }
        if (!removeu) {
            throw new RuntimeException("Tarefa não encontrada");
        }
        atualizarArquivoTarefa(tarefas);
    }
}