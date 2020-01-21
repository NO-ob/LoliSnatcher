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
    int loading = 0;
    int colNum = 0;
    int colMax = 1;
    String prevTags = "";
    public SearchController(){

    }
    @FXML
    private void processSearch(ActionEvent event){

        if (!searchField.getText().isEmpty()){
            imageGrid.getChildren().clear();
            imagePreviews.setVvalue(0);
            if (!prevTags.equals(searchField)){
                colNum = 0;
            }
            Label selected = (Label) booruSelector.getValue();
            ArrayList<BooruItem> fetched = model.search(searchField.getText(),selected.getText() );
            System.out.println(fetched.size());
             if (fetched.size() > 0) {
                 System.out.println(fetched.get(0).tags);

                 rowNum = 0;
                 imgCount = 0;
                 displayImagePreviews(fetched);
            }
        }

    }
    private void scrollLoad() {
        ArrayList<BooruItem> fetched = model.getNextPage(searchField.getText());
        displayImagePreviews(fetched);
    }


    private void displayImagePreviews(ArrayList<BooruItem> fetched){

        while (imgCount < fetched.size()){
            if (colNum > 3){rowNum++;colNum = 0;}
            ImageView image1 = new ImageView(new Image(fetched.get(imgCount).sampleURL,((imagePreviews.getLayoutBounds().getWidth() / 4) *0.9),0,true,false,true));
            image1.setId("img_"+imgCount);
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
        imagePreviews.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    System.out.println(oldValue.intValue() + " " +newValue.intValue());
                    if(newValue.intValue() == 1 && oldValue.intValue()!= 1){
                        //imagePreviews.setVvalue((double)imgCount / rowNum);
                        System.out.println("hit bottom");
                        scrollLoad();

                    }
                });
    }

    private void imageWindowLoader(String imgID) throws Exception {
        int id =  Integer.parseInt(imgID);
        model.imageWindowLoader(id);
    }
    @FXML
    private void snatcherWindowLoader() throws Exception {
        model.snatcherWindowLoader(searchField.getText());
    }
    public void setStage(Stage thisstage) {
        stage = thisstage;
        ObservableList<Label> booruChoices = FXCollections.observableArrayList();
        booruChoices.add(new Label("Gelbooru", new ImageView("https://gelbooru.com/favicon.ico")));
        booruChoices.add(new Label("Danbooru", new ImageView("https://i.imgur.com/7ek8bNs.png")));
        booruSelector.getItems().addAll(booruChoices);
        booruSelector.setCellFactory(param -> {
            return new ListCell<Label>() {
                @Override
                public void updateItem(Label item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        setGraphic(item.getGraphic());
                        setText(item.getText());
                    }
                }
            };
        });
        booruSelector.getSelectionModel().select(0);
    }
    public void putTag(String tag){
        searchField.appendText(" "+tag);
    }
}
