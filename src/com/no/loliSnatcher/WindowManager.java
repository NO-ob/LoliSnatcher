package com.no.loliSnatcher;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The WindowManager class is used to create windows and allow them to talk to each other
 */
public class WindowManager {

    private ImageWindowController imageController;
    private SearchController searchController;
    private SnatcherController snatcherController;


    public void searchWindowLoader(Stage searchStage) throws Exception {
        //Parent root = new FXMLLoader(getClass().getResource("Search.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("Search.fxml"));
        Parent root = searchLoader.load();
        Scene scene = new Scene(root);
        searchController = searchLoader.getController();
        searchController.setStage(searchStage);
        searchController.setWindowManager(this);
        searchStage.setTitle("Loli Snatcher");
        searchStage.setScene(scene);
        searchStage.show();

    }

    /** Opens an image window if it's not already open, if it is it will just parse it a new item
     *
     * @param booruItem
     * @throws Exception
     */
    public void imageWindowLoader(BooruItem booruItem) throws Exception {
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
        imageController.setItem(booruItem);

    }
    /** Open the snatcher window if it's not already open, if it is it will just pass it a new item
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
                snatcherController.setWindowManager(this);
                snatcherController.dirField.setText(System.getProperty("user.home")+"/Pictures/");
                snatcherStage.setOnCloseRequest(event -> snatcherController = null);
            } catch (Exception e){}
        }
    }

    public void putTag(String tag){
        searchController.putTag(tag);
    }
}
