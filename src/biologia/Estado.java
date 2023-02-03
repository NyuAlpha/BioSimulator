package biologia;

public enum Estado {
	
	NEUTRO("neutro"),
	HAMBRE("hambre"),
	CAZA("caza"),
	PASTOREO("pastoreo"),
	HUIDA("huida"),
	DEFENSA("defensa"),
	LUCHA("lucha"),
	CELO("celo"),
	COPULA("copula"),
	PROTECCION("proteccion");
	//INFANCIA("infancia"),
	//MATERNIDAD("maternidad");
	
	private String estado;
	
	Estado(String estado){
		this.estado = estado;
	}
	
	public String getEstado() {
		return estado;
	}

}
