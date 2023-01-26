package actores;

import java.util.ArrayList;
import java.util.Random;

import graficos.Coordenadas;
import graficos.Mapa;

public class Animal extends Actor {
	
	private Actor objetivo;
	private int radioVision;
	private ArrayList<Coordenadas> entornoLibre;
	private int alimentacion;
	public static final int HERVIVORO = 1;
	public static final int CARNIVORO = 2;
	public static final int OMNIVORO = 3;
	
	
	private double relacionPesoHambre;
	private double relacionPesoMuerte;
	
	private double eficienciaAlimenticia;  

	
	public Animal(Mapa mapa, Coordenadas coordenadas, int alimentacion) {
		
		super(mapa, coordenadas);
		
		
		if(alimentacion == CARNIVORO) {
			eficienciaAlimenticia = 0.3;
			masa = 150;
			colorAsociado = TiposActores.CAZADOR_M.getColorRGB();
		}
		else {
			eficienciaAlimenticia = 0.8;
			masa = 300;
			colorAsociado = TiposActores.PASTOR_M.getColorRGB();
		}
		
		tamanno = Math.sqrt(masa);
		
		indiceCrecimiento = 0.00005; //0.01%
		indiceEtapaCrecimiento = 0.15; //20%
		metabolismoBasal = -0.0005; //- 0.1%
		
		mapa.putActor(this,coordenadas);
		objetivo = null;
		radioVision = 30;
		techoVital = 400;
		this.alimentacion = alimentacion;
		
		relacionPesoHambre = 0.95;
		relacionPesoMuerte = 0.5;



		
	}

	public void actuar() {
		
		if(isVivo) {
			estado = "";
			comprobarEntorno(1);

			
			
			//Si esta listo para reproducirse
			if(cicloVital >= techoVital * indiceEtapaCrecimiento && (masa >= (tamanno * tamanno))) {
					reproducirse();
					estado += "\n Listo para aparearse";
			}
			
			//Si tiene hambre 
			else if( (masa <= (tamanno * tamanno) * 1.1)) {
				estado += "\n Esta hambriento";
				if(objetivo == null)
					buscarPresa();
				else
					perseguirPresa();
				}
			else {
				estado += "\n No tiene hambre";
				mover();
			}
			
			metabolismo();
			estado += "\n" + toString();
		}
		}

	
	private void comprobarEntorno(int radio) {
		estado += "\n Busca espacio libre a su alrededor ";
		entornoLibre = new ArrayList<>();
		for (int i = -radio; i <= radio; i++) {
			for (int j = -radio; j <= radio; j++) {
				int xObjetivo = coordenadas.getX() + i;
				int yObjetivo = coordenadas.getY() + j;
				if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
					if(mapa.isLibre(xObjetivo,yObjetivo,coordenadas.getZ())) {
						Coordenadas libre = new Coordenadas(xObjetivo,yObjetivo,coordenadas.getZ());
						entornoLibre.add(libre);
					}
				}
			}
		}
	}
	
	private void mover() {

		estado += "\n Quiere moverse";
		if(!entornoLibre.isEmpty()) {
			estado += "\n Se mueve";
			Coordenadas coordenadasViejas = coordenadas;
			coordenadas = entornoLibre.get(random.nextInt(entornoLibre.size()));
			mapa.moverActor(this, coordenadasViejas,coordenadas, coordenadas.getZ());
		}
		else {
			estado += "\n No hay suficiente espacio para moverse";
		}
	}
	
	
	private void buscarPresa() {
		estado += "\n Procede a buscar presas";
		int radioBusqueda = 1;
		ArrayList<Actor> presas;
		Actor presa = null;
		while(objetivo == null && radioBusqueda < radioVision) {
			presas = new ArrayList<>();
			for (int i = -radioBusqueda; i <= radioBusqueda; i++) {
				for (int j = -radioBusqueda; j <= radioBusqueda; j++) {
					int xObjetivo = coordenadas.getX() + i;
					int yObjetivo = coordenadas.getY() + j;
					if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
						presa = mapa.getActor(xObjetivo,yObjetivo,alimentacion);
						if(presa != null) {
							if(presa instanceof Animal) {
								Animal presaAnimal = (Animal)presa;
								//Comprueba que no vaya a cazar a otro carnivoro
								if(presaAnimal.getTipoAlimentacion() != Animal.CARNIVORO) {
									presas.add(presa);
								}
							}
							else {
								presas.add(presa);
							}
							
						}
					}
				}
			}
			if(!presas.isEmpty()) {
				objetivo = presas.get(random.nextInt(presas.size()));
				String vivo = (objetivo.isVivo())? "vivo":"muerto";
				estado += "\n Presa detectada ->  " + vivo;
				return;
			}
			radioBusqueda++;
		}
		estado += "\n No ha encontrado presas cercanas";
		mover();
	}
	
	private void perseguirPresa() {
		estado += "\n Persigue una presa";
		double distancia = coordenadas.calcularDistancia(objetivo.getCoordenadas());
			
			//Arreglar esto
			/*if(distancia > radioVision || !objetivo.isVivo()) {
				estado += "\n Presa demasiado lejos";
				objetivo = null;
				buscarPresa();
			}*/
		
		if(objetivo.isVivo()) {
			
			//Si la presa esta cerca se moverá hacia la presa 
			if(distancia > 1.6 ) {
				estado += "\n Presa cercana";
				//Calcula las coordenadas hacia la que debe moverse para acercarse al objetivo y si esta
				//libre se moverá hacia allí
				int x = coordenadas.getX() +  coordenadas.calcularDireccionX(objetivo.getCoordenadas());
				int y = coordenadas.getY() +  coordenadas.calcularDireccionY(objetivo.getCoordenadas());
				if(mapa.isLibre(x, y, coordenadas.getZ())) {
					mapa.moverActor(this,x,y);
					coordenadas = new Coordenadas(x,y,coordenadas.getZ());
				}
			}
			//Si la presa esta justo al lado
			else {
				estado += "\n Presa al lado";
				comer();
			}
		}
		else {
			estado += "\n Presa muerta";
			objetivo = null;
			mover();
		}
	}
	
	private void comer() {
		masa += objetivo.getMasa() * eficienciaAlimenticia;
		objetivo.morir();
		estado += "\n Come a " + objetivo.toString();
		objetivo = null;

	}

	protected void reproducirse() {
		if(!entornoLibre.isEmpty()) {
			Coordenadas coordenadasNuevas = entornoLibre.get(random.nextInt(entornoLibre.size()));
			new Animal(mapa,coordenadasNuevas,alimentacion);
			masa -= 150;
			estado += "\n Se reproduce";
		}
		else {
			estado += "\n No hay suficiente espacio para reproducirse";
		}
	}
	
	/**
	 * Simula el metabolismo de un animal, si pierde demasiada masa muere.
	 */
	protected void metabolismo() {
		super.metabolismo();
		masa += masa * metabolismoBasal;
		if(masa < (tamanno * tamanno)/2) {
			estado += "\n Ha perdido demasiado peso";
			morir();
		}
	}
	
	
	/**
	 * devuelve una cadena con informacion relevante sobre el animal
	 * @return la cadena de texto con informacion relevante sobre el animal
	 */
	public String toString() {
		
		String objS = (objetivo!=null)? "1":"0";
		String dieta= (alimentacion==2)? "Carn:":"Herb:"; 
		return new String().format("%s -> %.0f/%.0f, e:%d, %s%s" ,coordenadas.toString(),masa,tamanno,cicloVital
				,dieta,objS);
	}

	
	public int getTipoAlimentacion() {
		return alimentacion;
	}
}
