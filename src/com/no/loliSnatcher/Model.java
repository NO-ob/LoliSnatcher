package com.no.loliSnatcher;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * The Model class is used to allow different windows or objects which multiple windows use to talk to eachother
 */
public class Model {
    private GelbooruHandler booruHandler;
    ImageWindowController imageController = null;
    ArrayList<BooruItem> booruItems = null;
    public SearchController searchController;
    public SnatcherController snatcherController;
    public Model(SearchController controller){
        searchController = controller;
    }

    /** A Function which calls a search on a booruhandler
     *
     *
     * @param tags
     * @param booruName
     * @return ArrayList of Booru Items
     */
    public ArrayList<BooruItem> search(String tags,String booruName){
        switch (booruName){
            case ("Gelbooru"):
                booruHandler = new GelbooruHandler();
                break;
            case("Danbooru"):
                booruHandler = new DanbooruHandler();
        }
         
        
        booruItems = booruHandler.Search(tags);
        return booruItems;
    }

    /** Calls a search without making a new array
     *
     * @param tags
     * @return ArrayList of Booru Items
     */
    public ArrayList<BooruItem> getNextPage(String tags){
        return booruHandler.Search(tags);
    }

    /** Opens an image window if it's not already open, if it is it will just pass it a new item
     *
     * @param id
     * @throws Exception
     */
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
            ImageStage.setOnCloseRequest(event -> imageController = null);
            } catch (Exception e){}
        }
        imageController.setItem(booruItems.get(id));

    }
    /** Open the snatcher window if it's not already open
     *
     * @param tags
     * @throws Exception
     */
    public void snatcherWindowLoader(String tags) throws Exception {
        if (snatcherController == null){
            try {
                Stage snatcherStage = new Stage();
                FXMLLoader snatcherLoader = new FXMLLoader(getClass().getResource("Snatcher.fxml"));
                Parent root = snatcherLoader.load();
                snatcherController = snatcherLoader.getController();
                snatcherController.setTags(tags);
                snatcherStage.setTitle("Snatcher");
                snatcherStage.setScene(new Scene(root));
                snatcherStage.show();
                snatcherController.setStage(snatcherStage);
                snatcherController.setModel(this);
                snatcherController.dirField.setText(System.getProperty("user.home")+"/Pictures/");
                snatcherStage.setOnCloseRequest(event -> snatcherController = null);
            } catch (Exception e){}
        }
    }

    public void putTag(String tag){
        searchController.putTag(tag);
    }
}
