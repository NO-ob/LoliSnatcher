package com.no.loliSnatcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
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

/**
 * Controller for the Snatcher window this is what handles the batch downloading [Snatching]
 */
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

    /**
     * Does tasks which need to be done on window creation, this cant be done when the controller instance is created
     * because the GUI doesn't exist at that point
     * @param snatcherStage
     */
    public void setStage(Stage snatcherStage){

        stage = snatcherStage;
        // Adds booru items to the ComboBox
        ObservableList<Booru> booruChoices = FXCollections.observableArrayList();
        booruChoices.add(new Booru("Gelbooru", "https://gelbooru.com/favicon.ico"));
        booruChoices.add(new Booru("Danbooru", "https://i.imgur.com/7ek8bNs.png"));
        booruSelector.getItems().addAll(booruChoices);
        // Need to specify a custom cell factory to be able to display icons and text
        booruSelector.setCellFactory(param -> new ListCell<Booru>() {
            final ImageView graphicNode = new ImageView();
            @Override
            public void updateItem(Booru item, boolean empty) {

                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                    graphicNode.setImage(null);
                }else {
                    setText(item.getName());
                    graphicNode.setImage(item.getFavicon());
                    setGraphic(graphicNode);
                }
            }
        });
        booruSelector.setButtonCell((ListCell) booruSelector.getCellFactory().call(null));
        // Sets default item to first in booruChoices
        booruSelector.getSelectionModel().select(0);
    }

    /**
     * This is the function which actually gets and then writes all the iamge files
     */
    @FXML
    public void snatch(){
        String tags = tagsField.getText();
        String dirPath = dirField.getText();
        int amount = Integer.parseInt(amountField.getText());
        /** Append / to the end of the directory path so that it is a directory
         * otherwise the last part of the path will be part of the file name
         */
        if (!dirPath.substring(dirPath.length() - 1).equals("/")){
            dirField.appendText("/");
            dirPath = dirField.getText();
        }
        // Make directory if it doesn't exist
        File dir = new File(dirPath);
        if (!dir.exists()){dir.mkdir();}

        Booru selected = (Booru) booruSelector.getValue();
        switch (selected.getName()){
            case("Gelbooru"):
                booruHandler = new GelbooruHandler();
                break;
            case("Danbooru"):
                booruHandler = new DanbooruHandler();
        }
        //Sets limit to 100 if bigger than as gelbooru only allows for 100 items per page
        if (amount <= 100){booruHandler.limit = amount;} else {booruHandler.limit = 100;}

        ArrayList<BooruItem> fetched = null;
        progress.setText("Fetching Image Data");

        // Loads pages consecutively until the desired amount of images have been fetched
        while (amount > 0){
            fetched = booruHandler.Search(tags);
            System.out.println(fetched.size() + " " + amount);
            amount -= booruHandler.limit;
        }

        ArrayList<BooruItem> finalFetched = fetched;
        // Task  is used so this bit of code is run on a seperate thread to the GUI otherwise it freezes until this work is done
        Task writer = new Task<Void>(){
            @Override public Void call() {
                for (int i = 0; i < finalFetched.size(); i++){
                    BooruItem item = finalFetched.get(i);
                    // Skips item if it is webm or gif as the javafx image objects cant display them
                    if (item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1).equals("webm") || item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1).equals("gif")) {
                        System.out.println("skipped: " + item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                    } else {
                        // Updates the title of the task with the image url
                        updateTitle(item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                        // Creates a new image from the URL of the current booruitem
                        Image image;
                        image = new Image(item.fileURL);
                        System.out.println(image.getUrl());
                        while (image.getProgress() != 1){
                            System.out.println("waiting");
                        }
                        //Writes current Image to new file
                        File imageFile = new File(dirField.getText() + item.fileURL.substring(item.fileURL.lastIndexOf("/") + 1));
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        try {
                            ImageIO.write(bufferedImage, item.fileURL.substring(item.fileURL.lastIndexOf(".") + 1), imageFile);

                        } catch (IOException e) {
                            throw new RuntimeException();
                        }

                    }
                    //Updates the task progress
                    updateProgress(i, finalFetched.size());
                }
                return null;
            }

        };

        // Displays progress and filename in the window
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(writer.progressProperty());
        fileName.textProperty().bind(writer.titleProperty());
        main.add(bar,1,7);
        /** Remove progress bar when task has finished as a progress bar
         * makes the PC lock up when it is not being updated
         */
        writer.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                main.getChildren().remove(bar);
            }
        });
        writer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                main.getChildren().remove(bar);
            }
        });
        Thread writerThread = new Thread(writer);
        writerThread.start();


    }


    public void setTags(String tags) {
        tagsField.setText(tags);
    }
}
