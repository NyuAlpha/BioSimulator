package simulador;

import graficos.Mapa;

public class Main {

	public static void main(String[] args) {

		
		final int ANCHO = 120;
		final int ALTO = 90;
		Mapa mapa = new Mapa(ANCHO,ALTO);
		SimuladorGUI GUI = new SimuladorGUI(mapa.getImagenMapa());
		SimuladorLogica simulador = new SimuladorLogica(GUI,mapa);
		
	}

}
