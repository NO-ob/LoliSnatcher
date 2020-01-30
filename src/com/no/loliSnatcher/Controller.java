package com.no.loliSnatcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public abstract class Controller {
    WindowManager windowManager;
    Stage stage;
    BooruHandler booruHandler;
    ArrayList<BooruItem> fetched;
    protected File settingsFile;
    @FXML
    ComboBox booruSelector;

    public void setWindowManager(WindowManager windowManager){this.windowManager = windowManager;}

    /** Sets the stage object with and also draws the combo box for selecting a booru
     *
     * @param stage
     */
    public void setStage(Stage stage){

        this.stage = stage;
        updateSettings();
        setBooruSelector();
    }
    /** A Function which sets a booru handler based on the booruName parsed to it
     * @param booru
     */
    public BooruHandler getBooruHandler(Booru booru, int limit){
        System.out.println(booru.getType());
        switch (booru.getType()){
            case("Gelbooru"):
                System.out.println(booru.getBaseURL());
                return new GelbooruHandler(limit, booru.getBaseURL());
            case("Danbooru"):
                System.out.println(booru.getBaseURL());
                return  new DanbooruHandler(limit, booru.getBaseURL());
            case("Moebooru"):
                System.out.println(booru.getBaseURL());
                return new MoebooruHandler(limit, booru.getBaseURL());
        }
        //System.out.println("set booru "+ booru.getName()+" " + booru.getBaseURL());
        return null;
    }
    abstract void updateSettings();

    public void validateDir(String path){
        //make sure dir path is a directory and not a file
        if (path.substring(path.length() - 1).equals("/")){
            path += "/";
        }
        File dir = new File(path);
        //Make config dir if it doesn't exist
        if (!dir.exists()){dir.mkdir();}
    }


    /**
     * Adds options to the booru selector combo box, it will load them /config/*.booru if they exist
     */
    public void setBooruSelector(){
        booruSelector.getItems().clear();
        // Adds booru items to the ComboBox
        ObservableList<Booru> booruChoices = FXCollections.observableArrayList();
        booruChoices.add(new Booru("Gelbooru", "https://gelbooru.com/favicon.ico","Gelbooru", "https://gelbooru.com"));
        booruChoices.add(new Booru("Danbooru", "https://i.imgur.com/7ek8bNs.png", "Danbooru", "https://danbooru.donmai.us"));
        booruChoices.add(new Booru("Yande.re", "https://i.imgur.com/nBzBZMw.png", "Moebooru","https://yande.re"));

        File dir = new File(System.getProperty("user.home")+"/.loliSnatcher/config/");
        //get array for .booru files from config directory
        File[] files = dir.listFiles((d, name) -> name.endsWith(".booru"));
        if (files.length > 0){
            for (int i = 0; i < files.length; i++){
                booruChoices.add(new Booru(files[i]));
            }
        }

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
                } else {
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
}
