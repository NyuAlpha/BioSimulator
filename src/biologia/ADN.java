package biologia;

import java.util.Random;

import actores.Animal;

public class ADN {

	private Gen [][] adn;
	private Random random;
	private int sexoBiologico;
	
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
	public static ADN crearADNAnimal(int sexo,int especie) {
		
		//Genes diferenciales de sexo y dieta
		Gen sex,espe,diet,eficiencia_kcal,vel,crecimiento,lactancia;
		
		if(sexo == BodyAnimal.MACHO)
			sex = new Gen (TipoGen.SEXUAL,BodyAnimal.MACHO,Gen.MAS,true,Gen.DOMINANTE);
		else
			sex = new Gen(TipoGen.SEXUAL,BodyAnimal.HEMBRA,Gen.FEM,true,Gen.DOMINANTE);
		
		if(especie == Animal.VEGETARIANO) {
			
			espe = new Gen (TipoGen.ESPECIE,0,Gen.MIX,true,Gen.DOMINANTE); //0 herbivoros
			diet = new Gen (TipoGen.ALIMENTACION,0,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.3f,Gen.MIX,true,Gen.DOMINANTE);
			vel = new Gen(TipoGen.VELOCIDAD,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.0030f,Gen.MIX,true,Gen.DOMINANTE);
			lactancia = new Gen(TipoGen.LACTANCIA,0.15f,Gen.MIX,true,Gen.DOMINANTE);
		}
		else if(especie == Animal.HUMANO) {
			espe = new Gen (TipoGen.ESPECIE,1,Gen.MIX,true,Gen.DOMINANTE); //1 omnívoros
			diet = new Gen (TipoGen.ALIMENTACION,0.5f,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			vel = new Gen(TipoGen.VELOCIDAD,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.002f,Gen.MIX,true,Gen.DOMINANTE);
			lactancia = new Gen(TipoGen.LACTANCIA,0.25f,Gen.MIX,true,Gen.DOMINANTE);
		}
		else {
			espe = new Gen (TipoGen.ESPECIE,2,Gen.MIX,true,Gen.DOMINANTE); //2 carnívoros
			diet = new Gen (TipoGen.ALIMENTACION,1,Gen.MIX,true,Gen.DOMINANTE);
			eficiencia_kcal = new Gen (TipoGen.EFICIENCIA_KCAL,0.2f,Gen.MIX,true,Gen.DOMINANTE);
			vel = new Gen(TipoGen.VELOCIDAD,0.3f,Gen.MIX,true,Gen.DOMINANTE);
			crecimiento = new Gen(TipoGen.CRECIMIENTO,0.002f,Gen.MIX,true,Gen.DOMINANTE);
			lactancia = new Gen(TipoGen.LACTANCIA,0.25f,Gen.MIX,true,Gen.DOMINANTE);
		}
	
		Gen[][] nuevoADN= {
			{new Gen(TipoGen.SEXUAL,Gen.FEM,Gen.FEM,true,Gen.DOMINANTE),sex}
			,{espe,espe}
			,{diet,diet}
			,{eficiencia_kcal,eficiencia_kcal}
			,{vel,vel}
			,{crecimiento,crecimiento}
			,{lactancia,lactancia}
			//,{new Gen(TipoGen.CRECIMIENTO,0.0001f,Gen.MAS,true,Gen.DOMINANTE), new Gen(TipoGen.CRECIMIENTO,0.0001f,Gen.MAS,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.MADUREZ_SEXUAL,0.3f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.MADUREZ_SEXUAL,0.3f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.METABOLISMO,0.002f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.METABOLISMO,0.002f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.ENVEJECIMIENTO,0.0005f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ENVEJECIMIENTO,0.0005f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.ETAPA_CRECIMIENTO,0.3f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ETAPA_CRECIMIENTO,0.3f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.VISION,0.15f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.VISION,0.15f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.PARTO,0.02f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.PARTO,0.02f,Gen.MIX,true,Gen.DOMINANTE)}
			,{new Gen(TipoGen.FUERZA,0.5f,Gen.MIX,true,Gen.DOMINANTE),new Gen(TipoGen.FUERZA,0.5f,Gen.MIX,true,Gen.DOMINANTE)}
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
				 {new Gen(TipoGen.CRECIMIENTO,0.01f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.CRECIMIENTO,0.01f,Gen.MIX,true,Gen.DOMINANTE)}
				,{new Gen(TipoGen.ENVEJECIMIENTO,0.002f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ENVEJECIMIENTO,0.002f,Gen.MIX,true,Gen.DOMINANTE)}
				,{new Gen(TipoGen.ETAPA_CRECIMIENTO,0.3f,Gen.MIX,true,Gen.DOMINANTE), new Gen(TipoGen.ETAPA_CRECIMIENTO,0.3f,Gen.MIX,true,Gen.DOMINANTE)}
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
