package actores;

import java.util.ArrayList;
import java.util.Random;

import graficos.Coordenadas;
import graficos.Mapa;

public class Vegetal extends Actor {

	private double fotosintesis;
	
	public Vegetal(Mapa mapa, Coordenadas coordenadas) {
		super(mapa, coordenadas);
		capa = 1;
		masa = 50;
		indiceCrecimiento = 0.02; //2%
		tamanno = Math.sqrt(masa);
		indiceEtapaCrecimiento = 0.2; //20%
		//metabolismoBasal = -0.005; //- 0.5%
		fotosintesis = 0.03;
		
		techoVital = 100;
		mapa.putActor(this,coordenadas,capa);
	}

	@Override
	public void actuar() {
		estado = "";
		if(random.nextInt(100) < 10)
			reproducirse();
	}

	
	protected void reproducirse() {

		ArrayList <Coordenadas> coordenadasLibres = new ArrayList<>();
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				int xObjetivo = coordenadas.getX() + i;
				int yObjetivo = coordenadas.getY() + j;
				if( !(xObjetivo < 0 || xObjetivo >= mapa.getAncho() || yObjetivo < 0 || yObjetivo >= mapa.getAlto())) {
					if(mapa.isLibre(xObjetivo,yObjetivo,capa)) {
						Coordenadas libre = new Coordenadas(xObjetivo,yObjetivo);
						coordenadasLibres.add(libre);
					}
				}
			}
		}
		if(!coordenadasLibres.isEmpty()) {
			Coordenadas coordenadasNuevas = coordenadasLibres.get(random.nextInt(coordenadasLibres.size()));
			new Vegetal(mapa,coordenadasNuevas);
		}
	}

	/**
	 * Simula el metabolismo de la planta
	 */
	protected void metabolismo() {
		super.metabolismo();
		//Una planta siempre crea masa de la aparente nada
		masa += masa * fotosintesis;
	}
}
