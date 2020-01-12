package com.no.loliSnatcher;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Model {
    private BooruHandler gelbooru;
    ImageWindowController imageController = null;
    ArrayList<BooruItem> booruItems = null;
    public ArrayList<BooruItem> search(String tags){
        gelbooru = new BooruHandler();
        booruItems = gelbooru.Search(tags);
        return booruItems;
    }

    public ArrayList<BooruItem> getNextPage(String tags){
        return gelbooru.Search(tags);
    }

    public void imageWindowLoader(int id) throws Exception {
        if (imageController == null){
            try {
            Stage ImageStage = new Stage();
            FXMLLoader imageLoader = new FXMLLoader(getClass().getResource("ImageWindow.fxml"));
            Parent root = imageLoader.load();
            imageController = imageLoader.getController();
            ImageStage.setTitle("Img");
            ImageStage.setScene(new Scene(root));
            ImageStage.show();
            imageController.setStage(ImageStage);
            } catch (Exception e){}
        }
        imageController.setImg(booruItems.get(id).fileURL);
        imageController.setTags(booruItems.get(id).tags);

    }
}
