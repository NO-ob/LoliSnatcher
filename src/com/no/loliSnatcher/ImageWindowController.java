package com.no.loliSnatcher;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageWindowController {
    private Stage stage;
    @FXML
    private ImageView fullImage;
    @FXML
    private VBox main;
    @FXML
    private Text txtTags;
    @FXML
    private ListView tagList;
    private Model model;
    private BooruItem imageItem;
    public void setModel(Model mod){
        model = mod;
    }
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
    public void setItem(BooruItem item) {
        imageItem = item;
    }
    public void setImg(){
        fullImage.setImage(new Image(imageItem.fileURL));
        fullImage.setFitWidth(stage.getHeight()*0.8);
        fullImage.setPreserveRatio(true);
    }
    public void setTags(){
        ObservableList<String> splitTags = FXCollections.observableArrayList(imageItem.tags.split(" "));
        tagList.setItems(splitTags);
        tagList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + tagList.getSelectionModel().getSelectedItem());
                model.putTag((String) tagList.getSelectionModel().getSelectedItem());
            }
        });
    }
    @FXML
    private void saveImage(){
        File imageFile = new File(System.getProperty("user.home")+"/Pictures/test."+ imageItem.fileURL.substring(imageItem.fileURL.lastIndexOf("/")+1));
        BufferedImage image = SwingFXUtils.fromFXImage(fullImage.getImage(),null);
        System.out.println(imageItem.fileURL.substring(imageItem.fileURL.lastIndexOf(".") +1 ));

        try {
            ImageIO.write(image, imageItem.fileURL.substring(imageItem.fileURL.lastIndexOf(".")+1),imageFile);

        } catch (IOException e){
            throw new RuntimeException();
        }
    }
}
