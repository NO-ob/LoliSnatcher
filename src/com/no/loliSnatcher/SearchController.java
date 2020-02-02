package com.no.loliSnatcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class SearchController extends Controller{
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    private FlowPane imageGrid;

    int imgCount = 0;
    int imgLoaded = 0;
    int rowNum = 0;
    int colNum = 0;
    int limit = 20;
    int colMax = 4;
    boolean imageThumbnails = true;
    String prevTags = "";
    String prevBooru = "";
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

            //Gets Booru selected in the ComboBox
            Booru selected = (Booru) booruSelector.getValue();
            booruHandler = getBooruHandler(selected,limit);

            fetched = booruHandler.Search(searchField.getText());
            prevTags = searchField.getText();
            prevBooru = selected.getName();

            // Displays images if the fetched list is not empty
             if (fetched.size() > 0) {
                 rowNum = 0;
                 imgCount = 0;
                 imgLoaded = 0;
                 colNum = 0;
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
            if (colNum > colMax){rowNum++;colNum = 0;}

            ImageView imageView = null;
            Image image = null;
            //Load thumbnails if thumbnail setting is enabled or image is gif/webm
            if (imageThumbnails || fetched.get(imgCount).getExt().equals("gif") || fetched.get(imgCount).getExt().equals("webm")){
                image = new Image(fetched.get(imgCount).getThumbnailURL(),0,0,true,false,true);
            } else {
                if(colMax < 4){
                    image = new Image(fetched.get(imgCount).getSampleURL(),(imagePreviews.getLayoutBounds().getWidth()/4),0,true,false,true);
                } else {
                    image = new Image(fetched.get(imgCount).getSampleURL(),((imagePreviews.getLayoutBounds().getWidth()/colMax)),0,true,false,true);
                }

            }
            //imgLoaded is incremented when a image finishes loading
            image.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                if(newValue.intValue() == 1){
                    imgLoaded ++;
                    System.out.println("loaded: "+ imgLoaded + " count:" + imgCount + "mem: " + Runtime.getRuntime().totalMemory() /1024/1024 + "MB");
                    System.gc();
                }
            });

            imageView = new ImageView(image);
            imageView.fitWidthProperty().bind(imagePreviews.widthProperty().divide(colMax).multiply(0.9));
            imageView.setPreserveRatio(true);
            StackPane sp = new StackPane();
            sp.setId("img_"+imgCount);
            sp.getChildren().add(imageView);
            sp.getChildren().add(new Text(Integer.toString(imgCount)));
            // Calls the windowManager to load the Image window and parses it a booruItem when clicked
            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                         @Override public void handle(MouseEvent event) {
                                             try {
                                                 String id = event.getSource().toString().substring(event.getSource().toString().lastIndexOf("_")+1,event.getSource().toString().lastIndexOf("]"));
                                                 windowManager.imageWindowLoader(fetched.get(Integer.parseInt(id)),searchField.getText());
                                             } catch (Exception e) {
                                                 System.out.println("SearchController::displayImagePreviews::setOnMouseClicked");
                                                 System.out.println(e.toString());
                                             }

                                         }});


            imageGrid.getChildren().add(sp);
            imgCount ++;
            colNum ++;
        }
        // Adds a listener to the ScrollPane so that when the bottom is reached more images can be loaded
        imagePreviews.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    // Only run scroll load if all images are in loaded state to prevent infinite loading
                    if(newValue.intValue() == 1 && imgLoaded == imgCount){
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
                                break;
                            case("Preview Mode"):
                                if(input.split(" = ")[1].equals("Sample")){
                                    imageThumbnails = false;
                                }
                                break;
                            case("Preview Columns"):
                                if (Integer.parseInt(input.split(" = ")[1]) > 0){
                                    colMax = Integer.parseInt(input.split(" = ")[1]);
                                }
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("SearchController::loadSettings \n Error reading settings \n");
                    System.out.println(e.toString());
                } catch (ArrayIndexOutOfBoundsException e){
                //Swallow since if this is thrown the setting is empty
            }
            } catch (FileNotFoundException e){
                System.out.println("SearchController::loadSettings \n settings.conf not found \n");
                System.out.println(e.toString());
            }

        }
    }





}
