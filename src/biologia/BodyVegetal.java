package biologia;

import actores.Actor;

public class BodyVegetal extends Body{

	public BodyVegetal(ADN adn,Actor actor) {
		super(adn,actor);
		setTamanno(2);
	}

	public void metabolismo() {
		
		if(edad < (TipoGen.ETAPA_CRECIMIENTO.getMaximo() * adn.getValorGen(TipoGen.ETAPA_CRECIMIENTO))) {
			//Metabolismo vegetal actuando sumando peso en funcion de la masa, a más masa más producción
			float TasaCrecimiento = (adn.getValorGen(TipoGen.CRECIMIENTO) * TipoGen.CRECIMIENTO.getMaximo());
			masa += masa * TasaCrecimiento;
		}
		super.metabolismo();
	}
}
