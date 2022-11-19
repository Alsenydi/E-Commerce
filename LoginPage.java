import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginPage extends Application {
	private Stage primaryStage;

	public static void main(String[] args) {
        launch(args);
    }
	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	this.primaryStage = primaryStage;
       
    	primaryStage.setTitle("Login Page");

        // Create the registration form grid pane
        GridPane gridPane = createLogingFormPane();
        // Add UI controls to the registration form grid pane
        addUIControls(gridPane);
        // Create a scene with registration form grid pane as the root node
        Scene scene = new Scene(gridPane, 550, 500);
		
        // Set the scene in primary stage	
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }


    private GridPane createLogingFormPane() {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);

        // Set a padding of 40px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(80, 80, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return gridPane;
    }

    private void addUIControls(GridPane gridPane) {
      
    	// Add User Name Label
        Label userNameLabel = new Label("User Name : ");
        gridPane.add(userNameLabel, 0,1);

        // Add User Name Text Field
        TextField userNameField = new TextField();
        userNameField.setPrefHeight(30);
        gridPane.add(userNameField, 1,1);

      
        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 3);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(30);
        gridPane.add(passwordField, 1, 3);
 
        
        // Add Submit Button
        Button loginButton = new Button("Submit");
        loginButton.setPrefHeight(30);
        loginButton.setDefaultButton(true);
        loginButton.setPrefWidth(100);
        
        gridPane.add(loginButton, 0, 5, 2, 1); // spans for 2 column
        GridPane.setHalignment(loginButton, HPos.CENTER); 
        GridPane.setMargin(loginButton, new Insets(20, 0,20,0));

        // Login button action event handler
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	// Checks if any userNameField empty or not
            	if(userNameField.getText().isEmpty()) {
            		Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter your username");
                    return;
                }

            	// Checks if any passwordField empty or not
            	if(passwordField.getText().isEmpty()) {
                	Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a password");
                    return;
                }
                
            	
                try {
                	// If all fields contains data then fetch user data from database
                	User user = DatabaseManager.getInstance().getUser(userNameField.getText(), passwordField.getText());
                	Helpers.setCurrentUser(user); // save user data for further user
                	new HomePage().start(primaryStage); //go to home page
    			} catch (Exception e) {
    				Helpers.showAlert(Alert.AlertType.ERROR, "Login Error!", e.getMessage());
    			}
            }
        });
    }
}