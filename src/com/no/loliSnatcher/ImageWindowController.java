package com.no.loliSnatcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ImageWindowController {
    private Stage stage;
    @FXML
    private ImageView fullImage;
    @FXML
    private VBox main;
    @FXML
    private Text txtTags;

    public void setStage(Stage thisstage){
        stage = thisstage;
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(number);
                if ((double)t1 > 0) {
                    fullImage.setFitHeight(((double) number) * 0.8);
                }
                //stage.setHeight(fullImage.getFitHeight() + 200);
            }
        });
    }
    public void setImg(String url){
        //fullImage = new Image(url);
        fullImage.setImage(new Image(url));
        fullImage.setFitWidth(stage.getWidth());
        fullImage.setPreserveRatio(true);
    }
    public void setTags(String tags){
        txtTags.setText(tags);
    }
}
