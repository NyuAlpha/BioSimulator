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
	
	public Feto(ADN adn, BodyAnimal bodyMadre) {
		this.adn = adn;
		this.bodyMadre = bodyMadre;
		tamanno = 2; //tamaño base para todos los fetos
		masa = tamanno * tamanno;
		cicloVital = 0;
		
	}
	
	
	public void desarrollo() {
		
		//envejece un poco
		cicloVital += adn.getValorGen(TipoGen.ENVEJECIMIENTO) * TipoGen.ENVEJECIMIENTO.getMaximo();
		
		if( cicloVital < adn.getValorGen(TipoGen.PARTO) * TipoGen.PARTO.getMaximo()) {
			double multiplicador = ((Math.sqrt(masa) / tamanno) - 0.5) * 2; //El crecimiento varía en función de la relación masa/altura
			double tamannoGanado = tamanno * ((adn.getValorGen(TipoGen.CRECIMIENTO) * TipoGen.CRECIMIENTO.getMaximo()) * multiplicador);
			tamanno += tamannoGanado;
			double masaGanada = tamannoGanado * tamannoGanado;
			masa += masaGanada;
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
	
	

}
