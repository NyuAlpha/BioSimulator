package biologia;

import actores.Actor;

public class Body {

	protected Actor actor;
	protected ADN adn;
	
	//Variables biológicas generales
	protected double masa;
	protected double tamanno;
	protected double edad;
	protected boolean adulto; //cuando deja de crecer será true
	
	protected final int TECHO_VITAL = TipoGen.ETAPA_CRECIMIENTO.getMaximo();
	
	
	
	//================================================================================
	
	public Body( ADN adn,Actor actor) {
		this.adn = adn;
		this.actor = actor;
		edad = 0;
		adulto = false;
	}
	
	public void metabolismo() {
		
		if(!adulto) {
			//Mientras esta en etapa de crecimiento
			double multiplicador = (getRelMasaAltura() - 0.5) * 2; //El crecimiento varía en función de la relación masa/altura
			double tamannoGanado = tamanno * ((adn.getValorGen(TipoGen.CRECIMIENTO) * TipoGen.CRECIMIENTO.getMaximo()) * multiplicador);
			tamanno += tamannoGanado;
			//Comprueba si ya no puede crecer y entonces lo convierte en adulto
			if( ! (edad < (TipoGen.ETAPA_CRECIMIENTO.getMaximo() * adn.getValorGen(TipoGen.ETAPA_CRECIMIENTO)))) {
				adulto = true;
			}
		}
		comprobarCicloVital();
	}
	
	private void comprobarCicloVital() {
		//envejece un poco
		edad += adn.getValorGen(TipoGen.ENVEJECIMIENTO) * TipoGen.ENVEJECIMIENTO.getMaximo();
		//Si llega al final de su ciclo vital morirá
		if(edad > TECHO_VITAL) {
			actor.morir();
		}
	}
	
	public double getRelMasaAltura() {
		return (Math.sqrt(masa) / tamanno);
	}

	public double getTamanno() {
		return tamanno;
	}

	public void setTamanno(double tamanno) {
		this.tamanno = tamanno;
		masa = tamanno * tamanno;
	}

	public double getMasa() {
		return masa;
	}

	public void setMasa(double sumador) {
		masa += sumador;
	}
	
	public double getEdad() {
		return edad;
	}

	public void setEdad(double edad) {
		this.edad = edad;
	}

	public ADN getAdn() {
		return adn;
	}
	
	public boolean getAdulto() {
		return adulto;
	}
	
	
}
