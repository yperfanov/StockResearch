package package1;

import java.awt.Desktop;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StockListFileChooser extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String newLine = "\n";
	
	private JButton addSymbolList, addSymbol;
	private JButton deleteSymbol, deleteAll;
	private JButton showList;
	
	private JTextArea log;
	private JFileChooser fc;
	
	StockListFileChooser(int x, int y) {
		super(null);
		
		this.setBounds(x, y, 320, 420);
		log = new JTextArea(5, 15);
		log.setBackground(SystemColor.menu);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		
		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setBounds(10, 119, 300, 300);
		logScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new FileNameExtensionFilter("text files only", "txt"));
		
		addSymbolList = new JButton("Add Symbol List",
				createImageIcon("addIcon.gif"));
		addSymbolList.setBounds(10, 11, 145, 25);
		addSymbolList.addActionListener(this);
		
		addSymbol = new JButton("Add Symbol",
				createImageIcon("addIcon.gif"));
		addSymbol.setBounds(10, 47, 145, 25);
		addSymbol.addActionListener(this);
		
		deleteSymbol = new JButton("Delete Symbol",
				createImageIcon("deleteIcon.gif"));
		deleteSymbol.setBounds(165, 11, 145, 25);
		deleteSymbol.addActionListener(this);
		
		deleteAll = new JButton("Delete All",
				createImageIcon("deleteIcon.gif"));
		deleteAll.setBounds(165, 47, 145, 25);
		deleteAll.addActionListener(this);
		
		showList = new JButton("Show List",
				createImageIcon("showIcon.png"));
		showList.setBounds(10, 83, 145, 25);
		showList.addActionListener(this);
		
		this.add(addSymbolList);
		this.add(addSymbol);
		this.add(deleteSymbol);
		this.add(deleteAll);
		this.add(showList);
		this.add(logScrollPane);
	
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addSymbolList) {
			int returnVal = fc.showOpenDialog(StockListFileChooser.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				log.append("Extracting tickers from " + file.getName() + " ..." + newLine);
				createTickerList(file);
				try {
					ManageTickerList.saveTickerList();
				} catch (IOException | URISyntaxException e1) {
					log.append("Could not extract tickers from the specified file." + newLine);
					e1.printStackTrace();
					StringWriter writer = new StringWriter();
					e1.printStackTrace(new PrintWriter(writer));
					ErrorBox.showDialog("Error!", writer.toString());
				}
				log.append("Tickers from " + file.getName() + " added successfully." + newLine);
			} else {
				log.append("List not added." + newLine);
			}
			
		} else if (e.getSource() == addSymbol) {
			String input = (String) JOptionPane.showInputDialog(null, "Enter Symbol:", null,
					JOptionPane.PLAIN_MESSAGE, createImageIcon("next.png"),
					null, null);
			String enteredSymbol = input.toUpperCase();
			if (enteredSymbol.contains("$")) {
				enteredSymbol = enteredSymbol.replace('$', '-');
			}
			if (enteredSymbol != null) {
				if (GUI.tickerList.contains(enteredSymbol)) {
					log.append("Symbol not added. It is already in the list." + newLine);
				} else {
					GUI.tickerList.add(enteredSymbol);
					try {
						ManageTickerList.saveTickerList();
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
						StringWriter writer = new StringWriter();
						e1.printStackTrace(new PrintWriter(writer));
						ErrorBox.showDialog("Error!", writer.toString());
					}
					log.append(enteredSymbol + " successfully added to the list." + newLine);
				}
			}
			
		} else if ((e.getSource() == deleteSymbol)){
			String input = (String) JOptionPane.showInputDialog(null, "Enter Symbol:", null,
					JOptionPane.PLAIN_MESSAGE, createImageIcon("deleteIcon.gif"),
					null, null);
			String enteredSymbol = input.toUpperCase();
			if (enteredSymbol != null) {
				if (GUI.tickerList.contains(enteredSymbol)) {
					GUI.tickerList.remove(enteredSymbol);
					try {
						ManageTickerList.saveTickerList();
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
						StringWriter writer = new StringWriter();
						e1.printStackTrace(new PrintWriter(writer));
						ErrorBox.showDialog("Error!", writer.toString());
					}
					log.append(enteredSymbol + " deleted from list." + newLine);
				} else {
					log.append(enteredSymbol + " is not in the list." + newLine);
				}
			}
			
		} else if (e.getSource() == deleteAll) {
			int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete "
					+ "all symbols?", null, JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (GUI.tickerList.isEmpty()) {
					log.append("Operation failed. List is already empty." + newLine);
				} else {
					GUI.tickerList.clear();
					try {
						ManageTickerList.saveTickerList();
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
						StringWriter writer = new StringWriter();
						e1.printStackTrace(new PrintWriter(writer));
						ErrorBox.showDialog("Error!", writer.toString());
					}
					log.append("All symbols deleted. List is empty." + newLine);
				}
			} else {
				log.append("List not deleted." + newLine);
			}
			
		} else if (e.getSource() == showList) {
			if (GUI.tickerList.isEmpty()) {
				log.append("List is empty." + newLine);
			} else {
				showList();
			}
		}
	}
	
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = StockListFileChooser.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;
		}
	}
	
	private void createTickerList(File file) {
		Scanner tickerScanner = null;
		try {
			tickerScanner = new Scanner(file);
			while (tickerScanner.hasNext()) {
				String ticker = tickerScanner.next().toUpperCase();
				if (ticker.contains("$")) {
					ticker = ticker.replace('$', '-');
				}
				GUI.tickerList.add(ticker);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			tickerScanner.close();
		}
	}
	
	private void showList() {
		TreeSet<String> sortedList = new TreeSet<String>(GUI.tickerList);
		File file = null;
		PrintWriter pw = null;
		try {
			file = new File(MakeDir.listPath);
			pw = new PrintWriter(file);
			for (String ticker : sortedList) {
				pw.println(ticker);
			}
			
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(file);
			}
			
		} catch (IOException fnfe) {
			fnfe.printStackTrace();
			StringWriter writer = new StringWriter();
			fnfe.printStackTrace(new PrintWriter(writer));
			ErrorBox.showDialog("Error!", writer.toString());
		} finally {
			pw.close();
		}
	}
}
