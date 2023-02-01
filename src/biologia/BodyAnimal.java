package biologia;

import actores.Actor;
import actores.Animal;
import actores.Feto;

public class BodyAnimal extends Body{

	

	
	//Variables de animal
	private int radioVision;
	private int hambre;

	private Utero utero;
	private double eficiencia_kcal;
	private boolean fertil; //cuando se haya desarrollado sexualmente
	private boolean encinta;//Solo será true en hembras preñadas

	public static final int HEMBRA = 0;
	public static final int MACHO = 1;
	private int sexo;
	
	public static final int HERBIVORO = 0;
	public static final int OMNIVORO = 1;
	public static final int CARNIVORO = 2;
	private int tipoAlimentacion;
	
	public static final int SIN_HAMBRE = 0;
	public static final int HAMBRIENTO = 1;
	public static final int MUY_HAMBRIENTO = 2;

	public static final int SIN_CELO = 0;
	public static final int CELO = 1;
	private int libido;
	
	public static final int SALUDABLE = 100;
	public static final int HERIDO = SALUDABLE/2;
	private int salud;
	
	public static final int MIEDO = 0;
	public static final int PASIVO = 1;
	public static final int AGRESIVO = 2;
	private int agresividad;
	
	private int velocidad;
	private double fuerza;//daño base
	
	public BodyAnimal(ADN adn,Actor actor) {
		super(adn,actor);
		setTamanno(1);
		generarComunes();
	}
	
	public BodyAnimal(Feto feto,Actor actor) {
		super(feto.getAdn(), actor);
		tamanno = feto.getTamanno();
		masa = feto.getMasa();
		edad = feto.getCicloVital();
		generarComunes();
	}
	
	public void generarComunes() {
		radioVision = (int)(adn.getValorGen(TipoGen.VISION) * TipoGen.VISION.getMaximo());
		hambre = 0;
		sexo = adn.getSexoBiologico();
		utero = null;
		if(sexo == HEMBRA) {
			utero = new Utero(this);
		}
		if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) > TipoGen.ALIMENTACION.getMaximo()*0.9 ){
			tipoAlimentacion = CARNIVORO;
		}
		else if((adn.getValorGen(TipoGen.ALIMENTACION) * TipoGen.ALIMENTACION.getMaximo()) > TipoGen.ALIMENTACION.getMaximo()*0.3){
			tipoAlimentacion = OMNIVORO;
		}
		else {
			tipoAlimentacion = HERBIVORO;
		}
		eficiencia_kcal = (adn.getValorGen(TipoGen.EFICIENCIA_KCAL) * TipoGen.EFICIENCIA_KCAL.getMaximo());
		fertil = false;
		libido = BodyAnimal.SIN_CELO;
		encinta = false;
		velocidad = (int) (adn.getValorGen(TipoGen.VELOCIDAD) * TipoGen.VELOCIDAD.getMaximo() + 0.1);
		salud = SALUDABLE;
		agresividad = PASIVO;
		fuerza = (adn.getValorGen(TipoGen.FUERZA) * TipoGen.FUERZA.getMaximo());
	}
	
	//Lo que hará siempre un cuerpo independiente de las circunstancias
	public void actoInvoluntario() {
		if(encinta)
			gestacion();
		sanar();
		metabolismo();
		calcularHambre();
		calcularLibido();
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
			libido = SIN_CELO;
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
	
	public double calcularAtaque(double masaVictima) {
		double dannoInfligido = (masa /masaVictima) * fuerza * masa;
		return dannoInfligido;
	}
	
	public void infligirDanno(double dannoInfligido) {
		salud -= dannoInfligido;
	}
	
	private void calcularHambre() {
		if(this.getRelMasaAltura() < 0.6)
			hambre= BodyAnimal.MUY_HAMBRIENTO;
		else if((Math.sqrt(masa) / tamanno) < 0.9)
			hambre= BodyAnimal.HAMBRIENTO;
		else
			hambre= BodyAnimal.SIN_HAMBRE;
	}
	
	private void calcularLibido() {
		//Comprueba si esta en etapa fertil o no
		if(!fertil) {
			if(edad > (TipoGen.MADUREZ_SEXUAL.getMaximo() * adn.getValorGen(TipoGen.MADUREZ_SEXUAL)))
				fertil = true;
		}
		else if(!encinta) {
			//Si tiene poca hambre
			libido = (hambre <= BodyAnimal.HAMBRIENTO)?  BodyAnimal.CELO : BodyAnimal.SIN_CELO;
		}
	}
	
	private void sanar() {
		if(salud < SALUDABLE)
			salud += 1;
		else
			salud = SALUDABLE;
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

	public boolean isFertil() {
		return fertil;
	}
	
	public boolean isEncinta() {
		return encinta;
	}
	
	public int getVelocidad() {
		return velocidad;
	}

	public int getSalud() {
		return salud;
	}
	
	public int getAgresividad() {
		return agresividad;
	}
	
	public float getEspecie() {
		return adn.getValorGen(TipoGen.ESPECIE) * TipoGen.ESPECIE.getMaximo();
	}

	public void setAgresividad(int agresividad) {
		this.agresividad = agresividad;
	}
	

	
}