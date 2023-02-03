package simulador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import actores.Actor;
import actores.Animal;
import actores.Vegetal;
import biologia.ADN;
import biologia.Body;
import biologia.BodyAnimal;
import biologia.BodyVegetal;
import graficos.Coordenadas;
import graficos.Mapa;

public class SimuladorLogica implements Runnable
{
	private Mapa mapa;
	private JTextField inputConsola;
	private JTextArea outputConsola;
	private SimuladorGUI GUI;
	private long iteracion;
	private boolean continuar;
	private boolean mostrarDatos;
	private int pausaMilisegundos;
	private Coordenadas coordenadaAnimalSeleccionada;

	
	public SimuladorLogica(SimuladorGUI GUI, Mapa mapa) {
		this.GUI = GUI;
		this.mapa = mapa;
		inputConsola = GUI.getInputConsola();
		outputConsola = GUI.getOutputConsola();
		continuar = false;
		mostrarDatos = false;
		pausaMilisegundos = 0;
		coordenadaAnimalSeleccionada = new Coordenadas(0,0,Mapa.CAPA_ANIMAL);

		
		inputConsola.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gestionarComandos(inputConsola.getText());
				inputConsola.setText("");
			}	
		});

		reset();

	}
	
	public synchronized void run() {
		while(continuar) {
			actualizarSimulacion(pausaMilisegundos);
		}
	}

	
	
	private synchronized void iterar() {
		int medidorAnimales = 0;
		int medidorVegetales = 0;
		//Aquí se añaden los animales marcados
		HashSet<Actor> marcados = new HashSet<>();
		Iterator <Actor> it =mapa.getListaActores().iterator();
		while(it.hasNext()) {
			Actor actor = it.next();
			actor.actuar();
			if(actor instanceof Animal) {
				medidorAnimales++;
				if(mostrarDatos) {
					marcados.add(actor);
				}
			}
			else {medidorVegetales++;}
			
			if(actor.getMarcar() && !mostrarDatos) {
				marcados.add(actor);
			}
		}
		
		for(Actor actor : marcados) {
			outputConsola.append(actor.toString());
		}
		
		actualizarMapa();
		outputConsola.append("\n Fin de iteración nº " + ++iteracion +"  Pobl A/V = " + medidorAnimales +"|"+ medidorVegetales);
	}
	

	
	private void actualizarSimulacion(int milisegundos) {
		double inicio = System.nanoTime();
		while((System.nanoTime() - inicio) < (milisegundos * 1000000 )) {
			
		}
		iterar();
	}
	
	
	public void gestionarComandos(String cadena) {
		String[] tokens = cadena.split(" ");
		String comando = tokens[0].toLowerCase();
		switch(comando){
			case "clear":
				outputConsola.setText("");
				break;
			case "put":
				tokens[0] = "";
				outputConsola.append("\n ");
				for (String s : tokens) {
					outputConsola.append(s + " ");
				}

				break;
			case "stop":
				continuar = false;
				outputConsola.append("\n" + cadena);
				break;
			case "start":
				continuar = true; //Si ya es true no deberia repetir este case
				new Thread(this).start();
				outputConsola.append("\n" + cadena);
				break;
			case "reset":
				continuar = false;
				reset();
				break;
			case "":
				if(!continuar) //pequeña chapuza
					iterar();
				break;
			case "speed":
				pausaMilisegundos = Integer.parseInt(tokens[1]); // posibilidad de error
				break;
			case "sdata":
				outputConsola.append("\n Modo mostrar datos ON");
				mostrarDatos = !mostrarDatos;
				break;
			case "mark":
				int x = Integer.parseInt(tokens[1]);
				int y = Integer.parseInt(tokens[2]);
				int z = Integer.parseInt(tokens[3]);
				Actor actor = mapa.getActor(x,y,z);
				if(actor.isVivo())
					actor.setMarcar();
				actualizarMapa();
				break;
			case "add":
				//Solo inserta si la iteracción esta parada para evitar errores
				if(!continuar) {
					char c = tokens[1].charAt(0);
					insert(c,coordenadaAnimalSeleccionada);
					actualizarMapa();
				}
				else {
					outputConsola.append("\n Imposible insertar durante la simulación");
				}
				break;
			default:
				outputConsola.setText("\n Comando no reconocido");
				break;
		}
	}
	
	private void insert(char c, Coordenadas coordenadas) {
		ADN adnM = null;
		ADN adnH = null;
		int tamanno = 0;
		if(c == 'c') {
			adnM = ADN.crearADNAnimal(BodyAnimal.MACHO,Animal.DEPREDADOR);
			adnH = ADN.crearADNAnimal(BodyAnimal.HEMBRA,Animal.DEPREDADOR);
			tamanno = 11;
		}
		else if(c == 'o') {
			adnM = ADN.crearADNAnimal(BodyAnimal.MACHO,Animal.HUMANO);
			adnH = ADN.crearADNAnimal(BodyAnimal.HEMBRA,Animal.HUMANO);
			tamanno = 7;
		}
		else if(c == 'h') {
			adnM = ADN.crearADNAnimal(BodyAnimal.MACHO,Animal.VEGETARIANO);
			adnH = ADN.crearADNAnimal(BodyAnimal.HEMBRA,Animal.VEGETARIANO);
			tamanno = 5;
		}
		if (adnM == null){
			outputConsola.append("\n especie incorrecta");
		}
		else {
			Coordenadas coordenadasH = new Coordenadas(coordenadas.getX()-1,coordenadas.getY(),coordenadas.getZ());
			new Animal(mapa,coordenadas,adnM,tamanno);
			new Animal(mapa,coordenadasH,adnH,tamanno);
		}
	}

	private void crearActores() {
		
		//Debe ser siempre par para cada especie
		final int numeroVegetales = 1200;
		final int numeroHerbivoros = 60;
		final int numeroCarnivoros = 4;
		final int numeroOmnivoros = 0;
		
		Random random = new Random();
		for (int i = 0; i < numeroVegetales; i++) {
			int x = random.nextInt(Mapa.ANCHO);
			int y = random.nextInt(Mapa.ALTO);
			if(mapa.isLibre(x, y, Mapa.CAPA_VEGETAL)) {
				new Vegetal(mapa,new Coordenadas(x,y,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal(),2);
			}
		}
		crearAnimales(numeroHerbivoros,Actor.VEGETARIANO,6);
		crearAnimales(numeroCarnivoros,Actor.DEPREDADOR,10);
		crearAnimales(numeroOmnivoros,Actor.HUMANO,6);
	}
		
	private void crearAnimales(int numeroIndividuos,int especie, int tamanno) {
		
		Random random = new Random();
		int x;
		int y;
		for (int i = 0; i < numeroIndividuos; i+=2) { //
			for(int sexo = 0; sexo<= BodyAnimal.MACHO ; sexo++) { //itera de macho a hembra, que es el valor 1
				do {
					x = random.nextInt(Mapa.ANCHO);
					y = random.nextInt(Mapa.ALTO);
				}
				while(!isLibre(x,y,Mapa.CAPA_ANIMAL));
				int tamannoFinal = tamanno + sexo;
				new Animal(mapa,new Coordenadas(x,y,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(sexo,especie),tamanno);
			}
		}
	}
	
	private boolean isLibre(int x, int y , int capa) {
		return mapa.isLibre(x, y, capa);
	}
	
	private void actualizarMapa() {
		mapa.dibujarMapa();
		GUI.redibujar();
		mapa.actualizarListas();
	}
	
	private synchronized void reset() {
		iteracion = 0;
		mapa.reset();
		crearActores();
		actualizarMapa();
	}

	//Selecciona una coordenada, por defecto la animal
	public void setCoordenadaAnimalSeleccionada(int x, int y) {
		this.coordenadaAnimalSeleccionada = new Coordenadas(x,y,Mapa.CAPA_ANIMAL);
	}
	
}
