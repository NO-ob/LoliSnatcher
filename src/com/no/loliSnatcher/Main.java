package com.no.loliSnatcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loliSnatcher.fxml"));
        primaryStage.setTitle("Loli Snatcher");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

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