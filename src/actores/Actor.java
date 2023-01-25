package actores;

import java.util.Random;

import graficos.Coordenadas;
import graficos.Mapa;

public abstract class Actor {
	
	protected Mapa mapa;
	protected Coordenadas coordenadas;
	protected boolean isVivo;
	protected Random random;
	
	protected double masa;
	protected double indiceCrecimiento;
	protected double tamanno;
	protected double indiceEtapaCrecimiento;
	protected double metabolismoBasal;
	
	protected int cicloVital;
	protected int techoVital;
	
	//Estado del actor, meramente informativo
	protected String estado;
	boolean marcar;
	//protected int[][] tileArray;


	
	public Actor(Mapa mapa,Coordenadas coordenadas) {
		this.mapa = mapa;
		this.coordenadas = coordenadas;
		isVivo = true;
		random = new Random();
		cicloVital = 0;
		estado = "";
		marcar = false;
	}
	
	
	public void setMasa(int masa) {
		this.masa = masa;
	}
	
	public double getMasa() {
		return masa;
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
	
	public void actuar() {

	}
	
	protected void metabolismo() {
		//Si llega al final de su ciclo vital morirá
		if(++cicloVital > techoVital) {
			estado += "\n Muere";
			this.morir();
		}
		//Si esta en etapa de crecimiento crecerá, restando peso a cambio
		if(cicloVital <= indiceEtapaCrecimiento * techoVital) {
			double masaPerdida = masa * indiceCrecimiento;
			tamanno += Math.sqrt(masaPerdida);
			masa -= masaPerdida;
		}
	}
	
	public String toString() {
		return "";
	}
	
	public void setMarcar() {
		marcar = !marcar;
	}
	
	public boolean getMarcar() {
		return marcar;
	}
	
	
	public String getEstado() {
		return estado;
	}
	
	protected abstract void reproducirse();
	
}
