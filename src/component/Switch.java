package component;

import java.awt.Point;
import java.util.ArrayList;

import drawing.Measures;

public class Switch {
	
	private String nome;
	private Point position, sourceArea, endArea;
	private char[] local, north, south, east, west;
	private ArrayList<Point> drawTopLeft;
	private ArrayList<Point> drawBottomRight;
	
	/**
	 * Construtor da classe
	 * @param Nome identificador do switch (xy).
	 */
	public Switch(String name, Point sourceArea){
		this.position = new Point();
		this.nome = name;
		definePositionByName();
		this.sourceArea = sourceArea;
		this.endArea = new Point(sourceArea.x + Measures.SWITCH_FACE, 
				sourceArea.y + Measures.SWITCH_FACE);
		local = null;
		north = null;
		south = null;
		east = null;
		west = null;
		drawTopLeft = new ArrayList<>();
		drawBottomRight = new ArrayList<>();
	}
	
	/**
	 * Informa a coordenada do bottom left da regiao e ajusta a posicao para 
	 * desenho.
	 * @param p
	 */
	public void setBottomRight(Point p){
		this.drawBottomRight.add(p);
	}
	
	/**
	 * Informa a coordenada do top right da regiao e ajusta a posicao para 
	 * desenho
	 * @param p
	 */
	public void setTopLeft(Point p){
		this.drawTopLeft.add(p);
	}
	
	/**
	 * Verifica se o ponto passado está contido na área desse switch.
	 * @param x Ponto em x.
	 * @param y Ponto em y.
	 * @return True se pertence, false caso contrário.
	 */
	public boolean isSwitchArea(int x, int y){
		if((x > sourceArea.x) && (x < endArea.x) && 
				(y > sourceArea.y) && (y < endArea.y)){
			return true;
		}
		return false;
	}
	
	/**
	 * Retorna a lista de coordenadas das posicoes topLeft das regioes.
	 * @return Lista de posicoes.
	 */
	public ArrayList<Point> getTopLeftList(){
		return drawTopLeft;
	}
	
	/**
	 * Retorna a lista de coordenadas das posicoes bottomRight das regioes.
	 * @return Lista de posicoes.
	 */
	public ArrayList<Point> getBottomRightList(){
		return drawBottomRight;
	}
	
	/**
	 * Retorna a posição em X inicial de desenho do switch.  
	 * @return int.
	 */
	public int getXPositonDraw(){
		
		return (int) sourceArea.getX();
	}
	
	/**
	 * Retorna a posição em Y inicial de desenho do switch.  
	 * @return int.
	 */
	public int getYPositionDraw(){
		
		return (int) sourceArea.getY();
	}
	
	/**
	 * Retorna a posição do switch
	 * @return retorna objeto Point
	 */
	public Point getSwitchPosition(){
		
		return position;
	}
	
	/**
	 * Retorna inteiro da posição X do switch
	 * @return int x
	 */
	public int getX(){
		
		return (int) position.getX();
	}
	
	/**
	 * Retorna inteiro da posição Y do switch
	 * @return int y
	 */
	public int getY(){
		
		return (int) position.getY();
	}
	
	/**
	 * Retorna o nome do switch.
	 * @return ID do switch em forma de string.
	 */
	public String getName(){
		return this.nome;
	}
	
	/**
	 * Determina a posição do switch pelo seu nome
	 */
	private void definePositionByName(){
		
		this.position.x = Integer.parseInt(this.nome.substring(0, 1));
		this.position.y = Integer.parseInt(this.nome.substring(1, 2));
		
	}
	
	/**
	 * Separa as restrições e guarda no objeto component.Switch
	 * @param restrictions String com todas as restrições.
	 */
	public void setRestrictions(String restrictions)
	{
		String[] portsRestrict = restrictions.split(" ");
		for(int i=1;i<=5;i++)
		{
			char[] portRestrict = portsRestrict[i].substring(2, portsRestrict[i].length()-1).toCharArray();
			
			switch (portsRestrict[i].charAt(0))
			{
				case 'I': 	local=portRestrict;
							break;
				case 'N': 	north=portRestrict;
							break;
				case 'S': 	south=portRestrict;
							break;
				case 'E': 	east=portRestrict;
							break;
				case 'W': 	west=portRestrict;
							break;			
			}					
		}
				
	}
	
	/**
	 * Retorna as restrições da porta local.
	 * @return Vetor de char com as inicias das portas de acesso restrito.
	 */
	public char[] getLocalRestrictions(){
		
		if(local.length == 0){
			return null;
		}
		
		return local;
		
	}
	
	/**
	 * Retorna as restrições da porta north.
	 * @return Vetor de char com as iniciais das portas de acesso restrito.
	 */
	public char[] getNorthRestrictions(){
		
		if((north != null) && (north.length > 0)){
			return north;
		}
		
		return null;
	}
	
	/**
	 * Retorna as restrições da porta south.
	 * @return Vetor de char com as iniciais das portas de acesso restrito.
	 */
	public char[] getSouthRestrictions(){
		
		if((south != null) && (south.length > 0)){
			return south;
		}
		
		return null;
	}
	
	/**
	 * Retorna as restrições da porta east.
	 * @return Vetor de char com as iniciais das portas de acesso restrito.
	 */
	public char[] getEastRestrictions(){
		
		if((east != null) && (east.length > 0)){
			return east;
		}
		
		return null;
	}
	
	/**
	 * Retorna as restrições da porta west.
	 * @return Vetor de char com as iniciais das portas de acesso restrito.
	 */
	public char[] getWestRestrictions(){
		
		if((west != null ) && (west.length > 0)){
			return west;
		}
		
		return null;
	}

}
