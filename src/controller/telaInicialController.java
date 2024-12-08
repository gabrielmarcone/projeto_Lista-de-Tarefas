package controller;

import java.io.FileNotFoundException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class telaInicialController {

    @FXML
    private void handleButtonComecar(ActionEvent event) {
        try {
        java.net.URL audioPath = getClass().getResource("/icons/click.wav");
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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/segundaTela.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Lista de Tarefas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
