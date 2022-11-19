import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PendingOrder extends Application {
	private TableView<OrderModel> tableView;
	private final static String TABLE_CELL_CENTER = "-fx-alignment: CENTER;";

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Pending Order Page");
		tableView = new TableView<OrderModel>();

		// Table View table column header
		TableColumn<OrderModel, Integer> oderID = new TableColumn<>("Order ID");
		TableColumn<OrderModel, String> userName = new TableColumn<>("User Name");
		TableColumn<OrderModel, Integer> totalPrice = new TableColumn<>("Total Price");
		TableColumn<OrderModel, Integer> itemCount = new TableColumn<>("Item Count");
		TableColumn<OrderModel, HBox> action = new TableColumn<>("Action");

		// Binds Table View table column with model field
		oderID.setCellValueFactory(new PropertyValueFactory<>("oderID"));
		userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
		itemCount.setCellValueFactory(new PropertyValueFactory<>("itemCount"));
		totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
		action.setCellValueFactory(new PropertyValueFactory<>("action"));

		// Add columns in table view
		tableView.getColumns().add(oderID);
		tableView.getColumns().add(userName);
		tableView.getColumns().add(itemCount);
		tableView.getColumns().add(totalPrice);
		tableView.getColumns().add(action);

		//Center text alignment of table column 
		oderID.setStyle(TABLE_CELL_CENTER);
		userName.setStyle(TABLE_CELL_CENTER);
		itemCount.setStyle(TABLE_CELL_CENTER);
		totalPrice.setStyle(TABLE_CELL_CENTER);
		action.setStyle(TABLE_CELL_CENTER);

		// Table column size will be automatically adjust
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Back button add
		Button backBtn = new Button("Back");
		backBtn.setPrefHeight(30);
		backBtn.setPrefWidth(100);
		backBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new HomePage().start(primaryStage);
			}
		});
		
		// Add back & table view in vertical box
		VBox vbox = new VBox(20);
		vbox.getChildren().add(backBtn);
		vbox.getChildren().add(tableView);

		// Vertical box padding and alignment configuration
		vbox.setPadding(new Insets(20, 20, 20, 20));
		vbox.setAlignment(Pos.CENTER);
		
		// create new scene with vertical box
		Scene scene = new Scene(vbox, 550, 500);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		loadItems();
	}

	// This method all items in table view
	private void loadItems() {
		
		// Loading item done on parallel thread for not making the UI hang
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					// Loops through the pending order list fetched from database
					for (OrderModel order : DatabaseManager.getInstance().getPendingOrders()) {
						addItem(order);
					}
				} catch (SQLException e) {
					Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
				}
			}
		});	
	}
	
	private void checkItemsAvailable(OrderModel orderModel) throws Exception {
		HashMap<Integer, ItemModel> itemsMap =  Helpers.getItemsMap();
		
		Cart cart = DatabaseManager.getInstance().getCart(orderModel.getUserName(), CART_STATUS.PENDING);
		
		if (cart != null) {
			for (ItemModel item : cart.getItems()) {
				ItemModel product = itemsMap.get(item.getItemID());
				if(product == null) {
					throw new Exception("Product may have been deleted");
				} else if(product.getQuantity() < item.getQuantity()) {
					throw new Exception("Item Name : " + item.getItemName() + ", checkout count is more than available count");
				}
			}
		}
	}
	

	// This method is used to add order in table view
	private void addItem(OrderModel orderModel) {
		ComboBox<String> comboBox = getComboBox();
		orderModel.setAction(comboBox);
		
		//Combo box item selection change listener
		comboBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			//Show confirmation alert
			Optional<ButtonType> result = Helpers.showAlert(Alert.AlertType.CONFIRMATION, "Delete!", "Do you want to change status to " + newValue + "?");
			if(result.get() == ButtonType.OK) { // If pressed Ok in the alert message
				try {
					System.out.println(newValue);
					if (newValue.equals(CART_STATUS.DELIVERED.toString())) { //if deliver option selected then check product availability
						checkItemsAvailable(orderModel);
					}
					
					// Change order status in database
					DatabaseManager.getInstance().changeChartStatus(orderModel.getOderID(), newValue);
					
					Helpers.showAlert(Alert.AlertType.INFORMATION, "Success", "Status changed successfully");
					tableView.getItems().remove(orderModel); // removes order from table view
				} catch (Exception e) {
					Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
				}
			}
		}); 
		
		tableView.getItems().add(orderModel);
	}

	// This method helps to create a combo box with different status
	private static ComboBox<String> getComboBox() {
		ObservableList<String> list = FXCollections.observableArrayList(CART_STATUS.PENDING.toString(), 
				CART_STATUS.DELIVERED.toString(), CART_STATUS.REJECTED.toString());

		ComboBox<String> comboBox = new ComboBox<String>(list); 
		comboBox.getSelectionModel().select(CART_STATUS.PENDING.toString()); // select PENDING as default
		return comboBox;
	}

}

