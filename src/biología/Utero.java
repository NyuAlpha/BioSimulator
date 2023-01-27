package biología;

import actores.Feto;

public class Utero {

	Feto feto;
	BodyAnimal madre;
	
	public Utero(BodyAnimal madre) {
		this.madre = madre;
		feto = null;
	}

	public void fecundacion(ADN adn) {
		feto = new Feto(adn,madre);
		actuar();
	}
	
	public void actuar() {
		feto.desarrollo();
	}
	
	public void parir() {
		feto = null;
	}
}
