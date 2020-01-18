package com.no.loliSnatcher;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.TextField;
public class SnatcherController {
    Model model;
    Stage stage;
    @FXML
    GridPane main;
    @FXML
    TextField tagsField;
    @FXML
    TextField dirField;
    @FXML
    TextField amountField;
    @FXML
    Label fileName;
    @FXML
    Label progress;
    public void setModel(Model mod){model = mod;}
    public void setStage(Stage snatcherStage){stage = snatcherStage;}

    @FXML
    public void snatch(){
        String tags = tagsField.getText();
        String dir = dirField.getText();
        int amount = Integer.parseInt(amountField.getText());
        BooruHandler gelbooru = new BooruHandler();
        if (amount <= 100){gelbooru.limit = amount;} else{gelbooru.limit = 100;}

        ArrayList<BooruItem> fetched = null;
        progress.setText("Fetching Image Data");

        while (amount > 0){
            fetched = gelbooru.Search(tags);
            System.out.println(fetched.size() + " " + amount);
            amount -= gelbooru.limit;
        }
        ArrayList<BooruItem> finalFetched = fetched;
        Task writer = new Task<Void>(){
            @Override public Void call() {
                for (int i = 0; i < finalFetched.size(); i++){
                    BooruItem item = finalFetched.get(i);
                    if (item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1).equals("webm") || item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1).equals("gif")) {
                        System.out.println("skipped: " + item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                    } else {
                        updateTitle(item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                        System.out.println(i);
                        System.out.println(item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                        Image image;
                        image = new Image(item.fileURL);
                        System.out.println(image.getUrl());
                        while (image.getProgress() != 1){
                            System.out.println("waiting");
                        }
                        File imageFile = new File(dirField.getText() + item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        try {
                            ImageIO.write(bufferedImage, item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1), imageFile);

                        } catch (IOException e) {
                            throw new RuntimeException();
                        }

                    }
                    updateProgress(i, finalFetched.size());
                    updateTitle(item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1));
                }
                return null;
            }

        };
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(writer.progressProperty());
        fileName.textProperty().bind(writer.titleProperty());
        main.add(bar,1,6);
        new Thread(writer).start();




    }


    public void setTags(String tags) {
        tagsField.setText(tags);
    }
}
