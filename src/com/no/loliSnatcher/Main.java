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
        //Parent root = new FXMLLoader(getClass().getResource("Search.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("Search.fxml"));
        Parent root = searchLoader.load();
        Scene scene = new Scene(root);
        SearchController searchController = searchLoader.getController();
        searchController.setStage(primaryStage);
        primaryStage.setTitle("Loli Snatcher");
        primaryStage.setScene(scene);
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