import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// This class is used show & add/update new product
public class ItemModPage extends Application {
	private Stage primaryStage;
	private TableView<ItemModel> tableView;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Item Modification Page");
		tableView = Helpers.getItemTableView();
		
		//Add Item button configuration
		Button addItemBtn = new Button("Add Item");
		addItemBtn.setPrefHeight(30);
		addItemBtn.setPrefWidth(100);
		
		// add item button event listener
		addItemBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showAddFormDialog(null); //Show form to add item
			}
		});

		// Back button configuration
		Button backBtn = new Button("Back");
		backBtn.setPrefHeight(30);
		backBtn.setPrefWidth(100);
		
		// Back button event listener
		backBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new HomePage().start(primaryStage);
			}
		});
		
		//Add buttons in horizontal box
		HBox hBox = new HBox(20);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().add(addItemBtn);
		hBox.getChildren().add(backBtn);

		//Add horizontal box & table view in vertical box
		VBox vbox = new VBox(20);
		vbox.getChildren().add(hBox);
		vbox.getChildren().add(tableView);

		vbox.setPadding(new Insets(20, 20, 20, 20));
		vbox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vbox, 550, 500);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		loadItems();	
	}

	// This method is used to show a form for add & update of item
	private void showAddFormDialog(ItemModel itemModel) {
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
		saveBtn.disableProperty().bind(itemNameField.textProperty().isEqualTo("")
				.or(quantityField.textProperty().isEqualTo(""))
				.or(priceField.textProperty().isEqualTo("")));

		// add action handlers for the dialog buttons.
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {
				if (itemModel != null) { //when update itemModel is not null
					itemModel.setItemName(itemNameField.getText());
					itemModel.setQuantity(quantityField.getNumber());
					itemModel.setUnitPrice(priceField.getNumber());
					tableView.refresh();
				} else { //when add itemModel is null
					addItem(new ItemModel(tableView.getItems().size() + 1, itemNameField.getText(), quantityField.getNumber(), priceField.getNumber(), null));	
				}
				Helpers.writeItems(tableView.getItems()); //stores item in file
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

	//load all item in the table view
	private void loadItems() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<ItemModel> items = Helpers.getItems();
				for (ItemModel item : items) {
					addItem(item);
				}
			}
		});
	}
	
	//This method is used to add an item in table view
	private void addItem(ItemModel itemModel) {
		Button deleteButton = Helpers.getButton(getClass(), "delete.png");
		Button editButton = Helpers.getButton(getClass(), "edit.png");

		HBox action = new HBox(editButton, deleteButton);
		action.setAlignment(Pos.CENTER);
		itemModel.setAction(action);
		tableView.getItems().add(itemModel);

		//item delete button event listener
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Optional<ButtonType> result = Helpers.showAlert(Alert.AlertType.CONFIRMATION, "Delete!", "Do you want to delete?");
				if(result.get() == ButtonType.OK) {
					tableView.getItems().remove(itemModel);
					Helpers.writeItems(tableView.getItems());
				} 
			}
		});

		//item edit button event listener
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showAddFormDialog(itemModel);
			}
		});

	}
}

//This class is created to create number that only accepts digits
class NumberField extends TextField {
	public NumberField() {
		this.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			public void handle( KeyEvent t ) {
				char ar[] = t.getCharacter().toCharArray();
				char ch = ar[t.getCharacter().toCharArray().length - 1];
				if (!(ch >= '0' && ch <= '9')) {
					t.consume();
				}
			}
		});
	}
	
	//returns integer number
	public int getNumber() {
		if(getText().isEmpty()) {
			return 0;
		}
		return Integer.parseInt(getText());
	}
	
}
