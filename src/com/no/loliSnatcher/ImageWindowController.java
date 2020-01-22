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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** Controller for the window which will display the full sized image of the sample selected in the search window
 *
 */
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
    /**
     * Does tasks which need to be done on window creation, this cant be done when the controller instance is created
     * because the GUI doesn't exist at that point
     **/
    public void setStage(Stage stage){
        this.stage = stage;

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
        fullImage.setImage(new Image(imageItem.fileURL));
        fullImage.setFitWidth(stage.getHeight()*0.8);
        fullImage.setPreserveRatio(true);

    }
    public void setTags(){
        ObservableList<String> splitTags = FXCollections.observableArrayList(imageItem.tags.split(" "));
        tagList.setItems(splitTags);
        /**When a specific tag is clicked on it will call model.put tag to parse it to the search controller
         * which will add it to the search field
         */
        tagList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + tagList.getSelectionModel().getSelectedItem());
                model.putTag((String) tagList.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** Creates a new file and then writes an image to it. It writes the image thats in an image view instead of getting the
     * image from its url and writing that to save on memory
     */
    @FXML
    private void saveImage(){

        File imageFile = new File(System.getProperty("user.home")+"/Pictures/test."+ imageItem.fileURL.substring(imageItem.fileURL.lastIndexOf("/")+1));
        BufferedImage image = SwingFXUtils.fromFXImage(fullImage.getImage(),null);

        try {
            ImageIO.write(image, imageItem.fileURL.substring(imageItem.fileURL.lastIndexOf(".")+1),imageFile);

        } catch (IOException e){
            throw new RuntimeException();
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
                Desktop.getDesktop().browse(new URI(imageItem.postURL));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else{
            // Linux opening - should also work in MacOS
            try {
                Runtime.getRuntime().exec("xdg-open " + imageItem.postURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
