package window;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
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
import java.io.IOException;
import java.util.ArrayList;

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
		readLCFiles();
	}

	private void load(String title, String[] args) {

		RBR rbr = null;
		Segmentation sbr = null;
		
		//seleciona o modo multi-cenario
		int question = JOptionPane.showOptionDialog(null, 
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
	private void readLCFiles()
	{
		//1 - listar todos os arquivos LC (feito)
		//2 - processar cada arquivo (feito)
				
		File dir = new File("./LCs/");
		File[] files = dir.listFiles();
		
		if(files != null)
		{
			int n = 0;
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
				
				n++;
			}
			
			System.out.println(n + " LC files processed.");
		}
	}
	
	/**
	 * Get the processed information and save in a file.
	 */
	private void saveInfo()
	{
		//3 - salvar as informacoes
	}
	
	/**
	 * Get all the saved information and generate the srt.h file.
	 */
	private void createSTRFile()
	{
		//4 - montar o srt.h com as info salvas
	}
}
