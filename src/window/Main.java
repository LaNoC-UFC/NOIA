package window;

//import javafx.stage.FileChooser;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

//import rbr.RBR;
//import sbr.Segmentation;

public class Main {

	JFileChooser fileChooser;
	FileNameExtensionFilter filter;
	
	public static void main(String[] args) {
		Main mainClass = new Main(args);
		
		
		//String file = (args.length < 1) ? "input.txt" : args[0];
		//String file = (args.length < 1) ? mainClass.selectFile(fileChooser) : args[0];
		
		
		/*
		Segmentation sbr = new Segmentation(file);
		RBR rbr = new RBR(file,"Restriction.txt");
		
		//Topology topology = new Topology(sbr, rbr);
		
		GUIclass gui = new GUIclass("Network-on-Chip Interactive Analysis", rbr, sbr);
		//GUIclass gui = new GUIclass("Network-on-Chip Interactive Analysis");
		gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		gui.setSize(800, 400);
		gui.setVisible(true);
		gui.setResizable(true);
		*/
	}
	
	/**
	 * Construtor da classe Main.
	 * @param args Strings passadas por parâmetro na execução do programa.
	 */
	public Main(String[] args){
		/*
		fileChooser = new JFileChooser();
		filter = new FileNameExtensionFilter("TXT file", "txt");
		fileChooser.setFileFilter(filter);
		
		String file = (args.length < 1) ? selectFile(fileChooser) : args[0];
		
		Segmentation sbr = new Segmentation(file);
		RBR rbr = new RBR(file,"Restriction.txt");
		*/		
		
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GUIclass gui = new GUIclass("Network-on-Chip Interactive Analysis", args);
		
		gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		
		//gui.setSize(800, 400);
		gui.setVisible(true);
		gui.setResizable(false);
		
	}
	
	public String selectFile(JFileChooser chooser){

		int returnValue;
		do{
			returnValue = chooser.showOpenDialog(null);
		}while(returnValue != chooser.APPROVE_OPTION);
		
		return chooser.getSelectedFile().getAbsolutePath();
		
	}

}
