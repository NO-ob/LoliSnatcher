package loliSnatcher;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

/**
 * The WindowManager class is used to create windows and allow them to talk to each other
 */
public class WindowManager {

    private ImageWindowController imageController;
    private SearchController searchController;
    private SnatcherController snatcherController;
    private SettingsController settingsController;


    public void searchWindowLoader(Stage searchStage) throws Exception {
        //Parent root = new FXMLLoader(getClass().getResource("Search.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("Search.fxml"));
        Parent root = searchLoader.load();
        Scene scene = new Scene(root);
        searchController = searchLoader.getController();
        searchStage.setScene(scene);
        searchController.setStage(searchStage);
        searchController.setWindowManager(this);
        searchStage.setTitle("Loli Snatcher");
        File tmp = new File(System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/search.css");
        if (tmp.exists()){
            scene.getStylesheets().add("file://" + System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/search.css");
        }
        searchStage.show();

    }

    /** Opens an image window if it's not already open, if it is it will just parse it a new item
     *
     * @param booruItem
     * @param searchTags
     * @throws Exception
     */
    public void imageWindowLoader(BooruItem booruItem, String searchTags) throws Exception {
        if (imageController == null){
            try {
                Stage ImageStage = new Stage();
                FXMLLoader imageLoader = new FXMLLoader(getClass().getResource("ImageWindow.fxml"));
                Parent root = imageLoader.load();
                imageController = imageLoader.getController();
                ImageStage.setTitle("Image");
                Scene scene = new Scene(root);
                File tmp = new File(System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/image.css");
                if (tmp.exists()){
                    scene.getStylesheets().add("file://" + System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/image.css");
                }
                ImageStage.setScene(scene);
                ImageStage.show();
                imageController.setStage(ImageStage);
                imageController.setWindowManager(this);
                ImageStage.setOnCloseRequest(event -> imageController = null);
            } catch (Exception e){
                System.out.println("\nWindowManager::imageWindowLoader");
                System.out.println(e.toString());
            }
        }
        imageController.setItem(booruItem,searchTags);

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
                Scene scene = new Scene(root);
                File tmp = new File(System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/settings.css");
                if (tmp.exists()){
                    scene.getStylesheets().add("file://" + System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/settings.css");
                }
                snatcherStage.setScene(scene);
                snatcherStage.show();
                snatcherController.setStage(snatcherStage);
                snatcherController.setWindowManager(this);
                snatcherStage.setOnCloseRequest(event -> snatcherController = null);
            } catch (Exception e){
                System.out.println("\nWindowManager::snatcherWindowLoader");
                System.out.println(e.toString());
            }
        }
    }
    /** Opens the settings window if it's not already open
     *
     * @throws Exception
     */
    public void settingsWindowLoader() throws Exception {
        if (settingsController == null){
            try {
                Stage settingsStage = new Stage();
                FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
                Parent root = settingsLoader.load();
                settingsController = settingsLoader.getController();
                settingsStage.setTitle("Settings");
                Scene scene = new Scene(root);
                settingsStage.setScene(scene);
                File tmp = new File(System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/settings.css");
                if (tmp.exists()){
                    scene.getStylesheets().add("file://" + System.getProperty("user.home")+"/.loliSnatcher/config/theme/active/settings.css");
                }
                settingsStage.show();
                settingsController.setStage(settingsStage);
                settingsController.setWindowManager(this);
                settingsController.updateSettings();
                settingsStage.setOnCloseRequest(event -> {settingsController = null; searchController.setBooruSelector();});
            } catch (Exception e){
                System.out.println("\nWindowManager::settingsWindowLoader");
                System.out.println(e.toString());
            }
        }

    }

    public void putTag(String tag){
        searchController.putTag(tag);
    }
}
