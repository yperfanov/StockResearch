package package1;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import package1.Stock.APITag;

public class GUI {

	private JFrame frame;
	private JPanel panel;

	private JTextField textField;
	private JTextField textField1;

	private JTable tableBasics;
	private JTable tableShortInterest;
	private JTable tableLiveFeeds;
	private JTable tablePD;
	JTable tableLiveFeeds1;
	JTable commonTableBasics;
	JTable fundamentalsTable;

	private JEditorPane countryAllocationEditorPane;
	private JEditorPane editorPaneTopHoldings;
	private JEditorPane editorPaneTopSectors;
	private JEditorPane editorPaneLiveQuotes;
	private JEditorPane yahooNews;
	private JEditorPane seekingAlphaNews;
	private JEditorPane googleNews;
	private JEditorPane usefulLinksEditorPane;
	JEditorPane usefulLinksEditorPane1;
	JEditorPane editorPaneLiveQuotes1;
	JTable commonTableShortInterest;
	JEditorPane googleNews1;
	JEditorPane seekingAlphaNews1;
	JEditorPane yahooNews1;

	private JScrollPane countryAllocationScroll;
	private JScrollPane yahooNewsScroll;
	private JScrollPane seekingAlphaNewsScroll;
	private JScrollPane googleNewsScroll;

	private JLabel fullName;
	private JLabel labelTimeOfLastTrade;
	JLabel fullName1;
	JLabel labelTimeOfLastTrade1;

	private CEEFund stock;
	private Stock commonStock;

	private static Map<String, CEEFund> stockContainer = new HashMap<String, CEEFund>();

	static Set<String> tickerList = new HashSet<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					showErrorBox(e, "Error!");
				}
			}
		});
	}
	
	private static void showErrorBox(Exception e, String message) {
		e.printStackTrace();
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		if (e.getMessage().equals("String index out of range: -2")) {
			ErrorBox.showDialog("Invalid Input!", writer.toString());
		} else {
			ErrorBox.showDialog(message, writer.toString());
		}
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 */
	public GUI() throws FileNotFoundException, ClassNotFoundException, IOException, URISyntaxException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 */
	private void initialize() throws FileNotFoundException, ClassNotFoundException, IOException, URISyntaxException {
		MakeDir m = new MakeDir();
		m.makeDir();
		
		tickerList = ManageTickerList.loadTickerList();

		frame = new JFrame("Stock Research 1.0");
		
		frame.setBounds(100, 100, 1244, 781);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		JMenu help = new JMenu("Help");
		menubar.add(help);
		
		JMenuItem about = new JMenuItem("About Stock Research");
		help.add(about);
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String info = String.format("Stock Research collects stock data from several websites* and displays the info on one window.\n\n"
						+ "Stock Research was written by Yuliyan Perfanov yperfanov@yahoo.com.\n"
						+ "It is free software, and can be used, modified and distributed under the terms of the Do What\n"
						+ "The Fuck You Want To Public License (WTFPL).\n\n"
						+ "*finance.yahoo.com, google.com/finance, cefconnect.com, wsj.com, seekingalpha.com,\n"
						+ " www.dividend.com/ex-dividend-dates.phpk");
				JOptionPane.showMessageDialog(null, info, "About Stock Research", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		JMenuItem manual = new JMenuItem("Manual");
		help.add(manual);
		manual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = null;
				PrintWriter pw = null;
				Scanner sc = null;
				//URL manualPath = null;
				InputStream manualPath = null;
				try {
					//manualPath = getClass().getResource("manual.txt");
					manualPath = getClass().getResourceAsStream("manual.StockResearch");
					file = new File(MakeDir.manualPath);
					pw = new PrintWriter(file);
					sc = new Scanner(manualPath);
					sc.useDelimiter("\\Z");
					pw.print(sc.next());
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().edit(file);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					pw.close();
					sc.close();
				}
			}
			
		});

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Common Stocks", null, panel_2, null);
		panel_2.setLayout(null);
		
		panel = new JPanel();
		tabbedPane.addTab("CEE Funds", null, panel, null);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Dividends", null, panel_1, null);
		panel_1.setLayout(null);
		
		fullName = createLabel(313, 11, 667, 37, null, Font.BOLD | Font.ITALIC);
		fullName.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
		panel.add(fullName);

		labelTimeOfLastTrade = createLabel(313, 59, 175, 25, null, Font.ITALIC);
		labelTimeOfLastTrade.setFont(new Font("Arial", Font.ITALIC, 14));
		labelTimeOfLastTrade.setToolTipText("15 minutes delayed");
		panel.add(labelTimeOfLastTrade);

		JLabel lblNewLabel = createLabel(10, 11, 54, 29, "Ticker", Font.BOLD);
		panel.add(lblNewLabel);

		JLabel lblBasics = createLabel(10, 55, 67, 29, "Basics", Font.BOLD);
		panel.add(lblBasics);

		JLabel lblShortInterest = createLabel(10, 242, 131, 29, "Short Interest", Font.BOLD);
		panel.add(lblShortInterest);

		JLabel lblNewLabel_1 = createLabel(10, 369, 131, 29, "Top Holdings", Font.BOLD);
		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = createLabel(610, 242, 169, 29, "Country Allocation", Font.BOLD);
		panel.add(lblNewLabel_2);

		JLabel lblPremiumdiscount = createLabel(308, 242, 165, 29, "Premium/Discount", Font.BOLD);
		panel.add(lblPremiumdiscount);

		JLabel lblTopSectors = createLabel(610, 369, 131, 29, "Top Sectors", Font.BOLD);
		panel.add(lblTopSectors);

		JLabel lblNews = createLabel(1014, 19, 93, 29, "News", Font.BOLD);
		panel.add(lblNews);

		JLabel lblYahooFinance = createLabel(900, 55, 143, 29, "Yahoo Finance:", Font.ITALIC);
		panel.add(lblYahooFinance);

		JLabel lblSeekingAlpha = createLabel(900, 242, 143, 29, "Seeking Alpha:", Font.ITALIC);
		panel.add(lblSeekingAlpha);

		JLabel lblGoogleFinance = createLabel(900, 429, 143, 29, "Google Finance:", Font.ITALIC);
		panel.add(lblGoogleFinance);

		JLabel lblUsefulLinks = createLabel(610, 142, 90, 14, "Useful Links:", Font.ITALIC);
		lblUsefulLinks.setFont(new Font("Arial", Font.ITALIC, 14));
		panel.add(lblUsefulLinks);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 51, 863, 2);
		panel.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 362, 863, 7);
		panel.add(separator_1);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 240, 863, 2);
		panel.add(separator_2);

		JButton btnNewButton = new JButton("Show Info");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.addActionListener(createActionListener());
		btnNewButton.setBounds(151, 11, 114, 28);
		panel.add(btnNewButton);

		JButton liveFeedsUpdateButton = createUpdateButton(487, "intra day data");
		liveFeedsUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ticker = textField.getText().toUpperCase();
				Stock stockWithOnlyLiveFeeds = null;
				try {
					stockWithOnlyLiveFeeds = new Stock(ticker, null, true, APITag.DEFAULT);
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					showErrorBox(e1, "Error!");
				}
				setLiveFeedsTable(stockWithOnlyLiveFeeds);
			}
		});
		panel.add(liveFeedsUpdateButton);

		JButton buttonNewsUpdate = createUpdateButton(1140, "news");
		buttonNewsUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ticker = textField.getText().toUpperCase();
				News updatedNews = null;
				try {
					updatedNews = new News(ticker, stockContainer.get(ticker).getFinanceYahooData()[13]);
				} catch (SAXException | IOException | ParserConfigurationException e1) {
					showErrorBox(e1, "Error!");
				}
				setNewsPanels(updatedNews);
			}
		});
		panel.add(buttonNewsUpdate);

		textField = new JTextField();
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField.setBounds(74, 11, 61, 28);
		panel.add(textField);
		textField.setColumns(10);
		textField.setToolTipText("Enter CEE Fund Ticker");
		textField.addActionListener(createActionListener());

		tableBasics = new JTable();
		tableBasics.setRowHeight(19);
		tableBasics.setShowHorizontalLines(false);
		tableBasics.setFillsViewportHeight(true);
		tableBasics.setEnabled(false);
		tableBasics.setRowSelectionAllowed(false);
		tableBasics.setFont(new Font("Arial", Font.PLAIN, 16));
		tableBasics.setBackground(SystemColor.menu);
		tableBasics.setShowVerticalLines(false);
		tableBasics.setModel(new DefaultTableModel(
				new Object[][] { { "Ex-dividend Date:", null }, { "Dividend:", null }, { "Yield:", null },
						{ "Exchange:", "" }, { "Shares Outstanding:", null }, { "52 Week High:", null },
						{ "52 Week Low:", null }, { "Average Daily Volume:", null }, },
				new String[] { "New column", "New column" }));
		tableBasics.getColumnModel().getColumn(0).setPreferredWidth(113);
		tableBasics.getColumnModel().getColumn(1).setPreferredWidth(60);
		tableBasics.setBounds(10, 86, 286, 154);
		panel.add(tableBasics);

		tableShortInterest = new JTable();
		tableShortInterest.setShowVerticalLines(false);
		tableShortInterest.setShowGrid(false);
		tableShortInterest.setRowHeight(19);
		tableShortInterest.setRowSelectionAllowed(false);
		tableShortInterest.setEnabled(false);
		tableShortInterest.setShowHorizontalLines(false);
		tableShortInterest.setBackground(SystemColor.menu);
		tableShortInterest.setFont(new Font("Arial", Font.PLAIN, 16));
		tableShortInterest.setModel(new DefaultTableModel(
				new Object[][] { { "Settlement Date", "Short Interest" }, { null, null }, { null, null }, },
				new String[] { "New column", "New column" }));
		tableShortInterest.getColumnModel().getColumn(0).setPreferredWidth(87);
		tableShortInterest.getColumnModel().getColumn(1).setPreferredWidth(76);
		tableShortInterest.setForeground(UIManager.getColor("Table.foreground"));
		tableShortInterest.setBounds(10, 282, 232, 76);
		panel.add(tableShortInterest);

		tableLiveFeeds = new JTable();
		tableLiveFeeds.setModel(new DefaultTableModel(
				new Object[][] { { "Last Trade:", null }, { "Change:", null }, { "Volume:", null },
						{ "Days Range:", null }, { "Open:", null }, { "Previous Close:", null }, },
				new String[] { "New column", "New column" }));
		tableLiveFeeds.getColumnModel().getColumn(0).setPreferredWidth(79);
		tableLiveFeeds.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableLiveFeeds.setShowVerticalLines(false);
		tableLiveFeeds.setShowHorizontalLines(false);
		tableLiveFeeds.setRowSelectionAllowed(false);
		tableLiveFeeds.setRowHeight(19);
		tableLiveFeeds.setFont(new Font("Arial", Font.PLAIN, 16));
		tableLiveFeeds.setFillsViewportHeight(true);
		tableLiveFeeds.setEnabled(false);
		tableLiveFeeds.setBackground(SystemColor.menu);
		tableLiveFeeds.setBounds(313, 86, 277, 114);
		panel.add(tableLiveFeeds);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		tablePD = new JTable();
		tablePD.setEditingColumn(1);
		tablePD.setModel(new DefaultTableModel(new Object[][] { { "Current:", null }, { "52 Week Avg:", null },
				{ "52 Week High:", null }, { "52 Week Low:", null }, }, new String[] { "New column", "New column" }));
		tablePD.setRowHeight(19);
		tablePD.getColumnModel().getColumn(0).setPreferredWidth(74);
		tablePD.getColumnModel().getColumn(1).setPreferredWidth(53);
		tablePD.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
		tablePD.setShowVerticalLines(false);
		tablePD.setShowHorizontalLines(false);
		tablePD.setRowSelectionAllowed(false);
		tablePD.setFont(new Font("Arial", Font.PLAIN, 16));
		tablePD.setFillsViewportHeight(true);
		tablePD.setEnabled(false);
		tablePD.setBackground(SystemColor.menu);
		tablePD.setBounds(313, 282, 217, 76);
		panel.add(tablePD);

		yahooNews = createEditorPaneWithHyperlink();
		yahooNewsScroll = createScrollPane(900, 86, 310, 154, yahooNews);
		panel.add(yahooNewsScroll);

		seekingAlphaNews = createEditorPaneWithHyperlink();
		seekingAlphaNewsScroll = createScrollPane(900, 274, 310, 154, seekingAlphaNews);
		panel.add(seekingAlphaNewsScroll);

		googleNews = createEditorPaneWithHyperlink();
		googleNewsScroll = createScrollPane(900, 462, 310, 154, googleNews);
		panel.add(googleNewsScroll);

		countryAllocationEditorPane = createEditorPane();
		countryAllocationScroll = createScrollPane(610, 282, 180, 76, countryAllocationEditorPane);
		countryAllocationScroll.setBorder(BorderFactory.createEmptyBorder());
		countryAllocationEditorPane.setCaretPosition(0);
		panel.add(countryAllocationScroll);

		editorPaneTopHoldings = createEditorPane();
		editorPaneTopHoldings.setBounds(10, 394, 540, 301);
		panel.add(editorPaneTopHoldings);

		editorPaneTopSectors = createEditorPane();
		editorPaneTopSectors.setBounds(610, 394, 286, 301);
		panel.add(editorPaneTopSectors);

		editorPaneLiveQuotes = createEditorPane();
		editorPaneLiveQuotes.setBounds(600, 59, 265, 66);
		panel.add(editorPaneLiveQuotes);

		usefulLinksEditorPane = createEditorPaneWithHyperlink();
		usefulLinksEditorPane.setBounds(600, 156, 212, 84);
		panel.add(usefulLinksEditorPane);

		//second tab

		URL url = getClass().getResource("divs_pic.jpg");
		JLabel lblNewLabel_3 = new JLabel();
		lblNewLabel_3.setIcon(new ImageIcon(url));
		lblNewLabel_3.setBounds(422, 145, 353, 425);
		panel_1.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Ex-Dividend Dates");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
		lblNewLabel_4.setBounds(435, 11, 353, 34);
		panel_1.add(lblNewLabel_4);

		DividendChooser divCho = new DividendChooser(63, 145);
		panel_1.add(divCho);

		StockListFileChooser stockCho = new StockListFileChooser(842, 145);
		panel_1.add(stockCho);

		JLabel lblNewLabel_5 = new JLabel("Generate Ex-Dividend Dates");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setFont(new Font("Arial", Font.PLAIN, 18));
		lblNewLabel_5.setBounds(63, 103, 290, 32);
		panel_1.add(lblNewLabel_5);

		JLabel lblManageTickerList = new JLabel("Manage Tickers List");
		lblManageTickerList.setHorizontalAlignment(SwingConstants.CENTER);
		lblManageTickerList.setFont(new Font("Arial", Font.PLAIN, 18));
		lblManageTickerList.setBounds(842, 102, 320, 34);
		panel_1.add(lblManageTickerList);


		/**
		 * Common Stock panel, same as CEE funds panel, I am lazy making the
		 * design without repeating the code so I will just copy it.
		*/ 
		textField1 = new JTextField();
		textField1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField1.setBounds(74, 11, 61, 28);
		panel_2.add(textField1);
		textField1.setColumns(10);
		textField1.setToolTipText("Enter Common Stock Ticker");
		textField1.addActionListener(createActionListener1());

		fullName1 = createLabel(313, 11, 667, 37, null, Font.BOLD | Font.ITALIC);
		fullName1.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
		panel_2.add(fullName1);

		labelTimeOfLastTrade1 = createLabel(313, 59, 175, 25, null, Font.ITALIC);
		labelTimeOfLastTrade1.setFont(new Font("Arial", Font.ITALIC, 14));
		labelTimeOfLastTrade1.setToolTipText("15 minutes delayed");
		panel_2.add(labelTimeOfLastTrade1);

		JLabel tickerCommon = createLabel(10, 11, 54, 29, "Ticker", Font.BOLD);
		panel_2.add(tickerCommon);

		JLabel basicsCommon = createLabel(10, 55, 67, 29, "Basics", Font.BOLD);
		panel_2.add(basicsCommon);

		JLabel shortInterest = createLabel(10, 242, 131, 29, "Short Interest", Font.BOLD);
		panel_2.add(shortInterest);

		JLabel news = createLabel(1014, 19, 93, 29, "News", Font.BOLD);
		panel_2.add(news);

		JLabel yahooFinance = createLabel(900, 55, 143, 29, "Yahoo Finance:", Font.ITALIC);
		panel_2.add(yahooFinance);

		JLabel seekingAlpha = createLabel(900, 242, 143, 29, "Seeking Alpha:", Font.ITALIC);
		panel_2.add(seekingAlpha);

		JLabel googleFinance = createLabel(900, 429, 143, 29, "Google Finance:", Font.ITALIC);
		panel_2.add(googleFinance);

		JLabel usefulLinks = createLabel(610, 142, 90, 14, "Useful Links:", Font.ITALIC);
		usefulLinks.setFont(new Font("Arial", Font.ITALIC, 14));
		panel_2.add(usefulLinks);

		JLabel fundamentals = createLabel(308, 242, 165, 29, "Fundamentals:", Font.BOLD);
		panel_2.add(fundamentals);

		JSeparator separator1 = new JSeparator();
		separator1.setBounds(10, 51, 863, 2);
		panel_2.add(separator1);

		JSeparator separator_23 = new JSeparator();
		separator_23.setBounds(10, 240, 863, 2);
		panel_2.add(separator_23);

		JButton btnNewButton1 = new JButton("Show Info");
		btnNewButton1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton1.addActionListener(createActionListener1());
		btnNewButton1.setBounds(151, 11, 114, 28);
		panel_2.add(btnNewButton1);

		fundamentalsTable = new JTable();
		fundamentalsTable.setEditingColumn(1);
		fundamentalsTable.setModel(new DefaultTableModel(
				new Object[][] { { "Earnings Per Share:", null }, { "EPS Estimate Current Year:", null },
						{ "EPS Estimate Next Year:", null }, { "EPS Estimate Next Quarter:", null },
						{ "Revenue:", null }, { "Book Value Per Share:", null }, { "P/E Ratio:", null },
						{ "EBITDA:", null }, { "Market Capitalization:", null }, { "Float Shares:", null } },
				new String[] { "New column", "New column" }));
		fundamentalsTable.setRowHeight(19);
		fundamentalsTable.getColumnModel().getColumn(0).setPreferredWidth(154);
		fundamentalsTable.getColumnModel().getColumn(1).setPreferredWidth(53);
		fundamentalsTable.setShowVerticalLines(false);
		fundamentalsTable.setShowHorizontalLines(false);
		fundamentalsTable.setRowSelectionAllowed(false);
		fundamentalsTable.setFont(new Font("Arial", Font.PLAIN, 16));
		fundamentalsTable.setFillsViewportHeight(true);
		fundamentalsTable.setEnabled(false);
		fundamentalsTable.setBackground(SystemColor.menu);
		fundamentalsTable.setBounds(313, 282, 417, 576);
		panel_2.add(fundamentalsTable);

		commonTableBasics = new JTable();
		commonTableBasics.setRowHeight(19);
		commonTableBasics.setShowHorizontalLines(false);
		commonTableBasics.setFillsViewportHeight(true);
		commonTableBasics.setEnabled(false);
		commonTableBasics.setRowSelectionAllowed(false);
		commonTableBasics.setFont(new Font("Arial", Font.PLAIN, 16));
		commonTableBasics.setBackground(SystemColor.menu);
		commonTableBasics.setShowVerticalLines(false);
		commonTableBasics.setModel(new DefaultTableModel(
				new Object[][] { { "Ex-dividend Date:", null }, { "Dividend Per Share:", null }, { "Yield:", null },
						{ "Exchange:", "" }, { "Shares Outstanding:", null }, { "52 Week High:", null },
						{ "52 Week Low:", null }, { "Average Daily Volume:", null }, },
				new String[] { "New column", "New column" }));
		commonTableBasics.getColumnModel().getColumn(0).setPreferredWidth(113);
		commonTableBasics.getColumnModel().getColumn(1).setPreferredWidth(60);
		commonTableBasics.setBounds(10, 86, 286, 154);
		panel_2.add(commonTableBasics);

		commonTableShortInterest = new JTable();
		commonTableShortInterest.setShowVerticalLines(false);
		commonTableShortInterest.setShowGrid(false);
		commonTableShortInterest.setRowHeight(19);
		commonTableShortInterest.setRowSelectionAllowed(false);
		commonTableShortInterest.setEnabled(false);
		commonTableShortInterest.setShowHorizontalLines(false);
		commonTableShortInterest.setBackground(SystemColor.menu);
		commonTableShortInterest.setFont(new Font("Arial", Font.PLAIN, 16));
		commonTableShortInterest.setModel(new DefaultTableModel(
				new Object[][] { { "Settlement Date", "Short Interest" }, { null, null }, { null, null },
					{null, null}, {"Change:", null}, {"Short Float %:", null}, {"Days To Cover:", null}},
				new String[] { "New column", "New column" }));
		commonTableShortInterest.getColumnModel().getColumn(0).setPreferredWidth(87);
		commonTableShortInterest.getColumnModel().getColumn(1).setPreferredWidth(76);
		commonTableShortInterest.setForeground(UIManager.getColor("Table.foreground"));
		commonTableShortInterest.setBounds(10, 282, 232, 276);
		panel_2.add(commonTableShortInterest);

		tableLiveFeeds1 = new JTable();
		tableLiveFeeds1.setModel(new DefaultTableModel(
				new Object[][] { { "Last Trade:", null }, { "Change:", null }, { "Volume:", null },
						{ "Days Range:", null }, { "Open:", null }, { "Previous Close:", null }, },
				new String[] { "New column", "New column" }));
		tableLiveFeeds1.getColumnModel().getColumn(0).setPreferredWidth(79);
		tableLiveFeeds1.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableLiveFeeds1.setShowVerticalLines(false);
		tableLiveFeeds1.setShowHorizontalLines(false);
		tableLiveFeeds1.setRowSelectionAllowed(false);
		tableLiveFeeds1.setRowHeight(19);
		tableLiveFeeds1.setFont(new Font("Arial", Font.PLAIN, 16));
		tableLiveFeeds1.setFillsViewportHeight(true);
		tableLiveFeeds1.setEnabled(false);
		tableLiveFeeds1.setBackground(SystemColor.menu);
		tableLiveFeeds1.setBounds(313, 86, 277, 114);
		panel_2.add(tableLiveFeeds1);

		yahooNews1 = createEditorPaneWithHyperlink();
		JScrollPane yahooNewsScroll1 = createScrollPane(900, 86, 310, 154, yahooNews1);
		panel_2.add(yahooNewsScroll1);

		seekingAlphaNews1 = createEditorPaneWithHyperlink();
		JScrollPane seekingAlphaNewsScroll1 = createScrollPane(900, 274, 310, 154, seekingAlphaNews1);
		panel_2.add(seekingAlphaNewsScroll1);

		googleNews1 = createEditorPaneWithHyperlink();
		JScrollPane googleNewsScroll1 = createScrollPane(900, 462, 310, 154, googleNews1);
		panel_2.add(googleNewsScroll1);

		editorPaneLiveQuotes1 = createEditorPane();
		editorPaneLiveQuotes1.setBounds(600, 59, 265, 66);
		panel_2.add(editorPaneLiveQuotes1);

		usefulLinksEditorPane1 = createEditorPaneWithHyperlink();
		usefulLinksEditorPane1.setBounds(600, 156, 212, 84);
		panel_2.add(usefulLinksEditorPane1);
		
		JButton liveFeedsUpdateButton1 = createUpdateButton(487, "intra day data");
		liveFeedsUpdateButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ticker = textField1.getText().toUpperCase();
				Stock stockWithOnlyLiveFeeds = null;;
				try {
					stockWithOnlyLiveFeeds = new Stock(ticker, null, true, APITag.DEFAULT);
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					showErrorBox(e1, "Error!");
				}
				setLiveFeedsTable1(stockWithOnlyLiveFeeds);
			}
		});

		JButton buttonNewsUpdate1 = createUpdateButton(1140, "news");
		buttonNewsUpdate1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ticker = textField1.getText().toUpperCase();
				News updatedNews = null;
				try {
					updatedNews = new News(ticker, commonStock.getFinanceYahooData()[13]);
				} catch (SAXException | IOException | ParserConfigurationException e1) {
					showErrorBox(e1, "Error!");
				}
				setNewsPanels1(updatedNews);
			}
		});
		panel_2.add(liveFeedsUpdateButton1);
		panel_2.add(buttonNewsUpdate1);
	}

	private JButton createUpdateButton(int x, String tipText) {
		JButton button = new JButton("Update");
		button.setFont(new Font("Arial", Font.PLAIN, 10));
		button.setBounds(x, 59, 70, 20);
		button.setToolTipText("Click to update " + tipText);
		return button;
	}

	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(SystemColor.menu);
		editorPane.setContentType("text/html");
		return editorPane;
	}

	private JEditorPane createEditorPaneWithHyperlink() {
		JEditorPane editorPane = createEditorPane();
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							showErrorBox(e1, "Error!");
						}
					}
				}
			}
		});
		return editorPane;

	}

	private JScrollPane createScrollPane(int x, int y, int width, int height, JEditorPane editorPane) {
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBounds(x, y, width, height);
		scrollPane.setPreferredSize(new Dimension(250, 145));
		scrollPane.setMinimumSize(new Dimension(10, 10));
		return scrollPane;
	}

	private JLabel createLabel(int x, int y, int width, int height, String lblText, int fontStyle) {
		JLabel label = new JLabel(lblText);
		label.setFont(new Font("Arial", fontStyle, 18));
		label.setBounds(x, y, width, height);
		return label;
	}

	private ActionListener createActionListener() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String ticker = textField.getText().toUpperCase();
				try {
					if (ticker.equals(null)) {
						throw new NullPointerException();
					}
					usefulLinksEditorPane.setText(setUsefulLinks(ticker));
					if (stockContainer.containsKey(ticker)) {
						Stock fund = new Stock(ticker, null, true, APITag.DEFAULT);
						News news = new News(ticker, stockContainer.get(ticker).getFinanceYahooData()[13]);
						loadData(stockContainer.get(ticker), fund, news);
					} else {
						stock = new CEEFund(ticker);
						stockContainer.put(ticker, stock);
						loadData(stock, stock, stock.getNews());
					}

				} catch (Exception ex) {
					showErrorBox(ex, "Error!");
				}
			}
		};
		return al;
	}

	/**
	 * 21 - e(earnings per share)
	 * 22 - e7(EPS Estimate Current Year)
	 * 23 - e8(EPS Estimate Next Year)
	 * 24 - e9("EPS Estimate Next Quarter)
	 * 25 - s6(Revenue)
	 * 26 - b4(Book Value Per Share)
	 * 27 - r(P/E Ratio)
	 * 28 - j4(EBITDA)
	 * 29 - j1(market capitalization)
	 * 30 - f6(float shares)
	 * @return
	 */
	private ActionListener createActionListener1() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String ticker = textField1.getText().toUpperCase();
				try {
					if (ticker.equals(null)) {
						throw new NullPointerException();
					}
					usefulLinksEditorPane1.setText(setUsefulLinks1(ticker));

					commonStock = new Stock(ticker, "ee7e8e9s6b4rj4j1f6", false, APITag.DEFAULT_AND_NEW);
					loadData1(commonStock, commonStock.getNews());

				} catch (Exception ex) {
					showErrorBox(ex, "Error!");
				}
			}
		};
		return al;

	}

	private void loadData(CEEFund stock, Stock stockWithOnlyRealTimeData, News news) {
		fullName.setText(stock.getCefConnectData()[0]);
		editorPaneTopHoldings.setText(stock.getTopHoldingsInHTML());
		editorPaneTopSectors.setText(stock.getTopSectorsInHTML());
		countryAllocationEditorPane.setText(stock.getCountryAllocationInHTML());
		countryAllocationEditorPane.setCaretPosition(0);
		tableBasics.setValueAt(stock.getFinanceYahooData()[15], 0, 1);
		tableBasics.setValueAt(stock.getCefConnectData()[2], 1, 1);
		tableBasics.setValueAt(stock.getCefConnectData()[1], 2, 1);
		tableBasics.setValueAt(stock.getFinanceYahooData()[13], 3, 1);
		tableBasics.setValueAt(stock.getCefConnectData()[4], 4, 1);
		tableBasics.setValueAt(stock.getFinanceYahooData()[18], 5, 1);
		tableBasics.setValueAt(stock.getFinanceYahooData()[19], 6, 1);
		tableBasics.setValueAt(stock.getFinanceYahooData()[20], 7, 1);
		tablePD.setValueAt(stock.getCefConnectData()[5], 0, 1);
		tablePD.setValueAt(stock.getCefConnectData()[6], 1, 1);
		tablePD.setValueAt(stock.getCefConnectData()[7], 2, 1);
		tablePD.setValueAt(stock.getCefConnectData()[8], 3, 1);
		tableShortInterest.setValueAt(stock.getShortInterestData()[0], 1, 0);
		tableShortInterest.setValueAt(stock.getShortInterestData()[1], 2, 0);
		tableShortInterest.setValueAt(stock.getShortInterestData()[2], 1, 1);
		tableShortInterest.setValueAt(stock.getShortInterestData()[3], 2, 1);
		setLiveFeedsTable(stockWithOnlyRealTimeData);
		setNewsPanels(news);
	}

	private void loadData1(Stock stock, News news) {
		fullName1.setText(stock.getFinanceYahooData()[0]);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[15], 0, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[14], 1, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[16], 2, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[13], 3, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[17], 4, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[18], 5, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[19], 6, 1);
		commonTableBasics.setValueAt(stock.getFinanceYahooData()[20], 7, 1);

		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[21], 0, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[22], 1, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[23], 2, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[24], 3, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[25], 4, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[26], 5, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[27], 6, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[28], 7, 1);
		fundamentalsTable.setValueAt(stock.getFinanceYahooData()[29], 8, 1);
		
		if (!stock.getFinanceYahooData()[30].equalsIgnoreCase("N/A")) {
			String convertedString = new DecimalFormat("#,###").format(Double.parseDouble(stock.getFinanceYahooData()[30]));
			fundamentalsTable.setValueAt(convertedString, 9, 1);
		} else {
			fundamentalsTable.setValueAt(stock.getFinanceYahooData()[30], 9, 1);
		}
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[0], 1, 0);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[1], 2, 0);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[2], 1, 1);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[3], 2, 1);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[4], 4, 1);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[5], 5, 1);
		commonTableShortInterest.setValueAt(stock.getShortInterestData()[6], 6, 1);
		setLiveFeedsTable1(stock);
		setNewsPanels1(news);
	}

	private void setNewsPanels(News news) {
		yahooNews.setText(news.getYahooNewsInHTML());
		yahooNews.setCaretPosition(0);
		seekingAlphaNews.setText(news.getSeekingAlphaNewsInHTML());
		seekingAlphaNews.setCaretPosition(0);
		googleNews.setText(news.getGoogleNewsInHTML());
		googleNews.setCaretPosition(0);
	}

	private void setNewsPanels1(News news) {
		yahooNews1.setText(news.getYahooNewsInHTML());
		yahooNews1.setCaretPosition(0);
		seekingAlphaNews1.setText(news.getSeekingAlphaNewsInHTML());
		seekingAlphaNews1.setCaretPosition(0);
		googleNews1.setText(news.getGoogleNewsInHTML());
		googleNews1.setCaretPosition(0);
	}

	private void setLiveFeedsTable(Stock stock) {
		labelTimeOfLastTrade.setText("as of " + stock.getFinanceYahooData()[2] + ", " + stock.getFinanceYahooData()[3]);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[1], 0, 1);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[4], 1, 1);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[9], 2, 1);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[10], 3, 1);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[11], 4, 1);
		tableLiveFeeds.setValueAt(stock.getFinanceYahooData()[12], 5, 1);
		editorPaneLiveQuotes.setText(stock.getRealTimeQuotesInHTML());
	}

	private void setLiveFeedsTable1(Stock stock) {
		labelTimeOfLastTrade1
				.setText("as of " + stock.getFinanceYahooData()[2] + ", " + stock.getFinanceYahooData()[3]);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[1], 0, 1);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[4], 1, 1);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[9], 2, 1);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[10], 3, 1);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[11], 4, 1);
		tableLiveFeeds1.setValueAt(stock.getFinanceYahooData()[12], 5, 1);
		editorPaneLiveQuotes1.setText(stock.getRealTimeQuotesInHTML());
	}

	private String setUsefulLinks(String ticker) {
		String usefulLinks = "<table>" + "<tr>" + "<td>" + "<a href=\"http://finance.yahoo.com//q?s=" + ticker
				+ "\"><font size=\"4\">Yahoo Finance</font></a><br></br>" + "</td>" + "<td>"
				+ "<a href=\"https://www.google.com/finance?q=" + ticker
				+ "\"><font size=\"4\">Google Finance</font></a><br></br>" + "</td></tr>" + "<tr>" + "<td>"
				+ "<a href=\"http://seekingalpha.com/symbol/" + ticker
				+ "\"><font size=\"4\">Seeking Alpha</font></a><br></br>" + "</td>" + "<td>"
				+ "<a href=\"http://www.cefconnect.com/fund/" + ticker
				+ "\"><font size=\"4\">CEF Connect</font></a><br></br>" + "</td></tr>" + "<tr>" + "<td>"
				+ "<a href=\"http://www.nasdaq.com/symbol/" + ticker + "\"><font size=\"4\">Nasdaq</font></a><br></br>"
				+ "</td>" + "<td>" + "<a href=\"https://twitter.com/search?f=tweets&vertical=default&q=%24" + ticker
				+ "&src=typd\"><font size=\"4\">Twitter Feeds</font></a><br></br>" + "</td></tr>";
		return usefulLinks;
	}

	private String setUsefulLinks1(String ticker) {
		String usefulLinks = "<table>" + "<tr>" + "<td>" + "<a href=\"http://finance.yahoo.com//q?s=" + ticker
				+ "\"><font size=\"4\">Yahoo Finance</font></a><br></br>" + "</td>" + "<td>"
				+ "<a href=\"https://www.google.com/finance?q=" + ticker
				+ "\"><font size=\"4\">Google Finance</font></a><br></br>" + "</td></tr>" + "<tr>" + "<td>"
				+ "<a href=\"http://seekingalpha.com/symbol/" + ticker
				+ "\"><font size=\"4\">Seeking Alpha</font></a><br></br>" + "</td>" + "<td>"
				+ "<a href=\"http://finviz.com/quote.ashx?t=" + ticker
				+ "\"><font size=\"4\">Finviz</font></a><br></br>" + "</td></tr>" + "<tr>" + "<td>"
				+ "<a href=\"http://www.nasdaq.com/symbol/" + ticker + "\"><font size=\"4\">Nasdaq</font></a><br></br>"
				+ "</td>" + "<td>" + "<a href=\"https://twitter.com/search?f=tweets&vertical=default&q=%24" + ticker
				+ "&src=typd\"><font size=\"4\">Twitter Feeds</font></a><br></br>" + "</td></tr>";
		return usefulLinks;
	}
}
