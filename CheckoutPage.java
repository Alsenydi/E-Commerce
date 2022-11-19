import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CheckoutPage extends Application {
	private Cart cart;
	private Stage primaryStage;
	private TableView<ItemModel> searchTableView;
	private TableView<ItemModel> cartTableView;
	
	// Create button & configure height & width
	private Button getButton(String text) {
		Button button = new Button(text);
		button.setPrefHeight(30);
		button.setPrefWidth(100);
		return button;
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Checkout Page");
	
		// Item Name label configuration
		Label itemNameLablel = new Label("Item Name");
		itemNameLablel.setPrefHeight(30);
		
		// Item Name text Field configuration
		TextField itemNameField = new TextField();
		itemNameField.setPrefWidth(150);
		itemNameField.setPrefHeight(30);
		
		// Search button added 
		Button searchItemBtn = getButton("Search");
		searchItemBtn.disableProperty().bind(itemNameField.textProperty().isEqualTo(""));
		searchTableView = Helpers.getItemTableView();
		
		// Search button event listener
		searchItemBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrayList<ItemModel> result = Helpers.getItemsByName(itemNameField.getText());
				for (ItemModel itemModel : result) {
					addinSearchView(itemModel); // add result in table view
				}
			}
		});

		// Back button configuration
		Button backBtn = getButton("Back");
		
		// Back button event listener
		backBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new HomePage().start(primaryStage); // go back to home page
			}
		});
		
		
		HBox hBox = new HBox(20);
		hBox.setAlignment(Pos.CENTER);
		
		//Add Items in horizontal box
		hBox.getChildren().add(itemNameLablel);
		hBox.getChildren().add(itemNameField);
		hBox.getChildren().add(searchItemBtn);
		hBox.getChildren().add(backBtn);

		// Vertical box configuration and add item into it
		VBox vbox = new VBox(20);
		vbox.getChildren().add(hBox);
		vbox.getChildren().add(searchTableView);
		vbox.getChildren().add(new Label("Selected Items for Purchase"));
		cartTableView = Helpers.getItemTableView();
		vbox.getChildren().add(cartTableView);
		Button checkoutBtn = getButton("Checkout");
		
		// Checkout button action listener
		checkoutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {
				if (cartTableView.getItems().size() != 0) {
					try {
						DatabaseManager.getInstance().changeChartStatus(cart.getCartID(), CART_STATUS.PENDING.toString());
						int taotalPrice = 0, itemCount = 0;
						for (ItemModel item : cartTableView.getItems()) {
							taotalPrice += item.getQuantity()*item.getUnitPrice();
							itemCount += item.getQuantity();
						}
						Helpers.showAlert(AlertType.INFORMATION, "Success", "Your order for " + itemCount + " items " + taotalPrice  + "$ was completed successfully.");
						
						cartTableView.getItems().clear();
						cart = null;
					} catch (SQLException e) {
						Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
					}
					
				} 
			}
		});
		
		vbox.getChildren().add(checkoutBtn);
		
		vbox.setPadding(new Insets(20, 20, 20, 20));
		vbox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vbox, 550, 500);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		loadPendingCart();
	}

	private void loadPendingCart() {
		try {
			cart = DatabaseManager.getInstance().getCart(Helpers.getCurrentUser().getUserName(), CART_STATUS.TEMP);
			if (cart != null) {
				for (ItemModel item : cart.getItems()) {
					addToCart(item);
				}
			}
		} catch (SQLException e) {
			Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
		}
	}

	private void showAddFormDialog(ItemModel itemModel, boolean isNewAdd) {
		// initialize the dialog.
		int dialogWidth = 400, dialogHeight = 200;
		final Stage dialog = new Stage();
		dialog.setTitle("Item Information");
		dialog.initOwner(primaryStage);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setX(primaryStage.getX() + primaryStage.getWidth()/2-dialogWidth/2);
		dialog.setY(primaryStage.getY() + primaryStage.getHeight()/2-dialogHeight/2);

		// create a grid for the data entry.
		GridPane grid = new GridPane();
		final TextField itemNameField = new TextField();
		final NumberField quantityField = new NumberField();
		final NumberField priceField = new NumberField();
		itemNameField.setDisable(true);
		priceField.setDisable(true);
		grid.addRow(0, new Label("Item Name"), itemNameField);
		grid.addRow(1, new Label("Quantity"), quantityField);
		grid.addRow(2, new Label("Unit Price"), priceField);
		grid.setHgap(10);
		grid.setVgap(10);
		GridPane.setHgrow(itemNameField, Priority.ALWAYS);
		GridPane.setHgrow(quantityField, Priority.ALWAYS);
		GridPane.setHgrow(priceField, Priority.ALWAYS);

		// create action buttons for the dialog.
		Button saveBtn = new Button("Save");
		saveBtn.setDefaultButton(true);
		saveBtn.setPrefHeight(25);
		saveBtn.setPrefWidth(70);

		if (itemModel != null) {
			itemNameField.setText(itemModel.getItemName());
			quantityField.setText("" + itemModel.getQuantity());
			priceField.setText("" + itemModel.getUnitPrice());
		}

		// only enable the save button when there has been some text entered.
		saveBtn.disableProperty().bind(quantityField.textProperty().isEqualTo(""));

		// add action handlers for the dialog buttons.
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {
				itemModel.setQuantity(quantityField.getNumber());
				try {
					if (isNewAdd) { // if new Item to be added
						searchTableView.getItems().remove(itemModel);
						if (cart == null) {
							cart = DatabaseManager.getInstance().getNewCart(Helpers.getCurrentUser().getUserName());
						}
						DatabaseManager.getInstance().addCartItems(cart.getCartID(), itemModel.getItemID(), itemModel.getQuantity());
						addToCart(itemModel);
					} else { // If already added
						cartTableView.refresh();
						DatabaseManager.getInstance().updateCartItem(cart.getCartID(), itemModel.getItemID(), itemModel.getQuantity());
					}	
				} catch (Exception e) {
					Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
				}			
				
				dialog.close();
			}
		});
		
		VBox layout = new VBox(10);
		grid.setPadding(new Insets(20, 20, 20, 20));
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(grid, saveBtn);
		layout.setPadding(new Insets(5));
		dialog.setScene(new Scene(layout, dialogWidth, dialogHeight));
		dialog.show();
	}
	
	// This method add item in search table view
	private void addinSearchView(ItemModel itemModel) {
		if (!searchTableView.getItems().contains(itemModel)) {
			Button cartButton = Helpers.getButton(getClass(), "cart.png");
			
			HBox action = new HBox(cartButton);
			action.setAlignment(Pos.CENTER);
			itemModel.setAction(action);
			searchTableView.getItems().add(itemModel);

			cartButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showAddFormDialog(itemModel, true);
				}
			});

		}
	}

	// This method used to add item to cart table vew
	private boolean addToCart(ItemModel itemModel) {
		if (!cartTableView.getItems().contains(itemModel)) {
			Button deleteButton = Helpers.getButton(getClass(), "delete.png");
			Button editButton = Helpers.getButton(getClass(), "edit.png");

			HBox action = new HBox(editButton, deleteButton);
			action.setAlignment(Pos.CENTER);
			itemModel.setAction(action);
			cartTableView.getItems().add(itemModel);
			
			//cart item delete action listener
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Optional<ButtonType> result = Helpers.showAlert(Alert.AlertType.CONFIRMATION, "Delete!", "Do you want to delete?");
					if(result.get() == ButtonType.OK) {
						try {
							cartTableView.getItems().remove(itemModel);
							DatabaseManager.getInstance().deletedCartItems(cart.getCartID(), itemModel.getItemID());
						} catch (SQLException e) {
							Helpers.showAlert(AlertType.ERROR, "Error Occured", e.getMessage());
						}
					} 
				}
			});

			//cart item edit action listener
			editButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showAddFormDialog(itemModel, false);
				}
			});
			return true;
		}
		return false;
	}

}

