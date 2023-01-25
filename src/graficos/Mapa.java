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
	private int largo;
	
	//Capas posibles de las coordenadas de Z
	public static final int CAPA_BASE = 0;
	public static final int CAPA_VEGETAL = 1;
	public static final int CAPA_ANIMAL = 2;
	private static final int CAPAS_TOTALES = 3;
	
	/**
	 * Crea un mapa con un ancho y un alto de tiles o celdas
	 * @param ancho - anchura del mapa en tiles o celdas
	 * @param largo - altura del mapa en tiles o celdas
	 */
	public Mapa(int ancho, int largo) {
		
		this.ancho = ancho;
		this.largo = largo;
		
		fabricaTiles = new FabricaTiles();
		int anchuraPixeles =  ancho * FabricaTiles.LADO_TILE ;
		int alturaPixeles = largo * FabricaTiles.LADO_TILE ;
		imagenMapa = new BufferedImage(anchuraPixeles, alturaPixeles, BufferedImage.TYPE_INT_RGB);
		
		//se resetea el mapa al crearlo
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
	
	public void moverActor(Actor actor, Coordenadas coordenadasViejas , Coordenadas coordenadasNuevas , int posZ) {
		campo[coordenadasViejas.getX()][coordenadasViejas.getY()][posZ] = null;
		campo[coordenadasNuevas.getX()][coordenadasNuevas.getY()][posZ] = actor;
	}
	
	public void moverActor(Actor actor,int posicionX ,int posicionY) {
		int posZ = actor.getCoordenadas().getZ();
		campo[actor.getCoordenadas().getX()][actor.getCoordenadas().getY()][posZ] = null;
		campo[posicionX][posicionY][posZ] = actor;
	}
	
	public void moverActorA(Actor actor,int direccionX ,int direccionY) {
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

		for(int x = 0 ; x < ancho ; x++) {
			for(int y = 0 ; y < largo ; y++) {
				dibujarCelda(x,y,fabricaTiles.TIERRA,false);
				Actor planta = getActor(x, y ,CAPA_VEGETAL);
				Actor animal = getActor(x, y,CAPA_ANIMAL);
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
		return largo;
	}

	/**
	 * Devuelve la imagen del mapa
	 * @return la imagen del mapa
	 */
	public BufferedImage getImagenMapa() {
		return imagenMapa;
	}
	
	public void reset() {
		campo = new Actor[ancho][largo][CAPAS_TOTALES];
		actualizarMapa();
	}
}
