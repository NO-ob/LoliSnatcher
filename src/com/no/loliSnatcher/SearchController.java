package com.no.loliSnatcher;

import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class SearchController {
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private Text output;
    @FXML
    private Model model = new Model();
    @FXML
    private ScrollPane imagePreviews;
    @FXML
    private GridPane imageGrid;
    int imgCount = 0;
    int rowNum = 0;
    int loading = 0;
    @FXML
    private void processSearch(ActionEvent event){

        if (!searchField.getText().isEmpty()){
            imageGrid.getChildren().clear();
            ArrayList<BooruItem> fetched = model.search(searchField.getText());
            System.out.println(fetched.get(1).tags);

            rowNum = 0;
            imgCount = 0;
            displayImagePreviews(fetched);

        }

    }
    private void scrollLoad() {
        ArrayList<BooruItem> fetched = model.getNextPage(searchField.getText());
        displayImagePreviews(fetched);
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
    }


    private void displayImagePreviews(ArrayList<BooruItem> fetched){
        int colNum = 0;
        loading = 1;
        while (imgCount < fetched.size()){
            if (colNum > 3){rowNum++;colNum = 0;}
            ImageView image1 = new ImageView(fetched.get(imgCount).thumbnailURL);
            image1.setFitWidth((imagePreviews.getLayoutBounds().getWidth() / 4) *0.9);
            image1.setPreserveRatio(true);
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
        loading = 0;
        imagePreviews.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    if(newValue.doubleValue() == 1 && loading==0){
                        System.out.println("hit bottom");
                        scrollLoad();

                    }
                });
    }

    private void imageWindowLoader(String imgID) throws Exception {
        int id =  Integer.parseInt(imgID);
        model.imageWindowLoader(id);
    }

}
