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
            ArrayList<BooruItem> fetched = model.search(searchField.getText());
            System.out.println(fetched.get(1).tags);
            int rowNum = 0;
            for (int i = 0; i <= 20; i+=4) {
                for (int y = 0; y <=3; y++){
                    ImageView image1 = new ImageView(fetched.get(i+y).sampleURL);
                    image1.setFitWidth(100);
                    image1.setPreserveRatio(true);
                    image1.setId("img_"+fetched.get(i+y).postID);
                    imageGrid.add(image1, y, rowNum);

                }
                rowNum++;
            }
        }

    }
}
