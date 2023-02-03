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
	private Animal madre;
	private Animal cria;
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

	//Cuando nace un bebé
	private Animal(Mapa mapa, Coordenadas coordenadas,Feto feto,Animal madre) {
		super(mapa, coordenadas);
		body = new BodyAnimal(feto,this);
		especie = (int)(body.getEspecie() + 0.1); //al ser un float se le aplica un margen para evitar malos redondeos
		this.madre = madre;
		iniciarVariablesComunes();
	}
	
	//Animales prefabricados
	public Animal(Mapa mapa, Coordenadas coordenadas,ADN adn,double tamanno) {
		super(mapa, coordenadas);
		body = new BodyAnimal(adn,this);
		body.setTamanno(tamanno);
		body.setEdad(31);
		especie = (int)(body.getEspecie() + 0.1); //al ser un float se le aplica un margen para evitar malos redondeos
		madre = null;
		iniciarVariablesComunes();
	}
	
	private void iniciarVariablesComunes() {
		establecerAsociaciones();
		mapa.putActor(this,coordenadas);
		coordenadaObjetiva = null;
		
		objetivo = null;
		cria = null;
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
			cria = new Animal(mapa,coordenadasLibres,feto,this);
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
					if(body.isLactante()) {
						if(comprobarMadre())
							rondarMadre();
					}
					else
						mover();
					
					if(!comprobarPeligro()) {//huida, lucha,protección o neutro
						if(!comprobarHambre()){//hambre o neutro
							comprobarLibido(); // celo o neutro
						} 
					}
					break;
				case HAMBRE:
					mover();
					gestionarHambre();
					break;
				case CAZA:
					if(comprobarSalud()) {//huida o caza pero no asigna objetivo
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
				case PROTECCION:
					comprobarPeligro();//huida, lucha,protección o neutro, gestiona todo en las crias
					break;
				case LUCHA:
					if(!comprobarPeligroLuchaCaza()) {//huida o lucha
						if(conseguirOponente()) { //neutro o lucha
							if(especie != Actor.VEGETARIANO)
								comer(); // neutro y anula el objetivo
							else
								matar(); // neutro y anula el objetivo
						}
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
				case COPULA:
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
	

	private void gestionarHambre() {
		if(body.isLactante()) {
			if(!comprobarPeligro())//huida, protección o hambre
				if(comprobarMadre()) {
					if(perseguirMadre()) {
						mamar();//Neutro
					}
				}
				else
					mover();
		}
		else {
			if(body.getTipoAlimentacion() != BodyAnimal.HERBIVORO) {
				if(!comprobarPeligroLuchaCaza())
					buscarPresas();
			}
			else {
				if(!comprobarPeligro())
					buscarVegetal();
			}//huida, lucha, pastoreo, caza o hambre
		}
	}
	
	//Cambia a celo o nada
	private void comprobarLibido() {
		if(body.getLibido() == BodyAnimal.CELO && cria==null)
			estado = Estado.CELO;
	}
	
	//Comprueba si tiene madre y si esta viva, además la cambia a null si esta muerta
	private boolean comprobarMadre() {
		if(madre != null) {
			if(madre.isVivo())
				return true;
			else
				madre = null;
		}
		return false;
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
	private boolean comprobarSalud() {
		if(body.getSalud() < BodyAnimal.HERIDO) {
			estado = Estado.HUIDA;
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
			if(animal.isVivo()) {
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
		}
		//Si hay peligrosidad	
		if (peligrosidadTotal >= peligrosidadAlta/2){
			//Si es lactante buscará a su madre o huirá si no la tiene
			if(body.isLactante()) {
				if(comprobarMadre()) {
					estado = Estado.PROTECCION;
					perseguirMadre();
				}
				else {
					pasarAHuida(mayorAmenaza);
				}
				return true;
			}
			//Si no es una cria  y la peligrosidad es muy alta
			//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas.
			if(peligrosidadTotal >= peligrosidadAlta) {
				pasarAHuida(mayorAmenaza);
				return true;
			}
			//Si no es lactante pasa a modo lucha,y fija a su objetivo	
			pasarALucha(mayorAmenaza);
			return true;
		}
		//En caso de baja peligrosidad el estado es neutro
		else {
			if(estado == Estado.PROTECCION)
				estado = Estado.NEUTRO;

			return false;
		}
	}
	
	private void pasarAHuida(Animal amenaza) {
		estado = Estado.HUIDA;
		objetivo = amenaza;
		controlarHuida();
	}
	
	private void pasarALucha(Animal amenaza) {
		estado = Estado.LUCHA;
		objetivo = amenaza;
	}
	
	private boolean comprobarPeligroDeCazador(){
		double radioAmenaza = body.getRadioVision() / 2;
		Animal mayorAmenaza = null;
		double peligrosidad = 0;
		double peligrosidadTotal = 0;
		double peligrosidadAlta = 0.8; //una peligrosidad de 2 es una amenaza severa 
		double mayorPeligrosidad = peligrosidad;
		for(Animal animal : amenazas) {
			if(animal.isVivo()) {
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
		}
		//El estado del animal varia en funcion de la peligrosidad total
		//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas.
		if(peligrosidadTotal >= peligrosidadAlta) {
			pasarAHuida(mayorAmenaza);
			return true;
		}
		return false;
	}
	
	private boolean comprobarPeligroLuchaCaza(){
		double radioAmenaza = body.getRadioVision()/2;
		Animal mayorAmenaza = null;
		double peligrosidad = 0;
		double peligrosidadTotal = 0;
		double peligrosidadAlta = 0.8; //una peligrosidad de 2 es una amenaza severa 
		double mayorPeligrosidad = peligrosidad;
		for(Animal animal : amenazas) {
			if(animal.isVivo()) {
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
		}
		//El estado del animal varia en funcion de la peligrosidad total
		//Además en un futuro cercano aquí se añadira una función para huir de todas las amenazas con funcion de dirección
		if(peligrosidadTotal >= peligrosidadAlta) {
			pasarAHuida(mayorAmenaza);
			return true;
		}
		return false;
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
						return true;
					}
					else if(bodyAnimal.getSalud() > BodyAnimal.HERIDO) {
						((Animal)objetivo).setEstado(Estado.LUCHA);
					}
					else {
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
							((Animal)objetivo).setEstado(Estado.LUCHA);
						}
						else {
							((Animal)objetivo).setEstado(Estado.DEFENSA);
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
		
	//se acerca a su madre, si esta al lado devuelve true
	private boolean perseguirMadre() {
		int v = 0;//se movera tantas veces como su velocidad
		while(v < body.getVelocidad()) {
			if(coordenadas.calcularDistancia(madre.getCoordenadas()) > body.getRadioVision()) {
				mover();//Si la madre esta muy lejos se mueve al azar
			}
			else if(coordenadas.calcularDistancia(madre.getCoordenadas()) > 1.5) {
				perseguir(madre.getCoordenadas());
			}
			else
				return true;//Cuando la tiene al lado
			v++;
		}
		return false;
	}
	
	private void rondarMadre() {
		int v = 0;//se movera tantas veces como su velocidad
		while(v < body.getVelocidad()) {
				
			if(coordenadas.calcularDistancia(madre.getCoordenadas()) > body.getRadioVision()) {
				mover();//Si la madre esta muy lejos se mueve al azar
			}
			else if(coordenadas.calcularDistancia(madre.getCoordenadas()) > body.getRadioVision()/4) {
				perseguir(madre.getCoordenadas());
			}
			else {
				mover();
			}
			v++;
		}
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
						estado = Estado.COPULA;
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
							estado = Estado.COPULA;
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
	
	//mama de la madre
	private void mamar() {
		//Si la madre no esta muerta de hambre podrá mamar de ella
		if(madre.getBody().getHambre() != BodyAnimal.MUY_HAMBRIENTO) {
			double masaLeche = body.getTamanno();//Cantidad equivalente al tamaño
			madre.getBody().setMasa(-masaLeche);//Le roba masa a la madre
			body.setMasa(masaLeche);//Se la suma
			estado = Estado.NEUTRO; //Una vez come pasa a estado neutro
		}
	}
	
	private void matar() {
		((Animal)objetivo).morir();
		objetivo = null;
		estado = Estado.NEUTRO;
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
		else {
			objetivo = null;
			estado = Estado.NEUTRO;
		}
	}
	
	//Retorna false si gana la batalla y true si la batalla continua
	private boolean lucharAMuerte() {
		if(objetivo.isVivo()) {
				BodyAnimal bodyAnimal = ((Animal)objetivo).getBody();
				bodyAnimal.infligirDanno(body.calcularAtaque(bodyAnimal.getMasa()));				
				if (bodyAnimal.getSalud() <= 0) {
					((Animal)objetivo).morir();
					return false;
				}
				else if(bodyAnimal.getSalud() > BodyAnimal.HERIDO) {
				}
				return true;
			}
		//Si esta muerto termina
		return false;
	}
	
	private boolean isMismoObjetivo(Actor actorObjetivo,HashSet<Animal> listaActores) {
		for(Animal ajeno : listaActores) {
			if(actorObjetivo == ajeno.getObjetivo()) {
				return true;
			}
		}
		return false;
	}
	
	public void desmadrarse() {
		if(comprobarMadre())
			madre.perderCria();
	}
	
	//De momento no es necesario, pero se usará
	public void perderCria() {
		cria = null;
	}
	
	public void morir() {
		//Antes de llamar a la super clase comprueba si tiene madre, si la tiene perderá a su cría
		if(madre != null)
			madre.perderCria();
		super.morir();
	}
	
	
	public String toString() {
		
		
		String libido = (body.getLibido()==BodyAnimal.CELO)? "\u2764": ""; //corazón o corazón partido
		String sexo = (body.getSexo()==BodyAnimal.HEMBRA)? "\u2640": "\u2642";//Hembra y macho
		String obje = (objetivo != null)? "\n\t"+objetivo.getDataShort():"";
		String espec ;
		String salud;
		String lactante = (body.isLactante())? "\u2763": "";
		if(especie == Actor.DEPREDADOR)
			espec = "\u2694";
		else if(especie == Actor.HUMANO)
			espec = "\u262F";
		else if(especie == Actor.VEGETARIANO)
			espec = "\u2658";
		else
			espec = "\u26B6";
		
		if(body.getSalud() > BodyAnimal.HERIDO)
			salud = "\u2661\u2661";
		else if(body.getSalud() <= 0)
			salud = " \u2670";
		else
			salud = " \u2661";
		
		return String.format("\n   %s %s %.0f/%.0f e:%d %s %s"
							,espec,coordenadas,body.getTamanno(),body.getMasa(),(int)body.getEdad(),sexo,libido)
		+ String.format(" %s %s dt:%d %s %s",salud,lactante,body.getTipoAlimentacion(),estado.getEstado(),obje);
	}
	
	public String getDataShort() {
		
		String libido = (body.getLibido()==BodyAnimal.CELO)? "\u2764": ""; //corazón o corazón partido
		String sexo = (body.getSexo()==BodyAnimal.HEMBRA)? "\u2640": "\u2642";//Hembra y macho
		String espec ;
		String salud;
		String lactante = (body.isLactante())? "\u2763": "";
		if(especie == Actor.DEPREDADOR)
			espec = "\u2694";
		else if(especie == Actor.HUMANO)
			espec = "\u262F";
		else if(especie == Actor.VEGETARIANO)
			espec = "\u2658";
		else
			espec = "\u26B6";
		
		if(body.getSalud() > BodyAnimal.HERIDO)
			salud = "\u2661\u2661";
		else if(body.getSalud() <= 0)
			salud = " \u2670";
		else
			salud = " \u2661";

		return String.format("-> %s %s %.0f/%.0f e:%d %s %s"
							,espec,coordenadas,body.getTamanno(),body.getMasa(),(int)body.getEdad(),sexo,libido)
		+ String.format(" %s %s dt:%d %s",salud,lactante,body.getTipoAlimentacion(),estado.getEstado());
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
