package biología;

import actores.Actor;
import actores.Animal;
import actores.Feto;

public class BodyAnimal extends Body{

	
	//Variables de animal
	private int radioVision;
	private int hambre;
	private int sexo;
	private Utero utero;
	private double eficiencia_kcal;
	private boolean fertil; //cuando se haya desarrollado sexualmente
	private boolean encinta;//Solo será true en hembras preñadas

	public static final int SIN_HAMBRE = 0;
	public static final int HAMBRIENTO = 1;
	public static final int MUY_HAMBRIENTO = 2;

	public static final int SIN_CELO = 0;
	public static final int CELO = 1;

	private int tipoAlimentacion;
	private int libido;
	
	public BodyAnimal(ADN adn,Actor actor) {
		super(adn,actor);
		setTamanno(1);
		radioVision = (int)(adn.getValorGen(TipoGen.VISION) * TipoGen.VISION.getMaximo());
		hambre = 0;
		sexo = adn.getSexoBiologico();
		utero = null;
		if(sexo == ADN.HEMBRA) {
			utero = new Utero(this);
		}
		if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) < TipoGen.ALIMENTACION.getMaximo()/2 ) {
			tipoAlimentacion = ADN.HERBIVORO;
		}
		else {
			tipoAlimentacion = ADN.CARNIVORO;
		}
		eficiencia_kcal = (adn.getValorGen(TipoGen.EFICIENCIA_KCAL) * TipoGen.EFICIENCIA_KCAL.getMaximo());
		fertil = false;
		libido = BodyAnimal.SIN_CELO;
		encinta = false;
	}
	
	public BodyAnimal(Feto feto,Actor actor) {
		super(feto.getAdn(), actor);
		tamanno = feto.getTamanno();
		masa = feto.getMasa();
		cicloVital = feto.getCicloVital();
		radioVision = (int)(adn.getValorGen(TipoGen.VISION) * TipoGen.VISION.getMaximo());
		hambre = 0;
		sexo = adn.getSexoBiologico();
		utero = null;
		if(sexo == ADN.HEMBRA) {
			utero = new Utero(this);
		}
		if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) < TipoGen.ALIMENTACION.getMaximo()/2 ) {
			tipoAlimentacion = ADN.HERBIVORO;
		}
		else {
			tipoAlimentacion = ADN.CARNIVORO;
		}
		eficiencia_kcal = (adn.getValorGen(TipoGen.EFICIENCIA_KCAL) * TipoGen.EFICIENCIA_KCAL.getMaximo());
		fertil = false;
		libido = BodyAnimal.SIN_CELO;
		encinta = false;
	}

	public void metabolismo() {
			
		//Metabolismo animal actuando restando peso en funcion de la masa, a más masa más gasto energético
		float metabolismo = (adn.getValorGen(TipoGen.METABOLISMO) * TipoGen.METABOLISMO.getMaximo());
		masa -= masa * metabolismo;
		//Muere de hambre cuando la masa es menor a la mitad del cuadrado del tamaño
		if(masa < (tamanno * tamanno)/2) {
			actor.morir();
		}
		//Funciones generales metabólicas
		super.metabolismo();
		//Ahora ya puede calcular el hambre y posteriormente la líbido
		calcularHambre();
		calcularLibido();
	}
	
	private void calcularHambre() {
		if(this.getRelMasaAltura() < 0.8) {
			hambre= BodyAnimal.MUY_HAMBRIENTO;
		}
		else if((Math.sqrt(masa) / tamanno) < 1) {
			hambre= BodyAnimal.HAMBRIENTO;
		}
		else{
			hambre= BodyAnimal.SIN_HAMBRE;
		}
	}
	
	private void calcularLibido() {
		//Comprueba si esta en etapa fertil o no
		if(!fertil) {
			if(cicloVital > (TipoGen.MADUREZ_SEXUAL.getMaximo() * adn.getValorGen(TipoGen.MADUREZ_SEXUAL))) {
				fertil = true;
			}
		}
		else {
			//Si tiene poca hambre
			libido = (hambre <= BodyAnimal.HAMBRIENTO)?  BodyAnimal.CELO : BodyAnimal.SIN_CELO;
		}
	}
	
	public void digestion(Body bodyAlimento) {
		masa += bodyAlimento.getMasa() * eficiencia_kcal;
	}
	
	//Solo utilizable en hembras
	public void insertarEsperma(Gen [] espermatozoide) {
		if(utero == null) {
			System.err.println("Se intento insertar semen en un macho");
		}
		else {
			Gen [] ovulo = adn.meiosis();
			utero.fecundacion(ADN.fusionADN(ovulo , espermatozoide));
			encinta = true;
		}
	}
	
	public void gestacion() {
		utero.actuar();
	}
	
	public void parto(Feto feto) {
		Animal madre = (Animal)actor;
		madre.parir(feto);
		utero.parir();
		encinta = false;
	}
	
	/*===================================================
	 * 				GETTERS Y SETTERS
	 ===================================================*/
	public int getRadioVision() {
		return radioVision;
	}
	
	public int getHambre() {
		return hambre;
	}
	
	public int getLibido() {
		return libido;
	}

	public int getSexo() {
		return sexo;
	}

	public Utero getUtero() {
		return utero;
	}

	public int getTipoAlimentacion() {
		return tipoAlimentacion;
	}

	public boolean getFertil() {
		return fertil;
	}
	
	public boolean getEncinta() {
		return encinta;
	}
	
}
