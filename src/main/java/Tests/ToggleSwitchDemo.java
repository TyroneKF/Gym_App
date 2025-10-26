
package Tests;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToggleSwitchDemo extends Application
{
    
    private boolean switchedOn = false;
    
    @Override
    public void start(Stage stage)
    {
        Label label = new Label("OFF");
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold;");
        
        // Background track
        StackPane background = new StackPane();
        background.setPrefSize(60, 30);
        background.setStyle("-fx-background-color: #ccc; -fx-background-radius: 30px;");
        
        // Circular knob
        Circle circle = new Circle(15);
        circle.setFill(Color.WHITE);
        circle.setTranslateX(- 15); // start position
        
        // Combine track + circle + label
        StackPane switchPane = new StackPane(background, circle, label);
        switchPane.setPadding(new Insets(10));
        switchPane.setOnMouseClicked(this :: handleToggle);
        
        // Animate circle sliding
        TranslateTransition moveToRight = new TranslateTransition(Duration.seconds(0.25), circle);
        moveToRight.setToX(15);
        
        TranslateTransition moveToLeft = new TranslateTransition(Duration.seconds(0.25), circle);
        moveToLeft.setToX(- 15);
        
        // When clicked, animate + change color
        switchPane.setOnMouseClicked(event -> {
            if (switchedOn)
            {
                background.setStyle("-fx-background-color: #ccc; -fx-background-radius: 30px;");
                label.setText("OFF");
                label.setTextFill(Color.WHITE);
                moveToLeft.play();
                switchedOn = false;
            }
            else
            {
                background.setStyle("-fx-background-color: #4cd964; -fx-background-radius: 30px;");
                label.setText("ON");
                label.setTextFill(Color.WHITE);
                moveToRight.play();
                switchedOn = true;
            }
        });
        
        Scene scene = new Scene(new StackPane(switchPane), 200, 100, Color.LIGHTGRAY);
        stage.setTitle("Circular Toggle Switch");
        stage.setScene(scene);
        stage.show();
    }
    
    private void handleToggle(MouseEvent event)
    {
        // handled in lambda
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
}

