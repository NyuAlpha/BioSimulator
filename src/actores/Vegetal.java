package actores;

import java.util.ArrayList;
import java.util.Random;

import biología.ADN;
import biología.BodyAnimal;
import biología.BodyVegetal;
import graficos.Coordenadas;
import graficos.Mapa;
import graficos.TiposActores;

public class Vegetal extends Actor {

	private BodyVegetal body;
	
	public Vegetal(Mapa mapa, Coordenadas coordenadas, ADN adn) {
		super(mapa, coordenadas);
		body = new BodyVegetal(adn,this);
		colorAsociado = TiposActores.CESPED.getColorRGB();
		margenAsociado = 0;
		mapa.putActor(this,coordenadas);
	}

	@Override
	public void actuar() {
		if(random.nextInt(100) < 5) {
			reproducirse();
		}
		body.metabolismo();
	}

	
	protected void reproducirse() {

		ArrayList <Coordenadas> coordenadasLibres = new ArrayList<>();
		int r = 4;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				int xObjetivo = coordenadas.getX() + i;
				int yObjetivo = coordenadas.getY() + j;
				if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
					if(mapa.isLibre(xObjetivo,yObjetivo,coordenadas.getZ())) {
						Coordenadas libre = new Coordenadas(xObjetivo,yObjetivo,coordenadas.getZ());
						coordenadasLibres.add(libre);
					}
				}
			}
		}
		if(!coordenadasLibres.isEmpty()) {
			Coordenadas coordenadasNuevas = coordenadasLibres.get(random.nextInt(coordenadasLibres.size()));
			new Vegetal(mapa,coordenadasNuevas,body.getAdn());
		}
	}
	
	public BodyVegetal getBody() {
		return body;
	}
	
	public String toString() {
		return String.format("\n m:%.1f/t:%.1f e:%d",body.getMasa(),body.getTamanno(),(int)body.getCicloVital());
	}

}
