package graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class FabricaTiles {
	
	private int[][] hojaTiles2D;
	public static final int LADO_TILE = 7;
	
	//Lista de tiles representados en forma de array de enteros en formato RGB
	public final int [][] HIERBA;
	public final int [][] TIERRA;
	public final int [][] HEMBRA;
	public final int [][] MACHO;
	public final int [][] HEMBRA_INCUBANDO;
	public final int [][] REPRODUCCION;
	public final int [][] CRIA_H;
	public final int [][] CRIA_M;
	
	public FabricaTiles() {
		try {
			BufferedImage hojaTiles =  ImageIO.read(new File(".\\src\\img\\actores2.png"));
			hojaTiles2D = new int[hojaTiles.getWidth()][hojaTiles.getHeight()];
			for (int i = 0; i < hojaTiles.getWidth(); i++) {
				for (int j = 0; j < hojaTiles.getHeight(); j++) {
					hojaTiles2D[i][j] = hojaTiles.getRGB(i, j);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		HEMBRA = generarTile(0,0);
		MACHO = generarTile(1,0);
		CRIA_H = generarTile(2,0);
		CRIA_M = generarTile(3,0);
		HEMBRA_INCUBANDO = generarTile(0,1);
		HIERBA = generarTile(1,1);
		TIERRA = generarTile(2,1);
		REPRODUCCION = generarTile(3,1);

		
	}
	
	private int [][] generarTile(int xPos , int yPos) {

		int [][] tilePixel = new int[LADO_TILE][LADO_TILE];

		for(int x = 0; x < LADO_TILE ; x++) {
			for(int y = 0; y < LADO_TILE; y++) {
				tilePixel[x][y] = hojaTiles2D[xPos * LADO_TILE + x ][yPos * LADO_TILE + y];
			}
		}
		return tilePixel;
	}
	
}
