package com.no.loliSnatcher;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SearchController extends Controller{
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    private GridPane imageGrid;
    @FXML
    private ComboBox booruSelector;
    ArrayList<BooruItem> booruItems = null;
    private GelbooruHandler booruHandler;
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
            ArrayList<BooruItem> fetched = search(searchField.getText(),selected.getName());
            // Displays images if the fetched list is not empty
             if (fetched.size() > 0) {
                 rowNum = 0;
                 imgCount = 0;
                 displayImagePreviews(fetched);
            }
        }

    }

    public ArrayList<BooruItem> getNextPage(String tags){
        return booruHandler.Search(tags);
    }

    /** A Function which calls a search on a booruhandler
     *
     *
     * @param tags
     * @param booruName
     * @return ArrayList of Booru Items
     */
    public ArrayList<BooruItem> search(String tags,String booruName){
        switch (booruName){
            case ("Gelbooru"):
                booruHandler = new GelbooruHandler();
                break;
            case("Danbooru"):
                booruHandler = new DanbooruHandler();
        }


        booruItems = booruHandler.Search(tags);
        return booruItems;
    }

    /**
     * Gets the next page of Images
     */
    private void scrollLoad() {
        ArrayList<BooruItem> fetched = getNextPage(searchField.getText());
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
            // Calls the windowManager to load the Image window and parses it a booruItem when clicked
            image1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                         @Override public void handle(MouseEvent event) {
                                             try {
                                                 String id = event.getSource().toString().split(",")[0].substring(17);
                                                 windowManager.imageWindowLoader(booruItems.get(Integer.parseInt(id)));
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

    /** Calls the model to load the snatcher window and parses it the selected booru
     *
     * @throws Exception
     */
    @FXML
    private void snatcherWindowLoader() throws Exception {
        Booru selected = (Booru) booruSelector.getValue();
        windowManager.snatcherWindowLoader(searchField.getText());
    }

    public void putTag(String tag){
        searchField.appendText(" "+tag);
    }
}
