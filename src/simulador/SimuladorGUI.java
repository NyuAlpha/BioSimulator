package simulador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import graficos.Mapa;

/**Esta clase es la interfaz gráfica de un simulador con un panel de visualización y dos consolas.
 * Una para la entrada y otra para la salida.
 * @author Victor
 * 
 */
public class SimuladorGUI extends JFrame{

	//Los tres componentes esenciales de la interfaz
	private JTextArea outputConsola;
	private JTextField inputConsola;
	private PanelSimulacion panelSimulacion;
	private JScrollPane scrollPaneOutput;
	private SimuladorLogica simulador;
	
	/** Crea una GUI para el simulador
	 * @param imagenMapa - la imagen del mapa que va a ser dibujada en el simulador
	 */
	public SimuladorGUI(BufferedImage imagenMapa) {
		

		this.simulador = simulador;
		JMenuBar menuBar = new JMenuBar();
		JMenu menuAyuda = new JMenu("Ayuda ");
		JMenuItem menuComandos = new JMenuItem("Lista de comandos");
		menuAyuda.add(menuComandos);
		menuBar.add(menuAyuda);
		setJMenuBar(menuBar);
		
		/*
		 *Se le da formato a cada uno de los componentes
		 */
		JLabel lblMapa = new JLabel("ÁREA DE SIMULACIÓN");
		lblMapa.setForeground(Color.CYAN);
			
		JLabel lblPanel = new JLabel("PANEL DE INFORMACIÓN");
		lblPanel.setForeground(Color.CYAN);
			
		JLabel lblInput = new JLabel("INPUT: ");
		lblInput.setForeground(Color.CYAN);
			

		JTextField textFieldMargen = new JTextField(" >> ");
		textFieldMargen.setBackground(new Color(30,30,30));
		textFieldMargen.setForeground(Color.WHITE);
		textFieldMargen.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,12));
		textFieldMargen.setCaretColor(Color.WHITE);
		textFieldMargen.setBorder(null);
		textFieldMargen.setEnabled(false);

				
		outputConsola = new JTextArea();
		outputConsola.setEnabled(false);
		outputConsola.setBackground(new Color(30,30,30));
		outputConsola.setForeground(Color.WHITE);
		outputConsola.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,15));
		outputConsola.setCaretColor(Color.WHITE);
		
		panelSimulacion = new PanelSimulacion(imagenMapa,outputConsola);
		
		inputConsola = new JTextField();
		inputConsola.setBackground(new Color(30,30,30));
		inputConsola.setForeground(Color.WHITE);
		inputConsola.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,15));
		inputConsola.setCaretColor(Color.WHITE);
		inputConsola.setBorder(null);
		
		scrollPaneOutput = new JScrollPane(outputConsola);
		scrollPaneOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneOutput.setBorder(null);
		
		scrollPaneOutput.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
	        }
	    });

		
		/*
		 * Diseño y colocación de los distintos paneles en un GroupLayout
		 */
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
									.addComponent(lblMapa)
									.addComponent(panelSimulacion,GroupLayout.DEFAULT_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
									.addComponent(lblPanel)
									.addComponent(scrollPaneOutput,GroupLayout.DEFAULT_SIZE,
											500, GroupLayout.PREFERRED_SIZE)))
						.addGroup(layout.createSequentialGroup()
								.addComponent(lblInput)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(textFieldMargen,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
								.addComponent(inputConsola)))

				.addContainerGap()
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblMapa)
						.addComponent(lblPanel))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(panelSimulacion,GroupLayout.DEFAULT_SIZE,
		                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPaneOutput))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblInput)
						.addComponent(textFieldMargen,GroupLayout.PREFERRED_SIZE,30,GroupLayout.PREFERRED_SIZE)
						.addComponent(inputConsola,GroupLayout.DEFAULT_SIZE,
		                        30, GroupLayout.PREFERRED_SIZE)
						)
				.addContainerGap()
				);
		
		/*
		 * Formato y funciones de la ventana principal
		 */
		setTitle("SIMULADOR 1.0");
		getContentPane().setBackground(Color.DARK_GRAY);
		setSize(getPreferredSize());
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Texto por defecto en la consola de salida
		outputConsola.setText(" Aquí habrá información solicitada por el input de "
				+ "\n consola referente a datos de simulación."
				+ "\n\n /clear para limpiar la pantalla...");
	}
	
	/**
	 * @return devuelve la consola de entrada de datos
	 */
	public JTextField getInputConsola() {
		return inputConsola;
	}
	
	/**
	 * @return devuelve la consola de salida de datos
	 */
	public JTextArea getOutputConsola() {
		return outputConsola;
	}
	

	public void setSimulador(SimuladorLogica simulador) {
		this.simulador = simulador;
		panelSimulacion.setSimulador(simulador);
	}
	
	public void redibujar() {

		//scrollPaneOutput.getVerticalScrollBar().setValue(scrollPaneOutput.getVerticalScrollBar().getMaximum());
		this.panelSimulacion.repaint();
	}
}

/**
 * Esta clase añade la imagen de simulación y la dibuja, solo será usada como
 *  componente por la clase SimuladorGUI
 * @author Victor
*/

class PanelSimulacion extends JPanel implements MouseListener{
	
	private BufferedImage imagenMapa;
	private JTextArea outputConsola;
	private SimuladorLogica simulador;

	/**
	 * @param imagenMapa la imagen de la simulación
	 */
	public PanelSimulacion (BufferedImage imagenMapa, JTextArea outputConsola) {
		this.imagenMapa = imagenMapa;
		this.outputConsola = outputConsola;
		this.simulador = simulador;
		this.setPreferredSize(new Dimension( imagenMapa.getWidth() + 8, imagenMapa.getHeight() + 39));
		this.addMouseListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		g.drawImage(imagenMapa,0,0,imagenMapa.getWidth(),imagenMapa.getHeight(),null);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX()/Mapa.TILE_WIDTH;
		int y = e.getY()/Mapa.TILE_WIDTH;
		outputConsola.append( "\n Coordenada seleccionada -> " + x + "-" + y);
		simulador.setCoordenadaAnimalSeleccionada(x,y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setSimulador(SimuladorLogica simulador) {
		this.simulador = simulador;
	}
	
	
}
