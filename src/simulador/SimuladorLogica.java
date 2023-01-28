package simulador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import actores.Actor;
import actores.Animal;
import actores.Vegetal;
import biología.ADN;
import biología.Body;
import biología.BodyAnimal;
import biología.BodyVegetal;
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

	
	public SimuladorLogica(SimuladorGUI GUI, Mapa mapa) {
		this.GUI = GUI;
		this.mapa = mapa;
		inputConsola = GUI.getInputConsola();
		outputConsola = GUI.getOutputConsola();
		continuar = false;
		mostrarDatos = false;
		pausaMilisegundos = 0;

		
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
		Iterator <Actor> it =mapa.getListaActores().iterator();
		while(it.hasNext()) {
			Actor actor = it.next();
			actor.actuar();

			if(actor instanceof Animal) {
				medidorAnimales++;
				if(mostrarDatos) {
					outputConsola.append(actor.toString());
				}
			}
			else {medidorVegetales++;}
			
			if(actor.getMarcar() && !mostrarDatos) {
				outputConsola.append(actor.toString());
			}
		}
		Random random = new Random();
		int x = random.nextInt(Mapa.ANCHO);
		int y = random.nextInt(Mapa.ALTO);
		if(mapa.isLibre(x, y, Mapa.CAPA_VEGETAL)) {
			new Vegetal(mapa,new Coordenadas(x,y,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
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
				mapa.getActor(x,y,z).setMarcar();
				actualizarMapa();
				break;
			case "insert":
				//Solo inserta si la iteracción esta parada para evitar errores
				if(!continuar) {
					String tipo = tokens[1];
					x = Integer.parseInt(tokens[2]);
					y = Integer.parseInt(tokens[3]);
					if(tipo.equals("car")) {
						insertCarnivoro(x,y);
					}
					else if(tipo.equals("omn")) {
						insertOmnivoro(x,y);
					}
					
					else if(tipo.equals("her")) {
						insertHerbivoro(x,y);
					}

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
	
	private void insertCarnivoro(int x, int y) {
		new Animal(mapa,new Coordenadas(x,y,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(ADN.MACHO,ADN.CARNIVORO),5);
	}
	
	private void insertOmnivoro(int x, int y) {
		new Animal(mapa,new Coordenadas(x,y,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(ADN.MACHO,ADN.OMNIVORO),5);
	}
	
	private void insertHerbivoro(int x, int y) {
		new Animal(mapa,new Coordenadas(x,y,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(ADN.HEMBRA,ADN.HERBIVORO),5);
	}
	
	
	private void crearActores() {
		
		new Animal(mapa,new Coordenadas(11,20,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(ADN.HEMBRA,ADN.HERBIVORO),5);
		new Animal(mapa,new Coordenadas(20,30,Mapa.CAPA_ANIMAL),ADN.crearADNAnimal(ADN.MACHO,ADN.HERBIVORO ),5);
		new Vegetal(mapa,new Coordenadas(16,25,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(16,40,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(17,25,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(18,40,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(19,28,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(20,40,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(40,55,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(30,40,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(35,28,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
		new Vegetal(mapa,new Coordenadas(25,40,Mapa.CAPA_VEGETAL),ADN.crearADNVegetal());
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
}
