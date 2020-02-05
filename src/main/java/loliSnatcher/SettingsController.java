package loliSnatcher;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Controller for the settings window
 */
public class SettingsController extends Controller {
    @FXML
    private GridPane globalOpts, searchOpts,snatcherOpts,booruOpts;
    @FXML
    private TextField searchLimitField,defaultTagsField,savePathField,booruNameField,baseURLField,faviconField,fileNameField,sleepField,previewColumnsField;
    private ComboBox<String> previewMode;
    @Override public void setStage(Stage stage) {

        this.stage = stage;

        previewMode = new ComboBox<String>();
        previewMode.getItems().add("Thumbnail");
        previewMode.getItems().add("Sample");
        searchOpts.add(previewMode,1,2);
        previewMode.getSelectionModel().select(0);
    }
    /**
     * Update will check if a settings file exists and then either load default values or values from the file
     */
    @Override
    public void updateSettings(){
        settingsFile = new File(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf");
        loadDefaults();
        if (settingsFile.exists()){loadSettings();}
    }

    /**
     * Set default values for the text fields
     */
    private void loadDefaults(){
        savePathField.setText(System.getProperty("user.home")+"/Pictures/");
        searchLimitField.setText("20");
        fileNameField.setText("/$SEARCH[1]/$HASH.$EXT");
        sleepField.setText("250");
        previewColumnsField.setText("6");
    }

    /**
     * Read settings from settings.conf and set the text field texts
     */
    private void loadSettings(){
        String input;
        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home")+"/.loliSnatcher/config/settings.conf"));
            System.out.println("reading settings");
            try {
                while ((input = br.readLine()) != null){
                    //Splits line and then switches on the option name
                    if (input.split(" = ").length > 1) {
                        switch (input.split(" = ")[0]) {
                            case ("Save Path"):
                                savePathField.setText(input.split(" = ")[1]);
                                break;
                            case ("Limit"):
                                searchLimitField.setText(input.split(" = ")[1]);
                                break;
                            case ("Default Tags"):
                                defaultTagsField.setText(input.split(" = ")[1]);
                                break;
                            case ("File Name"):
                                fileNameField.setText(input.split(" = ")[1]);
                                break;
                            case ("Sleep Time"):
                                sleepField.setText(input.split(" = ")[1]);
                                break;
                            case ("Preview Mode"):
                                if (input.split(" = ")[1].equals("Sample")) {
                                    previewMode.getSelectionModel().select(1);
                                }
                                break;
                            case ("Preview Columns"):
                                previewColumnsField.setText(input.split(" = ")[1]);
                                break;
                        }
                    }
                }
                System.out.println("EOF");
            } catch (IOException e) {
                System.out.println("SettingsController::loadSettings \n Error reading settings \n");
                System.out.println(e.toString());
            } catch (ArrayIndexOutOfBoundsException e){
                //Swallow since if this is thrown the setting is empty
                e.printStackTrace();
            }
        } catch (FileNotFoundException e){
            System.out.println("SettingsController::loadSettings \n settings.conf not found \n");
            System.out.println(e.toString());
        }
    }

    /**
     * Writes settings to settings.conf
     */
    @FXML
    private void writeSettings() {
        boolean settingsError = false;
        String errorString = "Please Check The Following Settings: ";
        try {
            validateDir(System.getProperty("user.home")+"/.loliSnatcher/config/");

            if (!booruNameField.getText().isEmpty()){
                String booruType = testBooru();
                if (!booruType.isEmpty()) {
                    try {
                        FileWriter fw = new FileWriter(System.getProperty("user.home")+"/.loliSnatcher/config/" + booruNameField.getText() + ".booru");
                        System.out.println("Writing file " + booruNameField.getText() + ".booru");
                        fw.write("Booru Name" + " = " + booruNameField.getText());
                        fw.write(System.lineSeparator());
                        fw.write("Booru Type" + " = " + booruType);
                        fw.write(System.lineSeparator());
                        fw.write("Favicon URL" + " = " + faviconField.getText());
                        fw.write(System.lineSeparator());
                        fw.write("Base URL" + " = " + baseURLField.getText());
                        fw.write(System.lineSeparator());
                        fw.flush();
                        fw.close();
                    } catch(IOException e) {
                        System.out.println("\nSettingsController::writeSettings::Booru\n");
                        System.out.println(e.toString());
                    }
                }
            }
            FileWriter fw  = new FileWriter(settingsFile);
            validateDir(savePathField.getText());
            fw.write("Save Path" + " = " + savePathField.getText());
            fw.write(System.lineSeparator());
            fw.write("File Name" + " = " + fileNameField.getText());
            fw.write(System.lineSeparator());
            fw.write("Preview Mode" + " = " + previewMode.getSelectionModel().getSelectedItem());
            fw.write(System.lineSeparator());
            // Check if limit is within bounds
            if (isNum(searchLimitField.getText().replace(" ",""))) {
                if (Integer.parseInt(searchLimitField.getText().replace(" ","")) <= 0 || Integer.parseInt(searchLimitField.getText().replace(" ","")) > 100) {
                    //set limit field red
                    searchLimitField.setStyle("-fx-control-inner-background: #ff0000");
                    errorString += "\n Limit";
                    settingsError = true;
                } else {
                    fw.write("Limit" + " = " + searchLimitField.getText());
                    fw.write(System.lineSeparator());
                }
            }
            fw.write("Default Tags" + " = " + defaultTagsField.getText());
            fw.write(System.lineSeparator());
            if (Long.parseLong(sleepField.getText().replace(" ","")) >= 0){
                fw.write("Sleep Time" + " = " + sleepField.getText());
                fw.write(System.lineSeparator());
            }
            // Validate preview columns
            if (isNum(previewColumnsField.getText().replace(" ",""))) {
                if (Integer.parseInt(previewColumnsField.getText().replace(" ","")) <= 0) {
                    //set limit field red
                    previewColumnsField.setStyle("-fx-control-inner-background: #ff0000");
                    errorString += "\n Preview Columns";
                    settingsError = true;
                } else {
                    fw.write("Preview Columns" + " = " + previewColumnsField.getText());
                    fw.write(System.lineSeparator());
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e){
            System.out.println("\nSettingsController::writeSettings\n");
            System.out.println(e.toString());
        }
        // Show error alert with offending settings names
        if (settingsError){
            Alert alert = new Alert(Alert.AlertType.ERROR,errorString, ButtonType.CLOSE);
            alert.show();
        }

    }

    /**
     * Called when a settings group button is clicked. This will hide the gridpanes for options and display one depending
     * on which button was clicked
     * @param event
     */
    @FXML
    private void switchOpts(Event event){
        //Get the button that called the function
        Button clicked = (Button) event.getSource();
        //Hide all options
        globalOpts.setVisible(false);
        searchOpts.setVisible(false);
        snatcherOpts.setVisible(false);
        booruOpts.setVisible(false);
        //Display options based on button id
        switch (clicked.getId()){
            case ("globalButton"):
                globalOpts.setVisible(true);
                break;
            case ("searchButton"):
                searchOpts.setVisible(true);
                break;
            case ("snatcherButton"):
                snatcherOpts.setVisible(true);
                break;
            case ("booruButton"):
                booruOpts.setVisible(true);
                break;
        }
    }
    private boolean isNum(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    @FXML
    private String testBooru(){

        String booruType = "";

        // Tests
        if (!baseURLField.getText().isEmpty()) {
            // Test Gelbooru
            BooruHandler testHandler = new GelbooruHandler(1,baseURLField.getText());
            ArrayList<BooruItem> testFetched = testHandler.Search("");
            if (testFetched == null || testFetched.size() == 0) {
                System.out.println("No Results");
            } else {
                System.out.println(testFetched.get(0));
                booruType = "Gelbooru";
            }
            //Test Danbooru
            if (booruType.isEmpty()) {
                testHandler = new DanbooruHandler(1, baseURLField.getText());
                testFetched = testHandler.Search("");
                if (testFetched == null || testFetched.size() == 0) {
                    System.out.println("No Results");
                } else {
                    System.out.println(testFetched.get(0));
                    booruType = "Danbooru";
                }
            }
            //Test MoeBooru
            if (booruType.isEmpty()) {
                testHandler = new MoebooruHandler(1, baseURLField.getText());
                testFetched = testHandler.Search("");
                if (testFetched == null || testFetched.size() == 0) {
                    System.out.println("No Results");
                } else {
                    System.out.println(testFetched.get(0));
                    booruType = "Moebooru";
                }
            }
        }

        // Loop through booru textfields and set colour red if they are empty
        for (int i = 0; i < booruOpts.getChildren().size(); i++){
            Node node = booruOpts.getChildren().get(i);
            if (node.getClass().toString().equals("class javafx.scene.control.TextField") ){
                if (((TextField) node).getText().isEmpty()){
                    ((TextField) node).setStyle("-fx-control-inner-background: #ff0000");
                    booruType = "";
                }
            }
        }
        //Set URL Field red if the test fails
        if (!booruType.isEmpty()){
            baseURLField.setStyle("-fx-control-inner-background: #00ff00");
        } else {
            baseURLField.setStyle("-fx-control-inner-background: #ff0000");
        }
        System.out.println(booruType);
        return booruType;

    }

}
