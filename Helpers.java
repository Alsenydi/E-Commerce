import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Helpers {
	
	private static final String ITEMS_STORAGE_FILE = "items.txt";
	private final static String TABLE_CELL_CENTER = "-fx-alignment: CENTER;";
	private static User currentUser;
	
	//This method used to show alert
	public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		return alert.showAndWait();
	}
	
	//This method used to write product items into a file
	public static void writeItems(ObservableList<ItemModel> items) {
		try {
			BufferedWriter bufferedWriter  = new BufferedWriter(new FileWriter(new File(ITEMS_STORAGE_FILE)));
		
			for (ItemModel item : items) {
				bufferedWriter.write(item.getItemID() + "\t" + item.getItemName() 
				+ "\t" + item.getQuantity() + "\t" + item.getUnitPrice() + "\n");
			}
			
			bufferedWriter.close();
		} catch (Exception e) {
			showAlert(AlertType.ERROR, "Product Write Error", e.getMessage());
		}
	}

	// This method return product hash map
	public static HashMap<Integer, ItemModel>  getItemsMap() {
		HashMap<Integer, ItemModel> items = new HashMap<>();
		File file = new File(ITEMS_STORAGE_FILE);
		
		try {
			if (!file.exists()) {
				file.createNewFile();
				return items;
			}
			
			BufferedReader bufferedReader  = new BufferedReader(new FileReader(file));
			String line = null;
			Scanner scanner = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				scanner = new Scanner(line);
				scanner = scanner.useDelimiter("\t");
				ItemModel itemModel = new ItemModel(scanner.nextInt(), scanner.next(), scanner.nextInt(), scanner.nextInt(), null);
				items.put(itemModel.getItemID(), itemModel);
			}
					
			bufferedReader.close();
		} catch (Exception e) {
			showAlert(AlertType.ERROR, "Product Write Error", e.getMessage());
		}
		
		return items;
	}
	
	//This method return all product items list stored in file
	public static ArrayList<ItemModel> getItems() {
		ArrayList<ItemModel> itemModels = new ArrayList<>();
		File file = new File(ITEMS_STORAGE_FILE);
		if (!file.exists()) {
			return itemModels;
		}
		
		try {
			BufferedReader bufferedReader  = new BufferedReader(new FileReader(file));
			String line = null;
			Scanner scanner = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				scanner = new Scanner(line);
				scanner = scanner.useDelimiter("\t");
				itemModels.add(new ItemModel(scanner.nextInt(), scanner.next(), scanner.nextInt(), scanner.nextInt(), null));
			}
					
			bufferedReader.close();
		} catch (Exception e) {
			showAlert(AlertType.ERROR, "Product Write Error", e.getMessage());
		}
		return itemModels;
	}

	// This method returns products that matches given name
	public static ArrayList<ItemModel> getItemsByName(String name) {
		ArrayList<ItemModel> result = new ArrayList<>();
		ArrayList<ItemModel> allItems = Helpers.getItems();
		
		for (ItemModel itemModel : allItems) {
			if(itemModel.getItemName().toLowerCase().contains(name.toLowerCase())) {
				result.add(itemModel);
			}
		}
		
		return result;
	}
	
	// This method table view to item
	public static TableView<ItemModel> getItemTableView() {
		TableView<ItemModel> tableView = new TableView<ItemModel>();

		TableColumn<ItemModel, Integer> itemID = new TableColumn<>("Item ID");
		TableColumn<ItemModel, Integer> unitPrice = new TableColumn<>("Unit Price");
		TableColumn<ItemModel, String> itemName = new TableColumn<>("Item Name");
		TableColumn<ItemModel, Integer> quantity = new TableColumn<>("Quantity");
		TableColumn<ItemModel, HBox> action = new TableColumn<>("Action");

		itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
		itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		unitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
		action.setCellValueFactory(new PropertyValueFactory<>("action"));

		tableView.getColumns().add(itemID);
		tableView.getColumns().add(itemName);
		tableView.getColumns().add(quantity);
		tableView.getColumns().add(unitPrice);
		tableView.getColumns().add(action);

		itemID.setStyle(TABLE_CELL_CENTER);
		itemName.setStyle(TABLE_CELL_CENTER);
		unitPrice.setStyle(TABLE_CELL_CENTER);
		quantity.setStyle(TABLE_CELL_CENTER);
		action.setStyle(TABLE_CELL_CENTER);

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		return tableView;
	}
	
	//this method is used to create image button
	public static Button getButton(Class<?> classV, String imageName) {
		Button button = new Button();
		Image image = new Image(classV.getResourceAsStream("/" + imageName));
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(15);
		imageView.setFitWidth(15);
		button.setGraphic(imageView);
		button.setBackground(Background.EMPTY);
		return button;
	}
	
	//this returns current logged in user
	public static User getCurrentUser() {
		return currentUser;
	}

	//this stores current logged in user
	public static void setCurrentUser(User currentUser) {
		Helpers.currentUser = currentUser;
	}
}
