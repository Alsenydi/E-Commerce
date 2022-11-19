import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// This class is used to show user options as Buttons
public class HomePage extends Application implements EventHandler<ActionEvent>{
	private Stage primaryStage;
	private Button addEmployee;
	private Button addCustomer;
	private Button pendingOrder;
	private Button itemModification;
	private Button checkOut;
	private Button logout;
	
	// This methods creates button and configure height & width
	private void addButton(VBox vBox, Button button) {
		button.setPrefWidth(200);
		button.setPrefHeight(35);
		button.setOnAction(this);
		vBox.getChildren().add(button);
	}
	

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Home Page");

		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(70,70,70,70));

		//Adding HBox with spacing value
		VBox vBox = new VBox(25); 
		vBox.setPadding(new Insets(20,20,20,20));
	
		addEmployee = new Button("Add Employee");
		addCustomer = new Button("Add Customer");
		pendingOrder = new Button("Pending Order");
		itemModification = new Button("Item Modification");
		checkOut = new Button("Checkout Items");
		logout = new Button("Log Out");

		String role = Helpers.getCurrentUser().getRole();
		
		// User wise visible option selection
		if (role.equals(USER_TYPE.MANAGER.toString())) { //If login user is manager
			addButton(vBox, addEmployee);
			addButton(vBox, addCustomer);
			addButton(vBox, pendingOrder);
		} else if (role.equals(USER_TYPE.EMPLOYEE.toString())) {//If login user is employee
			addButton(vBox, addCustomer);
			addButton(vBox, itemModification);
			addButton(vBox, pendingOrder);
		}  else if (role.equals(USER_TYPE.CUSTOMER.toString())) { //If login user is customer
			addButton(vBox, addCustomer);
			addButton(vBox, checkOut);
		}
		
		addButton(vBox, logout);
	
		vBox.setAlignment(Pos.CENTER);
		//Add HBox and GridPane layout to BorderPane Layout
		borderPane.setCenter(vBox);

		//Adding BorderPane to the scene 
		Scene scene = new Scene(borderPane, 550, 500);
        
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	@Override
	public void handle(ActionEvent event) {
		try {
			// Home Page Buttons click action listener new page will open according to button click
			if (event.getSource() == logout) { 
				new LoginPage().start(primaryStage);
				Helpers.setCurrentUser(null); // If user logout then set null to current user
			} else if (event.getSource() == addCustomer) {
				new AddUserPage(USER_TYPE.CUSTOMER).start(primaryStage);
			}  else if (event.getSource() == addEmployee) {
				new AddUserPage(USER_TYPE.EMPLOYEE).start(primaryStage);
			} else if (event.getSource() == itemModification) {
				new ItemModPage().start(primaryStage);
			} else if (event.getSource() == pendingOrder) {
				new PendingOrder().start(primaryStage);
			} else if (event.getSource() == checkOut) {
				new CheckoutPage().start(primaryStage);
			}
		}catch (Exception e) {
			Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
		}		
	}

}