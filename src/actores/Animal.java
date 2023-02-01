package actores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import biologia.ADN;
import biologia.BodyAnimal;
import biologia.Estado;
import graficos.Coordenadas;
import graficos.Mapa;
import graficos.TiposActores;

public class Animal extends Actor {
	
	private Actor objetivo;
	private HashSet<Animal> parejas;
	private HashSet<Animal> presas; 
	private HashSet<Vegetal> pasto;
	private HashSet<Animal> iguales;
	private HashSet<Animal> amenazas;
	private HashSet<Animal> aliados;
	
	private ArrayList<Coordenadas> entornoLibre;
	private BodyAnimal body;
	private Coordenadas coordenadaObjetiva;
	
	
	//=========================================
	//             Nuevo

	private Estado estado;
	private boolean movimiento;
	

    //=========================================

	//Constructor de reproduccion, solo usable desde una instancia de tipo animal
	private Animal(Mapa mapa, Coordenadas coordenadas,Feto feto) {
		
		super(mapa, coordenadas);
		body = new BodyAnimal(feto,this);
		especie = (int)(body.getEspecie() + 0.1); //al ser un float se le aplica un margen para evitar malos redondeos
		iniciarVariablesComunes();
	}
	
	//Constructor inicial
	public Animal(Mapa mapa, Coordenadas coordenadas,ADN adn,double tamanno) {
		
		super(mapa, coordenadas);
		body = new BodyAnimal(adn,this);
		body.setTamanno(tamanno);
		body.setEdad(30);
		especie = (int)(body.getEspecie() + 0.1); //al ser un float se le aplica un margen para evitar malos redondeos
		iniciarVariablesComunes();
	}
	
	private void iniciarVariablesComunes() {
		establecerAsociaciones();
		mapa.putActor(this,coordenadas);
		coordenadaObjetiva = null;
		
		objetivo = null;
		estado = Estado.NEUTRO;
		movimiento = true;
	}
	
	private void establecerAsociaciones() {
		
		if(marcar) {
			colorAsociado = TiposActores.MARCADO.getColorRGB();
		}

		else {
			if(especie == Animal.DEPREDADOR) {
				if(body.getSexo() == BodyAnimal.HEMBRA)
					colorAsociado = TiposActores.CAZADOR_H.getColorRGB();
				else 
					colorAsociado = TiposActores.CAZADOR_M.getColorRGB();
			}
			else if(especie == Animal.HUMANO) {
				if(body.getSexo() == BodyAnimal.HEMBRA)
					colorAsociado = TiposActores.MIX_H.getColorRGB();
				else 
					colorAsociado = TiposActores.MIX_M.getColorRGB();
			}
			else{
				if(body.getSexo() == BodyAnimal.HEMBRA)
					colorAsociado = TiposActores.PASTOR_H.getColorRGB();
				else 
					colorAsociado = TiposActores.PASTOR_M.getColorRGB();
			}
		}
		
		if(body.isEncinta()) {
			margenAsociado = 0;
		}
		else {
			margenAsociado = (body.getAdulto())? 1:2;
		}
	}
		
	private void mover() {
		if(movimiento) {
			comprobarEntorno();
			if(!entornoLibre.isEmpty()) {
				Coordenadas coordenadasNuevas = entornoLibre.get(random.nextInt(entornoLibre.size()));
				mapa.moverActorA(this, coordenadasNuevas.getX(),coordenadasNuevas.getY());
				coordenadas = coordenadasNuevas;
			}
		}
	}
	
	private void moverA(int x, int y) {
		
		if( !(x < 0 || x >= Mapa.ANCHO || y < 0 || y >= Mapa.ALTO)) {
			if(mapa.isLibre(x, y, coordenadas.getZ())) {						
				mapa.moverActorA(this,x,y);
				coordenadas = new Coordenadas(x,y,coordenadas.getZ());
			}
			else 
				mover();
		}
		else
			mover();
	}
	
	private void perseguir(Coordenadas coordenadaObjetivo) {
			//Calcula las coordenadas hacia la que debe moverse para acercarse al objetivo y si esta
			//libre se moverá hacia allí
			int x = coordenadas.getX() + (coordenadas.calcularDireccionX(coordenadaObjetivo));
			int y = coordenadas.getY() + (coordenadas.calcularDireccionY(coordenadaObjetivo));
			moverA(x,y);
	}
	
	private void huir(Coordenadas coordenadaPeligro) {
		//Calcula la dirección contraria al peligro
		int x = coordenadas.getX() -  (coordenadas.calcularDireccionX(coordenadaPeligro));
		int y = coordenadas.getY() -  (coordenadas.calcularDireccionY(coordenadaPeligro));
		moverA(x,y);
	}
	
	//Solo el macho inicia el apareamiento, además no comprueba si es correspondido
	private void aparearse() {
		if(body.getSexo() == BodyAnimal.MACHO) {
			((Animal) objetivo).getBody().insertarEsperma(body.getAdn().meiosis());
			((Animal) objetivo).setObjetivo(null);
			((Animal) objetivo).setEstado(Estado.NEUTRO);
			estado = Estado.NEUTRO;
			objetivo = null;
		}
	}
	
	public void parir(Feto feto) {
		comprobarEntorno();
		if(!entornoLibre.isEmpty()) {
			Coordenadas coordenadasLibres = entornoLibre.get(random.nextInt(entornoLibre.size()));
			new Animal(mapa,coordenadasLibres,feto);
		}
	}

	
	//============================================================================
	//============================================================================
	//                          NUEVO
	//============================================================================
	//============================================================================
	
	public void actuar() {
		//Solo actua si no ha muerto aun en esta ronda
		if(isVivo) {
			//Analisis de alrededores
			analizarEntorno();
			//procesos voluntarios
			actoVoluntario();
			//Procesos biológicos
			body.actoInvoluntario();
			//Calcula la pariencia en pantalla del animal
			establecerAsociaciones();
		}
	}
	
	//Cerebro del animal
	private void actoVoluntario() {
			switch(estado){
				case NEUTRO:
					mover();
					if(!comprobarPeligro()) {//huida, lucha o neutro
						if(!comprobarHambre()){//hambre o neutro
							comprobarLibido(); // celo o neutro
						} 
					}
					break;
				case HAMBRE:
					mover();
					if(body.getTipoAlimentacion() != BodyAnimal.HERBIVORO) {
						if(!comprobarPeligroLuchaCaza())
							buscarPresas();
					}
					else {
						if(!comprobarPeligro())
							buscarVegetal();
					}//huida, lucha, pastoreo, caza o hambre
					break;
				case CAZA:
					if(comprobarEstado()) {//huida o caza pero no asigna objetivo
					//if(!comprobarPeligroDeCazador()) {//huida, lucha o caza
						if(conseguirPresa())//HAMBRE  solo si murió el objetivo
							comer(); // come y pasa a neutro
					}
					break;
				case PASTOREO:
					if(!comprobarPeligro()) {//huida, lucha pastoreo
						if(conseguirVegetal())//HAMBRE  solo si murió el objetivo
							comer();//come y pasa a neutro
					}
					break;
				case HUIDA:
					controlarHuida(); //Neutro o huida
					break;
				case LUCHA:
					if(!comprobarPeligroLuchaCaza()) {//huida, lucha o no FALLO EN LUCHA O NO
						conseguirOponente();
					}
					break;
				case CELO:
					if(!comprobarPeligro()) {//huida, lucha celo
						if(!comprobarHambre()) { //WARNING deberia comprobar la líbido,ya que depende del hambre
												 //hambre o celo
							if(objetivo != null)
								perseguirPareja();//reproduccion o celo
							else {
								mover();
								comprobarParejas();
							}
						}
					}
					break;
				case REPRODUCCION:
					if(!comprobarPeligro()) {//huida, lucha o reproduccion
						aparearse();
					}
					break;
				//A este estado solo se accede externamente cuando es agredido
				case DEFENSA:
					if(lucharAMuerte()) { // si gana comprueba si hay más peligro
						comprobarPeligro();
					}
					break;
			}
	}
	
	//=================METODOS DE ESTADO==========================
	
	//Comprueba si tiene hambre, si no la tiene pasa a neutro, devuelve si tiene hambre o no
	private boolean comprobarHambre() {
		if(body.getHambre() >= BodyAnimal.HAMBRIENTO) {
			estado = Estado.HAMBRE;
			return true;
		}
		return false;
	}
	
	//Cambia a celo o nada
	private void comprobarLibido() {
		if(body.getLibido() == BodyAnimal.CELO)
			estado = Estado.CELO;
	}
	
	//Busca alimento, devuelve true y lo fija cuando lo encuentra
	private boolean buscarPresas() {
		
		//Debe decidir la presa con más masa dentro de las menos peligrosas
		Animal elegido = null;
		double masaMayor = 0;
		double masa = 0;
		for(Animal animal : presas) {
			double peligrosidad = animal.getBody().getMasa() / body.getMasa();
			//va buscando al de mayor masa
			if(peligrosidad < 0.8) {
				masa = animal.getBody().getMasa();
				if(masa > masaMayor) {
					masaMayor = masa;
					elegido = animal;
				}
			}
		}
		//Si encontró una presa la fija como objetivo y se pone en modo caza
		if(elegido != null) {
			body.setAgresividad(BodyAnimal.AGRESIVO);
			objetivo = elegido;
			estado = Estado.CAZA;
			return true;
		}
		//Si no la encuentra y es un omnívoro buscará plantas	
		if(elegido == null && body.getTipoAlimentacion() != BodyAnimal.OMNIVORO){
			return buscarVegetal();}
		else {
			return false;}
	}
	
	//Busca plantas y si encuentra una buena la fija como su objetivo, cambia su estado a CAZA
	private boolean buscarVegetal() {
		Vegetal vegetalElegido = null;
		//Variables usadas para calcular el individuo con mayor masa
		double masaMayor = 0;
		double masa = 0;
		masaMayor = 0;
		//Busca el mejor
		for(Vegetal vegetal : pasto) {
			masa = vegetal.getBody().getMasa();
			if(masa > masaMayor && !isMismoObjetivo(vegetal,iguales)) {
				masaMayor = masa;
				vegetalElegido= vegetal;
			}
		}
		//Si lo encuentra
		if(vegetalElegido != null) {
			objetivo = vegetalElegido;
			estado = Estado.PASTOREO;
			return true;
		}
		return false;
	}
	
	private boolean comprobarParejas() {
		Animal parejaElegida = null;
		//Variables para medir el atractivo de la pareja
		double mejorSet = 0;
		double set = 0;
		//Busca la mejor pareja
		for(Animal pareja : parejas) {
			double masa = pareja.getBody().getMasa();
			double relacionMasaAltura = pareja.getBody().getRelMasaAltura();
			set = masa * relacionMasaAltura;
			if(set > mejorSet && !isMismoObjetivo(pareja,iguales)) {
				mejorSet = set;
				parejaElegida = pareja;
			}
		}
		//Si la encuentra la elije como objetivo
		if(parejaElegida != null) {
			objetivo = parejaElegida;
			return true;
		}
		return false; //Si no encuentra
	}
	
		
	//Escanea el entorno cercano y añade todo lo que ve a las distintas listas
	private void analizarEntorno() {
		resetListas();
		//Calcula el radio de visión de un animal para realizar una busqueda 
		int radioBusqueda = body.getRadioVision();
		Animal animal = null;
		Vegetal vegetal = null;
		//recorre todo el radio de búsqueda en busca de objetivos
		for (int i = -radioBusqueda; i <= radioBusqueda; i++) {
			for (int j = -radioBusqueda; j <= radioBusqueda; j++) {
				//Para que no se añada a si mismo
				if(i == 0 && j==0)
					continue;
				
				//Coordenadas que va a comprobar
				int xObjetivo = coordenadas.getX() + i; 
				int yObjetivo = coordenadas.getY() + j;
				//Comprueba que esten dentro de los limites del mapa
				if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
					//pide al mapa el animal y vegetales de dichas coordenadas
					animal = (Animal)mapa.getActor(xObjetivo,yObjetivo,Mapa.CAPA_ANIMAL);
					//En caso de que pueda comer hierba lo añade a la lista de pasto
					if(especie != Actor.DEPREDADOR) {
						vegetal = (Vegetal)mapa.getActor(xObjetivo,yObjetivo,Mapa.CAPA_VEGETAL);
						if(vegetal != null)
							pasto.add(vegetal);
					}
					//Ahora debe decidir a que lista se añaden los animales
					
					if(animal != null) {
						
						//Si son de la misma especie
						if(especie == animal.getEspecie()) {
							//Al ser de la misma especie los añade a sus iguales y aliados
							iguales.add(animal);
							aliados.add(animal);
							//Si son de distinto sexo y es fertil las añade a posibles parejas futuras
							BodyAnimal bodyAnimal = animal.getBody();
							if(body.getSexo() != bodyAnimal.getSexo() && bodyAnimal.isFertil() && !bodyAnimal.isEncinta()) {
								parejas.add(animal);
							}
						}
						//Si no son de la misma especie son amenazas
						else {
							amenazas.add(animal);
						}
						//Si el animal prefiere la carne lo añade a presas, independientemente de su especie
						if(body.getTipoAlimentacion() != BodyAnimal.HERBIVORO) {
							presas.add(animal);
						}
					}
				}
			}			
		}
	}
	
	//Comprueba la zona libre de un entorno inmediato (1 unidad)
	private void comprobarEntorno() {
		entornoLibre = new ArrayList<>();
		int radioBusqueda = 1;
		//Realiza la busqueda
		for (int i = -radioBusqueda; i <= radioBusqueda; i++) {
			for (int j = -radioBusqueda; j <= radioBusqueda; j++) {
				int xObjetivo = coordenadas.getX() + i;
				int yObjetivo = coordenadas.getY() + j;
				if( !(xObjetivo < 0 || xObjetivo >= Mapa.ANCHO || yObjetivo < 0 || yObjetivo >= Mapa.ALTO)) {
					if(mapa.isLibre(xObjetivo,yObjetivo,coordenadas.getZ()))
						entornoLibre.add(new Coordenadas(xObjetivo,yObjetivo,coordenadas.getZ()));
				}
			}
		}
	}
	
	
	//Solo usado en un depredador, si no está muy herido sigue cazando pase lo que pase
	private boolean comprobarEstado() {
		if(body.getSalud() < BodyAnimal.HERIDO) {
			estado = Estado.HUIDA;
			body.setAgresividad(BodyAnimal.MIEDO);
			return false;
		}
		return true;
	}
	
	//comprueba el peligro alrededor y modifica su estado, si pasa a modo lucha devuelve un objetivo concreto.
	private boolean comprobarPeligro(){
		double radioAmenaza = body.getRadioVision() / 2;
		Animal mayorAmenaza = null;
		double peligrosidad = 0;
		double peligrosidadTotal = 0;
		double peligrosidadAlta = 0.8; //una peligrosidad de 2 es una amenaza severa 
		double mayorPeligrosidad = peligrosidad;
		for(Animal animal : amenazas) {
			double distancia = coordenadas.calcularDistancia(animal.getCoordenadas());
			if(distancia < radioAmenaza) {
				//Comprueba el objetivo del otro animal, si es el mismo aumenta la peligrosidad
				Actor objetivoActor = animal.getObjetivo();
				if(objetivoActor==this) {
					peligrosidadTotal ++;
				}
				//La peligrosidad se mide en base a todas las amenazas
				peligrosidad =  calcularPeligrosidad(animal);
				peligrosidadTotal += peligrosidad;
				
				//compara cual es más peligroso
				if(peligrosidad > mayorPeligrosidad) {
					mayorPeligrosidad = peligrosidad;
					mayorAmenaza = animal; 
				}
			}
		}
		//El estado del animal varia en funcion de la peligrosidad total
		//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas.
		if(peligrosidadTotal >= peligrosidadAlta) {
			body.setAgresividad(BodyAnimal.MIEDO);
			estado = Estado.HUIDA;
			objetivo = mayorAmenaza;
			return true;
		}
		//Si no fué muy alta la peligrosidad se pondrá en modo lucha,y fijará a su objetivo	
		else if (peligrosidadTotal >= peligrosidadAlta/2){
			body.setAgresividad(BodyAnimal.AGRESIVO);
			estado = Estado.LUCHA;
			objetivo = mayorAmenaza;
			return true;
		}
		//En caso de baja peligrosidad el estado no varia
		else {
			body.setAgresividad(BodyAnimal.PASIVO);
			return false;
		}
	}
	
	private boolean comprobarPeligroDeCazador(){
		double radioAmenaza = body.getRadioVision() / 2;
		Animal mayorAmenaza = null;
		double peligrosidad = 0;
		double peligrosidadTotal = 0;
		double peligrosidadAlta = 0.8; //una peligrosidad de 2 es una amenaza severa 
		double mayorPeligrosidad = peligrosidad;
		for(Animal animal : amenazas) {
			double distancia = coordenadas.calcularDistancia(animal.getCoordenadas());
			if(distancia < radioAmenaza) {
				//Comprueba el objetivo del otro animal, si es el mismo aumenta la peligrosidad
				Actor objetivoActor = animal.getObjetivo();
				if(objetivoActor==this) {
					peligrosidadTotal ++;
				}
				//La peligrosidad se mide en base a todas las amenazas
				peligrosidad =  calcularPeligrosidad(animal);
				peligrosidadTotal += peligrosidad;
				
				//compara cual es más peligroso
				if(peligrosidad > mayorPeligrosidad) {
					mayorPeligrosidad = peligrosidad;
					mayorAmenaza = animal; 
				}
			}
		}
		//El estado del animal varia en funcion de la peligrosidad total
		//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas.
		if(peligrosidadTotal >= peligrosidadAlta) {
			body.setAgresividad(BodyAnimal.MIEDO);
			estado = Estado.HUIDA;
			objetivo = mayorAmenaza;
			return true;
		}
		//En caso de baja peligrosidad el estado no varia
		else {
			body.setAgresividad(BodyAnimal.AGRESIVO);
			return false;
		}
	}
	
	private boolean comprobarPeligroLuchaCaza(){
		double radioAmenaza = body.getRadioVision()/2;
		Animal mayorAmenaza = null;
		double peligrosidad = 0;
		double peligrosidadTotal = 0;
		double peligrosidadAlta = 0.8; //una peligrosidad de 2 es una amenaza severa 
		double mayorPeligrosidad = peligrosidad;
		for(Animal animal : amenazas) {
			double distancia = coordenadas.calcularDistancia(animal.getCoordenadas());
			if(distancia < radioAmenaza) {

				//La peligrosidad se mide en base a todas las amenazas
				peligrosidad =  calcularPeligrosidad(animal);
				peligrosidadTotal += peligrosidad;
				
				//compara cual es más peligroso
				if(peligrosidad > mayorPeligrosidad) {
					mayorPeligrosidad = peligrosidad;
					mayorAmenaza = animal; 
				}
			}
		}
		//El estado del animal varia en funcion de la peligrosidad total
		//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas.
		if(peligrosidadTotal >= peligrosidadAlta) {
			body.setAgresividad(BodyAnimal.MIEDO);
			estado = Estado.HUIDA;
			objetivo = mayorAmenaza;
			return true;
		}
		//Si no fué muy alta la peligrosidad sigue luchando o cazando
		else {
			return false;
		}
	}

	
	
	//calcula la peligrosisdad con un algoritmo
	private double calcularPeligrosidad(Animal animal) {
		return animal.getBody().getMasa() / body.getMasa();
	}
	
	private boolean conseguirOponente () {

		if(objetivo.isVivo()) {

			int v = 0;//se movera tantas veces como su velocidad
			while(v < body.getVelocidad()) {
				//Si no lo tiene al lado lo persigue
				if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > 1.5) {
					perseguir(objetivo.getCoordenadas());
				}
				//Si está muy cerca le ataca
				else{
					BodyAnimal bodyAnimal = ((Animal)objetivo).getBody();
					bodyAnimal.infligirDanno(body.calcularAtaque(bodyAnimal.getMasa()));
					((Animal)objetivo).setObjetivo(this);//la víctima tiene a este actor como objetivo
					
					if (bodyAnimal.getSalud() <= 0) {
						if(especie != Actor.VEGETARIANO)
							comer();
						else
							matar();
					
						return true;
					}
					else if(bodyAnimal.getSalud() > BodyAnimal.HERIDO) {
						bodyAnimal.setAgresividad(BodyAnimal.AGRESIVO);
						((Animal)objetivo).setEstado(Estado.LUCHA);
					}
					else {
						bodyAnimal.setAgresividad(BodyAnimal.MIEDO);
						((Animal)objetivo).setEstado(Estado.HUIDA);
					}
					return false;
				}
				v++;
			}
			return false;
		}
		//Si el objetivo esta muerto
		objetivo = null;
		estado = Estado.NEUTRO;
		return false;
	}
	
	//Persigue al objetivo, si lo tiene al lado devuelve true
	private boolean conseguirPresa () {

		if(objetivo.isVivo()) {
			int v = 0;//se movera tantas veces como su velocidad
			while(v < body.getVelocidad()) {
				//Si no lo tiene al lado lo persigue
				if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > 1.5) {
					perseguir(objetivo.getCoordenadas());
				}
				//Si está muy cerca le ataca si es a un animal, si es a una planta no le hace nada
				else{
					if(objetivo instanceof Animal) {
						BodyAnimal bodyAnimal = ((Animal)objetivo).getBody();
						bodyAnimal.infligirDanno(body.calcularAtaque(bodyAnimal.getMasa()));
						((Animal)objetivo).setObjetivo(this);//la víctima tiene como objetivo al depredador
	
						if (bodyAnimal.getSalud() <= 0) {
							return true;
						}
						else if(bodyAnimal.getSalud() > BodyAnimal.HERIDO) {
							bodyAnimal.setAgresividad(BodyAnimal.AGRESIVO);
						}
						else {
							bodyAnimal.setAgresividad(BodyAnimal.MIEDO);
						}
						return false;
					}
				}
				v++;
			}
			return false;
		}
		//Si el objetivo esta muerto
		objetivo = null;
		estado = Estado.HAMBRE;
		return false;
	}
	
	
	//Persigue al objetivo, si lo tiene al lado devuelve true
		private boolean conseguirVegetal () {
			if(objetivo.isVivo()) {
				int v = 0;//se movera tantas veces como su velocidad
				while(v < body.getVelocidad()) {
					if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > 1.5) {
						perseguir(objetivo.getCoordenadas());
					}
					else
						return true;
					v++;
				}
				return false;
			}
			//Si el objetivo esta muerto
			objetivo = null;
			estado = Estado.HAMBRE;
			return false;
		}
		
	private boolean perseguirPareja() {
		//Los machos se acercan del todo, la hembra solo se acerca al area de vision del macho, solo el macho inicia el apareamiento
		if(objetivo.isVivo()) {
			if(body.getSexo() == BodyAnimal.MACHO) {
				int v = 0;//se movera tantas veces como su velocidad
				while(v < body.getVelocidad()) {

					if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > 1.5) {
						perseguir(objetivo.getCoordenadas());
					}
					//Al macho no le importa si no es correspondido, intentará aparearse igual
					else {
						estado = Estado.REPRODUCCION;
						return true;
					}
					v++;
				}
				return false;

			}
			//Si es hembra solo se acerca a su campo de vision
			else{
				int v = 0;//se movera tantas veces como su velocidad y mientras esté lejos su objetivo
				while(v < body.getVelocidad() ) {
					
					if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > (body.getRadioVision()/2)) {
						perseguir(objetivo.getCoordenadas());
					}
						
					//Si esta bastante cerca la hembra se queda quieta a la espera del macho que ella desea
					else{
						//comprueba si es correspondida
						if(((Animal)objetivo).getObjetivo() == this) {
							estado = Estado.REPRODUCCION;
							return true;					
						}
						return false;
					}
					v++;
				}
				return false;
			}
		}
		//Si el objetivo esta muerto
		objetivo = null;
		return false;
	}
	
	private void comer() {
		if(objetivo instanceof Animal) {
			body.digestion(((Animal) objetivo).getBody());
			objetivo.morir();
			objetivo = null;
		}
		else {
			body.digestion(((Vegetal) objetivo).getBody());
			objetivo.morir();
			objetivo = null;
		}
		estado = Estado.NEUTRO; //Una vez come pasa a estado neutro
	}
	
	//Comprueba si seguir huyendo o no
	private void controlarHuida() {
		if(objetivo.isVivo()) {
			int v = 0;//se movera tantas veces como su velocidad
			while(v < body.getVelocidad()) {
				//Si esta lejos deja de huir
				if(coordenadas.calcularDistancia(objetivo.getCoordenadas()) > body.getRadioVision()) {
					objetivo = null;
					estado = Estado.NEUTRO;
					break;//Si esta en mitad de la huida pero pierde el objetivo sale del bucle
				}
				//Si esta cerca sigue huyendo de el
				else {
					huir(objetivo.getCoordenadas());
				}
				v++;
			}
		}
	}
	
	//Retorna false si gana la batalla y true si la batalla continua
	private boolean lucharAMuerte() {
		if(objetivo.isVivo()) {
				BodyAnimal bodyAnimal = ((Animal)objetivo).getBody();
				bodyAnimal.infligirDanno(body.calcularAtaque(bodyAnimal.getMasa()));				
				if (bodyAnimal.getSalud() == 0) {
					((Animal)objetivo).morir();
					return false;
				}
				else if(bodyAnimal.getSalud() > BodyAnimal.HERIDO) {
					bodyAnimal.setAgresividad(BodyAnimal.AGRESIVO);
				}
				else {
					bodyAnimal.setAgresividad(BodyAnimal.MIEDO);
				}
				return true;
			}
		//Si esta muerto termina
		return false;
	}

	
	private void matar() {
		((Animal)objetivo).morir();
		objetivo = null;
	}
	
	private boolean isMismoObjetivo(Actor actorObjetivo,HashSet<Animal> listaActores) {
		for(Animal ajeno : listaActores) {
			if(actorObjetivo == ajeno.getObjetivo()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public String toString() {
		
		
		String libido = (body.getLibido()==BodyAnimal.CELO)? "\u2764": ""; //corazón o corazón partido
		String sexo = (body.getSexo()==BodyAnimal.HEMBRA)? "\u2640": "\u2642";//Hembra y macho
		String obje = (objetivo != null)? "\n\t"+objetivo.getCoordenadas().toString():"";
		return String.format("\n %s m:%.0f/t:%.0f e:%d %s %s X:%d"
							,coordenadas,body.getMasa(),body.getTamanno(),(int)body.getEdad(),sexo,libido,body.getLibido())
		+ String.format(" hp:%d dt:%d %s %s",body.getSalud(),body.getTipoAlimentacion(),estado.getEstado(),obje);
	}

	public BodyAnimal getBody() {
		return body;
	}
	
	
	public Estado getEstado() {
		return estado;
	}
	
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public int getEspecie() {
		return especie;
	}
	
	public Actor getObjetivo() {
		return objetivo;
	}
	
	public void setObjetivo(Actor objetivo) {
		 this.objetivo= objetivo;
	}
	
	public void setMovimiento(boolean movimiento) {
		this.movimiento = movimiento;
	}

	private void resetListas() {
		parejas = new HashSet<>();
		presas = new HashSet<>();
		pasto = new HashSet<>();
		iguales = new HashSet<>();
		amenazas = new HashSet<>();
		aliados = new HashSet<>();
	}
	
	
}
