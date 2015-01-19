package window;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import component.Link;
import drawing.Drawn;
import drawing.Measures;
import rbr.RBR;
import sbr.Segmentation;
import utils.ProgressEvent;
import utils.ProgressEventListener;

public class GUIclass extends JFrame implements ProgressEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JCheckBox segmentsCheckBox;
	private JCheckBox restrictionsCheckBox;
	private JCheckBox linksInfoCheckBox;
	private JCheckBox bridgeLinksCheckBox;
	private JCheckBox unitaryLinksCheckBox;
	private JCheckBox showRegionsCheckBox;
	private JLabel linkWeightMean;
	private JLabel linkWeightSTD;
	private JLabel ard;
	private JLabel rMax;
	private JLabel demagedLinksText;
	private JLabel linkWeight;
	private JButton saveImageButton;
	private JButton selectFile;
	private JPanel leftArea;
	private Drawn drawArea;
	private Segmentation sBr;
	private RBR Rbr;
	private int demagedLinks, rmax, ardSum, nARD;
	private JFileChooser fileChooser;
	private FileNameExtensionFilter filter;
	private String fileName;

	private JCheckBoxMenuItem[] showOptions;
	private ButtonGroup optionButtonGroup;
	private boolean firstInput;
	private Insets insets;
	private ProgressBar progressBar;
	private int question;

	public void reportProgress(ProgressEvent progress)
	{
		_progressBar.reportProgress(progress.getProgress(), progress.getMessage());

		if (progress.getProgress() == 100)
		{
			_progressFrame.setVisible(false);
			this.setVisible(true);
			Toolkit.getDefaultToolkit().beep();
		}
	}

	public GUIclass(String title, String[] args) {
		super(title);
//		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("nIcon1.png")));		
		
		load(title, args);
		
		if(question == 0)
		{
			readLCFiles();
		}
		
	}

	private void load(String title, String[] args) {

		RBR rbr = null;
		Segmentation sbr = null;
		
		//seleciona o modo multi-cenario
		question = JOptionPane.showOptionDialog(null, 
				"Would you like to execute in multi-scenario mode?", 
				"Multi-scenario mode", 
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
				null, null, null);
		
		if(question == 1)
		{
			fileChooser = new JFileChooser();
			filter = new FileNameExtensionFilter("TXT file", "txt");
			fileChooser.setFileFilter(filter);
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.setDialogTitle("Choose the Topology File");
			/****/
			firstInput = true;
			String file = (args.length < 1) ? selectFile(fileChooser) : args[0];
			
			//v2
			fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.setDialogTitle("Choose the Traffic Files Directory");
			/****/
			firstInput = true;
			String file2 = (args.length < 1) ? selectDirectory(fileChooser) : args[0];
			
			String[] NctOptions = {"1", "2"};
			int Nct = (JOptionPane.showOptionDialog(null, "Choose an Option:", "Nct", 
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
					null, NctOptions, null)) + 1;
			//v2
			
			createAndShowProgressBarGUI();
	
			// Carregamento
//			Segmentation sbr = new Segmentation(file);
			sbr = new Segmentation(file);
			System.out.println("PATH: " + file2);
			System.out.println("Nct: " + Nct);
//			RBR rbr = new RBR(file,"Restriction.txt", Nct, file2);
			rbr = new RBR(file,"Restriction.txt", Nct, file2);
	
			sbr.addProgressEventListener(this);
			rbr.addProgressEventListener(this);
	
			sbr.load();
			rbr.load();
	
			this.Rbr = rbr;
			this.sBr = sbr;
	
			JFrame one = new JFrame();
			one.pack();
			insets = one.getInsets();
			one = null;
		
			this.setSize(((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.right, 
				((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.top + 15);
		}else{
			this.setSize(300, 300);
		}

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		/****/

		//new scenario item
		JMenuItem newScenario = new JMenuItem("New Scenario");
		newScenario.setMnemonic('N');
		fileMenu.add(newScenario);
		newScenario.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				changeScenario();
//				System.out.println("REPAINT");
//				showOptions[3].setEnabled(drawArea.anyBridgeLink());
//				showOptions[4].setEnabled(drawArea.anyUnitary());
//				drawArea.repaint();
				//leftArea.repaint();
			}
		});

		//save image item
		JMenuItem saveImage = new JMenuItem("Save Image");
		saveImage.setMnemonic('S');
		fileMenu.add(saveImage);
		saveImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				drawArea.setSaveImage();
				saveImage();
				drawArea.repaint();
			}
		});

		//exit item
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		//cria e adiciona o menubar na aplicacao
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		bar.add(fileMenu);

		//menu de opcoes de visualizacao
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('v');

		JMenuItem scenarioInfo = new JMenuItem("Scenario Information");
		scenarioInfo.setMnemonic('I');
		viewMenu.add(scenarioInfo);
		if(question == 1)
		{
			//pega o RMAX e ARD
			rmax = 0; 
			ardSum = 0; 
			nARD = 0;
			metrics((int) Math.sqrt(this.Rbr.graphSize()));
			scenarioInfo.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					JOptionPane.showMessageDialog(new JFrame("Scenario Information"),
							"Topology File Name: " + fileName + "\n" + 
									"LW: " + String.format("%.3f", Rbr.LinkWeightMean()) + "\n" + 
									"STD: " + String.format("%.3f", Rbr.LingWeightStdDev()) + "\n" +
									"RMAX: " + rmax + "\n" + 
									"ARD: " + String.format("%.3f", Rbr.getArd()) + "\n" + 
									"Demaged Links: " + demagedLinks + 
									"\nMinimum Reconfiguration Delay: " + String.format("%.3f", Rbr.getArMin()) + 
									"\nMaximum Reconfiguration Delay: " + String.format("%.3f", Rbr.getArMax()) + 
									"\nStandard Deviation of Reconfiguration Delay: " + String.format("%.3f", Rbr.getArStd()) + 
									"\nMean Reconfiguration Delay: " + String.format("%.3f", Rbr.getAr()),
									"Scenario Information",
									JOptionPane.PLAIN_MESSAGE);
				}
			});
		}
		viewMenu.addSeparator();

		//nomes dos checkboxes
		String[] showOptionsNames = {"Segments", "Restrictions", 
				"Regions", "Bridge Links", "Unitary Links", "Links Weight", 
				"Failed Links", "Reconfiguration Delays"};
		showOptions = new JCheckBoxMenuItem[showOptionsNames.length];
		CheckBoxHandler checkboxHandler = new CheckBoxHandler();
		//cria os itens checkbox
		for(int i = 0; i < showOptionsNames.length; i++){
			showOptions[i] = new JCheckBoxMenuItem(showOptionsNames[i]);
			viewMenu.add(showOptions[i]);
			showOptions[i].addItemListener(checkboxHandler);
		}

		bar.add(viewMenu);

		//Menu de ajuda
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem instructionsItem = new JMenuItem("Instructions");
		instructionsItem.setMnemonic('t');
//		helpMenu.add(instructionsItem);

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.setMnemonic('A');
		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(new JFrame("About"),
						String.format(
								"         Network-on-Chip Interactive Analysis - NOIA v1.0.2\n" + 
								"                                       Developed by:\n" + 
								"                    A. C. Pinheiro (cadore@lesc.ufc.br)\n" + 
								"                    R. G. Mota (rafaelmota@lesc.ufc.br)\n" + 
								"                 J. M. Ferreira (joaomarcelo@lesc.ufc.br)\n" + 
								"            LANoC - Laboratório em Arquitetura de NoCs\n" + 
								"LESC - Laboratório de Engenharia de Sistemas de Computação"),
								"About NOIA",
								JOptionPane.NO_OPTION);

			}
		});
		helpMenu.add(aboutItem);

		bar.add(helpMenu);

		if(question == 1)
		{
			drawArea = new Drawn(this.Rbr.graphSize(), setLinks(), sbr, rbr);
			add(drawArea);
			
			MouseHandler mouse = new MouseHandler();
			drawArea.addMouseListener(mouse);
			
			//desativa o que nao existe
			showOptions[3].setEnabled(drawArea.anyBridgeLink());
			showOptions[4].setEnabled(drawArea.anyUnitary());
		}else{
			newScenario.setEnabled(false);
			saveImage.setEnabled(false);
			scenarioInfo.setEnabled(false);
			showOptions[0].setEnabled(false);
			showOptions[1].setEnabled(false);
			showOptions[2].setEnabled(false);
			showOptions[3].setEnabled(false);
			showOptions[4].setEnabled(false);
			showOptions[5].setEnabled(false);
			showOptions[6].setEnabled(false);
			showOptions[7].setEnabled(false);
		}
		

		
		/*

		segmentsCheckBox = new JCheckBox("Segments");
		restrictionsCheckBox = new JCheckBox("Restrictions");
		linksInfoCheckBox = new JCheckBox("Link Weight");
		bridgeLinksCheckBox = new JCheckBox("Bridge Links");
		unitaryLinksCheckBox = new JCheckBox("Unitary Links");
		showRegionsCheckBox = new JCheckBox("Show Regions");
		saveImageButton = new JButton("Save Image");
		selectFile = new JButton("Select New Scenery");
		linkWeight = new JLabel("Link Weight: - - ");

		saveImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				drawArea.setSaveImage();
				drawArea.repaint();
			}
		});
		 */
		/*
		fileChooser = new JFileChooser();
		filter = new FileNameExtensionFilter("TXT file", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setCurrentDirectory(new File("."));

		String file = (args.length < 1) ? selectFile(fileChooser) : args[0];

		Segmentation sbr = new Segmentation(file);
		RBR rbr = new RBR(file,"Restriction.txt");

		this.Rbr = rbr;
		this.sBr = sbr;

		linkWeightMean = new JLabel("LW: " + rbr.LinkWeightMean());
		linkWeightSTD = new JLabel("STD: " + rbr.LingWeightStdDev());

		//pega o RMAX e ARD
		rmax = 0; 
		ardSum = 0; 
		nARD = 0;
		metrics((int) Math.sqrt(this.Rbr.graphSize()));
		 */
		/*
		rMax = new JLabel("RMAX: " + rmax);
		ard = new JLabel("ARD: " + Rbr.getArd());
		demagedLinksText = new JLabel("Demaged Links: " + demagedLinks);
		 */
		/*
		leftArea = new JPanel();
		leftArea.setLayout(new GridLayout(13, 1));
		leftArea.add(linkWeightMean);
		leftArea.add(linkWeightSTD);
		leftArea.add(rMax);
		leftArea.add(ard);
		leftArea.add(demagedLinksText);
		//leftArea.add(linkWeight);
		leftArea.add(segmentsCheckBox);
		leftArea.add(restrictionsCheckBox);
		leftArea.add(linksInfoCheckBox);
		leftArea.add(bridgeLinksCheckBox);
		leftArea.add(unitaryLinksCheckBox);
		leftArea.add(showRegionsCheckBox);
		leftArea.add(saveImageButton);
		leftArea.add(selectFile);

		add(leftArea, BorderLayout.WEST);
		 */
		/*
		drawArea = new Drawn(this.Rbr.graphSize(), setLinks(), sbr, rbr);
		add(drawArea);
		 */
		/*
		// register listeners for JCheckBoxes
		CheckBoxHandler handler = new CheckBoxHandler();
		segmentsCheckBox.addItemListener(handler);
		restrictionsCheckBox.addItemListener(handler);
		linksInfoCheckBox.addItemListener(handler);
		bridgeLinksCheckBox.addItemListener(handler);
		unitaryLinksCheckBox.addItemListener(handler);
		showRegionsCheckBox.addItemListener(handler);
		 */
		/*
		ButtonHandler buttonhandler = new ButtonHandler();
		selectFile.addActionListener(buttonhandler);
		 */
		/*
		MouseHandler mouse = new MouseHandler();
		drawArea.addMouseListener(mouse);
		 */
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(800, 400);
		// setVisible(true);
		// setResizable(true);

	}

	public GUIclass(String string, RBR regBased, Segmentation sbr) {

		super(string);

		segmentsCheckBox = new JCheckBox("Segments");
		restrictionsCheckBox = new JCheckBox("Restrictions");
		linksInfoCheckBox = new JCheckBox("Link Weight");
		bridgeLinksCheckBox = new JCheckBox("Bridge Links");
		unitaryLinksCheckBox = new JCheckBox("Unitary Links");
		saveImageButton = new JButton("Save Image");
		selectFile = new JButton("Select New Scenery");

		saveImageButton.addActionListener(null);


		this.Rbr = regBased;
		this.sBr = sbr;

		linkWeightMean = new JLabel("LW: " + regBased.LinkWeightMean());
		linkWeightSTD = new JLabel("STD: " + regBased.LingWeightStdDev());

		//pega o RMAX e ARD
		rmax = 0; 
		ardSum = 0; 
		nARD = 0;
		metrics((int) Math.sqrt(this.Rbr.graphSize()));
		/*
		for(rbr.Router sw : regBased.switches()){
			if(sw.getRegions().size() > rmax){
				rmax = sw.getRegions().size();
			}

			ardSum += sw.getDistancia();
			nARD++;
		}*/

		rMax = new JLabel("RMAX: " + rmax);
		ard = new JLabel("ARD: " + Rbr.getArd());

		leftArea = new JPanel();
		leftArea.setLayout(new GridLayout(13, 1));
		leftArea.add(linkWeightMean);
		leftArea.add(linkWeightSTD);
		leftArea.add(rMax);
		leftArea.add(ard);
		leftArea.add(new JTextArea("Demaged Links: " + demagedLinks));
		//leftArea.add(linkWeight);
		leftArea.add(segmentsCheckBox);
		leftArea.add(restrictionsCheckBox);
		leftArea.add(linksInfoCheckBox);
		leftArea.add(bridgeLinksCheckBox);
		leftArea.add(unitaryLinksCheckBox);
		leftArea.add(saveImageButton);
		leftArea.add(selectFile);

		add(leftArea, BorderLayout.WEST);
		drawArea = new Drawn(this.Rbr.graphSize(), setLinks(), sbr, regBased);
		add(drawArea);
		/*
		// register listeners for JCheckBoxes
		CheckBoxHandler handler = new CheckBoxHandler();
		segmentsCheckBox.addItemListener(handler);
		restrictionsCheckBox.addItemListener(handler);
		linksInfoCheckBox.addItemListener(handler);
		bridgeLinksCheckBox.addItemListener(handler);
		unitaryLinksCheckBox.addItemListener(handler);
		 */
		ButtonHandler buttonhandler = new ButtonHandler();
		selectFile.addActionListener(buttonhandler);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setSize(800, 400);
		// setVisible(true);
		// setResizable(true);

	}

	/**
	 * Abre janela para seleï¿½ï¿½o de arquivo de texto.
	 * @param chooser 
	 * @return String com o caminho do aquivo selecionado.
	 */
	public String selectFile(JFileChooser chooser){

		int returnValue;

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		returnValue = chooser.showOpenDialog(null);
		if(returnValue == chooser.APPROVE_OPTION){

			fileName = chooser.getSelectedFile().getName();

			return chooser.getSelectedFile().getAbsolutePath();
		}

		if(firstInput)
			System.exit(0);

		return null;
	}
	
	public String selectDirectory(JFileChooser chooser){

		int returnValue;
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		returnValue = chooser.showOpenDialog(null);
		if(returnValue == chooser.APPROVE_OPTION){

//			fileName = chooser.getSelectedFile().getPath();

			return chooser.getSelectedFile().getAbsolutePath();
		}

		if(firstInput)
			System.exit(0);

		return null;
	}

	private void metrics(int dimension){
		int nLinks = 0;
		for(rbr.Router sw : this.Rbr.switches()){
			if(sw.getRegions().size() > rmax){
				rmax = sw.getRegions().size();
			}

			ardSum += sw.getDistancia();
			nARD++;
		}

		for(rbr.Link rbrLink : this.Rbr.links()){
			nLinks++;
		}


		demagedLinks = (dimension * 2 *(dimension - 1)) + (dimension * 2 * (dimension - 1)) - nLinks;
		demagedLinks = demagedLinks / 2;
	}

	public ArrayList<Link> setLinks() {
		ArrayList<Link> Links = new ArrayList<>();

		for (rbr.Link rbrLink : Rbr.links()) {			
			//One direction
			Links.add(new Link(rbrLink.getOrigem().getNome(), rbrLink
					.getDestino().getNome(), (int) Math.sqrt(Rbr.graphSize())));
			//Another direction
			/*Links.add(new Link(rbrLink.getDestino().getNome(), rbrLink
					.getOrigem().getNome(), (int) Math.sqrt(Rbr.graphSize())));*/
		}

		return Links;
	}

	//"Segments", "Restrictions", "Regions", "Bridge Links", "Unitary Links", "Links Weight"
	// private inner class for ItemListener event handling
	private class CheckBoxHandler implements ItemListener {
		// respond to checkbox events
		public void itemStateChanged(ItemEvent event) {
			// determine which CheckBoxes are checked
			drawArea.setBrokenLinks(showOptions[6].isSelected());
			drawArea.setSegments(showOptions[0].isSelected());
			drawArea.setRestrictions(showOptions[1].isSelected());
			drawArea.setLinkWeight(showOptions[5].isSelected());
			drawArea.setBridges(showOptions[3].isSelected());
			drawArea.setUnitaries(showOptions[4].isSelected());
			drawArea.setShowRegions(showOptions[2].isSelected());

			drawArea.repaint();
		} // end method itemStateChanged
	} // end private inner class CheckBoxHandler

	/**
	 * Trata o evento de um botï¿½o.
	 * @author GPNoC
	 *
	 */
	private class ButtonHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			changeScenario();
			drawArea.repaint();
			leftArea.repaint();
		}

	}

	/**
	 * Redefine a topologia a partir do arquivo selecionado.
	 * @return Null. Somente para controle de selecao de arquivo.
	 */
	private Object changeScenario(){
		
		firstInput = false;
		fileChooser.setDialogTitle("Choose the Topology File");
		String file = selectFile(fileChooser);
		if(file == null){
			return null;
		}
		
		//v2
		fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setDialogTitle("Choose the Traffic Files Directory");
		/****/
		firstInput = true;
		String file2 = selectDirectory(fileChooser);
		
		String[] NctOptions = {"1", "2"};
		int Nct = (JOptionPane.showOptionDialog(null, "Choose an Option:", "Nct", 
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
					null, NctOptions, null)) + 1;
		//v2

		showProgressBarGUI();

		final Segmentation sbr = new Segmentation(file);
		final RBR rbr = new RBR(file,"Restriction.txt", Nct, file2);
		this.Rbr = rbr;
		this.sBr = sbr;
		
		sbr.addProgressEventListener(this);
		rbr.addProgressEventListener(this);

		Thread t = new Thread()
		{
			public void run(){
				sbr.load();
				rbr.load();
				avoidThreadDelay();
			}
		};
		
		t.start();
		
//		this.Rbr = rbr;
//		this.sBr = sbr;

		/*
		linkWeightMean.setText("LW: " + rbr.LinkWeightMean());
		linkWeightSTD.setText("STD: " + rbr.LingWeightStdDev());
		 */

		//pega o RMAX e ARD
//		rmax = 0; 
//		ardSum = 0; 
//		nARD = 0;
//		metrics((int) Math.sqrt(this.Rbr.graphSize()));
//		/*
//		rMax.setText("RMAX: " + rmax);
//		ard.setText("ARD: " + Rbr.getArd());
//		demagedLinksText.setText("Demaged Links: " + demagedLinks);
//		 */
//		JFrame one = new JFrame();
//		one.pack();
//		insets = one.getInsets();
//		one = null;
//		this.setSize(((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
//				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
//				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.right, 
//				((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
//				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
//				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.top + 15);
//
//		//unselect all options for the new scenario
//		for(int i = 0; i < showOptions.length; i++){
//			showOptions[i].setSelected(false);
//		}
//		drawArea.resetDrawn(this.Rbr.graphSize(), setLinks(), sbr, rbr);

		return null;

	}
	
	public void avoidThreadDelay(){
		rmax = 0; 
		ardSum = 0; 
		nARD = 0;
		metrics((int) Math.sqrt(this.Rbr.graphSize()));
		/*
		rMax.setText("RMAX: " + rmax);
		ard.setText("ARD: " + Rbr.getArd());
		demagedLinksText.setText("Demaged Links: " + demagedLinks);
		 */
		JFrame one = new JFrame();
		one.pack();
		insets = one.getInsets();
		one = null;
		this.setSize(((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.right, 
				((int)Math.sqrt(this.Rbr.graphSize()) * Measures.SWITCH_FACE) 
				+ (((int)Math.sqrt(this.Rbr.graphSize()) - 1) * Measures.DISTANCE_BETWEEN_SWITCHES) 
				+ (2 * Measures.SWITCH_DISTANCE_FROM_PANEL) + insets.top + 15);

		//unselect all options for the new scenario
		for(int i = 0; i < showOptions.length; i++){
			showOptions[i].setSelected(false);
		}
		drawArea.resetDrawn(this.Rbr.graphSize(), setLinks(), sBr, Rbr);
//		System.out.println("REPAINT");
		showOptions[3].setEnabled(drawArea.anyBridgeLink());
		showOptions[4].setEnabled(drawArea.anyUnitary());
		drawArea.repaint();
	}

	/**
	 * Trata um evento do mouse.
	 * @author GPNoC
	 *
	 */
	private class MouseHandler implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			drawArea.setMousePosition(arg0.getPoint());
			/*
			if(linksInfoCheckBox.isSelected()){
				getLinkWeightByArea(arg0.getPoint());
				leftArea.repaint();
			}
			 */
			drawArea.repaint();
			if(showOptions[7].isSelected())
				drawArea.showSwitchArs();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Altera mostra o peso do link apontado pelo mouse.
	 * @param p Coordenada do mouse.
	 * @return Double em forma de string com o peso do link.
	 */
	private void getLinkWeightByArea(Point p){
		boolean isLink = false;

		for(Link link : drawArea.getLinks()){
			if(link.isInLinkArea(p.x, p.y)){
				linkWeight.setText("Link Weight: " + String.valueOf(link.getLinkWeight()));
				isLink = true;
				break;
			}
		}

		if(!isLink){
			linkWeight.setText("Link Weight: - - ");
		}
	}

	private void saveImage(){

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

				ImageIO.write(drawArea.getImageBuffer(), "PNG", file);

				Thread.sleep(300);
				drawArea.updateUI();
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
			drawArea.updateUI();
		}
	}

	/**
	 * Classe da barra de progresso.
	 * @author GPNoC
	 *
	 */
	private class ProgressBar extends JPanel implements Runnable {
		private JButton okButton;
		private JProgressBar progressBar;
		private JTextArea taskOutput;

		public ProgressBar(){
			super(new BorderLayout());

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Create the demo's UI.
			okButton = new JButton("Ok");
			okButton.setActionCommand("ok");
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);

			taskOutput = new JTextArea(5, 20);
			taskOutput.setMargin(new Insets(5,5,5,5));
			taskOutput.setEditable(false);

			JPanel panel = new JPanel();
			panel.add(progressBar);

			add(panel, BorderLayout.PAGE_START);
			add(new JScrollPane(taskOutput), BorderLayout.CENTER);
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(okButton);
			//add(buttonPanel, BorderLayout.PAGE_END);
		}

		public void reportProgress(int progress, String message)
		{
			progressBar.setValue(progress);
			taskOutput.append(message + "\n");

			final int length = taskOutput.getText().length();
			taskOutput.setCaretPosition(length);
		}

		public void reset()
		{
			progressBar.setVisible(true);
			taskOutput.setVisible(true);
			progressBar.setValue(0);
			taskOutput.setText("");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//provavelmente nao precisa usar thread
		}

	}

	private JDialog _progressFrame;
	private ProgressBar _progressBar;

	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 */
	private void createAndShowProgressBarGUI() {
		createProgressBarGUI();
		showProgressBarGUI();
	}

	private void createProgressBarGUI() {
		//Create and set up the content pane.
		_progressBar = new ProgressBar();

		_progressBar.setOpaque(true); //content panes must be opaque

		//Create and set up the window.
		_progressFrame = new JDialog(this, "Processing Input File", false);

		_progressFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		_progressFrame.setLocationRelativeTo(null);
		_progressFrame.setContentPane(_progressBar);
		_progressFrame.setAlwaysOnTop(true);
		_progressFrame.setSize(400, 200);
		_progressFrame.setResizable(false);

	}

	private void showProgressBarGUI() {		
		this.setVisible(false);

		//Display the window.
		_progressBar.reset();
		_progressFrame.setVisible(true);
	}
	
	/**
	 * Read and compute all LC files in a especified folder.
	 */
	private int readLCFiles()
	{
		//1 - listar todos os arquivos LC (feito)
		//2 - processar cada arquivo (feito)
				
		File dir = new File("./LCs/");
		File[] files = dir.listFiles();
		
		if(files != null)
		{
			try {
				int maxRegions = getMaxRegions(files);
				PrintWriter printer = new PrintWriter("srt.h", "UTF-8");
				int n = 0;
				printer.println("/*MAX REG = " + maxRegions + "*/");
				createSTRFile(printer, "BEGINNING", n, maxRegions);
				for(File input : files)
				{
					
					RBR rbr = null;
					Segmentation sbr = null;
					
					createAndShowProgressBarGUI();
					
					sbr = new Segmentation(input.getAbsolutePath());
					System.out.println("LC: " + input.getName());
					rbr = new RBR(input.getAbsolutePath(),"Restriction.txt", -90, "nonNCT");
			
					sbr.addProgressEventListener(this);
					rbr.addProgressEventListener(this);
			
					sbr.load();
					rbr.load();
					System.out.println("The LC file " + input.getName() + " has been processed.");
			
					this.Rbr = rbr;
					this.sBr = sbr;
					
					createSTRFile(printer, "MIDDLE", n, maxRegions);
					
					n++;
				}
				
				System.out.println(n + " LC files processed.");
				createSTRFile(printer, "END", n, maxRegions);
				printer.close();
				
				return n;
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Could not create the file.");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("UTF-8 encode format is not supported.");
			}
		}
		
		return (-1);
	}
	
	/**
	 * Used to count the maximum number of regions.
	 * @param files Vector of files.
	 * @return The maximum number of regions.
	 */
	private int getMaxRegions(File[] files)
	{
		int maximum = 0;
		int counter = 0;
		
		for(File input : files)
		{
			RBR rbrz = null;
			Segmentation sbrz = null;
			
			createAndShowProgressBarGUI();
			
			sbrz = new Segmentation(input.getAbsolutePath());
			System.out.println("LC: " + input.getName());
			rbrz = new RBR(input.getAbsolutePath(),"Restriction.txt", -90, "nonNCT");
	
			sbrz.addProgressEventListener(this);
			rbrz.addProgressEventListener(this);
	
			sbrz.load();
			rbrz.load();
			System.out.println("The LC file " + input.getName() + " has been processed.");
	
			this.Rbr = rbrz;
			this.sBr = sbrz;
			
			for(rbr.Router sw : Rbr.switches())
			{
				for(rbr.Region reg : sw.regions())
				{
					counter++;
				}
				if(counter > maximum)
				{
					maximum = counter;
				}
				counter = 0;
			}
		}
		
		return maximum;
	}
		
	/**
	 * Get all the information and write the srt.h file.
	 * @param printer Object to write the file.
	 * @param operation "BEGINNING" to write the header of file, 
	 * "MIDDLE" to write the informations, "END" to write the end of file.
	 * @param i Counter of number of LC files.
	 */
	private void createSTRFile(PrintWriter printer, String operation, int i, int maxRegions)
	{
		
		//4 - montar o srt.h com as info salvas (feito)
		switch(operation){
		case "BEGINNING":
			printer.println("#ifndef __SRT_H__");
			printer.println("#define __SRT_H__\n");
			printer.println("#include \"srtm_defs.h\"\n");
			printer.println("struct scenarios_routing_table srt[] =\n{");
			break;
		case "MIDDLE":
			if(i == 0){
				printer.print("\t{");
				printer.println("/* cenario " + i + " " + (int)Math.sqrt(Rbr.graphSize()) + 
						"x" + (int)Math.sqrt(Rbr.graphSize()) + "*/");
			}else{
				printer.print(",\n\t{");
				printer.println("/* cenario " + i + " " + (int)Math.sqrt(Rbr.graphSize()) + 
						"x" + (int)Math.sqrt(Rbr.graphSize()) + "*/");
			}
			
			printer.println("\t\t{/*vetor de gft*/");
//			writeSwitches(printer);
			printer.println("\t\t\t/*number of links: " + (int)(2 * (Math.sqrt(Rbr.graphSize()) * 
					(Math.sqrt(Rbr.graphSize()) - 1))) + "*/");
			writeScenarioLinks(printer);
			printer.println("\t\t},");
			
			printer.println("\t\t{/*vetor de pxrt*/");
			writeRegions(printer, maxRegions, maxRegions);
			printer.println("\n\t\t},");
			
			printer.println("\t\t{ /*vetor de metrics*/");
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
			DecimalFormat df = (DecimalFormat)nf;
			printer.println("\t\t\t" + doubleToInt(df.format(Rbr.getArd())) + ",");
			printer.println("\t\t\t" + doubleToInt(df.format(Rbr.LinkWeightMean())) + ",");
			printer.println("\t\t\t" + doubleToInt(df.format(Rbr.LingWeightStdDev())) + "");
			printer.println("\t\t}");
			
			printer.print("\t}");
			break;
		case "END":
			printer.println("\n};\n");
			printer.println("#endif");
			break;
		default:
			System.out.println("-- Escrita de teste --");
		}
	}
	
	/**
	 * Transform the double that already is in string format to an int times 10000.
	 * @param s Number.
	 * @return int value.
	 */
	private int doubleToInt(String s)
	{
		double d = Double.valueOf(s);
		d = d * 1000;
		return (int) d;
	}
	
	/**
	 * Print all the links in sequence.
	 * @param printer The object to write in file.
	 */
	private void writeScenarioLinks(PrintWriter printer)
	{
		
		int i = 0;
		
		for(int y = 0; y < Math.sqrt(Rbr.graphSize()); y++)
		{
			for(int x = 0; x < Math.sqrt(Rbr.graphSize()); x++)
			{
				i = writeLinkStatus(printer, i, false, x, y);
			}
		}
		
		for(int x = 0; x < Math.sqrt(Rbr.graphSize()); x++)
		{
			for(int y = 0; y < Math.sqrt(Rbr.graphSize()); y++)
			{
				i = writeLinkStatus(printer, i, true, x, y);
			}
		}
		
	}
	
	/**
	 * Used to write all the links in order based on the parameters.
	 * @param printer Object to write in file.
	 * @param index Index of each link (0 - (Number of links - 1)).
	 * @param orientation False, if horizontal. True if vertical.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @return The index updated.
	 */
	private int writeLinkStatus(PrintWriter printer, int index, boolean orientation, int x, int y)
	{
		int nLinks = (int)((2 * (Math.sqrt(Rbr.graphSize()) * 
				(Math.sqrt(Rbr.graphSize()) - 1))));
		if((!orientation) && (x < (Math.sqrt(Rbr.graphSize()) - 1))) //west > east
		{
			for(rbr.Link ch : Rbr.links())
			{
				if(ch.getOrigem().getNome().equals("" + x + y) && 
							ch.getDestino().getNome().equals("" + (x + 1) + y))
				{
					if(index < (nLinks - 1))
					{
						printer.println("\t\t\t{0,0,0,0}," + " /*[" + index + "] " + x + y + " -> " + (x + 1) + y + "*/");
					}else{
						printer.println("\t\t\t{0,0,0,0}" + " /*[" + index + "] " + x + y + " -> " + (x + 1) + y + "*/");
					}
					return (index + 1);
				}
			}
			if(index < (nLinks - 1))
			{
				printer.println("\t\t\t{1,0,0,0}," + " /*[" + index + "] " + x + y + " -> " + (x + 1) + y + "*/");
			}else{
				printer.println("\t\t\t{1,0,0,0}" + " /*[" + index + "] " + x + y + " -> " + (x + 1) + y + "*/");
			}
			return (index + 1);
			
		}else if((orientation) && (y < (Math.sqrt(Rbr.graphSize()) - 1))){ //south -> north
			for(rbr.Link ch : Rbr.links())
			{
				if(ch.getOrigem().getNome().equals("" + x + y) && 
							ch.getDestino().getNome().equals("" + x + (y + 1)))
				{
					if(index < (nLinks - 1))
					{
						printer.println("\t\t\t{0,0,0,0}," + " /*[" + index + "] " + x + y + " -> " + x + (y + 1) + "*/");
					}else{
						printer.println("\t\t\t{0,0,0,0}" + " /*[" + index + "] " + x + y + " -> " + x + (y + 1) + "*/");
					}
					return (index + 1);
				}
			}
			if(index < (nLinks - 1))
			{
				printer.println("\t\t\t{1,0,0,0}," + " /*[" + index + "] " + x + y + " -> " + x + (y + 1) + "*/");
			}else{
				printer.println("\t\t\t{1,0,0,0}" + " /*[" + index + "] " + x + y + " -> " + x + (y + 1) + "*/");
			}
			return (index + 1);
		}
		return index;
	}
	
	/**
	 * Use to print the switches information.
	 * @param printer Objecto to print in file.
	 */
	private void writeSwitches(PrintWriter printer)
	{
		
		for(int y = 0; y < Math.sqrt(Rbr.graphSize()); y++)
		{
			for(int x = 0; x < Math.sqrt(Rbr.graphSize()); x++)
			{
				if((x == 0) && (y == 0))
				{
					printer.println("\t\t\t{/*switch " + sBr.get("" + x + y).getNome() + "*/");
					printer.println("\t\t\t\t{");
				}else{
					printer.println(",\n\t\t\t{/*switch " + sBr.get("" + x + y).getNome() + "*/");
					printer.println("\t\t\t\t{");
				}
				
//				writeChannels(printer, sBr.get("" + x + y).getLinks(), x, y);
				for(rbr.Link ch: Rbr.links())
				{
					printer.println("\t\t\t\t\t/*" + ch.getOrigem().getNome() + " -> " + ch.getDestino().getNome() + "*/");
				}
				printer.println("\t\t\t\t}");
				printer.print("\t\t\t}");
			}
		}
	}
	
	/**
	 * Print the channels information of a switch.
	 * @param printer Object to print in file.
	 * @param links Links of a switch.
	 * @param x X coordinate of the switch.
	 * @param y Y coordinate of the switch.
	 */
	private void writeChannels(PrintWriter printer, Iterable<sbr.Link> links, int x, int y)
	{
		boolean brokenLink = true;
		String[] actives = {"", "", "", ""};
		int i = 0;
		int n = 0;
		
		for(sbr.Link link : links)
		{
			if(link.getDestino().getNome().equals("" + (x + 1) + y))
			{
				actives[0] = link.getDestino().getNome();
				n++;
			}else if(link.getDestino().getNome().equals("" + (x - 1) + y))
			{
				actives[1] = link.getDestino().getNome();
				n++;
			}else if(link.getDestino().getNome().equals("" + x + (y + 1)))
			{
				actives[2] = link.getDestino().getNome();
				n++;
			}else if(link.getDestino().getNome().equals("" + x + (y - 1)))
			{
				actives[3] = link.getDestino().getNome();
				n++;
			}
			
		}
		
		for(i = 0; i < 4; i++)
		{
			if(actives[i].equals("" + (x - 1) + y))
			{
				printer.print("\t\t\t\t\t{1,0,0,0}");
				if((n - 1) > 0)
				{
					printer.print(",");
				}
				printer.println(" /*" + x + y + 
						" -> " + actives[i] + "*/");
				n--;
				brokenLink = false;
			}else if(actives[i].equals("" + (x + 1) + y))
			{
				printer.print("\t\t\t\t\t{1,0,0,0}");
				if((n - 1) > 0)
				{
					printer.print(",");
				}
				printer.println(" /*" + x + y + 
						" -> " + actives[i] + "*/");
				n--;
				brokenLink = false;
			}else if(actives[i].equals("" + x + (y + 1)))
			{
				printer.print("\t\t\t\t\t{1,0,0,0}");
				if((n - 1) > 0)
				{
					printer.print(",");
				}
				printer.println(" /*" + x + y + 
						" -> " + actives[i] + "*/");
				n--;
				brokenLink = false;
			}else if(actives[i].equals("" + x + (y - 1)))
			{
				printer.println("\t\t\t\t\t{1,0,0,0} /*" + x + y + 
						" -> " + actives[i] + "*/");
				brokenLink = false;
			}
			
			if(brokenLink)
			{
				switch(i)
				{
				case 0:
					if((x + 1) < (Math.sqrt(Rbr.graphSize()) - 1))
					{
						printer.println("\t\t\t\t\t{0,0,0,0}, /*" + x + y +
								" -> " + (x + 1) + y +"*/");
					}
					break;
				case 1:
					if((x - 1) > 0)
					{
						printer.println("\t\t\t\t\t{0,0,0,0}, /*" + x + y +
								" -> " + (x - 1) + y +"*/");
					}
					break;
				case 2:
					if((y + 1) < (Math.sqrt(Rbr.graphSize()) - 1))
					{
						printer.println("\t\t\t\t\t{0,0,0,0}, /*" + x + y +
								" -> " + x + (y + 1) +"*/");
					}
					break;
				case 3:
					if((y - 1) > 0)
					{
						printer.println("\t\t\t\t\t{0,0,0,0} /*" + x + y +
								" -> " + x + (y - 1) +"*/");
					}
					break;
				}
			}
			brokenLink = true;
		}
		
	}
	
	/**
	 * Write the regions of each switch.
	 * @param printer Object the prints in a file.
	 */
	private void writeRegions(PrintWriter printer, int maxReg, int max)
	{
		for(int y = 0; y < Math.sqrt(Rbr.graphSize()); y++)
		{
			for(int x = 0; x < Math.sqrt(Rbr.graphSize()); x++)
			{
				if((x == 0) && (y == 0))
				{
					printer.println("\t\t\t{/*switch " + Rbr.get("" + x + y).getNome() + "*/");
					printer.println("\t\t\t\t{");
				}else{
					printer.println(",\n\t\t\t{/*switch " + sBr.get("" + x + y).getNome() + "*/");
					printer.println("\t\t\t\t{");
				}
				
				int i = 0;
				for(rbr.Region region : Rbr.get("" + x + y).regions())
				{
					if(i == 0)
					{
						printer.print("\t\t\t\t\t{" + getPortListNumber(region.getIp()) + "," + 
								hexInC(region.getDownLeft()) + "," + hexInC(region.getUpRight()) + "," +
								getPortListNumber(region.getOp()) + 
								"}/*in{" + region.getIp() + "} out{" + region.getOp() + "}*/");
					}else{
						printer.print(",\n\t\t\t\t\t{" + getPortListNumber(region.getIp()) + "," + 
								hexInC(region.getDownLeft()) + "," + hexInC(region.getUpRight()) + "," +
								getPortListNumber(region.getOp()) + 
								"}/*in{" + region.getIp() + "} out{" + region.getOp() + "}*/");
					}
					
//					printer.println("\t\t\t\t\tgetIp = " + region.getIp());
//					printer.println("\t\t\t\t\tgetOp = " + region.getOp());
					i++;
				}
				while(i < max)
				{
					printer.println(",");
					printer.print("\t\t\t\t\t{0,0x00,0x00,0}");
					i++;
				}
				
				printer.println("\n\t\t\t\t}");
				printer.print("\n\t\t\t}");
				if(i == maxReg)
				{
					copyFile(new File("./Table_package.vhd"), 
							new File("./Table_package_MAX.vhd"));
					
				}
			}
		}
	}
	
	/**
	 * Copy the specified file to another.
	 * @param target Target file to be copied.
	 * @param copyName Clone file name.
	 */
	private void copyFile(File target, File clone)
	{
		FileChannel input = null, output = null;
		
		try {
			input = new FileInputStream(target).getChannel();
			output = new FileOutputStream(clone).getChannel();
			
			output.transferFrom(input, 0, input.size());
			
			input.close();
			output.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the integer related to the port list. 
	 * @param s String port list.
	 * @return The decimal number.
	 */
	private int getPortListNumber(String s)
	{
		String binary = "";
		String[] portList = {"0", "0", "0", "0", "0"};
		
		
		for(int i = 0; i < s.length(); i++)
		{
			switch(s.charAt(i))
			{
			case 'I':
				portList[0] = "1";
				break;
			case 'S':
				portList[1] = "1";
				break;
			case 'N':
				portList[2] = "1";
				break;
			case 'W':
				portList[3] = "1";
				break;
			case 'E':
				portList[4] = "1";
				break;
			}
		}
		
		for(int i = 0; i < 5; i++)
		{
			binary += portList[i];
		}
		
		return Integer.parseInt(binary, 2);				
	}
				
	/**
	 * Transform the java hexadecimal number to C hexadecimal number.
	 * @param hex Decimal number.
	 * @return Hexadecimal in C notation;
	 */
	private String hexInC(String hex)
	{
		
		return ("0x" + hex.charAt(0) + hex.charAt(1));		
	}
}
