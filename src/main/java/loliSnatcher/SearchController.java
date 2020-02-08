package loliSnatcher;


import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.io.*;
import java.util.ArrayList;

public class SearchController extends Controller{
    private Stage stage;
    @FXML
    private HBox searchBar;
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    VBox main;
    @FXML
    private FlowPane imageGrid;
    boolean launched = false;
    int imgCount = 0;
    int imgLoaded = 0;
    int imgSelected = 0;
    int imgPrevSelected = 0;
    int rowNum = 0;
    int colNum = 0;
    int limit = 20;
    int colMax = 4;
    boolean imageThumbnails = true;
    String prevTags = "";
    String prevBooru = "";
    /**
     * Fetches an arrayList of BooruItems when the search button is clicked
     * @param
     */
    @FXML
    private void processSearch(){
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
            imgSelected = 0;
            imgPrevSelected = 0;
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
            ImageView imageView = null;
            Image image = null;
            //Load thumbnails if thumbnail setting is enabled or image is gif/webm
            if (imageThumbnails || fetched.get(imgCount).getExt().equals("gif") || fetched.get(imgCount).getExt().equals("webm")){
                image = new Image(fetched.get(imgCount).getThumbnailURL(),0,0,true,false,true);
            } else {
                if(colMax < 4){
                    image = new Image(fetched.get(imgCount).getSampleURL(),(imagePreviews.getLayoutBounds().getHeight()/4),0,true,false,true);
                } else {
                    image = new Image(fetched.get(imgCount).getSampleURL(),((imagePreviews.getLayoutBounds().getWidth()/colMax)),0,true,false,true);
                }

            }
            //imgLoaded is incremented when a image finishes loading
            image.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                if(newValue.intValue() == 1){
                    imgLoaded ++;
                }
            });

            imageView = new ImageView(image);
            imageView.fitWidthProperty().bind(imagePreviews.widthProperty().divide(colMax).multiply(0.9));
            imageView.setPreserveRatio(true);
            Button imgButton = new Button();
            imgButton.setId("img_"+imgCount);

            imgButton.setGraphic(imageView);
            imgButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    try {
                        System.out.println(event.getSource());
                        String id = event.getSource().toString().substring(event.getSource().toString().lastIndexOf("_")+1,event.getSource().toString().lastIndexOf(","));
                        //only load if the image isn't the previous loaded since it will causes and infinity loop if a video is loaded in mpv
                        if(imgSelected != Integer.parseInt(id)){
                            windowManager.imageWindowLoader(fetched.get(Integer.parseInt(id)),searchField.getText());
                            imgSelected = Integer.parseInt(id);
                            System.out.println("clicked" + id);
                            //request focus of clicked stack pane
                            StackPane tmp = (StackPane) event.getSource();
                            tmp.requestFocus();
                            imgSelected = Integer.parseInt(id);
                            imgPrevSelected = imgSelected;
                        }
                    } catch (Exception e) {
                        System.out.println("SearchController::displayImagePreviews::setOnMouseClicked");
                        System.out.println(e.toString());
                    }

                }});
            //Fire button press when button is focused
            imgButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                imgButton.fire();
            });
            imgButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 10, 0, 0, 4));
            imageGrid.getChildren().add(imgButton);

            // Adds a listener to the ScrollPane so that when the bottom is reached more images can be loaded
            imagePreviews.vvalueProperty().addListener(
                    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        // Only run scroll load if all images are in loaded state to prevent infinite loading
                        if(newValue.intValue() == 1 && imgLoaded == imgCount){
                            scrollLoad();
                        }
                        if (newValue.doubleValue() >= 0.11){
                            searchBar.setVisible(false);
                            searchBar.setManaged(false);
                        }
                        if (newValue.doubleValue() <= 0.1){
                            searchBar.setVisible(true);
                            searchBar.setManaged(true);
                        }
                    });

            imgCount ++;
            colNum ++;
        }
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
                                    if (input.split(" = ").length > 1 && !launched) {
                                        searchField.setText(input.split(" = ")[1]);
                                    }
                                }
                                break;
                            case("Preview Mode"):
                                if(input.split(" = ")[1].equals("Sample")){
                                    imageThumbnails = false;
                                } else {
                                    imageThumbnails = true;
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
        launched = true;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        updateSettings();
        setBooruSelector();
        //Things to do on keypresses
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                imgPrevSelected = imgSelected;
                switch (e.getCode()){
                    //search when enter is pressed
                    case ENTER:
                        processSearch();
                        break;
                }
            }
        });
    }
}