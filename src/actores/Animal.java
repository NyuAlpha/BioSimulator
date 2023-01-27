package actores;

import java.util.ArrayList;
import java.util.Random;

import biología.ADN;
import biología.BodyAnimal;
import graficos.Coordenadas;
import graficos.Mapa;

public class Animal extends Actor {
	
	private Actor objetivo;
	private Animal pareja;
	private ArrayList<Coordenadas> entornoLibre;
	private BodyAnimal body;


	//Constructor de reproduccion, solo usable desde una instancia de tipo animal
	private Animal(Mapa mapa, Coordenadas coordenadas,Feto feto) {
		
		super(mapa, coordenadas);
		body = new BodyAnimal(feto,this);
		iniciarVariablesComunes();
	}
	
	//Constructor inicial
	public Animal(Mapa mapa, Coordenadas coordenadas,ADN adn,double tamanno) {
		
		super(mapa, coordenadas);
		body = new BodyAnimal(adn,this);
		body.setTamanno(tamanno);
		iniciarVariablesComunes();
	}
	
	private void iniciarVariablesComunes() {
		establecerAsociaciones();
		mapa.putActor(this,coordenadas);
		objetivo = null;
		pareja = null;
	}
	
	private void establecerAsociaciones() {
		
		if(marcar) {
			colorAsociado = TiposActores.MARCADO.getColorRGB();
		}
		else {
			if(body.getTipoAlimentacion() == ADN.CARNIVORO) {
				if(body.getSexo() == ADN.HEMBRA)
					colorAsociado = TiposActores.CAZADOR_H.getColorRGB();
				else 
					colorAsociado = TiposActores.CAZADOR_M.getColorRGB();
			}
			else {
				if(body.getSexo() == ADN.HEMBRA)
					colorAsociado = TiposActores.PASTOR_H.getColorRGB();
				else 
					colorAsociado = TiposActores.PASTOR_M.getColorRGB();
			}
	
					
			margenAsociado = (body.getAdulto())? 1:2;
		}
	}
	
	public void actuar() {
		
		if(isVivo) {
			comprobarEntorno(1);
			
			if(body.getHambre() == BodyAnimal.HAMBRIENTO || body.getHambre() == BodyAnimal.MUY_HAMBRIENTO) {
				if(objetivo == null) {
					buscarObjetivo();
				}
				else {
					perseguirObjetivo();
				}	
			}
			//Si su líbido es alta
			else if((body.getLibido() == BodyAnimal.CELO && body.getSexo() == ADN.MACHO)){
				if(pareja == null) {
					buscarPareja();
				}
				else {
					perseguirPareja();
				}
			}
			else {
				mover();
			}
			
			if(body.getEncinta()) {
				body.gestacion();
			}
				
			body.metabolismo();
			establecerAsociaciones();
		}
	}

	
	private void comprobarEntorno(int radio) {
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

		if(!entornoLibre.isEmpty()) {
			Coordenadas coordenadasViejas = coordenadas;
			coordenadas = entornoLibre.get(random.nextInt(entornoLibre.size()));
			mapa.moverActor(this, coordenadasViejas,coordenadas, coordenadas.getZ());
		}
	}
	
	
	private void buscarObjetivo() {
		int radioBusqueda = 1;
		ArrayList<Actor> presas;
		Actor presa = null;
		while(objetivo == null && radioBusqueda < body.getRadioVision()) {
			presas = new ArrayList<>();
			for (int i = -radioBusqueda; i <= radioBusqueda; i++) {
				for (int j = -radioBusqueda; j <= radioBusqueda; j++) {
					int xObjetivo = coordenadas.getX() + i;
					int yObjetivo = coordenadas.getY() + j;
					if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
						presa = mapa.getActor(xObjetivo,yObjetivo,body.getTipoAlimentacion());
						if(presa != null) {
							if(presa instanceof Animal) {
								Animal animal = (Animal)presa;
								//Comprueba que no vaya a cazar a otro carnivoro
								if(animal.getBody().getTipoAlimentacion() != ADN.CARNIVORO) {
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
				return;
			}
			radioBusqueda++;
		}
		mover();
	}
	
	private void buscarPareja() {
		int radioBusqueda = 1;
		ArrayList<Animal> parejas;
		Animal parejaSex = null;
		while(pareja == null && radioBusqueda < body.getRadioVision()) {
			parejas = new ArrayList<>();
			//Va a buscar alrededor con el doble for, incrementando el area en cada iteracción de while mientras no encuentre nada
			for (int i = -radioBusqueda; i <= radioBusqueda; i++) {
				for (int j = -radioBusqueda; j <= radioBusqueda; j++) {
					//Coordenadas que va a comprobar
					int xObjetivo = coordenadas.getX() + i; 
					int yObjetivo = coordenadas.getY() + j;
					//Comprueba que esten dentro de los limites del mapa
					if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
						//pide el mapa el animal de dichas coordenadas
						parejaSex = (Animal)mapa.getActor(xObjetivo,yObjetivo,Mapa.CAPA_ANIMAL);
						if(parejaSex != null) {
							//Si la posible pareja es distinta a su sexo ,es fertil y no está preñada
							BodyAnimal bodyF = parejaSex.getBody();
							if(bodyF.getSexo() != body.getSexo() && bodyF.getFertil() && !bodyF.getEncinta()) {
								parejas.add(parejaSex);
							}
						}
					}
				}
			}
			if(!parejas.isEmpty()) {
				pareja = parejas.get(random.nextInt(parejas.size()));
				return;
			}
			radioBusqueda++;
		}
		mover();
	}
	
	private void perseguirObjetivo() {
		
		double distancia = coordenadas.calcularDistancia(objetivo.getCoordenadas());		
		if(objetivo.isVivo()) {
			
			//Si la presa esta cerca se moverá hacia la presa 
			if(distancia > 1.6 ) {
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
				comer(objetivo);
				objetivo = null;
			}
		}
		else {
			objetivo = null;
			mover();
		}
	}
	
	private void perseguirPareja() {
		
		double distancia = coordenadas.calcularDistancia(pareja.getCoordenadas());		
		if(pareja.isVivo()) {
			
			//Si la pareja esta cerca
			if(distancia > 1.6 ) {
				//Calcula las coordenadas hacia la que debe moverse para acercarse a la pareja y si esta
				//libre se moverá hacia allí
				int x = coordenadas.getX() +  coordenadas.calcularDireccionX(pareja.getCoordenadas());
				int y = coordenadas.getY() +  coordenadas.calcularDireccionY(pareja.getCoordenadas());
				if(mapa.isLibre(x, y, coordenadas.getZ())) {
					mapa.moverActor(this,x,y);
					coordenadas = new Coordenadas(x,y,coordenadas.getZ());
				}
			}
			//Si la presa esta justo al lado
			else {
				aparearse();
				//pareja = null;
			}
		}
		else {
			pareja = null;
			mover();
		}
	}
	
	private void comer(Actor alimento) {
		if(alimento instanceof Animal) {
			Animal a = (Animal)alimento;
			body.digestion(a.getBody());
		}
		else {
			Vegetal v = (Vegetal)alimento;
			body.digestion(v.getBody());
		}
		alimento.morir();
	}

	protected void aparearse() {
		pareja.getBody().insertarEsperma(body.getAdn().meiosis());
		pareja = null;
	}
	
	public void parir(Feto feto) {
		if(!entornoLibre.isEmpty()) {
			Coordenadas coordenadasLibres = entornoLibre.get(random.nextInt(entornoLibre.size()));
			new Animal(mapa,coordenadasLibres,feto);
		}
	}
	
	public String toString() {
		return String.format("\n m:%.1f/t:%.1f e:%d  H:%d sx:%d X:%d"
							,body.getMasa(),body.getTamanno(),(int)body.getCicloVital(),body.getHambre(),body.getSexo(),body.getLibido());
	}

	public BodyAnimal getBody() {
		return body;
	}
}
