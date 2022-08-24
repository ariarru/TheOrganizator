package login;

public abstract class Utente {
	
	protected String ID;
	protected String username;
	protected String password;
	private boolean admin;
	
	
	//semplice superclasse utente
	public Utente(String name, String password, String id, boolean admin) {
		this.username= name;
		this.password= password;
		this.ID= id;
		this.admin=admin;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	public String getID() {
		return ID;
	}
	public void setID(String newId) {
		this.ID= newId;
	}
	public void setUsername(String newUsername) {
		this.username= newUsername;
	}
	public void setPassword(String newPassword) {
		this.password= newPassword;
	}

}
