package component;

import java.awt.Point;

import drawing.Measures;

public class Link {
	
	private boolean isVertical, isBridge, isUnitary;
	private Point sourcePosition, endPosition, sourceDraw, endDraw, sourceArea, endArea;
	private String sourceSwitch, endSwitch;
	private int netDimension;
	private double weight;
	
	/**
	 * Construtor da classe
	 * @param source nome do switch inicial
	 * @param end nome do switch final
	 */
	public Link(String source, String end){
		
		this.sourceSwitch = source;
		this.endSwitch = end;
		this.sourcePosition = new Point();
		this.endPosition = new Point();
		
		definePoints();
		defineOrientation();

		this.sourceDraw = new Point();
		this.endDraw = new Point();
		this.sourceArea = new Point();
		this.endArea = new Point();
		
		defineDrawPoints();
		defineAreaPoints();
	}
	
	public Link(String source, String end, int n){
		
		this.netDimension = n;
		this.sourceSwitch = source;
		this.endSwitch = end;
		this.sourcePosition = new Point();
		this.endPosition = new Point();
		this.isBridge = false;
		this.isUnitary = false;
		
		definePoints();
		defineOrientation();

		this.sourceDraw = new Point();
		this.endDraw = new Point();
		this.sourceArea = new Point();
		this.endArea = new Point();
		
		defineDrawPoints();
		defineAreaPoints();
	}
	
	/**
	 * Retorna string com o nome do switch incial.
	 * @return
	 */
	public String getSourceName(){
		
		return sourceSwitch;
	}
	
	/**
	 * Retorna string com nome do switch final.
	 * @return
	 */
	public String getEndName(){
		
		return endSwitch;
	}
	
	public void setNetDimension(int n){
		
		this.netDimension = n;
	}
	
	/**
	 * Retorna se o link é vertical.
	 * @return Se true, é vertical. Caso contrário é horizontal.
	 */
	public boolean isVertical(){
		
		return this.isVertical;
	}
	
	/**
	 * Retorna a posição inicial para desenho do link
	 * @return objeto Point com as coordenadas origem
	 */
	public Point sourcePosition(){
		
		return this.sourcePosition;
	}
	
	/**
	 * Retorna o valor X da posição inicial
	 * @return valor x da posição x,y
	 */
	public int getSourcePositionX(){
		return (int) this.sourcePosition.getX();
	}
	
	/**
	 * Retorna o valor Y da posição inicial
	 * @return valor y da posição x,y
	 */
	public int getSourcePositionY(){
		return (int) this.sourcePosition.getY();
	}
	
	/**
	 * Retorna a posição final para desenho do link
	 * @return Objeto Point com as coordenadas destino
	 */
	public Point endPosition(){
		
		return this.endPosition;
	}
	
	/**
	 * Retorna o valor X da posição final
	 * @return valor x da posição x,y
	 */
	public int getEndPositionX(){
		return (int) this.endPosition.getX();
	}
	
	/**
	 * Retorna o valor Y da posição final
	 * @return valor y da posição x,y
	 */
	public int getEndPositionY(){
		return (int) this.endPosition.getY();
	}
	
	/**
	 * Retorna a posição em X incial para desenho.
	 * @return Inteiro referente ao X.
	 */
	public int getSourceDrawX(){
		
		return (int) this.sourceDraw.getX();
	}
	
	/**
	 * Retorna a posição em Y inicial para desenho.
	 * @return Inteiro referente ao Y.
	 */
	public int getSourceDrawY(){
		
		return (int) this.sourceDraw.getY();
	}
	
	/**
	 * Retorna a posição em X final para desenho.
	 * @return Inteiro referente ao X.
	 */
	public int getEndDrawX(){
		
		return (int) this.endDraw.getX();
	}
	
	/**
	 * Retorna a posição em Y final para desenho.
	 * @return Inteiro referente ao Y.
	 */
	public int getEndDrawY(){
		
		return (int) this.endDraw.getY();
	}
	
	/**
	 * Define os pontos source e end para desenho
	 */
	private void definePoints(){
		//Measures.SWITCH_FACE;
		
		this.sourcePosition.setLocation(Integer.parseInt(this.sourceSwitch.substring(0, 1)), 
				Integer.parseInt(this.sourceSwitch.substring(1, 2)));
		
		//this.sourcePosition.y = Integer.parseInt(this.sourceSwitch.substring(1, 2));
		this.endPosition.setLocation(Integer.parseInt(this.endSwitch.substring(0, 1)), 
				Integer.parseInt(this.endSwitch.substring(1, 2)));
		//this.endPosition.y = Integer.parseInt(this.endSwitch.substring(1, 2));
		//System.out.println(sourcePosition.x + "," + sourcePosition.y + " -> " + endPosition.x + "," + endPosition.y);
	}
	
	/**
	 * Define se é vertical ou horizontal
	 */
	private void defineOrientation(){
		
		if(this.sourcePosition.getY() != this.endPosition.getY()){
			this.isVertical = true;
		}else{
			this.isVertical = false;
		}
		
		//this.isVertical = (this.sourcePosition.getY() != this.endPosition.getY());
	}
	
	/**
	 * Calcula os pontos da seta para desenho
	 */
	private void defineDrawPoints(){
		
		if(isVertical){
			//direção VERTICAL
			if(sourcePosition.getY() < endPosition.getY()){
				//sentido NORTE
				/*sourceDraw.x = (int) sourcePosition.getX() + Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				sourceDraw.y = (int) sourcePosition.getY() - Measures.ARROW_DISTANCE_FROM_SWITCH;
				
				endDraw.x = (int) sourceDraw.getX();
				endDraw.y = (int) sourceDraw.getY() - Measures.ARROW_LENGTH;*/
				
				endDraw.x = definePointX(endPosition.x) + Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				endDraw.y = definePointY(endPosition.y) + Measures.ARROW_DISTANCE_FROM_SWITCH + Measures.SWITCH_FACE;
				
				sourceDraw.x = (int) endDraw.getX();
				sourceDraw.y = (int) endDraw.getY() + Measures.ARROW_LENGTH;
				//System.out.println("desenha em: " + sourceDraw.x + "," + sourceDraw.y + " - " + endDraw.x + "," + endDraw.y);
			}else{
				//sentido SUL
				//System.out.println("endPosition (" + endPosition.x + "," + endPosition.y + ")");
				endDraw.x = definePointX(endPosition.x) + Measures.SWITCH_FACE - Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				endDraw.y = definePointY(endPosition.y) - Measures.ARROW_DISTANCE_FROM_SWITCH;
				
				sourceDraw.x = (int) endDraw.getX();
				sourceDraw.y = (int) endDraw.getY() - Measures.ARROW_LENGTH;
				//System.out.println("desenha em: " + sourceDraw.x + "," + sourceDraw.y + " - " + endDraw.x + "," + endDraw.y);
			}
		}else{
			//direção HORIZONTAL
			if(sourcePosition.getX() < endPosition.getX()){
				//sentido LESTE
				endDraw.x = definePointX(endPosition.x) - Measures.ARROW_DISTANCE_FROM_SWITCH;
				endDraw.y = definePointY(endPosition.y) + Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				
				sourceDraw.x = endDraw.x - Measures.ARROW_LENGTH;
				sourceDraw.y = endDraw.y;
				//System.out.println("desenha em: " + sourceDraw.x + "," + sourceDraw.y + " - " + endDraw.x + "," + endDraw.y);
			}else{
				//sentido OESTE
				sourceDraw.x = definePointX(sourcePosition.x) - Measures.ARROW_DISTANCE_FROM_SWITCH;
				sourceDraw.y = definePointY(sourcePosition.y) + Measures.SWITCH_FACE - Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				
				endDraw.x = sourceDraw.x - Measures.ARROW_LENGTH;
				endDraw.y = sourceDraw.y;
			}
		}
	}
	
	/**
	 * Indica a o ponto de início de desenho do switch baseado no eixo da coordenada
	 * @param p Inteiro Y da coordenada do switch
	 * @return Inteiro indicando a posição de desenho.
	 */
	private int definePointY(int p){
		
		return (Measures.SWITCH_DISTANCE_FROM_PANEL + (((netDimension - 1) - p) * (Measures.SWITCH_FACE + Measures.DISTANCE_BETWEEN_SWITCHES)));
	}
	
	/**
	 * Indica a o ponto de início de desenho do switch baseado no eixo da coordenada
	 * @param p Inteiro X da coordenada do switch
	 * @return Inteiro indicando a posição de desenho.
	 */
	private int definePointX(int p){
		
		return (Measures.SWITCH_DISTANCE_FROM_PANEL + (p * (Measures.SWITCH_FACE + Measures.DISTANCE_BETWEEN_SWITCHES)));
	}
	
	/**
	 * Define a area do link para uso do mouse.
	 */
	private void defineAreaPoints(){
		if(isVertical){
			if(getEndPositionY() > getSourcePositionY()){
				//sentido norte
				sourceArea.x = sourceDraw.x - Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				sourceArea.y = endDraw.y;
				
				//endArea.x = sourceDraw.x + Measures.DISTANCE_BETWEEN_ARROWS 
				//		+ Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				endArea.x = sourceDraw.x + Measures.ARROWHEAD_MEASURES;
				endArea.y = sourceDraw.y; 
			}else{
				//sentido sul
				//sourceArea.x = sourceDraw.x - Measures.DISTANCE_BETWEEN_ARROWS 
				//		- Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				sourceArea.x = sourceDraw.x - Measures.ARROWHEAD_MEASURES;
				sourceArea.y = sourceDraw.y;
				
				endArea.x = endDraw.x + Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				endArea.y = endDraw.y;
			}
		}else{
			if(getEndPositionX() > getSourcePositionX()){
				//sentido leste
				sourceArea.x = sourceDraw.x;
				sourceArea.y = sourceDraw.y - Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				
				endArea.x = endDraw.x;
				//endArea.y = endDraw.y + Measures.DISTANCE_BETWEEN_ARROWS 
				//		+ Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				endArea.y = endDraw.y + Measures.ARROWHEAD_MEASURES;
			}else{
				//sentido oeste
				sourceArea.x = endDraw.x;
				//sourceArea.y = endDraw.y - Measures.DISTANCE_BETWEEN_ARROWS 
				//		- Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
				sourceArea.y = endDraw.y - Measures.ARROWHEAD_MEASURES;
				
				endArea.x = sourceDraw.x;
				endArea.y = sourceDraw.y + Measures.ARROW_DISTANCE_FROM_SWITCH_BORDER;
			}
		}
	}
	
	/**
	 * Verifica se a coordenada pertence area desse link.
	 * @param x eixo x
	 * @param y eixo y
	 * @return true se pertence, false se nao pertence.
	 */
	public boolean isInLinkArea(int x, int y){
		if((x > sourceArea.x) && (y > sourceArea.y) && 
				(x < endArea.x) && (y < endArea.y)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Seta o peso desse link.
	 * @param weight Peso do link.
	 */
	public void setLinkWeight(double weight){
		this.weight = weight;
	}
	
	/**
	 * Retorna o peso do link.
	 * @return Double peso.
	 */
	public double getLinkWeight(){
		return weight;
	}
	
	/**
	 * Retorna o peso do link em forma de string.
	 * @return String peso.
	 */
	public String getLinkWeitght(){
		return String.valueOf((int) weight);
	}
	
	/**
	 * Seta se e bridge.
	 * @param value true, se sim, false caso contrario.
	 */
	public void setIsBridge(boolean value){
		this.isBridge = value;
	}
	
	/**
	 * Seta se e bridge.
	 * @param value true se sim, false caso contrario.
	 */
	public void setIsUnitary(boolean value){
		this.isUnitary = value;
	}
	
	/**
	 * Retorna se o link e bridge.
	 * @return false se nao for, true caso contrario.
	 */
	public boolean isBridge(){
		return this.isBridge;
	}
	
	/**
	 * Retorna se o link e bridge.
	 * @return false se nao for, true caso contrario.
	 */
	public boolean isUnitary(){
		return this.isUnitary;
	}


}
