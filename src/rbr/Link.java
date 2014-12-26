/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbr;


/**
 *
 * @author Rafael
 */
public class Link {
    private Router origem;
    private Router destino;
    private int peso = 1; //Representa o peso do arco (no nosso caso será sempre 1)
    //Poderemos usar outros pesos para representar outras características da rede
    private String cor;
    private double weight;
    private double DFER;

    Link(Router origem, Router destino, String cor) 
    {
        this.cor = cor;
        this.origem = origem;
        this.destino = destino;
        this.weight=0.0;
    }
    
    public void setDFER(double DFER){
    	this.DFER=DFER;
    }
    
    public double getDFER(){
    	return this.DFER;
    }
    
    public String getCor () 
    {        
        return this.cor;
    }
        
    public void setPeso(int peso) 
    {
            
        this.peso = peso;
            
    }
        
    public int getPeso() 
    {
            
        return this.peso;
            
    }
    
    public Router src()
    {
    	return this.origem;
    }
    
    public Router dst()
    {
    	return this.destino;
    }
        
    public Router getDestino() 
    {
            
        return this.destino;
                    
    }
            
    public Router getOrigem() 
    {
            
        return this.origem;
                    
    }
    
    public void setWeight(double weight)
    {
    	this.weight=weight;
    }
    
    public void incremWeight()
    {
    	this.weight++;
    }
    
    public double weight()
    {
    	/*Changed setWeight at tools*/
    	return this.weight;
    }
    public double getWeight() 
    {
    	return this.weight;
    }
            
}
