package banking.view;

import banking.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnection.initializeDatabase();
        Parent root = FXMLLoader.load(getClass().getResource("/banking/view/LoginView.fxml"));
        primaryStage.setTitle("Banking System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
