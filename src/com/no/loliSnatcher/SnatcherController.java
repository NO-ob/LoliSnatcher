package com.no.loliSnatcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SnatcherController {
    Model model;
    Stage stage;
    GelbooruHandler booruHandler;
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
    @FXML
    ComboBox booruSelector;


    public void setModel(Model mod){model = mod;}
    public void setStage(Stage snatcherStage){

        stage = snatcherStage;
        ObservableList<Booru> booruChoices = FXCollections.observableArrayList();
        booruChoices.add(new Booru("Gelbooru", "https://gelbooru.com/favicon.ico"));
        booruChoices.add(new Booru("Danbooru", "https://i.imgur.com/7ek8bNs.png"));
        booruSelector.getItems().addAll(booruChoices);
        booruSelector.setCellFactory(param -> {
            return new ListCell<Booru>() {
                @Override
                public void updateItem(Booru item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        setText(item.getName());
                        setGraphic(new ImageView(item.getImageUrl()));
                    }
                }
            };
        });
        booruSelector.setButtonCell((ListCell) booruSelector.getCellFactory().call(null));
        booruSelector.getSelectionModel().select(0);
    }

    @FXML
    public void snatch(){
        String tags = tagsField.getText();

        String dirPath = dirField.getText();
        if (!dirPath.substring(dirPath.length() - 1).equals("/")){
            dirField.appendText("/");
            dirPath = dirField.getText();
        }

        File dir = new File(dirPath);
        if (!dir.exists()){dir.mkdir();}

        int amount = Integer.parseInt(amountField.getText());
        Booru selected = (Booru) booruSelector.getValue();
        switch (selected.getName()){
            case("Gelbooru"):
                booruHandler = new GelbooruHandler();
                break;
            case("Danbooru"):
                booruHandler = new DanbooruHandler();
        }

        if (amount <= 100){booruHandler.limit = amount;} else{booruHandler.limit = 100;}

        ArrayList<BooruItem> fetched = null;
        progress.setText("Fetching Image Data");

        while (amount > 0){
            fetched = booruHandler.Search(tags);
            System.out.println(fetched.size() + " " + amount);
            amount -= booruHandler.limit;
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
        main.add(bar,1,7);
        new Thread(writer).start();




    }


    public void setTags(String tags) {
        tagsField.setText(tags);
    }
}
