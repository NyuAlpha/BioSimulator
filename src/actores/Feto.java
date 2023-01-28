package actores;

import biología.ADN;
import biología.BodyAnimal;
import biología.TipoGen;

public class Feto {

	private ADN adn;
	private BodyAnimal bodyMadre;
	private double tamanno;
	private double masa;
	private double cicloVital;
	private int tipoAlimentacion;
	
	public Feto(ADN adn, BodyAnimal bodyMadre) {
		this.adn = adn;
		this.bodyMadre = bodyMadre;
		if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) > TipoGen.ALIMENTACION.getMaximo()*0.9 ){
			tipoAlimentacion = ADN.CARNIVORO;
			tamanno = 2;
		}
		else if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) > TipoGen.ALIMENTACION.getMaximo()*0.3){
			tipoAlimentacion = ADN.OMNIVORO;
			tamanno = 2;
		}
		else {
			tipoAlimentacion = ADN.HERBIVORO;
			tamanno = 3;
		}
		masa = tamanno * tamanno;
		cicloVital = 0;
		
	}
	
	
	public void desarrollo() {
		
		//envejece un poco
		cicloVital += adn.getValorGen(TipoGen.ENVEJECIMIENTO) * TipoGen.ENVEJECIMIENTO.getMaximo();
		
		if( cicloVital < adn.getValorGen(TipoGen.PARTO) * TipoGen.PARTO.getMaximo()) {
			double multiplicador = ((Math.sqrt(masa) / tamanno) - 0.5) * 2; //El crecimiento varía en función de la relación masa/altura
			double tamannoGanado = tamanno * ((adn.getValorGen(TipoGen.CRECIMIENTO) * TipoGen.CRECIMIENTO.getMaximo()) * multiplicador);
			tamannoGanado *= 5; //Al ser un feto desarrolla 5 veces más rapido de lo normal
			tamanno += tamannoGanado;
			//Calcula la masa en funcion al cuadrado de su tamaño, y la diferencia de masa se la quita a la madre
			double masaPrevia = masa;
			masa = tamanno * tamanno;
			double masaGanada = masa - masaPrevia;
			bodyMadre.setMasa(-masaGanada);
		}
		else {
			parto();
		}

	}
	
	private void parto() {
		bodyMadre.parto(this);
	}

	public ADN getAdn() {
		return adn;
	}

	public double getTamanno() {
		return tamanno;
	}

	public double getMasa() {
		return masa;
	}

	public double getCicloVital() {
		return cicloVital;
	}


	public int getTipoAlimentacion() {
		return tipoAlimentacion;
	}
	
	
	
	

}
