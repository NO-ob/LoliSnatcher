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

import java.io.*;
import java.util.ArrayList;

public class SearchController extends Controller{
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    private GridPane imageGrid;

    int imgCount = 0;
    int rowNum = 0;
    int colNum = 0;
    int limit = 20;
    String prevTags = "";
    /**
     * Fetches an arrayList of BooruItems when the search button is clicked
     * @param event
     */
    @FXML
    private void processSearch(ActionEvent event){
        // Resets the ScrollPane
            updateSettings();
            imageGrid.getChildren().clear();
            imagePreviews.setVvalue(0);
            if (searchField.getText().isEmpty()){searchField.setText(" ");}
            // Resets the column number if the search is a new search
            if (!prevTags.equals(searchField.getText())){
                colNum = 0;
            }
            //Gets Booru selected in the ComboBox
            Booru selected = (Booru) booruSelector.getValue();
            booruHandler = getBooruHandler(selected,limit);
            fetched = booruHandler.Search(searchField.getText());
            // Displays images if the fetched list is not empty
             if (fetched.size() > 0) {
                 rowNum = 0;
                 imgCount = 0;
                 displayImagePreviews(fetched);
            }


    }

    public ArrayList<BooruItem> getNextPage(String tags){
        return booruHandler.Search(tags);
    }



    /**
     * Gets the next page of Images
     */
    private void scrollLoad() {
        fetched = getNextPage(searchField.getText());
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
            System.out.println(fetched.get(imgCount).getFileURL() + "\n");
            ImageView image1 = new ImageView(new Image(fetched.get(imgCount).getSampleURL(),((imagePreviews.getLayoutBounds().getWidth() / 4) *0.9),0,true,false,true));
            image1.setId("img_"+imgCount);
            // Calls the windowManager to load the Image window and parses it a booruItem when clicked
            image1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                         @Override public void handle(MouseEvent event) {
                                             try {
                                                 String id = event.getSource().toString().split(",")[0].substring(17);
                                                 windowManager.imageWindowLoader(fetched.get(Integer.parseInt(id)));
                                             } catch (Exception e) {
                                                 System.out.println("SearchController::displayImagePreviews::setOnMouseClicked");
                                                 System.out.println(e.toString());
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
    @FXML
    private void settingsWindowLoader() throws Exception {
        windowManager.settingsWindowLoader();
    }
    public void putTag(String tag){
        searchField.appendText(" "+tag);
    }

    @Override
    public void updateSettings(){
        settingsFile = new File(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf");
        if (settingsFile.exists()){
            String input;
            try {
                BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf"));
                try {
                    while ((input = br.readLine()) != null){
                        //Splits line and then switches on the option name
                        switch(input.split(" = ")[0]){
                            case("Limit"):
                                limit = Integer.parseInt(input.split(" = ")[1]);
                                break;
                            case("Default Tags"):
                                if (searchField.getText().isEmpty()){
                                    if (input.split(" = ").length > 1) {
                                        searchField.setText(input.split(" = ")[1]);
                                    }

                                }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("SearchController::loadSettings \n Error reading settings \n");
                    System.out.println(e.toString());
                }
            } catch (FileNotFoundException e){
                System.out.println("SearchController::loadSettings \n settings.conf not found \n");
                System.out.println(e.toString());
            }

        }
    }





}
