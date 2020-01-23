package com.no.loliSnatcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        WindowManager windowManager = new WindowManager();
        windowManager.searchWindowLoader(primaryStage);

    }
    public static void main(String[] args){
        launch(args);

    }
}


/**
 *
 *
 * //ImageView imageView = new ImageView(url);
 //imageView.setFitHeight(100);
 //imageView.setFitWidth(100);

 HBox layout = new HBox();
 ArrayList<ImageView> images = new ArrayList<>();
 for (int i= 0 ; i <= 20 ; i++){
 //ImageView imageView = new ImageView(fetched.get(i).fileURL);
 //imageView.setFitHeight(100);
 //imageView.setFitWidth(100);
 images.add(new ImageView(fetched.get(i).fileURL));
 images.get(i).setFitHeight(200);
 images.get(i).setPreserveRatio(true);
 layout.getChildren().add(images.get(i));
 } */