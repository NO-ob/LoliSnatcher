package com.no.loliSnatcher;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SearchController {
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private Text output;
    @FXML
    private Model model = new Model(this);
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    private GridPane imageGrid;
    @FXML
    private ComboBox booruSelector;
    private Stage stage;
    int imgCount = 0;
    int rowNum = 0;
    int colNum = 0;
    String prevTags = "";
    /**
     * Calls the model to get an ArrayList of BooruItems when the searchButton is pressed
     * @param event
     */
    @FXML
    private void processSearch(ActionEvent event){
        // Resets the ScrollPane
        if (!searchField.getText().isEmpty()){
            imageGrid.getChildren().clear();
            imagePreviews.setVvalue(0);
            // Resets the column number if the search is a new search
            if (!prevTags.equals(searchField)){
                colNum = 0;
            }
            //Gets Booru selected in the ComboBox
            Booru selected = (Booru) booruSelector.getValue();
            // Calls the model to fetch booruItems from the booruHandler
            ArrayList<BooruItem> fetched = model.search(searchField.getText(),selected.getName());
            // Displays images if the fetched list is not empty
             if (fetched.size() > 0) {
                 rowNum = 0;
                 imgCount = 0;
                 displayImagePreviews(fetched);
            }
        }

    }

    /**
     * Gets the next page of Images
     */
    private void scrollLoad() {
        ArrayList<BooruItem> fetched = model.getNextPage(searchField.getText());
        displayImagePreviews(fetched);
    }

    /** Loads all of the image previews into the GridBox inside the ScrollPane
     *
     * @param fetched
     **/
    private void displayImagePreviews(ArrayList<BooruItem> fetched){

        while (imgCount < fetched.size()){
            // Resets column and increments the row when 4 Image views have been put in the grid
            if (colNum > 3){rowNum++;colNum = 0;}
            // Create an ImageView smaller than 1/4 of the width of the ScrollPane
            ImageView image1 = new ImageView(new Image(fetched.get(imgCount).sampleURL,((imagePreviews.getLayoutBounds().getWidth() / 4) *0.9),0,true,false,true));
            image1.setId("img_"+imgCount);
            // Calls image loader with the ide of an image when clicked on
            image1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                         @Override public void handle(MouseEvent event) {
                                             try {
                                                 imageWindowLoader(event.getSource().toString().split(",")[0].substring(17));
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }

                                         }});

            System.out.println("images: " + imgCount);
            imageGrid.add(image1,colNum, rowNum);
            imgCount ++;
            colNum ++;
        }
        // Adds a listener to the ScrollPane so that when the bottom is reached more images can be loaded
        imagePreviews.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    System.out.println(oldValue.intValue() + " " +newValue.intValue());
                    if(newValue.intValue() == 1 && oldValue.intValue()!= 1){
                        scrollLoad();

                    }
                });
    }

    /** Calls the model to load the image window and parses it the ArrayList index of that image
     *
     * @param imgID
     * @throws Exception
     */
    private void imageWindowLoader(String imgID) throws Exception {
        int id =  Integer.parseInt(imgID);
        model.imageWindowLoader(id);
    }

    /** Calls the model to load the snatcher window and parses it the selected booru
     *
     * @throws Exception
     */
    @FXML
    private void snatcherWindowLoader() throws Exception {
        Booru selected = (Booru) booruSelector.getValue();
        model.snatcherWindowLoader(searchField.getText());
    }
    /**
     * Does tasks which need to be done on window creation, this cant be done when the controller instance is created
     * because the GUI doesn't exist at that point
     * @param stage
     */
    public void setStage(Stage stage) {
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

    /**
     * Adds a tag to the search field
     * @param tag
     */
    public void putTag(String tag){
        searchField.appendText(" "+tag);
    }
}
