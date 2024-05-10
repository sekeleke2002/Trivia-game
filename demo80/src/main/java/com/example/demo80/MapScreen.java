package com.example.demo80;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MapScreen {

    public void display(Stage primaryStage) {
        StackPane puzzlePane = new StackPane();
        Scene puzzleScene = new Scene(puzzlePane, 800, 600);

        // Placeholder content for the puzzle screen
        // You can customize this according to your puzzle mini-game

        primaryStage.setScene(puzzleScene);
        primaryStage.setTitle("Puzzle Mini-Game");
        primaryStage.show();
    }
}
