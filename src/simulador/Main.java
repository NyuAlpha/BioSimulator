package simulador;

import graficos.Mapa;

public class Main {

	public static void main(String[] args) {

		

		Mapa mapa = new Mapa();
		SimuladorGUI GUI = new SimuladorGUI(mapa.getImagenMapa());
		SimuladorLogica simulador = new SimuladorLogica(GUI,mapa);
		
	}

}
