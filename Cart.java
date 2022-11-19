import java.util.ArrayList;

public class Cart {
	private int cartID;
	private ArrayList<ItemModel> items;
	
	// Constructor method
	public Cart(int cartID, ArrayList<ItemModel> items) {
		this.cartID = cartID;
		this.items = items;
	}

	// =================== setter & getter methods starts here=====================
	   
	public int getCartID() {
		return cartID;
	}

	public void setCartID(int cartID) {
		this.cartID = cartID;
	}

	public ArrayList<ItemModel> getItems() {
		return items;
	}

	public void setItems(ArrayList<ItemModel> items) {
		this.items = items;
	}
	
    // =================== setter & getter methods ends here=====================
	   
}
