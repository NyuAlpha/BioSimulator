package biología;

public class Gen {

	//Valores posibles en la dominancia de genes
	public static final int DOMINANTE = 0;
	public static final int RECESIVO = 1;
	public static final int CODOMINANCIA = 2;
	
	//Efecto del gen según el sexo
	//Que sea hembra o macho un animal dependerá de su primer gen (si lo posee es macho)
	public static final int FEM = 0;//Si un gen tiene esta marca solo se activará en hembras
	public static final int MAS = 1;//En machos
	public static final int MIX = 2;//En ambos sexos
	
	
	//valor del gen de 0 a 10
	private float valor;
	//Sexo del gen
	private int sex;
	//Control del gen (epigenética)
	private boolean activo;
	//dominancia del gen
	private int dominancia;
	//tipo de gen
	private TipoGen tipoGen;
	
	//Constructor de genes normales
	public Gen(TipoGen tipoGen, float valor, int sex, boolean activo, int dominancia) {
		this.tipoGen = tipoGen;
		this.valor = valor;
		this.sex = sex;
		this.activo = activo;
		this.dominancia = dominancia;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public int getDominancia() {
		return dominancia;
	}

	public void setDominancia(int dominancia) {
		this.dominancia = dominancia;
	}

	public TipoGen getTipoGen() {
		return tipoGen;
	}

	public void setTipoGen(TipoGen tipoGen) {
		this.tipoGen = tipoGen;
	}
	
	

}
