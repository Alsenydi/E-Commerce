public class User {
	private String userName;
	private String role;
	
	//Constructor method
	public User(String userName, String role) {
		this.userName = userName;
		this.role = role;
	}

	//=======================Setter & getter methods starts here============================
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	//=======================Setter & getter methods ends here============================
	
}
