package drawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import rbr.RBR;
import rbr.Region;
import rbr.Router;
import sbr.Segmentation;
import component.Link;
import component.Switch;

public class Drawn extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int topologyDimension;
	private ArrayList<Link> Links;
	private ArrayList<Switch> switches;
	private boolean showSegments;
	private boolean showRestrictions;
	private boolean highlightUnitaries;
	private boolean highlightBridges;
	private boolean highlightBronkenLinks;
	private boolean showRegions;
	private boolean showARs;
	private boolean showLinkWeight;
	private boolean saveImage;
	private Segmentation sr;
	private RBR regionBR;
	private Point mousePosition;
	private BufferedImage imageBuffer;
	private boolean unitary;
	private boolean anyBridge;
	private ArrayList<Link> brokenLinks;
	
	/**
	 * Construtor da classe que recebe a quantidade de switches da rede.
	 * @param n N�mero total de switches.
	 */
	public Drawn(int n){
		
		this.topologyDimension = (int) Math.sqrt(n);
		Links = null;
		showSegments = false;
		showRestrictions = false;
		highlightUnitaries = false;
		highlightBridges = false;
		highlightBronkenLinks = false;
		showRegions = false;
		showARs = false;
	}
	
	/**
	 * Construtor da classe utilizando o sbr e o rbr.
	 * @param n N�mero de switches na topologia.
	 * @param links Lista de links ativos na topologia.
	 * @param sbr
	 * @param rbr
	 */
	public Drawn(int n, ArrayList<Link> links, Segmentation sbr, RBR rbr){
		
		this.topologyDimension = (int) Math.sqrt(n);
		this.Links = links;
		showSegments = false;
		showRestrictions = false;
		highlightUnitaries = false;
		highlightBridges = false;
		highlightBronkenLinks = false;
		showRegions = false;
		showARs = false;
		showLinkWeight = false;
		saveImage = false;
		this.sr = sbr;
		this.regionBR = rbr;
		this.switches = new ArrayList<>();
		this.mousePosition = null;
		this.brokenLinks = new ArrayList<>();
		setSwitches();
		passRestrictions();
		passRegions();
		passAllLinkWeight();
		searchUnitaryLinks();
		searchBridgeLinks();
		findBronkenLinks();
	}
	
	/**
	 * M�todo usado quando � selecionada outra topologia para an�lise.
	 * @param n N�mero de switches na topologia.
	 * @param links Lista de links ativos na topologia.
	 * @param sbr
	 * @param rbr
	 */
	public void resetDrawn(int n, ArrayList<Link> links, Segmentation sbr, RBR rbr){
		
		this.topologyDimension = (int) Math.sqrt(n);
		this.Links = links;
		showSegments = false;
		showRestrictions = false;
		highlightUnitaries = false;
		highlightBridges = false;
		highlightBronkenLinks = false;
		showRegions = false;
		showARs = false;
		showLinkWeight = false;
		saveImage = false;
		this.mousePosition = null;
		this.sr = sbr;
		this.regionBR = rbr;
		this.switches.clear();
		this.brokenLinks.clear();
		setSwitches();
		passRestrictions();
		passRegions();
		passAllLinkWeight();
		searchUnitaryLinks();
		searchBridgeLinks();
		findBronkenLinks();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		this.setBackground(Color.GRAY);
		
		//drawExample(g);
		
		if(showSegments)
			drawSegments(g);
		
		if(highlightUnitaries)
			drawUnitaries(g);
		
		if(highlightBridges)
			drawBridges(g);
		
		if(showRegions)
			drawRegions(g);
		
		if(showARs)
			showSwitchArs();
				
		drawSwitches(g);
		drawLinks(g);
		
		if(showRestrictions)
			drawRestrictions(g);
		
		if(showLinkWeight)
			writeLinkWeight(g);
		
		if(highlightBronkenLinks)
			drawBrokenLinks(g);
		
		if(saveImage)
			saveImage(g);
		
	}
	
	public BufferedImage getImageBuffer(){
		return imageBuffer;
	}
	
	private void drawLinks(Graphics g)
	{
		for(Link a : Links)
			drawArrow(a, g);

	}
	
	private void drawBrokenLinks(Graphics g){
		g.setColor(Color.RED);
		
		for(Link l : brokenLinks){
			if(l.getEndPositionX() > l.getSourcePositionX()){
				g.fillRect(l.getSourceDrawX(), 
						l.getSourceDrawY(), 
						(Measures.ARROW_LENGTH / 3), 
						Measures.DISTANCE_BETWEEN_ARROWS);
				g.fillRect((l.getEndDrawX() - (Measures.ARROW_LENGTH / 3)), 
						l.getSourceDrawY(), 
						(Measures.ARROW_LENGTH / 3), 
						Measures.DISTANCE_BETWEEN_ARROWS);
			}else{
				g.fillRect(l.getSourceDrawX(), 
						l.getEndDrawY(), 
						Measures.DISTANCE_BETWEEN_ARROWS, 
						(Measures.ARROW_LENGTH / 3));
				g.fillRect(l.getSourceDrawX(), 
						(l.getSourceDrawY() - (Measures.ARROW_LENGTH / 3)), 
						Measures.DISTANCE_BETWEEN_ARROWS, 
						(Measures.ARROW_LENGTH / 3));
			}
		}
	}
		
	private void drawExample(Graphics g){
		

		//switch 11
		g.setColor(Color.BLACK);
		g.drawRect(94, 20, 40, 40);	
		
		//id 11
		g.drawString("11", 108, 45);
		
		//canal 11 -> 01
		g.setColor(Color.RED);
		int[] x1 = {67, 62, 67};
		int[] y1 = {42, 47, 52};
		g.drawPolyline(x1, y1, 3);
		g.drawLine(62, 47, 92, 47);
		
		//canal 01 -> 11
		g.setColor(Color.RED);
		int[] x2 = {87, 92, 87};
		int[] y2 = {28, 33, 38};
		g.drawPolyline(x2, y2, 3);
		g.drawLine(92, 33, 62, 33);
		
		//switch 01
		g.setColor(Color.BLACK);
		g.drawRect(20, 20, 40, 40);
		
		//id 01
		g.drawString("01", 34, 45);
		
		//canal 00 -> 01
		g.setColor(Color.RED);
		int[] x3 = {20, 25, 30};
		int[] y3 = {67, 62, 67};
		g.drawPolyline(x3, y3, 3);
		g.drawLine(25, 62, 25, 92);
		
		//canal 01 -> 00
		g.setColor(Color.RED);
		int[] x4 = {50, 55, 60};
		int[] y4 = {87, 92, 87};
		g.drawPolyline(x4, y4, 3);
		g.drawLine(55, 92, 55, 62);
		
		//switch 00
		g.setColor(Color.BLACK);
		g.drawRect(20, 94, 40, 40);
		
		//id 00
		g.drawString("00", 34, 119);
		
		//canal 00 -> 10
		g.setColor(Color.RED);
		int[] x5 = {87, 92, 87};
		int[] y5 = {94, 99, 104};
		g.drawPolyline(x5, y5, 3);
		g.drawLine(62, 99, 92, 99);
		
		//canal 10 -> 00
		g.setColor(Color.RED);
		int[] x6 = {67, 62, 67};
		int[] y6 = {124, 129, 134};
		g.drawPolyline(x6, y6, 3);
		g.drawLine(92, 129, 62, 129);
		
		//switch 10
		g.setColor(Color.BLACK);
		g.drawRect(94, 94, 40, 40);
		
		//id 10
		g.drawString("10", 108, 118);
		
	}
	
	/**
	 * Desenha todos os switches baseado na dimens�o da rede mesh. Identificando, tamb�m, cada switch.
	 */
	public void drawSwitches(Graphics g){
		//System.out.println("Iniciou o desenho dos switches...");
		for(int y = 0; y < topologyDimension; y++){
			for(int x = 0; x < topologyDimension; x++){
				drawSwitch(x, y, g);
				//System.out.println("Switch " + x + y + " desenhado.");
			}
		}
		//System.out.println("finalizou o desenho dos switches.");
	}	
	
	/**
	 * Desenha o switch baseado nos par�metros
	 */
	private void drawSwitch(int x, int y, Graphics g){
		
		g.setColor(Color.WHITE);
		g.fillRect(definePoint(x), definePoint(y), Measures.SWITCH_FACE, Measures.SWITCH_FACE);
		drawSwitchID(x, y, g);
		
	}
	
	/**
	 * Calcula o ponto para in�cio de desenho do switch.
	 * @param p N�mero referente ao ID do switch (X ou Y).
	 * @return Inteiro usado como ponto inicial de desenho do switch.
	 */
	private int definePoint(int p){
		
		return (Measures.SWITCH_DISTANCE_FROM_PANEL + (p * (Measures.SWITCH_FACE + Measures.DISTANCE_BETWEEN_SWITCHES)));
	}
	
	/**
	 * Desenha o ID do switch (string) no interior do quadrado representante desse switch.
	 * @param x Posi��o do switch em X.
	 * @param y Posi��o do switch em Y.
	 */
	private void drawSwitchID(int x, int y, Graphics g){
		
		g.setColor(Color.BLACK);
		g.drawString(""+x +(topologyDimension - 1 - y), (definePoint(x) + Measures.ID_DISTANCE_FROM_LEFT), (definePoint(y) + Measures.ID_DISTANCE_FROM_TOP));
	}
	
	/**
	 * Recebe e trata as informa��es do canal para desenhar
	 * @param source Switch de origem
	 * @param end Switch de Destino
	 */
	public void setChannel(String source, String end){
		
	}
	
	/**
	 * Desenha a seta relacionada ao canal baseando-se nos par�metros
	 */
	private void drawArrow(Link channel, Graphics g){
		g.setColor(Color.RED);
		g.drawLine(channel.getSourceDrawX(), channel.getSourceDrawY(), 
				channel.getEndDrawX(), channel.getEndDrawY());
		drawArrowhead(channel, g);
		
		//System.out.println(" - " + channel.getSourceDrawX() + "," + channel.getSourceDrawY() + " " 
		//		+ channel.getEndDrawX() + "," + channel.getEndDrawY());
		//drawArrowhead();
	}
	
	/**
	 * Desenha a ponta triangular da seta
	 */
	private void drawArrowhead(Link channel, Graphics g){
		/*
		int a, b, c;
		a = 2;
		b = 3;
		c = 4;
		
		int[] n = {a, b, c}; 
		
		//canal 00 -> 10
		g.setColor(Color.RED);
		int[] x5 = {87, 92, 87};
		int[] y5 = {94, 99, 104};
		g.drawPolyline(x5, y5, 3);
		g.drawLine(62, 99, 92, 99);
		
		//canal 00 -> 01
		g.setColor(Color.RED);
		int[] x3 = {20, 25, 30};
		int[] y3 = {67, 62, 67};
		g.drawPolyline(x3, y3, 3);
		g.drawLine(25, 62, 25, 92);
		
		//canal 01 -> 00
		g.setColor(Color.RED);
		int[] x4 = {50, 55, 60};
		int[] y4 = {87, 92, 87};
		g.drawPolyline(x4, y4, 3);
		g.drawLine(55, 92, 55, 62);
		*/
		g.setColor(Color.RED);
		if(channel.isVertical()){
			//dire��o vertical
			if(channel.getEndPositionY() > channel.getSourcePositionY()){
				//sentido norte
				int[] x = {(channel.getEndDrawX() - Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawX(), 
						(channel.getEndDrawX() + Measures.ARROWHEAD_MEASURES)};
				int[] y = {(channel.getEndDrawY() + Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawY(), 
						(channel.getEndDrawY() + Measures.ARROWHEAD_MEASURES)};
				g.drawPolyline(x, y, 3);
			}else{
				//sentido sul
				int x[] = {channel.getEndDrawX() - Measures.ARROWHEAD_MEASURES, 
						channel.getEndDrawX(), 
						channel.getEndDrawX() + Measures.ARROWHEAD_MEASURES};
				int y[] = {channel.getEndDrawY() - Measures.ARROWHEAD_MEASURES, 
						channel.getEndDrawY(), 
						channel.getEndDrawY() - Measures.ARROWHEAD_MEASURES};
				g.drawPolyline(x, y, 3);
			}
		}else{
			//dire��o horizontal
			if(channel.getEndPositionX() > channel.getSourcePositionX()){
				//sentido leste
				int[] x = {(channel.getEndDrawX() - Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawX(), 
						(channel.getEndDrawX() - Measures.ARROWHEAD_MEASURES)};
				int[] y = {(channel.getEndDrawY() - Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawY(), 
						(channel.getEndDrawY() + Measures.ARROWHEAD_MEASURES)};
				g.drawPolyline(x, y, 3);
			}else{
				//sentido oeste
				int[] x = {(channel.getEndDrawX() + Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawX(), 
						(channel.getEndDrawX() + Measures.ARROWHEAD_MEASURES)};
				int[] y = {(channel.getEndDrawY() - Measures.ARROWHEAD_MEASURES), 
						channel.getEndDrawY(), 
						(channel.getEndDrawY() + Measures.ARROWHEAD_MEASURES)};
				g.drawPolyline(x, y, 3);
			}
		}
		
	}
	
	public void setSegments(boolean val) {
		showSegments = val;
	}

	public void setRestrictions(boolean val) {
		showRestrictions = val;
	}

	public void setBridges(boolean val) {
		highlightBridges = val;
	}
	
	public void setUnitaries(boolean val) {
		highlightUnitaries = val;
	}
	
	public void setShowRegions(boolean val){
		showRegions = val;
	}
	
	public void setLinkWeight(boolean val){
		showLinkWeight = val;
	}
	
	public void setBrokenLinks(boolean val){
		highlightBronkenLinks = val;
	}
	
	public void setSaveImage(){
		saveImage = true;
	}
	
	/**
	 * Varre toda a topologia para desenhar os elementos iniciais.
	 * @param g
	 */
	private void drawSegments(Graphics g){
		for(sbr.Segment seg : sr.segments()){
			for(sbr.Link sbrLink : seg.getLinks()){
				drawSegment(g, 
						findLink(sbrLink.getOrigem().getNome(), 
								sbrLink.getDestino().getNome()),
								seg.getSwitchs().contains(sbrLink.getOrigem()),
								seg.getSwitchs().contains(sbrLink.getDestino()),
								false, false);
				//seg.getSwitchs().contains(sbrLink.getDestino()); 
			}
			for(sbr.Switch sw : seg.getSwitchs()){
				drawSegment(g, sw.getNome());
			}
		}
	}
	
	/**
	 * Desenha os segmentos unit�rios com destaque.
	 * @param g
	 */
	private void drawUnitaries(Graphics g){
		boolean unitary = false;
		for(sbr.Segment seg : sr.segments()){
			for(sbr.Link sbrLink : seg.getLinks()){
				if(!(seg.getSwitchs().contains(sbrLink.getOrigem()) || 
						seg.getSwitchs().contains(sbrLink.getDestino()))){
					drawSegment(g, 
							findLink(sbrLink.getOrigem().getNome(), 
									sbrLink.getDestino().getNome()),
									seg.getSwitchs().contains(sbrLink.getOrigem()),
									seg.getSwitchs().contains(sbrLink.getDestino()),
									true, false);
					unitary = true;
				}
				
			}
			
		}
	}
	
	/**
	 * Verifica se ha links unitarios na topologia
	 */
	private void searchUnitaryLinks(){
		unitary = false;
		int i = 0;
		for(sbr.Segment seg : sr.segments()){
			for(sbr.Link sbrLink : seg.getLinks()){
				if(!(seg.getSwitchs().contains(sbrLink.getOrigem()) || 
						seg.getSwitchs().contains(sbrLink.getDestino()))){
					i++;
					
				}
			}
		}
		if(i > 0){
			unitary = true;
		}
		
	}
	
	/**
	 * Retorna se existe algum link unitario na topologia.
	 * @return False se nao existe, true caso contrario.
	 */
	public boolean anyUnitary(){
		return this.unitary;
	}
	
	/**
	 * Desenha a �rea de segmento do link baseado nos valores passados por par�metro.
	 * @param link Link que ter� sua �rea de regi�o desenhada.
	 * @param source Booleano que indica se a origem do link pertence ao segmento.
	 * @param end Booleano que indica se o destino do link pertence ao segmento.
	 * @param drawUnitary Booleano que indica se o link unit�rio deve ser destacado.
	 */
	private void drawSegment(Graphics g, Link link, boolean source, boolean end, boolean drawUnitary, boolean drawBridge){
		if(drawUnitary){
			g.setColor(Color.ORANGE);
		}else if (drawBridge){
			g.setColor(Color.CYAN);
		}else{
			g.setColor(Color.LIGHT_GRAY);
		}
		//System.out.println("Desenhando regi�o do link.");
		if(link.getEndPositionY() > link.getSourcePositionY()){
			//norte
			//g.setColor(Color.DARK_GRAY);
			g.fillRect(link.getEndDrawX() - Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW, 
					link.getEndDrawY() + joinNorthEnd(end)/*+ Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH 
					- Measures.ARROW_DISTANCE_FROM_SWITCH*/, 
					Measures.DISTANCE_BETWEEN_ARROWS + 
					(2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW), 
					joinNorthSource(source, end))
					/*Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH))*/;
			//g.fillRect(30, 30, 50, 20);
			
		}else if(link.getEndPositionY() < link.getSourcePositionY()){
			//sul
			//g.setColor(Color.BLUE);
			g.fillRect(link.getSourceDrawX() - Measures.DISTANCE_BETWEEN_ARROWS 
					- Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW, 
					link.getSourceDrawY() + joinSouthSource(source), 
					Measures.DISTANCE_BETWEEN_ARROWS + 
					(2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW), 
					joinSouthEnd(source, end));
			
		}else if(link.getEndPositionX() > link.getSourcePositionX()){
			//leste
			//g.setColor(Color.GREEN);
			g.fillRect(link.getEndDrawX() + joinEastSource(source), 
					link.getEndDrawY() - Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW, 
					joinEastEnd(source, end), 
					Measures.DISTANCE_BETWEEN_ARROWS + (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW));
			
		}else{
			//oeste
			//g.setColor(Color.ORANGE);
			g.fillRect(link.getEndDrawX() + joinWestEnd(end), 
					link.getEndDrawY() - Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW - Measures.DISTANCE_BETWEEN_ARROWS, 
					joinWestSource(source, end), 
					Measures.DISTANCE_BETWEEN_ARROWS + (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_ARROW));
			
		}
	}
	
	/**
	 * Desenha a �rea de segmento baseado nos valores passados por par�metro.
	 * @param s Switch que ter� sua �rea de regi�o desenhada.
	 */
	private void drawSegment(Graphics g, String s){
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(definePoint(Integer.parseInt(s.substring(0, 1))) - Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH, 
				definePoint((topologyDimension - 1) - Integer.parseInt(s.substring(1, 2))) - Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH, 
						Measures.SWITCH_FACE + (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH), 
						Measures.SWITCH_FACE + (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH));
	}
	
	/**
	 * Percorre a lista de links e retorna o link do tipo component
	 * @param source Nome do switch de oringem.
	 * @param end Nome do switch de destino.
	 * @return Retorna o link ou null se n�o existir.
	 */
	private Link findLink(String source, String end){
		for(component.Link ch : this.Links){
			if(ch.getSourceName().equals(source) && ch.getEndName().equals(end)){
				
				return ch;
			}
		}
		
		return null;
	}
	
	/**
	 * Retorna um valor inteiro que faz com que as �reas de segmento do link e 
	 * do switch se juntem, caso perten�am ao mesmo segmento.
	 * @param source True ou false indicando se o switch final do link pertence 
	 * ao mesmo segmento.
	 * @return Inteiro com a medida de jun��o ou separa��o das �reas de segmento.
	 */
	private int joinNorthEnd(boolean end){
		if(end){
			return (Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH 
					- Measures.ARROW_DISTANCE_FROM_SWITCH - Measures.WHITE_SPACE);
		}
		
		return (Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH 
				- Measures.ARROW_DISTANCE_FROM_SWITCH);
	}
	
	/**
	 * Retorna um valor inteiro que faz com que as �reas de segmento do link e 
	 * do switch se juntem, caso perten�am ao mesmo segmento.
	 * @param source True ou false indicando se o switch inicial do link pertence 
	 * ao mesmo segmento.
	 * @return Inteiro com a medida de jun��o ou separa��o das �reas de segmento.
	 */
	private int joinNorthSource(boolean source, boolean end){
		if(source && end){
			//se a origem e destino pertencem
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ (2 * Measures.WHITE_SPACE));
		}else if(source || end){
			//se somente a origem pertence
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
			- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
			+ Measures.WHITE_SPACE);
		}
		//se nenhum pertence
		return (Measures.DISTANCE_BETWEEN_SWITCHES 
				- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH));
	}
	
	private int joinSouthSource(boolean source){
		if(source){
			//origem pertence
			return (Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH 
					- Measures.ARROW_DISTANCE_FROM_SWITCH 
					- Measures.WHITE_SPACE);
		}
		//origem n�o pertence
		return (Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH 
					- Measures.ARROW_DISTANCE_FROM_SWITCH);
	}
	
	private int joinSouthEnd(boolean source, boolean end){
		if(source && end){
			//se origem e destino pertencem
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ (2 * Measures.WHITE_SPACE));
		}else if(end || source){
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ Measures.WHITE_SPACE);
		}
		//se nenhum pertence
		return (Measures.DISTANCE_BETWEEN_SWITCHES 
				- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH));
	}
	
	/**
	 * Retorna um valor inteiro que faz com que as �reas de segmento do link e 
	 * do switch se juntem, caso perten�am ao mesmo segmento.
	 * @param end True ou false indicando se o switch inicial do link pertence 
	 * ao mesmo segmento.
	 * @return Inteiro com a medida de jun��o ou separa��o das �reas de segmento.
	 */
	private int joinEastSource(boolean source){
		if(source){
			
			return (- Measures.ARROW_LENGTH - Measures.ARROW_DISTANCE_FROM_SWITCH 
					+ Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH
					- Measures.WHITE_SPACE);
		}
		
		return (- Measures.ARROW_LENGTH - Measures.ARROW_DISTANCE_FROM_SWITCH 
				+ Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH);
	}
	
	/**
	 * Retorna um valor inteiro que faz com que as �reas de segmento do link e 
	 * do switch se juntem, caso perten�am ao mesmo segmento.
	 * @param source True ou false indicando se o switch inicial do link pertence 
	 * ao mesmo segmento.
	 * @return Inteiro com a medida de jun��o ou separa��o das �reas de segmento.
	 */
	private int joinEastEnd(boolean source, boolean end){
		if(source && end){
			//origem e destino pertencem
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ (2 * Measures.WHITE_SPACE));
		}else if(end || source){
			//somente destino pertence
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ Measures.WHITE_SPACE);
		}
		//se nenhum pertence
		return (Measures.DISTANCE_BETWEEN_SWITCHES 
				- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH));
	}
	
	private int joinWestEnd(boolean end){
		if(end){
			return (- Measures.ARROW_DISTANCE_FROM_SWITCH 
					+ Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH
					- Measures.WHITE_SPACE);
		}
		
		return (- Measures.ARROW_DISTANCE_FROM_SWITCH 
				+ Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH);
	}
	
	private int joinWestSource(boolean source, boolean end){
		if(source && end){
			//se origem e destino pertencem
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ (2 * Measures.WHITE_SPACE));
		}else if(source || end){
			//se somente origem pertence
			return (Measures.DISTANCE_BETWEEN_SWITCHES 
					- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH)
					+ Measures.WHITE_SPACE);
		}
		//se nenhum pertence
		return (Measures.DISTANCE_BETWEEN_SWITCHES 
				- (2 * Measures.FINAL_SEGMENT_BORDER_DISTANCE_FROM_SWITCH));
	}
	
	/**
	 * Desenhas as restri��es.
	 * @param g
	 */
	private void drawRestrictions(Graphics g){
		g.setColor(Color.GREEN);
		for(Switch sw : switches){
			if(sw.getNorthRestrictions() != null){
				//se houver restri��es para north
				drawNorthRestrictions(g, sw.getNorthRestrictions(), sw);
			}
			
			if(sw.getSouthRestrictions() != null){
				//se houver restri��es para south
				drawSouthRestrictions(g, sw.getSouthRestrictions(), sw);
			}
			
			if(sw.getEastRestrictions() != null){
				//se houver restri��es para east
				drawEastRestrictions(g, sw.getEastRestrictions(), sw);
			}
			
			if(sw.getWestRestrictions() != null){
				//se houver restri��es para west
				drawWestRestrictions(g, sw.getWestRestrictions(), sw);
			}
		}
	}
	
	/**
	 * Desenha as restri��es referentes � porta north do switch baseando-se 
	 * nos vetores de restri��es de cada switch.
	 * @param nRes Vetor de char de restri��es para a porta north.
	 */
	private void drawNorthRestrictions(Graphics g, char[] nRes, Switch sw){
		for(int i = 0; i < nRes.length; i++){
			if(nRes[i] == 'S'){
				//se for N-S
				g.fillRect(sw.getXPositonDraw() + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.FACE_RESTRICTION_INITIAL_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.FACE_RESTRICTION_LENGHT);
			}else if(nRes[i] == 'E'){
				//se for N-E
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
				
			}else{
				//se for N-W
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
			}
		}
	}
	
	/**
	 * Desenha as restri��es referentes � porta south do switch baseando-se 
	 * nos vetores de restri��es de cada switch.
	 * @param sRes Vetor de char de restri��es para a porta south.
	 */
	private void drawSouthRestrictions(Graphics g, char[] sRes, Switch sw){
		for(int i = 0; i < sRes.length; i++){
			if(sRes[i] == 'N'){
				//se for S-N
				g.fillRect(sw.getXPositonDraw() + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.FACE_RESTRICTION_INITIAL_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.FACE_RESTRICTION_LENGHT);
			}else if(sRes[i] == 'E'){
				//se for S-E
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
			}else{
				//se for S-W
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
			}
		}
	}
	
	/**
	 * Desenha as restri��es referentes � porta east do switch baseando-se 
	 * nos vetores de restri��es de cada switch.
	 * @param eRes Vetor de char de restri��es para a porta east.
	 */
	private void drawEastRestrictions(Graphics g, char[] eRes, Switch sw){
		for(int i = 0; i < eRes.length; i++){
			if(eRes[i] == 'W'){
				//se for E-W
				g.fillRect(sw.getXPositonDraw() - Measures.FACE_RESTRICTION_INITIAL_DISTANCE, 
						sw.getYPositionDraw() + Measures.RESTRICTION_DISTANCE, 
						Measures.FACE_RESTRICTION_LENGHT, 
						Measures.RESTRICTIONS_WIDTH);
			}else if(eRes[i] == 'S'){
				//se for E-S
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
			}else{
				//se for E-N
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
			}
		}
	}
	
	/**
	 * Desenha as restri��es referentes � porta west do switch baseando-se 
	 * nos vetores de restri��es de cada switch.
	 * @param wRes Vetor de char de restri��es para a porta west.
	 */
	private void drawWestRestrictions(Graphics g, char[] wRes, Switch sw){
		for(int i = 0; i < wRes.length; i++){
			if(wRes[i] == 'E'){
				//se for W-E
				g.fillRect(sw.getXPositonDraw() - Measures.FACE_RESTRICTION_INITIAL_DISTANCE, 
						sw.getYPositionDraw() + Measures.RESTRICTION_DISTANCE, 
						Measures.FACE_RESTRICTION_LENGHT, 
						Measures.RESTRICTIONS_WIDTH);
				
			}else if(wRes[i] == 'S'){
				//se for W-S
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						sw.getYPositionDraw() + Measures.SWITCH_FACE + Measures.RESTRICTION_DISTANCE, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
				
			}else{
				//se for W-N
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH);
				
				g.fillRect(sw.getXPositonDraw() - Measures.RESTRICTION_DISTANCE - Measures.CORNER_RESTRICTION_LENGTH, 
						sw.getYPositionDraw() - Measures.RESTRICTION_DISTANCE - Measures.RESTRICTIONS_WIDTH, 
						Measures.CORNER_RESTRICTION_LENGTH, 
						Measures.RESTRICTIONS_WIDTH);
				
			}
		}
	}
	
	/**
	 * Copia as restri��es de todos os switches do pacote sbr para os switches 
	 * do pacote component.
	 */
	private void passRestrictions(){
		for(sbr.Segment seg : sr.segments()){
			for(sbr.Switch sw : seg.getSwitchs()){
				getSwitchesByName(sw.getNome()).setRestrictions(sw.getRestrictions());
			}
		}
	}
	
	/**
	 * Estancia todos os switches do pacote component.
	 */
	private void setSwitches(){
		for(int y = 0; y < topologyDimension; y++){
			for(int x = 0; x < topologyDimension; x++){
				this.switches.add(new Switch("" + x + (topologyDimension - 1 - y), 
						new Point(definePoint(x), definePoint(y))));
			}
		}
	}
	
	/**
	 * Retorna o switch referente ao nome especificado.
	 * @param name Nome do switch.
	 * @return Switch identificado relativo ao nome name.
	 */
	private Switch getSwitchesByName(String name){
		for(Switch sw : switches){
			if(sw.getName().equals(name)){
				return sw;
			}
		}
		
		return null;
	}
	
	/**
	 * Copia as regioes de todos os routers do pacote rbr para os switches 
	 * do pacote component. Alterando valores para desenho de forma que 
	 * a area da regiao seja obtida por um TopLeft e um BottomRight.
	 */
	private void passRegions(){
		for(Router sw : regionBR.switches()){
			//cada switch
			for(Region region : sw.regions()){
				//cada regiao do switch sw
				getSwitchesByName(sw.getNome()).setBottomRight(new Point(
						Integer.parseInt(region.topRight().substring(0, 1)), 
						Integer.parseInt(region.bottomLeft().substring(1, 2))));
				getSwitchesByName(sw.getNome()).setTopLeft(new Point(
						Integer.parseInt(region.bottomLeft().substring(0, 1)), 
						Integer.parseInt(region.topRight().substring(1, 2))));
			}
		}
	}
	
	/**
	 * Repassa qual a posicao em que o mouse clicou.
	 * @param mouse
	 */
	public void setMousePosition(Point mouse){
		this.mousePosition = mouse;
	}
	
	/**
	 * Desenha as regioes do switch clicado.
	 * @param g
	 */
	private void drawRegions(Graphics g){
		if(mousePosition != null){
			g.setColor(Color.YELLOW);
			for(Switch sw : switches){
				//lista de switches
				if(sw.isSwitchArea(mousePosition.x, mousePosition.y)){
					//se esta na area do switch
					for(int i = 0; i < sw.getTopLeftList().size(); i++){
						//percorre todos os pontos que formam as regioes
						g.drawRoundRect(definePoint(sw.getTopLeftList().get(i).x) 
								- Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH, 
								definePoint((topologyDimension - 1) - sw.getTopLeftList().get(i).y) 
								- Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH, 
								(definePoint(sw.getBottomRightList().get(i).x) 
										- definePoint(sw.getTopLeftList().get(i).x)) 
								+ (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH) + Measures.SWITCH_FACE, 
								(definePoint((topologyDimension - 1) - sw.getBottomRightList().get(i).y) 
										- definePoint((topologyDimension - 1) - sw.getTopLeftList().get(i).y)) 
								+ (2 * Measures.SEGMENT_BORDER_DISTANCE_FROM_SWITCH) + Measures.SWITCH_FACE,
								20,20);
					}
					
				}
			}
		}
	}
	
	/**
	 * Repassa os pesos para os respectivos links.
	 */
	private void passAllLinkWeight(){
		//for(sbr.Segment seg : sr.segments()){
			for(rbr.Link link : regionBR.links()){
				getLink(link.getOrigem().getNome(), 
						link.getDestino().getNome()).setLinkWeight(link.getWeight());
				//System.out.println(link.getOrigem().getNome() + "-" + link.getDestino().getNome()
				//		+ " Peso: " + link.getWeight());
			}
		//}
	}
	
	/**
	 * Procura o link com origem e destino especificados.
	 * @param source Nome do switch de origem.
	 * @param end Nome do switch de destino.
	 * @return Link, se existir. Null se nao existir.
	 */
	private Link getLink(String source, String end){
		for(Link link : Links){
			if(link.getSourceName().equals(source) && link.getEndName().equals(end)){
				return link;
			}
		}
		return null;
	}

	/**
	 * Desenha os links bridge. Comparando todos os links da topologia com todos 
	 * os links pertencem a um segmento.
	 * @param g
	 */
	private void drawBridges(Graphics g){
		boolean bridge;
		for(component.Link channel : Links){
			bridge = true;
			for(sbr.Segment seg : sr.segments()){
				for(sbr.Link link : seg.getLinks()){
					/*System.out.println(link.getOrigem().getNome() + "-" 
						+ link.getDestino().getNome() + " is Unitary? " + link.isUnitary() 
						+ " is Bridge? " + link.isBridge());*/
					if((channel.getSourceName().equals(link.getOrigem().getNome())
							&& channel.getEndName().equals(link.getDestino().getNome()))
							|| (channel.getSourceName().equals(link.getDestino().getNome())
									&& channel.getEndName().equals(link.getOrigem().getNome()))){
						//se pertencer ao segmento
						bridge = false;
						//System.out.println(channel.getSourceName() + "-" + channel.getEndName() 
						//		+ " is Bridge? " + bridge);
					}
				}
			}
			if(bridge){
				//se nao pertencer a nenhum segmento
				drawSegment(g, channel, false, false, false, true);
				//System.out.println(channel.getSourceName() + "-" + channel.getEndName() 
				//		+ " is Bridge? " + bridge);
			}
		}
		
		/*for(Link link : Links){
			if(link.isBridge()){
				drawSegment(g, link, false, false, false, link.isBridge());
			}
		}*/
	}
	
	/**
	 * Procura se existem links bridge.
	 */
	private void searchBridgeLinks(){
		boolean bridge;
		anyBridge = false;
		int i = 0;
		for(component.Link channel : Links){
			bridge = true;
			for(sbr.Segment seg : sr.segments()){
				for(sbr.Link link : seg.getLinks()){
					if((channel.getSourceName().equals(link.getOrigem().getNome())
							&& channel.getEndName().equals(link.getDestino().getNome()))
							|| (channel.getSourceName().equals(link.getDestino().getNome())
									&& channel.getEndName().equals(link.getOrigem().getNome()))){
						//se pertencer ao segmento
						bridge = false;
						
					}
				}
			}
			if(bridge){
				//se nao pertencer a nenhum segmento
				i++;
			}
		}
		if(i > 0){
			anyBridge = true;
		}
	}
	
	/**
	 * Indica os links falhos.
	 */
	private void findBronkenLinks(){
		
		for(int i = 0; i < topologyDimension; i++){
			for(int j = 0; j < topologyDimension; j++){
				if((findLink("" + i + j, "" + (i + 1) + j) == null) && 
						(i < (topologyDimension - 1))){
					brokenLinks.add(new Link("" + i + j, 
							"" + (i + 1) + j, topologyDimension));
				}
				if((findLink("" + i + j, "" + i + (j + 1)) == null) && 
						(j < (topologyDimension - 1))){
					brokenLinks.add(new Link("" + i + j, 
							"" + i + (j + 1), topologyDimension));
				}
			}
		}

	}
	
	/**
	 * Indica se existe algum link bridge.
	 * @return false se nao existe, true caso contrario.
	 */
	public boolean anyBridgeLink(){
		return this.anyBridge;
	}
	
	public ArrayList<Link> getLinks(){
		return Links;
	}
	
	/**
	 * Desenha o valor do peso ao lado de cada link.
	 * @param g
	 */
	private void writeLinkWeight(Graphics g){
		g.setColor(Color.WHITE);
		for(Link link : Links){
			if(link.isVertical()){
				if(link.getEndPositionY() > link.getSourcePositionY()){
					//sentido norte
					g.drawString(link.getLinkWeitght(), 
							link.getEndDrawX() - (link.getLinkWeitght().length() * Measures.CHARACTER_WIDTH) 
								- Measures.CHARACTER_WIDTH, 
							link.getEndDrawY() + (Measures.ARROW_LENGTH / 2) + Measures.WEIGHT_DISTANCE_FROM_ARROW);
					
				}else{
					//sentido sul
					g.drawString(link.getLinkWeitght(), 
							link.getSourceDrawX() + Measures.WEIGHT_DISTANCE_FROM_ARROW, 
							link.getSourceDrawY() + (Measures.ARROW_LENGTH / 2) + Measures.WEIGHT_DISTANCE_FROM_ARROW);
				}
			}else{
				if(link.getEndPositionX() > link.getSourcePositionX()){
					//sentido leste
					g.drawString(link.getLinkWeitght(), 
							link.getSourceDrawX() + (Measures.ARROW_LENGTH / 2) 
								- (setStringPosition(link.getLinkWeitght().length()) * Measures.CHARACTER_WIDTH), 
							link.getSourceDrawY() - Measures.WEIGHT_DISTANCE_FROM_ARROW);
				}else{
					//sentido oeste
					g.drawString(link.getLinkWeitght(), 
							link.getEndDrawX() + (Measures.ARROW_LENGTH / 2) 
								- (setStringPosition(link.getLinkWeitght().length()) * Measures.CHARACTER_WIDTH), 
							link.getEndDrawY() + Measures.CHARACTER_HEIGHT + Measures.WEIGHT_DISTANCE_FROM_ARROW);
				}
			}
		}
	}
	
	private int setStringPosition(int n){
		if(n > 1){
			return n - 1;
		}else{
			return n;
		}
	}
	
	private void saveImage(Graphics g1){
		saveImage = false;
		imageBuffer = new BufferedImage((topologyDimension * Measures.SWITCH_FACE) 
				+ ((topologyDimension - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL), 
				(topologyDimension * Measures.SWITCH_FACE) 
				+ ((topologyDimension - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL), 
				BufferedImage.TYPE_INT_RGB);
		
		
		Graphics2D g = (Graphics2D) g1; 
		g = imageBuffer.createGraphics();
		
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, (topologyDimension * Measures.SWITCH_FACE) 
				+ ((topologyDimension - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL), 
				(topologyDimension * Measures.SWITCH_FACE) 
				+ ((topologyDimension - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL));
		
		if(showSegments)
			drawSegments(g);
		
		if(highlightUnitaries)
			drawUnitaries(g);
		
		if(highlightBridges)
			drawBridges(g);
		
		if(showRegions)
			drawRegions(g);
				
		drawSwitches(g);
		drawLinks(g);
		
		if(showRestrictions)
			drawRestrictions(g);
		
		if(showLinkWeight)
			writeLinkWeight(g);
		
		if(highlightBronkenLinks)
			drawBrokenLinks(g);
		/*
		JFileChooser fileChooser = new JFileChooser();		
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNG File", "png"));
		
		int retornoSaveDialog = fileChooser.showSaveDialog(null);
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		if(retornoSaveDialog == JFileChooser.APPROVE_OPTION){
			
			try {
				File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
				String filePath = file.getAbsolutePath();
				
				if (!filePath.toLowerCase().endsWith(".png"))
				    file = new File(filePath + ".png");
				
				//System.out.println("caminho para salvar: " + file.getAbsolutePath());
				
				ImageIO.write(imageBuffer, "PNG", file);
				
				Thread.sleep(300);
				updateUI();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
			}
		}else{
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateUI();
		}
		*/
	}
	
	public void showSwitchArs(){
		if(mousePosition != null){
			for(Switch sw : switches){
				//lista de switches
				if(sw.isSwitchArea(mousePosition.x, mousePosition.y)){
//					System.out.println("SWITCH " + sw.getName() + ": " + regionBR.getPairAr(sw.getName()));
					ArrayList<String> ars = regionBR.getPairAr(sw.getName());
					String arsList = "";
					for(String s : ars){
						arsList += s + "\n";
					}
					
					JOptionPane.showMessageDialog(new JFrame("Switch " + sw.getName() + " RDs"),
							arsList, "Switch " + sw.getName() + " RDs", JOptionPane.PLAIN_MESSAGE);
					
				}
			}
		}
	}
	
}
