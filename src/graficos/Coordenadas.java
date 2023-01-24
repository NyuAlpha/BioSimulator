package graficos;

public class Coordenadas {

	private int x;
	private int y;
	
	public Coordenadas(int x, int y ) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public String toString() {
		return x + "-" + y;
	}
	
	public double calcularDistancia(Coordenadas coordenadas) {
		int a = x - coordenadas.getX();
		int b = y - coordenadas.getY();
		return Math.sqrt((a*a) + (b*b));
	}

	public int calcularDireccionX(Coordenadas coordenadas) {
		if(x > coordenadas.getX()) {return -1;}
		else if(x < coordenadas.getX()) {return 1;}
		return 0;
		
	}
	
	public int calcularDireccionY(Coordenadas coordenadas) {
		if(y > coordenadas.getY()) {return -1;}
		else if(y < coordenadas.getY()) {return 1;}
		return 0;
	}

}
