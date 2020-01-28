package com.no.loliSnatcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Controller {
    WindowManager windowManager;
    Stage stage;
    GelbooruHandler booruHandler;
    ArrayList<BooruItem> fetched;
    @FXML
    ComboBox booruSelector;

    public void setWindowManager(WindowManager windowManager){this.windowManager = windowManager;}

    /** Sets the stage object with and also draws the combo box for selecting a booru
     *
     * @param stage
     */
    public void setStage(Stage stage){

        this.stage = stage;
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
    /** A Function which sets a booru handler based on the booruName parsed to it
     * @param booruName
     */
    public void setBooruHandler(String booruName){
        switch (booruName){
            case ("Gelbooru"):
                 booruHandler = new GelbooruHandler();
                break;
            case("Danbooru"):
                booruHandler = new DanbooruHandler();
        }

    }
}
