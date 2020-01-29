package com.no.loliSnatcher;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/** Controller for the window which will display the full sized image of the sample selected in the search window
 *
 */
public class ImageWindowController extends Controller{
    private Stage stage;
    @FXML
    private ImageView fullImage;
    @FXML
    private VBox main;
    @FXML
    private Text txtTags;
    @FXML
    private ListView tagList;
    private BooruItem imageItem;
    private String savePath;
    /**
     * Does tasks which need to be done on window creation, this cant be done when the controller instance is created
     * because the GUI doesn't exist at that point
     **/
    public void setStage(Stage stage){
        this.stage = stage;
        updateSettings();
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            //Listens to a change in height and then scales the image view accordingly
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(number);
                if ((double)t1 > 0) {
                    fullImage.setFitHeight(((double) number) * 0.8);
                }
            }
        });
    }
    public void setItem(BooruItem item) {
        imageItem = item;
        setTags();
        setImg();
    }
    public void setImg(){
        fullImage.setImage(new Image(imageItem.getFileURL()));
        fullImage.setFitWidth(stage.getHeight()*0.8);
        fullImage.setPreserveRatio(true);

    }
    public void setTags(){
        ObservableList<String> splitTags = FXCollections.observableArrayList(imageItem.getTags().split(" "));
        tagList.setItems(splitTags);
        /**When a specific tag is clicked on it will call model.put tag to parse it to the search controller
         * which will add it to the search field
         */
        tagList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + tagList.getSelectionModel().getSelectedItem());
                windowManager.putTag((String) tagList.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** Creates a new file and then writes an image to it. It writes the image thats in an image view instead of getting the
     * image from its url and writing that to save on memory
     */
    @FXML
    private void saveImage(){

        File imageFile = new File(savePath + imageItem.getFileURL().substring(imageItem.getFileURL().lastIndexOf("/")+1));
        BufferedImage image = SwingFXUtils.fromFXImage(fullImage.getImage(),null);

        try {
            ImageIO.write(image, imageItem.getFileURL().substring(imageItem.getFileURL().lastIndexOf(".")+1),imageFile);

        } catch (IOException e){
            System.out.println("ImageWindowController::saveImage");
            System.out.println("\n Failed to Write File \n" + imageItem.getFileURL().substring(imageItem.getFileURL().lastIndexOf("/")+1) + "\n");
        }
    }

    /** Opens the post url in the default web browser
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @FXML private void openImage() throws IOException,URISyntaxException {
        //Windows method for opening - currently untested
        if (System.getProperty("os.name").startsWith("Windows")){
            try {
                Desktop.getDesktop().browse(new URI(imageItem.getPostURL()));
            } catch (IOException | URISyntaxException e) {
                System.out.println("ImageWindowController::openImage");
                System.out.println("\n Failed to open browser in Windows \n");
            }
        } else{
            // Linux opening - should also work in MacOS
            try {
                Runtime.getRuntime().exec("xdg-open " + imageItem.getPostURL());
            } catch (IOException e) {
                System.out.println("ImageWindowController::openImage");
                System.out.println("\n Failed to open browser in Linux \n");
            }
        }
    }
    @Override
    public void updateSettings(){
        settingsFile = new File(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf");
        if (settingsFile.exists()){
            String input;
            try {
                BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf"));
                try {
                    while ((input = br.readLine()) != null){
                        //Splits line and then switches on the option name
                        switch(input.split(" = ")[0]){
                            case("Save Path"):
                                savePath = input.split(" = ")[1];
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("ImageWindowController::loadSettings \n Error reading settings \n");
                    System.out.println(e.toString());
                }
            } catch (FileNotFoundException e){
                System.out.println("ImageWindowController::loadSettings \n settings.conf not found \n");
                System.out.println(e.toString());
            }

        }
    }
}
