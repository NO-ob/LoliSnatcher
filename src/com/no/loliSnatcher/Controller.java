package com.no.loliSnatcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Controller {
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

    @FXML
    private void processSearch(ActionEvent event){

        if (!searchField.getText().isEmpty()){
            imageGrid.getChildren().clear();
            ArrayList<BooruItem> fetched = model.search(searchField.getText());
            System.out.println(fetched.get(1).tags);
            int rowNum = 0;
            int colNum = 0;
            int imgCount = 0;

            while (imgCount < fetched.size()){
                if (colNum > 3){rowNum++;colNum = 0;}
                ImageView image1 = new ImageView(fetched.get(imgCount).sampleURL);
                image1.setFitWidth((imagePreviews.getLayoutBounds().getWidth() / 4) *0.9);
                image1.setPreserveRatio(true);
                image1.setId("img_"+fetched.get(imgCount).postID);
                imageGrid.add(image1,colNum, rowNum);
                imgCount ++;
                colNum ++;
            }

        }

    }
}
