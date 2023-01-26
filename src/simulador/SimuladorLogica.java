package simulador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import actores.Actor;
import actores.Animal;
import actores.Vegetal;
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
		pausaMilisegundos = 100;

		
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
				if(mostrarDatos) {
					outputConsola.append("\n " + actor.toString());
				}
				if(actor.getMarcar()) {
					outputConsola.append(actor.getEstado());
				}
				medidorAnimales++;
			}
			else {medidorVegetales++;}
		}
		mapa.actualizarMapa();
		outputConsola.append("\n Fin de iteración nº " + ++iteracion +"  Pobl A/V = " + medidorAnimales +"|"+ medidorVegetales + "\n");
		GUI.redibujar();
		mapa.actualizarListas();
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
				mapa.actualizarMapa();
				GUI.redibujar();
				mapa.actualizarListas();
				break;
			default:
				outputConsola.setText("Comando no reconocido");
				break;
		}
	}
	
	private void crearActores() {
		new Animal(mapa,new Coordenadas(11,20,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(20,30,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(23,37,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(15,40,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(1,21,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(25,31,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);	
		new Animal(mapa,new Coordenadas(55,43,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(62,57,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		new Animal(mapa,new Coordenadas(70,60,Mapa.CAPA_ANIMAL),Animal.HERVIVORO);
		
		new Vegetal(mapa,new Coordenadas(70,50,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(60,70,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(50,60,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(15,25,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(40,40,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(17,22,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(10,9,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(18,22,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(10,12,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(10,6,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(24,22,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(15,12,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(2,22,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(15,3,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(12,22,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(5,12,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(22,7,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(8,32,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(9,9,Mapa.CAPA_VEGETAL));
		new Vegetal(mapa,new Coordenadas(0,0,Mapa.CAPA_VEGETAL));
		
		new Animal(mapa,new Coordenadas(35,35,Mapa.CAPA_ANIMAL),Animal.CARNIVORO);
	}
	
	private synchronized void reset() {
		iteracion = 0;
		mapa.reset();
		crearActores();
		mapa.actualizarMapa();
		GUI.repaint();
		mapa.actualizarListas();
	}
}
