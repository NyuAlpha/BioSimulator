package graficos;

import java.awt.image.BufferedImage;
import java.util.HashSet;

import actores.Actor;
import actores.Animal;

/**
 * Esta clase representa el mapa donde tendrá lugar la simulación.
 * @author Victor
 *
 */
public class Mapa {
	
	//Array donde están contenidos todos los actores del mapa
	private  Actor [][][] campo;
	//Lista de todos los actores del mapa
	private HashSet<Actor> actores;
	//Donde se dibuja el mapa y todos sus actores
	private BufferedImage imagenMapa;
	
	//Anchura y altura del mapa en tiles
	public static final int ANCHO = 110;
	public static final int ALTO = 80;
	//pixeles de anchura de cada tile
	public static final int TILE_WIDTH = 9;
	
	//Capas posibles de las coordenadas de Z
	public static final int CAPA_BASE = 0;
	public static final int CAPA_VEGETAL = 1;
	public static final int CAPA_ANIMAL = 2;
	private static final int CAPAS_TOTALES = 3;
	
	/**
	 * Crea un mapa con un ancho y un alto de tiles o celdas
	 */
	public Mapa() {
		
		//Crea un bufferedImage que representará el mapa
		int anchuraPixeles =  ANCHO * TILE_WIDTH ;
		int alturaPixeles = ALTO * TILE_WIDTH ;
		imagenMapa = new BufferedImage(anchuraPixeles, alturaPixeles, BufferedImage.TYPE_INT_RGB);
		
		//reset del mapa al crearlo
		reset();
	}
	
	
	/**
	 * Devuelve el Animal que hay en las posiciones pasadas como parametro
	 * @param x Coordenada x 
	 * @param y Coordenada y
	 * @param capa - capa de búsqueda
	 * @return el animal seleccionado 
	 */
	public Actor getActor(int x ,int y, int capa){
		return campo[x][y][capa];
	}
	
	public boolean isLibre(int x, int y, int capa) {
		if(campo[x][y][capa] == null) 
			return true;
		return false;
	}
	
	public void moverActorA(Actor actor,int posicionX ,int posicionY) {
		int posZ = actor.getCoordenadas().getZ();
		campo[actor.getCoordenadas().getX()][actor.getCoordenadas().getY()][posZ] = null;
		campo[posicionX][posicionY][posZ] = actor;
	}
	
	public void moverActorHacia(Actor actor,int direccionX ,int direccionY) {
		int posX = actor.getCoordenadas().getX();
		int posY = actor.getCoordenadas().getY();
		int posZ = actor.getCoordenadas().getZ();
		campo[posX][posY][posZ] = null;
		campo[posX + direccionX][posY + direccionY][posZ] = actor;
	}
	
	public void putActor(Actor actor, Coordenadas coordenadas) {
		campo[coordenadas.getX()][coordenadas.getY()][coordenadas.getZ()] = actor;
	}
	
	public void eliminarActor(Actor actor) {
		Coordenadas coordenadas = actor.getCoordenadas();
		campo[coordenadas.getX()][coordenadas.getY()][actor.getCoordenadas().getZ()] = null;
	}
	
	
	/*
	 * Devuelve la lista de actores que hay en el mapa
	 */
	public HashSet<Actor> getListaActores(){
		return actores;
	}

	/*
	 * Dibuja cada celda del mapa en su estado actual
	 */
	public void dibujarMapa() {

		for(int x = 0 ; x < ANCHO ; x++) {
			for(int y = 0 ; y < ALTO ; y++) {

				Actor planta = getActor(x, y ,CAPA_VEGETAL);
				Actor animal = getActor(x, y,CAPA_ANIMAL);

				if(planta != null) {
					dibujarCelda(planta.getMargenAsociado(),x,y,planta.getColorAsociado());
				}
				else {
					dibujarCelda(0,x,y,TiposActores.SUELO.getColorRGB());
				}
				
				if(animal != null) {
					dibujarCelda(animal.getMargenAsociado(),x,y,animal.getColorAsociado());
				}
			}
		}
	}

	/*
	 * Dibuja una celda en el mapa indicandole el margen que dejará vacio, las coordenadas de la celda X e Y 
	 * y el color que usará
	 */
	private void dibujarCelda(int margen,int celdaX , int celdaY, int colorRGB){
		for(int i = margen; i <TILE_WIDTH - margen;i++) {
			for(int j = margen; j <TILE_WIDTH - margen;j++) {
				imagenMapa.setRGB(celdaX * TILE_WIDTH +i,celdaY * TILE_WIDTH +j,  colorRGB );
			}
		}
	}
	
	/*
	 * Actualiza el array de actores que hay en el mapa
	 */
	public void actualizarListas() {
		actores = new HashSet<>();

		for(int x = 0 ; x < ANCHO ; x++) {
			for(int y = 0 ; y < ALTO ; y++) {
				Actor planta = getActor(x, y ,CAPA_VEGETAL);
				Actor animal = getActor(x, y,CAPA_ANIMAL);
				if(planta != null) {
					actores.add(planta);
				}
				if(animal != null) {
					actores.add(animal);
				}
			}
		}
	}
	
	/*
	 * Devuelve un BufferedImage con la imagen del mapa
	 */
	public BufferedImage getImagenMapa() {
		return imagenMapa;
	}
	
	/*
	 * Inicia el array del mapa haciendo un reset
	 */
	public void reset() {
		campo = new Actor[ANCHO][ALTO][CAPAS_TOTALES];
	}
}
