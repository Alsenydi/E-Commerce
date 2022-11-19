import javafx.beans.property.*;
import javafx.scene.control.ComboBox;

// Pending Order page table view model
public class OrderModel {
	private SimpleIntegerProperty oderID;
	private SimpleStringProperty userName;
	private SimpleIntegerProperty itemCount;
	private SimpleIntegerProperty totalPrice;
    private SimpleObjectProperty<ComboBox<String>> action;

    // Constructor method
	public OrderModel(int oderID, String userName, int itemCount, int totalPrice, ComboBox<String> action) {
		this.setOderID(oderID);
		this.setUserName(userName);
		this.setItemCount(itemCount);
		this.setTotalPrice(totalPrice);
		this.setAction(action);
	}
	
	//==========================Setter & getter methods starts here=======================
	public int getOderID() {
		return oderID.get();
	}

	public void setOderID(int oderID) {
		this.oderID = new SimpleIntegerProperty(oderID);
	}

	public String getUserName() {
		return userName.get();
	}

	public void setUserName(String userName) {
		this.userName = new SimpleStringProperty(userName);
	}

	public int getItemCount() {
		return itemCount.get();
	}

	public void setItemCount(int itemCount) {
		this.itemCount = new SimpleIntegerProperty(itemCount);
	}

	public int getTotalPrice() {
		return totalPrice.get();
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = new SimpleIntegerProperty(totalPrice);
	}

	public ComboBox<String> getAction() {
		return action.get();
	}

	public void setAction(ComboBox<String> action) {
		this.action =  new SimpleObjectProperty<ComboBox<String>>(action);;
	}
	//==========================Setter & getter methods end here=======================
	
	@Override
	public boolean equals(Object object) { //Object class Equal method overridden
		if (object instanceof OrderModel) {
			OrderModel itemModel = (OrderModel) object;
			return this.getOderID() == itemModel.getOderID();
		}
		return false;
	}

}
