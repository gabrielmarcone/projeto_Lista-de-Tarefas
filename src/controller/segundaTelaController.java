package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Tarefa;
import data.RepositorioTarefa;

public class segundaTelaController implements Initializable {

    @FXML
    private Button buttonAdicionarTarefa;

    @FXML
    private Button buttonImportante;

    @FXML
    private Button buttonBuscarTarefa;

    @FXML
    private Button buttonTarefasPendentes, buttonTarefasRealizadas, buttonLazer,
    buttonCasa, buttonCompras, buttonSaude, buttonTrabalho, buttonEstudos, buttonPessoal;

    @FXML
    private VBox VBoxListaDeTarefas;

    @FXML
    private TextField textFieldBuscarTarefa;

    private String filtroAtual = ""; 

    private ArrayList<Button> botoesCategoria = new ArrayList<>();

    private RepositorioTarefa repositorioTarefa = new RepositorioTarefa();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldBuscarTarefa.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ENTER) {
            handleButtonBuscarTarefa(new ActionEvent());
            }
        });
        limparTextField();
        carregarTarefas();
        inicializarBotoes();
    }

    private void limparTextField(){
        textFieldBuscarTarefa.clear();
    }
    
    // Carregar as tarefas salvas no repositório e gerar os containers estilizados
    private void carregarTarefas() {
        VBoxListaDeTarefas.getChildren().clear(); // Limpar a interface antes de atualizar
        ArrayList<Tarefa> tarefas = repositorioTarefa.getAllTarefas();

        if (tarefas == null || tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada.");
            return; // Não tenta carregar se a lista está vazia
        }

        // Adicionar tarefas não concluídas primeiro
        tarefas.stream()
            .filter(tarefa -> !tarefa.isFinalizada())
            .forEach(tarefa -> VBoxListaDeTarefas.getChildren().add(criarContainerTarefa(tarefa)));
    
        // Adicionar tarefas concluídas depois
        tarefas.stream()
            .filter(Tarefa::isFinalizada)
            .forEach(tarefa -> VBoxListaDeTarefas.getChildren().add(criarContainerTarefa(tarefa)));
        
    }

    private void inicializarBotoes() {
        botoesCategoria.add(buttonLazer);
        botoesCategoria.add(buttonCasa);
        botoesCategoria.add(buttonCompras);
        botoesCategoria.add(buttonSaude);
        botoesCategoria.add(buttonTrabalho);
        botoesCategoria.add(buttonEstudos);
        botoesCategoria.add(buttonPessoal);
        botoesCategoria.add(buttonImportante);
        botoesCategoria.add(buttonTarefasPendentes);
        botoesCategoria.add(buttonTarefasRealizadas);
    }

    private void resetarEstiloBotoes() {
        for (Button botao : botoesCategoria) {
            botao.setStyle("-fx-background-color: #ebf1f5; -fx-background-radius: 5;"); // Cor padrão
        }
        // Estilo original do botão "Importante"
        buttonImportante.setStyle("-fx-background-color: #2f5d75; -fx-background-radius: 5; -fx-text-fill: white;");
    }

    @FXML
    void handleButtonBuscarTarefa(ActionEvent event) {
        String textoBusca = textFieldBuscarTarefa.getText().trim().toLowerCase();

        if (textoBusca.isEmpty() && filtroAtual.isEmpty()) {
            carregarTarefas(); // Mostra todas as tarefas
        } else if (textoBusca.isEmpty()) {
            aplicarFiltroAtual(); // Aplica o filtro atual
        } else {
            VBoxListaDeTarefas.getChildren().clear(); // Limpa a lista de tarefas exibidas
            ArrayList<Tarefa> tarefas = repositorioTarefa.getAllTarefas();

            for (Tarefa tarefa : tarefas) {
                boolean pertenceAoFiltro = filtroAtual.isEmpty() || 
                        (filtroAtual.equals("Pendentes") && !tarefa.isFinalizada()) ||
                        (filtroAtual.equals("Realizadas") && tarefa.isFinalizada()) ||
                        (filtroAtual.equals("Importante") && tarefa.isImportante()) ||
                        (filtroAtual.equals(tarefa.getCategoria()));

                boolean contemBuscaNaOrdem = tarefa.getNome().toLowerCase().startsWith(textoBusca);

                if (pertenceAoFiltro && contemBuscaNaOrdem) {
                    VBoxListaDeTarefas.getChildren().add(criarContainerTarefa(tarefa));
                }
            }
        }
    }
    
    // Criar um container estilizado para exibir as tarefas
    private AnchorPane criarContainerTarefa(Tarefa tarefa) {
        AnchorPane container = new AnchorPane();
        container.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 16; -fx-background-radius: 16;");
        container.setPrefHeight(110); // Altura inicial

        // Título
        if (tarefa.isImportante()) {
            Text estrela = new Text("★");
            estrela.setStyle("-fx-font-size: 20px; -fx-fill: #FFA500;");
            AnchorPane.setTopAnchor(estrela, 5.0);
            AnchorPane.setLeftAnchor(estrela, 70.0);
            container.getChildren().add(estrela);
        }
        Text titulo = new Text(tarefa.getNome());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        titulo.setWrappingWidth(390); // Define a largura máxima para o texto
        AnchorPane.setTopAnchor(titulo, 5.0);
        AnchorPane.setLeftAnchor(titulo, tarefa.isImportante() ? 97.0 : 70.0);

        // Listener para ajustar a altura do container com base no tamanho do texto do título
        titulo.textProperty().addListener((observable, oldValue, newValue) -> {
            double alturaTexto = titulo.getBoundsInLocal().getHeight();
            if (alturaTexto + 40 > container.getPrefHeight()) { // 40 é a margem inicial
                container.setPrefHeight(alturaTexto + 70); // Ajusta a altura dinamicamente
            }
        });
    
        // Categoria
        Text categoria = new Text("Categoria: " + (tarefa.getCategoria().isEmpty() ? "Geral" : tarefa.getCategoria()));
        AnchorPane.setTopAnchor(categoria, 40.0);
        AnchorPane.setLeftAnchor(categoria, 70.0);
        
        // Descrição
        Text descricao = new Text("Descrição: " + tarefa.getDescricao());
        descricao.setWrappingWidth(360); // Define a largura máxima para o texto
        AnchorPane.setTopAnchor(descricao, 70.0);
        AnchorPane.setLeftAnchor(descricao, 70.0);
    
        // Listener para ajustar a altura do container com base no tamanho do texto
        descricao.textProperty().addListener((observable, oldValue, newValue) -> {
            double alturaTexto = descricao.getBoundsInLocal().getHeight();
            if (alturaTexto + 70 > container.getPrefHeight()) { // 70 é a margem inicial
                container.setPrefHeight(alturaTexto + 80); // 80 inclui o espaço extra para evitar corte
            }
        });
    
        // CheckBox para marcar como concluída
        CheckBox checkConcluir = new CheckBox("");
        checkConcluir.setSelected(tarefa.isFinalizada());
        AnchorPane.setTopAnchor(checkConcluir, 33.0);
        AnchorPane.setLeftAnchor(checkConcluir, 10.0);
    
        checkConcluir.setOnAction(event -> {
        if(checkConcluir.isSelected()){
            try {
                // Corrigir o caminho do recurso
                URL audioPath = getClass().getResource("/icons/task_check.wav");
                if (audioPath == null) {
                    throw new FileNotFoundException("Arquivo de áudio não encontrado: /icons/task_check.wav");
                }
        
                // Abrir o áudio diretamente a partir do recurso
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        // Atualizar tarefa
        tarefa.setFinalizada(checkConcluir.isSelected());
        repositorioTarefa.atualizarTarefa(tarefa);
        carregarTarefas();
    });

    
        // Botão para remover tarefa
        Button buttonRemover = new Button("Remover");
        buttonRemover.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        AnchorPane.setTopAnchor(buttonRemover, 10.0);
        AnchorPane.setRightAnchor(buttonRemover, 10.0);
    
        buttonRemover.setOnAction(event -> {
        // Mostrar diálogo de confirmação
        try {
            // Corrigir o caminho do recurso
            URL audioPath = getClass().getResource("/icons/remove.wav");
            if (audioPath == null) {
                throw new FileNotFoundException("Arquivo de áudio não encontrado: /icons/task_check.wav");
            }
    
            // Abrir o áudio diretamente a partir do recurso
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Remoção");
            alert.setHeaderText("Deseja realmente remover esta tarefa?");
            alert.setContentText("Esta ação não poderá ser desfeita.");

            // Obter a resposta do usuário
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    repositorioTarefa.deletarTarefa(tarefa); // Remover do repositório
                    carregarTarefas(); // Atualizar a interface
                }
            });
        });
    
        // Botão para editar tarefa
        Button buttonEditar = new Button("  Editar   ");
        buttonEditar.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white;");
        AnchorPane.setBottomAnchor(buttonEditar, 10.0);
        AnchorPane.setRightAnchor(buttonEditar, 10.0);
    
        buttonEditar.setOnAction(event -> {
            try {
                URL audioPath = getClass().getResource("/icons/click.wav");
                if (audioPath == null) {
                    throw new FileNotFoundException("Arquivo de áudio não encontrado: /icons/task_check.wav");
                }
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            abrirTelaEdicao(tarefa);
        });

    
        // Adiciona elementos ao container
        container.getChildren().addAll(titulo, descricao, categoria, checkConcluir, buttonRemover, buttonEditar);
        return container;
    }

    @FXML
    void handleButtonAdicionarTarefa(ActionEvent event) {
        try {
            URL audioPath = getClass().getResource("/icons/click.wav");
            if (audioPath == null) {
                throw new FileNotFoundException("Arquivo de áudio não encontrado: /icons/task_check.wav");
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Corrigindo o caminho do FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cadastroTela.fxml"));
            AnchorPane page = loader.load();
            
            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastrar Tarefa");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Impede interação com a janela principal enquanto essa estiver aberta.
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Configura o controlador da nova tela.
            cadastroTelaController controller = loader.getController();
            controller.setDialogStage(dialogStage); // Define o estágio do diálogo para o controlador.
            
            // Exibe a nova tela.
            dialogStage.showAndWait();

            carregarTarefas();
            
        } catch (IOException ex) {
            Logger.getLogger(segundaTelaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void handleButtonCategoria(ActionEvent event) {
        String categoriaSelecionada = "";

        if (event.getSource() == buttonLazer) {
            categoriaSelecionada = "Lazer";
        } else if (event.getSource() == buttonCasa) {
            categoriaSelecionada = "Casa";
        } else if (event.getSource() == buttonCompras) {
            categoriaSelecionada = "Compras";
        } else if (event.getSource() == buttonSaude) {
            categoriaSelecionada = "Saúde";
        } else if (event.getSource() == buttonTrabalho) {
            categoriaSelecionada = "Trabalho";
        } else if (event.getSource() == buttonEstudos) {
            categoriaSelecionada = "Estudos";
        } else if (event.getSource() == buttonPessoal) {
            categoriaSelecionada = "Pessoal";
        } else if (event.getSource() == buttonImportante) {
            categoriaSelecionada = "Importante";
        }

        // Atualiza o filtro atual
        if (filtroAtual.equals(categoriaSelecionada)) {
            filtroAtual = ""; // Remove o filtro
            resetarEstiloBotoes(); // Remove o destaque
            carregarTarefas(); // Recarrega todas as tarefas
        } else {
            filtroAtual = categoriaSelecionada; // Aplica o novo filtro
            resetarEstiloBotoes(); // Reseta estilos
            if (event.getSource() == buttonImportante) {
                ((Button) event.getSource()).setStyle("-fx-background-color: #00CED1; -fx-background-radius: 5; -fx-text-fill: white;"); // Cor especial para "Importante"
            } else {
                ((Button) event.getSource()).setStyle("-fx-background-color: #2f5d75; -fx-background-radius: 5; -fx-text-fill: white;"); // Destaque padrão
            }
            aplicarFiltroAtual(); // Aplica o filtro
        }

        handleButtonBuscarTarefa(null); // Atualiza a busca dentro do filtro
    }
    
    @FXML
    void handleButtonTarefasRealizadas(ActionEvent event) {
        if (filtroAtual.equals("Realizadas")) {
            filtroAtual = ""; // Remove o filtro
            resetarEstiloBotoes();
            carregarTarefas();
        } else {
            filtroAtual = "Realizadas"; // Aplica o filtro
            resetarEstiloBotoes();
            buttonTarefasRealizadas.setStyle("-fx-background-color: #2f5d75; -fx-background-radius: 5; -fx-text-fill: white;"); // Destaca o botão
            aplicarFiltroAtual();
        }
    }

    @FXML
    void handleButtonTarefasNaoRealizadas(ActionEvent event) {
        if (filtroAtual.equals("Pendentes")) {
            filtroAtual = ""; // Remove o filtro
            resetarEstiloBotoes();
            carregarTarefas();
        } else {
            filtroAtual = "Pendentes"; // Aplica o filtro
            resetarEstiloBotoes();
            buttonTarefasPendentes.setStyle("-fx-background-color: #2f5d75; -fx-background-radius: 5; -fx-text-fill: white;"); // Destaca o botão
            aplicarFiltroAtual();
        }
    }
    
    private void abrirTelaEdicao(Tarefa tarefa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cadastroTela.fxml"));
            AnchorPane page = loader.load();
    
            // Configura o estágio de diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Tarefa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
    
            // Passa a tarefa para o controlador (se existir)
            cadastroTelaController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            if (tarefa != null) {
                controller.setTarefaAtual(tarefa);
            }
    
            dialogStage.showAndWait();
            carregarTarefas(); // Atualiza a interface após salvar
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarFiltroAtual() {
        VBoxListaDeTarefas.getChildren().clear(); // Limpa a lista de tarefas exibidas
        ArrayList<Tarefa> tarefas = repositorioTarefa.getAllTarefas();
        
        for (Tarefa tarefa : tarefas) {
            if (filtroAtual.isEmpty() || // Sem filtro
            (filtroAtual.equals("Pendentes") && !tarefa.isFinalizada()) || // Filtro de pendentes
            (filtroAtual.equals("Realizadas") && tarefa.isFinalizada()) || // Filtro de realizadas
            (filtroAtual.equals("Importante") && tarefa.isImportante()) || // Filtro de importantes
            (filtroAtual.equals(tarefa.getCategoria()))) { // Filtro por categoria

            VBoxListaDeTarefas.getChildren().add(criarContainerTarefa(tarefa));
            }
        }
    }
}