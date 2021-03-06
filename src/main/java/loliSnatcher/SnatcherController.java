package loliSnatcher;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import java.io.*;
import java.util.ArrayList;

/**
 * Controller for the Snatcher window this is what handles the batch downloading [Snatching]
 */
public class SnatcherController extends Controller{
    Boolean DBEnabled = false;
    @FXML
    GridPane main;
    @FXML
    TextField tagsField;
    @FXML
    TextField dirField;
    @FXML
    TextField amountField;
    @FXML
    TextField fileNameField,sleepField;
    @FXML
    Label fileNameLabel;
    @FXML
    Label progress;
    int limit=20;


    @FXML
    public void snatch(){
        String tags = tagsField.getText();
        String dirPath = dirField.getText();
        String fileName = fileNameField.getText();
        String searchTags = tagsField.getText();
        long timeout = Long.parseLong(sleepField.getText());
        int amount = Integer.parseInt(amountField.getText());

        validateDir(dirPath);

        //Sets limit to 100 if bigger than as gelbooru only allows for 100 items per page
        if (amount <= 100){limit = amount;} else {limit = 100;}

        Booru selected = (Booru) booruSelector.getValue();
        booruHandler = getBooruHandler(selected,limit);


        ArrayList<BooruItem> fetched = null;
        progress.setText("Snatching Images");

        // Loads pages consecutively until the desired amount of images have been fetched
        while (amount > 0){
            fetched = booruHandler.Search(tags);
            System.out.println(fetched.size() + " " + amount);
            amount -= booruHandler.limit;
        }

        ArrayList<BooruItem> finalFetched = fetched;
        // Task  is used so this bit of code is run on a seperate thread to the GUI otherwise it freezes until this work is done
        Task writer = new Task<Void>(){
            @Override public Void call() {
                if(finalFetched.size() == 0){
                    updateTitle("No Results .·´¯`(>▂<)´¯`·.");
                }
                for (int i = 0; i < finalFetched.size(); i++){
                    BooruItem item = finalFetched.get(i);
                    //Updates the task progress
                    updateProgress(i, finalFetched.size());
                    // Skips item if it is webm or gif as the javafx image objects cant display them
                        // Updates the title of the task with the image url
                        updateTitle(item.getFileURL().substring(item.getFileURL().lastIndexOf("/") + 1));
                        ImageWriter imageWriter = new ImageWriter();
                        imageWriter.writeImage(imageWriter.makeFile(dirPath,fileName,item,searchTags),DBEnabled);
                    try {
                        updateTitle("");
                        Thread.sleep(timeout);
                    }
                    catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                updateTitle("Snatching Complete ＼(^ o ^)／");
                return null;
            }

        };

        // Displays progress and filename in the window
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(writer.progressProperty());
        fileNameLabel.textProperty().bind(writer.titleProperty());
        bar.prefWidthProperty().bind(main.widthProperty().multiply(0.7));
        main.add(bar,0,11,2,1);
        /** Remove progress bar when task has finished as a progress bar
         * makes the PC lock up when it is not being updated
         */
        writer.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                main.getChildren().remove(bar);
            }
        });
        writer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                main.getChildren().remove(bar);
            }
        });
        Thread writerThread = new Thread(writer);
        writerThread.start();


    }
    public void setTags(String tags) {
        tagsField.setText(tags);
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
                        switch(input.split(" = ")[0]){
                            case("Save Path"):
                                dirField.setText(input.split(" = ")[1]);
                                break;
                            case("File Name"):
                                fileNameField.setText(input.split(" = ")[1]);
                                break;
                            case("Sleep Time"):
                                sleepField.setText(input.split(" = ")[1]);
                                break;
                            case("Local Database"):
                                DBEnabled = Boolean.parseBoolean(input.split(" = ")[1]);
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("SearchController::loadSettings \n Error reading settings \n");
                    System.out.println(e.toString());
                }
            } catch (FileNotFoundException e){
                System.out.println("SearchController::loadSettings \n settings.conf not found \n");
                System.out.println(e.toString());
            }

        }
    }
}
