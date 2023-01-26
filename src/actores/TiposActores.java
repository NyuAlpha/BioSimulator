package actores;

import java.awt.Color;

public enum TiposActores {
	
	SUELO(Color.GRAY.getRGB()),
	ROCA(Color.DARK_GRAY.getRGB()),
	CESPED(Color.GREEN.getRGB()),
	CAZADOR_M(new Color(150,0,0).getRGB()),
	CAZADOR_H(new Color(255,62,112).getRGB()),
	PASTOR_M(new Color(0,55,113).getRGB()),
	PASTOR_H(new Color(63,200,255).getRGB()),
	MARCADO(Color.YELLOW.getRGB());
	
	private int colorRGB;
	
	private TiposActores(int colorRGB) {
		this.colorRGB = colorRGB;
	}
	
	public int getColorRGB() {
		return colorRGB;
	}
}
