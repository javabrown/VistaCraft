package com.javabrown.core.utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class VirtualMonitorExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a label for the virtual monitor
        Label label = new Label("This is the Virtual Monitor");

        // Create a StackPane as the root node
        StackPane root = new StackPane();
        root.getChildren().add(label);

        // Create a Scene with the StackPane as the root node
        Scene virtualMonitorScene = new Scene(root, 800, 600);

        // Set the primary stage to the primary monitor
        primaryStage.setScene(virtualMonitorScene);

        // Find the bounds of the primary screen
        Screen primaryScreen = Screen.getPrimary();
        primaryStage.setX(primaryScreen.getBounds().getMinX());
        primaryStage.setY(primaryScreen.getBounds().getMinY());
        primaryStage.setWidth(primaryScreen.getBounds().getWidth());
        primaryStage.setHeight(primaryScreen.getBounds().getHeight());

        // Show the primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
