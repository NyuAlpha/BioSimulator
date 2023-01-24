package graficos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	private ArrayList<Actor> actores;
	//Donde se crean todos los tiles que serán dibujados en el mapa
	private FabricaTiles fabricaTiles;
	//Donde se dibuja el mapa y todos sus actores
	private BufferedImage imagenMapa;
	//Anchura y altura del mapa
	private int ancho;
	private int alto;
	
	/**
	 * Crea un mapa con un ancho y un alto de tiles o celdas
	 * @param ancho - anchura del mapa en tiles o celdas
	 * @param alto - altura del mapa en tiles o celdas
	 */
	public Mapa(int ancho, int alto) {
		
		this.ancho = ancho;
		this.alto = alto;
		
		fabricaTiles = new FabricaTiles();
		int anchuraPixeles =  ancho * FabricaTiles.LADO_TILE ;
		int alturaPixeles = alto * FabricaTiles.LADO_TILE ;
		imagenMapa = new BufferedImage(anchuraPixeles, alturaPixeles, BufferedImage.TYPE_INT_RGB);
		
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
	
	public void moverActor(Actor actor, Coordenadas coordenadasViejas , Coordenadas coordenadasNuevas , int capa) {
		campo[coordenadasViejas.getX()][coordenadasViejas.getY()][capa] = null;
		campo[coordenadasNuevas.getX()][coordenadasNuevas.getY()][capa] = actor;
	}
	
	public void moverActor(Actor actor,int posicionX ,int posicionY) {
		int capa = actor.getCapa();
		campo[actor.getCoordenadas().getX()][actor.getCoordenadas().getY()][capa] = null;
		campo[posicionX][posicionY][capa] = actor;
	}
	
	public void moverActorA(Actor actor,int direccionX ,int direccionY) {
		int posX = actor.getCoordenadas().getX();
		int posY = actor.getCoordenadas().getY();
		int capa = actor.getCapa();
		campo[posX][posY][capa] = null;
		campo[posX + direccionX][posY + direccionY][capa] = actor;
	}
	
	public void putActor(Actor actor, Coordenadas coordenadas , int capa) {
		campo[coordenadas.getX()][coordenadas.getY()][capa] = actor;
	}
	
	public void eliminarActor(Actor actor) {
		Coordenadas coordenadas = actor.getCoordenadas();
		campo[coordenadas.getX()][coordenadas.getY()][actor.getCapa()] = null;
	}
	
	
	/**
	 * Devuelve la lista de actores que hay en el mapa
	 * @return la lista de actores del mapa
	 */
	public ArrayList<Actor> getListaActores(){
		return actores;
	}

	/**
	 * actualiza el mapa, copia a una lista todos los actores del mapa y dibuja todo el mapa
	 */
	public void actualizarMapa() {
		actores = new ArrayList<>();
		int capaAnimal = 2;
		int capaVegetal = 1;
		for(int x = 0 ; x < ancho ; x++) {
			for(int y = 0 ; y < alto ; y++) {
				dibujarCelda(x,y,fabricaTiles.TIERRA,false);
				Actor planta = getActor(x, y ,capaVegetal);
				Actor animal = getActor(x, y,capaAnimal);
				if(planta != null) {
					dibujarCelda(x,y,fabricaTiles.HIERBA,false);
					actores.add(planta);
				}
				if(animal != null) {
					if(((Animal) animal).getTipoAlimentacion() == Animal.HERVIVORO)
						{dibujarCelda(x,y,fabricaTiles.MACHO,false);}
					else if(((Animal) animal).getTipoAlimentacion() == Animal.CARNIVORO)
						{dibujarCelda(x,y,fabricaTiles.HEMBRA,false);}
					if(animal.getMarcar()) {
						dibujarCelda(x,y,fabricaTiles.HEMBRA,true);
					}
					actores.add(animal);
				}
			}
		}
	}
	
	/**
	 * Dibuja un tile en el mapa con las coordenadas y el tile especificado
	 * @param celdaX - la coordenada x
	 * @param celdaY - la coordenada y
	 * @param tile - el tile que dibujará
	 */
	private void dibujarCelda(int celdaX , int celdaY, int[][] tile, boolean marcado){
		for(int i = 0; i <FabricaTiles.LADO_TILE;i++) {
			for(int j = 0; j <FabricaTiles.LADO_TILE;j++) {
				int filtro = tile[i][j];
				if(marcado) {
					filtro = Color.yellow.getRGB();
				}
				if(filtro != 0)
					imagenMapa.setRGB(celdaX * FabricaTiles.LADO_TILE +i,celdaY * FabricaTiles.LADO_TILE +j,  filtro );
			}
		}
	}

	

	public int getAncho() {
		return ancho;
	}

	public int getAlto() {
		return alto;
	}

	/**
	 * Devuelve la imagen del mapa
	 * @return la imagen del mapa
	 */
	public BufferedImage getImagenMapa() {
		return imagenMapa;
	}
	
	public void reset() {
		campo = new Actor[ancho][alto][3];
		actualizarMapa();
	}
}
