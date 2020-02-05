package loliSnatcher;
import javafx.application.Application;
import javafx.stage.Stage;

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

