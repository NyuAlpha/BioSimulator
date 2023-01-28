package actores;

import java.util.Random;

import biología.Body;
import biología.TipoGen;
import graficos.Coordenadas;
import graficos.Mapa;
import graficos.TiposActores;

public abstract class Actor {
	
	//Instancias de interacción
	protected Mapa mapa;
	protected Coordenadas coordenadas;
	protected Random random;
	
	//Variables biológicas 
	protected double cicloVital;
	protected final int TECHO_VITAL = TipoGen.ETAPA_CRECIMIENTO.getMaximo();
	protected boolean isVivo; // no quitar
	
	//Estado del actor, meramente informativo
	protected int colorAsociado;  //Color del sprite en rgb
	protected int margenAsociado; // Controla el tamaño del sprite, a mayor margen menor sprite
	boolean marcar;


	public Actor(Mapa mapa,Coordenadas coordenadas) {
		this.mapa = mapa;
		this.coordenadas = coordenadas;
		isVivo = true;
		random = new Random();
		marcar = false;
	}
	
	
	public void morir() {
		isVivo = false;
		mapa.eliminarActor(this);
	}
	
	public boolean isVivo() {
		return isVivo;
	}
	
	public Coordenadas getCoordenadas() {
		return coordenadas;
	}
	
	protected void metabolismo() {
		//Si llega al final de su ciclo vital morirá
		if(cicloVital > TECHO_VITAL) {
			this.morir();
		}
	}
	
	
	public void setMarcar() {
		marcar = !marcar;
		if(marcar) {
			colorAsociado = TiposActores.MARCADO.getColorRGB();
		}
	}
	
	public boolean getMarcar() {
		return marcar;
	}
	
	public int getColorAsociado() {
		return colorAsociado;
	}
	
	public int getMargenAsociado() {
		return margenAsociado;
	}
	
	public abstract void actuar();
	
}
