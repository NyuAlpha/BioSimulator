package biología;

import java.util.Random;

public class ADN {

	private Gen [][] adn;
	private Random random;
	private int sexoBiologico;
	
	public static final int HEMBRA = 0;
	public static final int MACHO = 1;
	
	public static final int HERBIVORO = 0;
	public static final int OMNIVORO = 1;
	public static final int CARNIVORO = 2;

	
	public ADN(Gen [][] adn) {
		this.adn = adn;
		random = new Random();
		//Se suman los genes para decidirlo si es hembra o macho, pero realmente la suma no cambia nada
		sexoBiologico = (int)(adn[0][0].getValor() + adn[0][1].getValor() + 0.1); //Se suma 0.1 para evitar problemas de precisión
	}
	
	public int getSexoBiologico() {
		return sexoBiologico;
	}

	public static ADN fusionADN(Gen [] gametoA, Gen [] gametoB) {
		
		int longitud =  (gametoA.length + gametoB.length) / 2;
		Gen [][] nuevoADN = new Gen [longitud][2];
		for(int i = 0 ; i < longitud ; i++) {
			nuevoADN[i][0] = gametoA[i];
			nuevoADN[i][1] = gametoB[i];
		}
		return new ADN(nuevoADN);
	}
	
	public Gen[] meiosis() {
		Gen [] adnHaploide = new Gen [adn.length];
		for(int i = 0 ; i < adn.length ;i++) {
			adnHaploide[i] = adn[i][random.nextInt(2)]; 
		}
		return adnHaploide;
	}
	
	//Crea cadenas de ADN para los primeros seres animales de la simulación
	public static ADN crearADNAnimal(int sexo, int dieta) {
		
		//Genes diferenciales de sexo y dieta
		Gen sex,diet,eficiencia_kcal,vel,crecimiento;
		
		if(sexo == MACHO)
			sex = new Gen (TipoGen.SEXUAL,ADN.MACHO,Gen.MAS,true,Gen.DOMINANTE);
		else
			sex = new Gen(TipoGen.SEXUAL,ADN.HEMBRA,Gen.FEM,true,Gen.DOMINANTE);
		
		if(dieta == HERBIVORO) {
			diet = new Gen (TipoGen.ALIMENTACION,0,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.3f,Gen.MIX,true,Gen.DOMINANTE);
			Random r = new Random();
			float v = 0.1f;
			if(r.nextInt(2) == 0) {
				v = 0.2f;
			}
			vel = new Gen(TipoGen.VELOCIDAD,v,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.01f,Gen.MIX,true,Gen.DOMINANTE);
		}
		else if(dieta == OMNIVORO) {
			diet = new Gen (TipoGen.ALIMENTACION,0.5f,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			Random r = new Random();
			float v = 0.1f;
			if(r.nextInt(2) == 0) {
				v = 0.2f;
			}
			vel = new Gen(TipoGen.VELOCIDAD,v,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.003f,Gen.MIX,true,Gen.DOMINANTE);
		}
		else {
			diet = new Gen (TipoGen.ALIMENTACION,1,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			vel = new Gen(TipoGen.VELOCIDAD,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.003f,Gen.MIX,true,Gen.DOMINANTE);
		}
			
		Gen[][] nuevoADN= {
			{new Gen(TipoGen.SEXUAL,Gen.FEM,Gen.FEM,true,Gen.DOMINANTE),sex}
			,{diet,diet}
			,{eficiencia_kcal,eficiencia_kcal}
			,{vel,vel}
			,{crecimiento,crecimiento}
			,{new Gen(TipoGen.CRECIMIENTO,0.001f,Gen.MAS,true,Gen.DOMINANTE), new Gen(TipoGen.CRECIMIENTO,0.001f,Gen.MAS,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.MADUREZ_SEXUAL,0.15f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.MADUREZ_SEXUAL,0.15f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.METABOLISMO,0.002f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.METABOLISMO,0.002f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.ENVEJECIMIENTO,0.001f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ENVEJECIMIENTO,0.001f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.ETAPA_CRECIMIENTO,0.2f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ETAPA_CRECIMIENTO,0.2f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.VISION,0.3f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.VISION,0.3f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.PARTO,0.1f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.PARTO,0.1f,Gen.MIX,true,Gen.DOMINANTE)}
			,{null,null}//ADN relleno, para sustituir
			,{null,null}
			,{null,null}
			,{null,null}
			,{null,null}
			,{null,null}
			,{null,null}
			,{null,null}
			
		};	
		return new ADN(nuevoADN);
	}
	
	//Crea cadenas de ADN para los primeros seres animales de la simulación
		public static ADN crearADNVegetal() {
			
			Gen[][] nuevoADN= {
				 {new Gen(TipoGen.CRECIMIENTO,0.1f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.CRECIMIENTO,0.1f,Gen.MIX,true,Gen.DOMINANTE)}
				,{new Gen(TipoGen.ENVEJECIMIENTO,0.02f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ENVEJECIMIENTO,0.02f,Gen.MIX,true,Gen.DOMINANTE)}
				,{new Gen(TipoGen.ETAPA_CRECIMIENTO,0.5f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ETAPA_CRECIMIENTO,0.5f,Gen.MIX,true,Gen.DOMINANTE)}
			};
			return new ADN(nuevoADN);
		}
	
	
	//Obtiene el valor resultante de la expresión genética para un determinado grupo de genes
	public float getValorGen(TipoGen tipoGen) {
		
		Gen genA,genB;
		float valorA = 0f;
		float valorB = 0f;
		
		//Recorre todos los genes y va sumando los valores de genes relacionados
		for(int i = 0 ; i < adn.length ; i++) {
			genA = adn[i][0];
			genB = adn[i][1];
			
			if(genA != null && genB != null ) {
				valorA += comprobarGen(genA,tipoGen);
				valorB += comprobarGen(genB,tipoGen);
			}
		}
		return (valorA + valorB) / 2;
	}
	
	//Obtiene el valor en bruto de un gen cuando haya coincidencias de tipo y sexo
	private float comprobarGen(Gen gen,TipoGen tipoGen) {
		float valor = 0f;
		//Si el gen coincide con el tipo pasado por parametro
		if(gen.getTipoGen() == tipoGen) {
			//Cuando el sexo del gen es mixto
			if(gen.getSex() == Gen.MIX) {
				valor += gen.getValor();
			}
			//Si el sexo del gen coincide con el sexo del animal (0 hembras, 1 machos) y no sea mixto
			else if(gen.getSex() == sexoBiologico) {
				valor += gen.getValor();
			}
		}
		return valor;
	}
}
