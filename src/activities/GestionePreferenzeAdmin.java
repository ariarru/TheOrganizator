package activities;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class GestionePreferenzeAdmin {
	private VBox vb;

	public GestionePreferenzeAdmin() {
		generate();
	}
	
	//genera l'interfaccia dove dispone le preferenze
		private void generate() {
			GestionePreferenze.readPreferences();
			PreferenzeInterface prefInter=new PreferenzeInterface(GestionePreferenze.getAllPreferences());
			vb= new VBox(prefInter.getInterface());
			vb.setAlignment(Pos.CENTER);

		}
		public VBox getContent() {
			return vb;
		}
}
