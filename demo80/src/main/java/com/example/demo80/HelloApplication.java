package com.example.demo80;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0; // Track the number of correct answers
    private int totalQuestions = 0; // Track the total number of questions attempted
    private Timer questionTimer;
    private int elapsedTime = 0; // Elapsed time in seconds

    private Label questionLabel;
    private ImageView imageView;
    private RadioButton[] options;
    private Button submitButton;
    private Label feedbackLabel;
    private Label scoreLabel;
    private Button fiftyFiftyButton;
    private Button hintButton;
    private Label timerLabel;
    private Button restartButton;

    private MediaPlayer mediaPlayer;

    private TriviaQuestion[] triviaQuestions = {

            new TriviaQuestion(
                    "What is the capital city of Lesotho?",
                    new Image(getClass().getResourceAsStream("/Maseru.jpg")),
                    new String[]{"Maseru", "Quthing", "Butha Buthe", "Leribe"},
                    "Maseru"
            ),
            new TriviaQuestion(
                    "What is the official language of Lesotho?",
                    new Image(getClass().getResourceAsStream("/pere.jpg")),
                    new String[]{"English", "Chinese", "Sesotho", "Zulu"},
                    "Sesotho"
            ),
            new TriviaQuestion(
                    "What is the name of the traditional Basotho blanket?",
                    new Image(getClass().getResourceAsStream("/seanamarena.jpg")),
                    new String[]{"Mink", "Delela", "Seanamarena", "Bed"},
                    "Seanamarena"
            ),
            new TriviaQuestion(
                    "Which waterfall in Lesotho is one of the highest single-drop waterfalls in Africa?",
                    new Image(getClass().getResourceAsStream("/Maletsunyane.jpg")),
                    new String[]{"Katse Dam", "Mohale Dam", "Maletsunyane Falls", "Muela Dam"},
                    "Maletsunyane Falls"
            ),
            new TriviaQuestion(
                    "What is the highest point in Lesotho?",
                    new Image(getClass().getResourceAsStream("/Thabana-Ntlenyana.jpg")),
                    new String[]{"Qiloane", "Thabana Ntlenyana", "Mount Moorosi", "Tsikooane"},
                    "Thabana Ntlenyana"
            ),
            new TriviaQuestion(
                    "What is the traditional attire for Basotho men called?",
                    new Image(getClass().getResourceAsStream("/mokorotlo.jpg")),
                    new String[]{"Seshoeshoe", "Mokorotlo", "Jacket", "Tuku"},
                    "Mokorotlo")
            // Add more questions here
    };

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #2ECC71;"); // Set background color to green
        Scene scene = new Scene(root, 800, 600);

        questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000; -fx-font-weight: bold;");
        questionLabel.setFont(Font.font("Arial", 20));

        imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);

        options = new RadioButton[4];
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(0, 0, 20, 0)); // Add padding to bottom
        for (int i = 0; i < 4; i++) {
            options[i] = new RadioButton();
            options[i].setToggleGroup(toggleGroup);
            options[i].setStyle("-fx-font-size: 14px;");
            optionsBox.getChildren().add(options[i]);
        }

        submitButton = new Button("Hand in");
        submitButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        submitButton.setOnAction(e -> checkAnswer());

        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF0000;");

        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        fiftyFiftyButton = new Button("50/50");
        fiftyFiftyButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        fiftyFiftyButton.setOnAction(e -> useFiftyFifty());

        hintButton = new Button("Hint");
        hintButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        hintButton.setOnAction(e -> showHint());

        timerLabel = new Label("Time: 00:00"); // Initial time
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        HBox lifelineBox = new HBox(20);
        lifelineBox.setAlignment(Pos.CENTER);
        lifelineBox.setPadding(new Insets(10));
        lifelineBox.getChildren().addAll(fiftyFiftyButton, hintButton, timerLabel);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        centerBox.getChildren().addAll(questionLabel, imageView, optionsBox, submitButton, feedbackLabel);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(scoreLabel);

        root.setTop(lifelineBox);
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        showQuestion();
        startQuestionTimer();
        playBackgroundMusic();
        animateFlag(); // Adding animation for flag

        primaryStage.setScene(scene);
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.show();
    }

    private void startQuestionTimer() {
        questionTimer = new Timer();
        questionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    elapsedTime++;
                    updateTimerLabel();
                    if (elapsedTime >= 120) {
                        questionTimer.cancel();
                        showTimeUpMessage();
                    }
                });
            }
        }, 0, 1000); // Update every second
    }

    private void showTimeUpMessage() {
    }

    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeString);
    }

    private void useFiftyFifty() {
        // Implement logic to eliminate two incorrect options
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        String[] optionsText = currentQuestion.getOptions();
        int correctOptionIndex = -1;
        for (int i = 0; i < optionsText.length; i++) {
            if (optionsText[i].equals(currentQuestion.getCorrectAnswer())) {
                correctOptionIndex = i;
                break;
            }
        }
        int optionToKeep = correctOptionIndex;
        while (optionToKeep == correctOptionIndex) {
            optionToKeep = (int) (Math.random() * 4);
        }
        for (int i = 0; i < options.length; i++) {
            if (i != correctOptionIndex && i != optionToKeep) {
                options[i].setVisible(false);
            }
        }
        fiftyFiftyButton.setDisable(true);
    }

    private void showHint() {
        // Implement logic to provide a hint
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        String correctAnswer = currentQuestion.getCorrectAnswer();
        String hint = correctAnswer.substring(0, 1);
        feedbackLabel.setText("Hint: The first letter of the answer is '" + hint + "'.");
        hintButton.setDisable(true);
    }

    private void showQuestion() {
        if (currentQuestionIndex < triviaQuestions.length) {
            TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
            int questionNumber = currentQuestionIndex + 1; // Calculate question number
            String questionText = "Question " + questionNumber + ": " + currentQuestion.getQuestion();
            questionLabel.setText(questionText);
            imageView.setImage(currentQuestion.getImage());

            String[] optionsText = currentQuestion.getOptions();
            for (int i = 0; i < optionsText.length; i++) {
                options[i].setText(optionsText[i]);
                options[i].setSelected(false);
                options[i].setVisible(true);
                options[i].setDisable(false); // Re-enable options in case they were disabled from previous questions
            }

            feedbackLabel.setText("");
            submitButton.setDisable(false);
            fiftyFiftyButton.setDisable(false);
            hintButton.setDisable(false);
            elapsedTime = 0; // Reset the timer
            updateTimerLabel();
            totalQuestions++; // Increment total questions attempted
        } else {
            endGame();
        }
    }

    private void checkAnswer() {
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        RadioButton selectedOption = null;

        // Find the selected option
        for (RadioButton option : options) {
            if (option.isSelected()) {
                selectedOption = option;
                break;
            }
        }

        // If no option is selected, display an error message
        if (selectedOption == null) {
            feedbackLabel.setText("Please select an answer.");
            return;
        }

        // Check if the selected option is correct
        if (selectedOption.getText().equals(currentQuestion.getCorrectAnswer())) {
            score++;
            correctAnswers++; // Increment correct answers count
            feedbackLabel.setText("Correct! " + getExplanation(currentQuestion));
        } else {
            feedbackLabel.setText("Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer() +
                    ". " + getExplanation(currentQuestion));
        }

        // Move to the next question
        currentQuestionIndex++;
        scoreLabel.setText("Score: " + score);
        submitButton.setDisable(true);

        // Disable all options after the user has answered
        for (RadioButton option : options) {
            option.setDisable(true);
        }

        showQuestion();
    }

    private void endGame() {
        double percentage = (double) correctAnswers / totalQuestions * 100; // Calculate percentage
        String feedbackMessage;
        if (percentage >= 90) {
            feedbackMessage = "Fabulous";
        } else if (percentage >= 80) {
            feedbackMessage = "Promising";
        } else {
            feedbackMessage = "Improve";
        }
        feedbackLabel.setText(String.format("You got %.2f%%. %s", percentage, feedbackMessage));

        // Display correct answers for all questions
        StringBuilder correctAnswers = new StringBuilder("Correct answers:\n");
        for (int i = 0; i < triviaQuestions.length; i++) {
            TriviaQuestion question = triviaQuestions[i];
            correctAnswers.append("Question ").append(i + 1).append(": ").append(question.getCorrectAnswer()).append("\n");
        }
        feedbackLabel.setText(feedbackLabel.getText() + "\n\n" + correctAnswers.toString());

        // Cancel the question timer
        questionTimer.cancel();

        // Add restart button
        restartButton = new Button("Play Again");
        restartButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        restartButton.setOnAction(e -> restartGame());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(scoreLabel, restartButton);

        root.setBottom(buttonBox);
    }

    private void restartGame() {
        currentQuestionIndex = 0;
        score = 0;
        correctAnswers = 0;
        totalQuestions = 0;
        questionTimer.cancel();
        showQuestion();
        startQuestionTimer();
        scoreLabel.setText("Score: " + score);
        feedbackLabel.setText("");
        root.getChildren().remove(restartButton);
    }

    private void playBackgroundMusic() {
        try {
            // Specify the path to your media file
            String mediaPath = getClass().getResource("/music.mp3").toExternalForm();

            // Create a Media object
            Media media = new Media(mediaPath);

            // Create a MediaPlayer
            mediaPlayer = new MediaPlayer(media);

            // Set autoPlay to true
            mediaPlayer.setAutoPlay(true);
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    private void animateFlag() {
        Image flagImage = new Image(getClass().getResourceAsStream("/flag.png"));
        ImageView flagView = new ImageView(flagImage);
        flagView.setFitWidth(150);
        flagView.setFitHeight(150);

        // Translate transition
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), flagView);
        translateTransition.setFromX(-200); // Start from left side
        translateTransition.setToX(200); // Move to right side
        translateTransition.setAutoReverse(true); // Move back to left
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Repeat indefinitely

        // Scale transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), flagView);
        scaleTransition.setToX(1.5); // Scale horizontally by 1.5
        scaleTransition.setToY(1.5); // Scale vertically by 1.5
        scaleTransition.setAutoReverse(true); // Reverse the scaling
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE); // Repeat indefinitely

        // Fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), flagView);
        fadeTransition.setFromValue(1.0); // Start with full opacity
        fadeTransition.setToValue(0.3); // Fade to 30% opacity
        fadeTransition.setAutoReverse(true); // Reverse the fading
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE); // Repeat indefinitely

        // Parallel transition
        ParallelTransition parallelTransition = new ParallelTransition(flagView,
                translateTransition, scaleTransition, fadeTransition);
        parallelTransition.play();

        // Add the flag to the top-right corner of the screen
        BorderPane.setAlignment(flagView, Pos.TOP_RIGHT);
        BorderPane.setMargin(flagView, new Insets(10));
        root.getChildren().add(flagView);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class TriviaQuestion {
        private String question;
        private Image image;
        private String[] options;
        private String correctAnswer;

        public TriviaQuestion(String question, Image image, String[] options, String correctAnswer) {
            this.question = question;
            this.image = image;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public Image getImage() {
            return image;
        }

        public String[] getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }
    }

    private String getExplanation(TriviaQuestion question) {
        // You can add explanations for each question if you want
        return "";
    }
}
