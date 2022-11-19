import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

//This class contains UI creation & related method for 
public class AddUserPage extends Application { 
	private USER_TYPE userType;
	private Button submitButton;
	private Button backButton;
	private Stage primaryStage;
	
	// Constructor method
	public AddUserPage(USER_TYPE userType) {
		this.userType = userType;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	this.primaryStage = primaryStage;
       
    	primaryStage.setTitle("User Add Page");

        // Create the registration form grid pane
        GridPane gridPane = createRegistrationFormPane();
        // Add UI controls to the registration form grid pane
        addUIControls(gridPane);
        // Create a scene with registration form grid pane as the root node
        Scene scene = new Scene(gridPane, 500, 500);
		
        // Set the scene in primary stage	
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }


    private GridPane createRegistrationFormPane() {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);

        // Set a padding of 20px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
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

        
        // Add First Name Label
        Label firstNameLabel = new Label("First Name : ");
        gridPane.add(firstNameLabel, 0,2);

        // Add First Name Text Field
        TextField firstNameField = new TextField();
        firstNameField.setPrefHeight(30);
        gridPane.add(firstNameField, 1,2);


        // Add Last Name Label
        Label lastNameLabel = new Label("Last Name : ");
        gridPane.add(lastNameLabel, 0,3);

        // Add last Name Text Field
        TextField lastNameField = new TextField();
        firstNameField.setPrefHeight(30);
        gridPane.add(lastNameField, 1,3);

        
        // Add Email Label
        Label emailLabel = new Label("Email : ");
        gridPane.add(emailLabel, 0, 4);

        // Add Email Text Field
        TextField emailField = new TextField();
        emailField.setPrefHeight(30);
        gridPane.add(emailField, 1, 4);

        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 5);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(30);
        gridPane.add(passwordField, 1, 5);

        
        // Drop down combo box for position selection
        ObservableList<String> options = FXCollections.observableArrayList(
                USER_TYPE.EMPLOYEE.toString(),
                USER_TYPE.MANAGER.toString()
            );
        final ComboBox<String> comboBox = new ComboBox<String>(options);
        comboBox.getSelectionModel().select(0);
 
        TextField addressField = new TextField();
       
        if (userType == USER_TYPE.CUSTOMER) { // if user is customer then add address
        	// Add Name Label
            Label addressLabel = new Label("Address : ");
            gridPane.add(addressLabel, 0,6);

            // Add Name Text Field
            addressField.setPrefHeight(30);
            gridPane.add(addressField, 1,6);
        } else {// else add position 
        	// Add Name Label
            Label addressLabel = new Label("Position : ");
            gridPane.add(addressLabel, 0,6);

            // Add Name Text Field   
            comboBox.setPrefHeight(30);
            comboBox.setPrefWidth(320);
            
            firstNameField.setPrefHeight(30);
            gridPane.add(comboBox, 1,6);
        }
     
        
        HBox hBox = new HBox(20);
        
        // Create Back Button
        backButton = new Button("Back");
        backButton.setPrefHeight(30);
        backButton.setDefaultButton(true);
        backButton.setPrefWidth(100);
        
        
        // Create Submit Button
        submitButton = new Button("Submit");
        submitButton.setPrefHeight(30);
        submitButton.setPrefWidth(100);
        
        // Add buttons in horizontal box
        hBox.getChildren().add(backButton);
        hBox.getChildren().add(submitButton);
        hBox.setAlignment(Pos.CENTER);
        
        gridPane.add(hBox, 0, 9, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));

        // Back button action event handler
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	new HomePage().start(primaryStage);
            }
        });
        
        // Submit button action event handler       
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//checks if any input fields are empty. if empty then show alert message
            	if(userNameField.getText().isEmpty()) {
            		Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter user name");
                    return;
                }
            	if(firstNameField.getText().isEmpty()) {
            		Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter first name");
                    return;
                }
            	if(lastNameField.getText().isEmpty()) {
            		Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter last name");
                    return;
                }
                if(emailField.getText().isEmpty()) {
                	Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter your email id");
                    return;
                }
                if(passwordField.getText().isEmpty()) {
                	Helpers.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a password");
                    return;
                }
                
                String role;
                
                if (userType == USER_TYPE.CUSTOMER) {
                	role = USER_TYPE.CUSTOMER.toString();
                } else { // For employee there position
                	role = comboBox.getSelectionModel().getSelectedItem();
                }
                
                
                try {
                	// Insert form data into database user table
					DatabaseManager.getInstance().addUser(userNameField.getText(), firstNameField.getText(), lastNameField.getText(), 
							emailField.getText(), passwordField.getText(), addressField.getText(), role);

	                Helpers.showAlert(Alert.AlertType.CONFIRMATION, "Registration Successful!", "User Successfully Registered in System !" );
	                
	                // Clear all input fields
	                userNameField.clear();
	                firstNameField.clear();
	                lastNameField.clear();
	                emailField.clear();
	                passwordField.clear();
	                addressField.clear();
	                
				} catch (Exception e) {
					Helpers.showAlert(Alert.AlertType.ERROR, "Register Error!", e.getMessage());
				}
                

            }
        });
    }

}