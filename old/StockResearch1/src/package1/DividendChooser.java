package package1;

import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import stock_research.ExDivObject;

public class DividendChooser extends JPanel 
	implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5184919687911580609L;
	
	private JTextArea exDivScreen;
	private JFileChooser fc;
	private JButton todayExDivs;
	private JButton tomorrowExDivs;
	private JButton thisWeekExDivs;
	private JButton nextWeekExDivs;
	private JButton customExDivs;
	
	enum DateOption {
		TODAY, TOMORROW, THIS_WEEK, NEXT_WEEK;
	}
	
	private String[] container = new String[4];

	DividendChooser(int x, int y) {
		super(null);
		
		this.setBounds(x, y, 290, 420);
		exDivScreen = new JTextArea();
		exDivScreen.setBackground(SystemColor.menu);
		exDivScreen.setMargin(new Insets(5, 5, 5, 5));
		exDivScreen.setFont(new Font("Arial", Font.ITALIC, 14));
		exDivScreen.setEditable(false);
		
		JScrollPane exDivScreenScrollPane = new JScrollPane(exDivScreen);
		exDivScreenScrollPane.setBounds(10, 119, 210, 300);
		exDivScreenScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new FileNameExtensionFilter("text files", "txt"));
		
		todayExDivs = new JButton("Today");
		todayExDivs.setBounds(10, 11, 110, 25);
		todayExDivs.addActionListener(this);
		
		tomorrowExDivs = new JButton("Tomorrow");
		tomorrowExDivs.setBounds(10, 47, 110, 25);
		tomorrowExDivs.addActionListener(this);
		
		thisWeekExDivs = new JButton("This Week");
		thisWeekExDivs.setBounds(165, 11, 110, 25);
		thisWeekExDivs.addActionListener(this);
		
		nextWeekExDivs = new JButton("Next Week");
		nextWeekExDivs.setBounds(165, 47, 110, 25);
		nextWeekExDivs.addActionListener(this);
		
		customExDivs = new JButton("Choose Date");
		customExDivs.setBounds(10, 83, 110, 25);
		customExDivs.addActionListener(this);
		
		this.add(todayExDivs);
		this.add(tomorrowExDivs);
		this.add(thisWeekExDivs);
		this.add(nextWeekExDivs);
		this.add(customExDivs);
		this.add(exDivScreenScrollPane);
	}
	
	public void actionPerformed(ActionEvent e) {
		String[] dates = new String[2];
		if (e.getSource() == todayExDivs) {
			if (container[0] != null) {
				exDivScreen.setText(container[0]);
			} else {
				dates = setDates(DateOption.TODAY);
				container[0] = generateExDivs(dates[0], dates[1]);
				exDivScreen.setText(container[0]);
			}
		} else if (e.getSource() == tomorrowExDivs) {
			if (container[1] != null) {
				exDivScreen.setText(container[1]);
			} else {
				dates = setDates(DateOption.TOMORROW);
				container[1] = generateExDivs(dates[0], dates[1]);
				System.out.println(dates[0] + "  " + dates[1]);
				exDivScreen.setText(container[1]);
			}
		} else if (e.getSource() == thisWeekExDivs) {
			if (container[2] != null) {
				exDivScreen.setText(container[2]);
			} else {
				dates = setDates(DateOption.THIS_WEEK);
				container[2] = generateExDivs(dates[0], dates[1]);
				exDivScreen.setText(container[2]);
			}
		} else if (e.getSource() == nextWeekExDivs) {
			if (container[3] != null) {
				exDivScreen.setText(container[3]);
			} else {
				dates = setDates(DateOption.NEXT_WEEK);
				container[3] = generateExDivs(dates[0], dates[1]);
				exDivScreen.setText(container[3]);
			}
		} else if (e.getSource() == customExDivs) {
			String input = (String) JOptionPane.showInputDialog(null, "Enter date range (mm-dd mm-dd)");
			String[] inputDates = input.split(" ");
			String year = Integer.toString(LocalDate.now().getYear());
			for (int i = 0; i < 2; i++) {
				if (inputDates[i].length() == 4) {
					dates[i] = year + "-0" + inputDates[i];
				} else {
					dates[i] = year + "-" + inputDates[i];
				}
			}
			exDivScreen.setText(generateExDivs(dates[0], dates[1]));
		}
		exDivScreen.setCaretPosition(0);
	}
	
	private String generateExDivs(String startDate, String endDate) {
		StringBuilder sb = new StringBuilder();
		String newLine = "\n";
		
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		WebClient webClient = new WebClient();
		webClient.getCookieManager().clearCookies();
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.waitForBackgroundJavaScript(1000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		
		try {
			HtmlPage exDivsPage = webClient.getPage("http://www.dividend.com/ex-dividend-dates.php");

			HtmlForm form = exDivsPage.getHtmlElementById("filter-form");
			HtmlSubmitInput button = form.getInputByValue("Search Ex-Dividend Dates");
			HtmlTextInput unputDate_1 = form.getInputByName("ExDividendDate1");
			HtmlTextInput inputDate_2 = form.getInputByName("ExDividendDate2");
			
			unputDate_1.setValueAttribute(startDate);
			inputDate_2.setValueAttribute(endDate);
			
			exDivsPage = button.click();
			
			HtmlDivision  sss = (HtmlDivision) exDivsPage.getByXPath("//div[@class='calcBox']").get(1);
			if (sss.getTextContent().contains("No Ex-Dividend Dates Found.")) {
				sb.append("No Ex-Dividend Dates Found.");
				return sb.toString();
			}

			DomElement totalPagesElement = exDivsPage.getFirstByXPath("//div[@class='pageof']");
			String totalPagesElementText = totalPagesElement.getTextContent();
			int index = totalPagesElementText.lastIndexOf(" ");
			int totalPagesNumb = Integer.parseInt(totalPagesElementText.substring(index + 1));
			List<ExDivObject> list = new ArrayList<ExDivObject>();
			String tempDate = "0/";
			
			//iterates over all javascript dividend pages
			for (int j = 1; j <= totalPagesNumb; j++) {
				webClient.getCookieManager().clearCookies();
				
				HtmlAnchor dividendPageButton = exDivsPage.getFirstByXPath("//a[@href='javascript:page("+ j +")']");
				exDivsPage = dividendPageButton.click();
				HtmlTable table = (HtmlTable) exDivsPage.getElementsByTagName("table").get(1);
				String dividendsTable = table.asXml();
				
				Document doc = Jsoup.parse(dividendsTable);
				Elements rows = doc.getElementsByClass("yield");
				
				for (Element el : rows) {
					String ticker = el.select("td").get(0).text();
					if (GUI.tickerList.contains(ticker)) {
						try {
							double div = Double.parseDouble(el.select("td").get(5).text());
							String date = el.select("td").get(3).text();
							list.add(new ExDivObject(ticker, div, setDate(date, tempDate)));
							tempDate = date;
						} catch (NumberFormatException e) {
							continue;
						}
					}
				}
			}
			order(list);
			LocalDate temp = LocalDate.of(2006, 10, 25);
			for (int i = 0; i < list.size(); i++) {
				if (!temp.equals(list.get(i).getExDividendDate())) {
					if (i != 0) {
						sb.append(newLine);
					}
					sb.append(list.get(i).getExDividendDate() + newLine);
				}
				temp = list.get(i).getExDividendDate();
				sb.append(String.format("%s - %.3f\n", list.get(i).getTicker(), list.get(i).getExDividendAmount()));
			}
			
			if (sb.toString().isEmpty()) {
				sb.append("No Ex-Dividend Dates Found.");
			}
		
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} finally {
			webClient.close();
		}
		return sb.toString();
	}
	
	private LocalDate setDate(String inputDate, String tempDate) {
		LocalDate temp = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		int year = temp.getYear();
		//parsing and comparing input dates
		int index = inputDate.indexOf("/");
		int index1 = tempDate.indexOf("/");
		int date = Integer.parseInt(inputDate.substring(0, index));
		int date1 = Integer.parseInt(tempDate.substring(0, index1));
		if (date1 > date) {
			year++;
		}
		LocalDate finalDate = LocalDate.parse(inputDate + "/" + year, formatter);
		return finalDate;
	}
	
	private String[] setDates(DateOption dateOption) {
		String[] dateContainer = new String[2];
		LocalDate date = LocalDate.now();
		String todayDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String thisWeekFridayDate = df.format(cal.getTime());
		switch (dateOption) {
			case TODAY:
				dateContainer[0] = todayDate;
				dateContainer[1] = todayDate;
				break;
			case TOMORROW:
				String tomorrowDate = date.plus(1, ChronoUnit.DAYS).format(DateTimeFormatter.ISO_LOCAL_DATE);
				dateContainer[0] = tomorrowDate;
				dateContainer[1] = tomorrowDate;
				break;
			case THIS_WEEK:
				dateContainer[0] = todayDate;
				dateContainer[1] = thisWeekFridayDate;
				break;
			case NEXT_WEEK:
				cal.add(Calendar.DAY_OF_WEEK, 3);
				dateContainer[0] = df.format(cal.getTime());
				cal.add(Calendar.DAY_OF_WEEK, 4);
				dateContainer[1] = df.format(cal.getTime());
		}
		return dateContainer;
	}
	
	private void order(List<ExDivObject> exDivO) {
		Collections.sort(exDivO, new Comparator<ExDivObject>() {
			@Override
			public int compare(ExDivObject o1, ExDivObject o2) {
				int cmpDates = o1.getExDividendDate().compareTo(o2.getExDividendDate());
				if (cmpDates != 0) {
					return cmpDates;
				} else {
					int cmpTickers = o1.getTicker().compareTo(o2.getTicker());
					return cmpTickers;
				}
			}
		});
	}
}
