import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {

	private static DatabaseManager databaseManager;
	private Connection connection;
	private Statement statement;

	private final String DB_NAME= "ecommerce-system";
	private final String DB_USER= "postgres";
	private final String DB_PASSWORD= "123";

	// This singleton method is used to create instance to this class
	// Only one instance will be created 
	public static DatabaseManager getInstance() throws SQLException {
		if (databaseManager == null) {
			databaseManager = new DatabaseManager();
		}
		return databaseManager;
	}

	// Configure database connection 
	private DatabaseManager () throws SQLException {
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, DB_USER, DB_PASSWORD);
		statement = connection.createStatement();
	}

	//Add user information in users table 
	public void addUser(String userName, String firstName, String lastName, String email, String passsword, String address, String position) throws Exception {
		if (isUserNameExist(userName)) {
			throw new Exception("User already exist with user name : " + userName);
		}

		if (isEmailExist(email)) {
			throw new Exception("User already exist with email : " + email);
		}

		String sql = "INSERT INTO users(username, first_name, last_name, password, email, address, position)"
				+ "VALUES ('" + userName + "','" + firstName + "','"  + lastName + "','"  + passsword + "','" + email + "','" + address + "','" + position  +"');";
		statement.executeUpdate(sql);	
	}

	// get user using username & password 
	public User getUser(String userName, String password) throws Exception {
		String sql = "SELECT * FROM users where username='" + userName + "' AND password='"  + password + "';";
		ResultSet resultSet = statement.executeQuery(sql);
		User user = null;
		if ( resultSet.next() ) {
			user = new User(userName, resultSet.getString("position"));
			resultSet.close();	
			return user;
		}
		throw new Exception("User credential not matched!");
	}

	// check if an user already exist in database
	private boolean isUserNameExist(String userName) throws SQLException {
		String sql = "SELECT * FROM users where username='" + userName + "';";
		ResultSet resultSet = statement.executeQuery(sql);

		if ( resultSet.next() ) {
			resultSet.close();
			return true;
		}
		return false;
	}

	//checks if email already exist
	private boolean isEmailExist(String email) throws SQLException {
		String sql = "SELECT * FROM users "
				+ "where email='" + email + "';";
		ResultSet resultSet = statement.executeQuery(sql);

		if ( resultSet.next() ) {	
			return true;
		}
		return false;
	}

	//this method change cart status e.g delivered, rehected etc
	public void changeChartStatus(int cartID, String status) throws SQLException {
		String sql = "UPDATE cartmaster set status = '" + status 
				+ "' where cart_id=" + cartID + ";";
		statement.executeUpdate(sql);
	}

	//add new cart in cartmaster table
	public Cart getNewCart(String userName) throws SQLException {
		String sql = "INSERT INTO cartmaster(username, status)"
				+ "VALUES ('" + userName + "','" + CART_STATUS.TEMP +"');";
		statement.executeUpdate(sql);
		return getCart(userName, CART_STATUS.TEMP);
	}	

	//add cart items in cart details table
	public void addCartItems(int cartID, int itemID, int quantity) throws SQLException {
		String sql = "INSERT INTO cartdetails(cart_id, item_id, quantity)"
				+ "VALUES (" + cartID + "," + itemID + ","  + quantity +");";
		statement.executeUpdate(sql);		
	}

	//Update cart item quantity
	public void updateCartItem(int cartID, int itemID, Integer quantity) throws SQLException {
		String sql = "UPDATE cartdetails "
                + "SET quantity = " + quantity 
                + " WHERE item_id = " + itemID + " AND cart_id=" + cartID;
		statement.executeUpdate(sql);
	}

	//delete items from the cart
	public void deletedCartItems(int cartID, int itemID) throws SQLException {
		String sql = "DELETE from cartdetails "
				+ "where cart_id = " + cartID  + " AND item_id=" + itemID + ";";
		statement.executeUpdate(sql);		
	}

	//This method update given OrderModel with total price and item count
	private void updateOrder(HashMap<Integer,ItemModel> itemPrices, OrderModel order) throws SQLException {
		int totalPrice = 0;
		int itemCount = 0;
		String sql = "SELECT * FROM cartdetails "
				+ "where cart_id=" + order.getOderID() + ";";
		ResultSet resultSet = statement.executeQuery(sql);

		while (resultSet.next()) {
			int itemID = resultSet.getInt("item_id");
			ItemModel itemModel = itemPrices.get(itemID);
			
			if (itemModel != null) {
				int quatity = resultSet.getInt("quantity");
				totalPrice +=  itemModel.getUnitPrice() * quatity;
				itemCount += quatity;
			}
		}
		resultSet.close();
		order.setItemCount(itemCount);
		order.setTotalPrice(totalPrice);
	}

	//this methods returns pending order list
	public ArrayList<OrderModel> getPendingOrders() throws SQLException {
		HashMap<Integer,ItemModel> itemPrices = Helpers.getItemsMap();

		ArrayList<OrderModel> orders = new ArrayList<>();

		String sql = "SELECT * FROM cartmaster "
				+ "where status='" + CART_STATUS.PENDING + "';";
		ResultSet resultSet = statement.executeQuery(sql);

		while (resultSet.next()) {
			orders.add(new OrderModel(resultSet.getInt("cart_id"), resultSet.getString("userName"), 0, 0, null));
		}

		for (OrderModel order : orders) {
			updateOrder(itemPrices, order);
		}
		return orders;
	}

	//this method returns all items in a cart
	private ArrayList<ItemModel> getItemsByCartID(int cartID) throws SQLException {
		ArrayList<ItemModel> result = new ArrayList<>();

		HashMap<Integer, ItemModel> itemMap = Helpers.getItemsMap();
		
		String sql = "SELECT * FROM cartdetails "
				+ "where cart_id=" + cartID + ";";
		ResultSet resultSet = statement.executeQuery(sql);
		while (resultSet.next()) {
			int itemID = resultSet.getInt("item_id");
			int quatity = resultSet.getInt("quantity");
			
			ItemModel itemModel = itemMap.get(itemID);
			
			if (itemModel != null) {
				itemModel.setQuantity(quatity);
				result.add(itemModel);
			}
		}
		
		return result;
	}
	
	//This method returns user temporary cart which has not been submitted 
	public Cart getCart(String userName, CART_STATUS status) throws SQLException {
		Cart cart = null;

		String sql = "SELECT * FROM cartmaster "
				+ "where status='" + status + "' AND username='" + userName + "';";
		ResultSet resultSet = statement.executeQuery(sql);
		if (resultSet.next()) {
			int cartID = resultSet.getInt("cart_id");
			resultSet.close();
			cart =  new Cart(cartID, getItemsByCartID(cartID));
		}
		return cart;
	}

	void close() throws SQLException {
		statement.close();
		connection.close();
	}

}