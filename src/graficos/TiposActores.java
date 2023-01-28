package graficos;

import java.awt.Color;

public enum TiposActores {
	
	SUELO(Color.LIGHT_GRAY.getRGB()),
	ROCA(Color.DARK_GRAY.getRGB()),
	CESPED(new Color(190,255,195).getRGB()),
	CAZADOR_M(new Color(128,0,0).getRGB()),
	CAZADOR_H(new Color(255,0,0).getRGB()),
	PASTOR_M(new Color(0,0,128).getRGB()),
	PASTOR_H(new Color(0,0,255).getRGB()),
	MIX_M(new Color(128,0,128).getRGB()),
	MIX_H(new Color(255,0,255).getRGB()),
	MARCADO(Color.YELLOW.getRGB());
	
	private int colorRGB;
	
	private TiposActores(int colorRGB) {
		this.colorRGB = colorRGB;
	}
	
	public int getColorRGB() {
		return colorRGB;
	}
}
