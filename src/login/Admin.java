package login;

public class Admin extends Utente {

	public Admin(String name, String password, String id) {
		super(name, password, id, true);
	}

	@Override
	public boolean isAdmin() {
		return true;
	}
}
