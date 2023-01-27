package biología;

public enum TipoGen {

	SEXUAL(1),
	METABOLISMO(1),
	CRECIMIENTO(1),
	ETAPA_CRECIMIENTO(100),
	MADUREZ_SEXUAL(ETAPA_CRECIMIENTO.getMaximo()), //El maximo debe coincidir siempre con el límite de crecimiento
	PARTO(ETAPA_CRECIMIENTO.getMaximo()/5),      // Como máximo hasta la quinta parte del desarrollo máximo
	APETITO(100),
	EFICIENCIA_KCAL(1),
	AGRESIVIDAD(100),
	VELOCIDAD(10),
	VISION(100),
	ALIMENTACION(10),
	SOCIABILIDAD(100),
	ENVEJECIMIENTO(100);
	
	private int maximo;	
	
	TipoGen(int maximo){
		this.maximo = maximo;
	}
	
	public int getMaximo() {
		return maximo;
	}
	
}
