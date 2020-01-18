package com.no.loliSnatcher;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;

public class Model {
    private BooruHandler gelbooru;
    ImageWindowController imageController = null;
    ArrayList<BooruItem> booruItems = null;
    private SearchController searchController;
    public Model(SearchController controller){
        searchController = controller;
    }

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
            imageController.setModel(this);
              ImageStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                  @Override
                  public void handle(WindowEvent event) {
                      imageController = null;
                  }
              });
            } catch (Exception e){}
        }
        imageController.setItem(booruItems.get(id));
        imageController.setTags();
        imageController.setImg();

    }
    public void searchWindowLoader(){

    }
    public void putTag(String tag){
        searchController.putTag(tag);
    }
}
