import javafx.beans.property.*;
import javafx.scene.layout.HBox;

// This model class is used for table view where items need to be shown
public class ItemModel {
	private SimpleIntegerProperty itemID;
	private SimpleStringProperty itemName;
	private SimpleIntegerProperty quantity;
	private SimpleIntegerProperty unitPrice;
    private SimpleObjectProperty<HBox> action;
    
    //Constructor method
    public ItemModel(int itemID, String itemName, int quantity, int unitPrice, HBox action) {
    	setItemID(itemID);
    	setItemName(itemName);
    	setQuantity(quantity);
    	setUnitPrice(unitPrice);
    	setAction(action);
	}
   
    // =================== setter & getter methods starts here=====================
    
	public int getItemID() {
		return itemID.get();
	}

	public void setItemID(int itemID) {
		this.itemID = new SimpleIntegerProperty(itemID);
	}


	public String getItemName() {
		return itemName.get();
	}

	public void setItemName(String itemName) {
		this.itemName = new SimpleStringProperty(itemName);
	}

	public Integer getQuantity() {
		return quantity.get();
	}

	public void setQuantity(int quantity) {
		this.quantity = new SimpleIntegerProperty(quantity);
	}

	public int getUnitPrice() {
		return unitPrice.get();
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = new SimpleIntegerProperty(unitPrice);
	}

	public HBox getAction() {
		return action.get();
	}

	public void setAction(HBox action) {
		this.action = new SimpleObjectProperty<HBox>(action);
	}
	
	// =================== setter & getter methods ends here=====================
	   
	// Object class equals is overridden. 
	@Override
	public boolean equals(Object object) {
		if (object instanceof ItemModel) {
			ItemModel itemModel = (ItemModel) object;
			return this.getItemID() == itemModel.getItemID();
		}
		return false;
	}

}
