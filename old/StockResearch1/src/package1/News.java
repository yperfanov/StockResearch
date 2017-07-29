package package1;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class News {

	private String googleNewsInHTML;
	private String yahooNewsInHTML;
	private String seekingAlphaNewsInHTML;

	News(String ticker, String exchange) throws SAXException, IOException, ParserConfigurationException {
		this.googleNewsInHTML = this.setGoogleNewsInHTML(ticker, exchange);
		this.yahooNewsInHTML = this.setSortedNews(
				"https://feeds.finance.yahoo.com/rss/2.0/headline?s=" + ticker + "&region=US&lang=en-US", ticker, '*',
				this.checkDayLightSavingTime());
		this.seekingAlphaNewsInHTML = this.setSortedNews("http://seekingalpha.com/api/sa/combined/" + ticker + ".xml",
				ticker, '-', 7);
	}

	String getGoogleNewsInHTML() {
		return this.googleNewsInHTML;
	}

	String getYahooNewsInHTML() {
		return this.yahooNewsInHTML;
	}

	String getSeekingAlphaNewsInHTML() {
		return this.seekingAlphaNewsInHTML;
	}

	private final String setGoogleNewsInHTML(String ticker, String exchange) throws SAXException, IOException, ParserConfigurationException {
		StringBuilder sb = new StringBuilder();
		String url = "https://www.google.com/finance/company_news?q=" + setExchangeName(exchange) + ":" + ticker
				+ "&output=rss";
		TreeMap<LocalDateTime, ArrayList<String>> data = new TreeMap<LocalDateTime, ArrayList<String>>();
		ArrayList<String> googleNewsData = this.getDataFromXML(url);
		for (int i = 0; i < googleNewsData.size(); i += 3) {
			ArrayList<String> titleAndLink = new ArrayList<String>(2);
			titleAndLink.add(googleNewsData.get(i));
			titleAndLink.add(googleNewsData.get(i + 1));
			data.put(LocalDateTime.parse(googleNewsData.get(i + 2), DateTimeFormatter.RFC_1123_DATE_TIME)
					.plusHours(this.checkDayLightSavingTime()), titleAndLink);
		}

		NavigableMap<LocalDateTime, ArrayList<String>> nmap = data.descendingMap();

		for (Entry<LocalDateTime, ArrayList<String>> element : nmap.entrySet()) {
			String dateToString = element.getKey()
					.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
			sb.append("<a href=\"" + element.getValue().get(1) + "\">" + element.getValue().get(0) + "</a>"
					+ "<br><font size=\"3\">" + dateToString + "</font></br><br></br>");
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	private final String setSortedNews(String url, String ticker, char symbol, int hours) throws SAXException, IOException, ParserConfigurationException {
		ArrayList<String> newsDataContainer = getDataFromXML(url);
		for (int i = 1; i < newsDataContainer.size() - 1; i += 3) {
			int index = newsDataContainer.get(i).indexOf(symbol);
			switch (symbol) {
			case '*':
				newsDataContainer.set(i,
						newsDataContainer.get(i).substring(index + 1, newsDataContainer.get(i).length()));
				break;
			case '-':
				if (index != -1) {
					newsDataContainer.set(i, newsDataContainer.get(i).substring(0, index));
				} else {
					newsDataContainer.set(i, "http://seekingalpha.com/symbol/" + ticker + "/news");
				}
				break;
			}
			LocalDateTime pubDate = LocalDateTime
					.parse(newsDataContainer.get(i + 1), DateTimeFormatter.RFC_1123_DATE_TIME).plusHours(hours);
			newsDataContainer.set(i + 1,
					pubDate.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH)));
		}
		String newsInHTML = writeInHTML(newsDataContainer);
		return newsInHTML;
	}

	private String writeInHTML(ArrayList<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size() - 2; i += 3) {
			sb.append("<a href=\"" + list.get(i + 1) + "\"><font size=\"4\">" + list.get(i) + "</font></a>"
					+ "<br><font size=\"3\">" + list.get(i + 2) + "</font></br><br></br>");
		}
		return sb.toString();
	}

	private ArrayList<String> getDataFromXML(String url)
			throws SAXException, IOException, ParserConfigurationException {
		ArrayList<String> data = new ArrayList<String>();
		org.jsoup.nodes.Document doc = null;
		for (int retries = 0; retries < 5; retries++) {
		    try {
		    	doc = Jsoup.connect(url).timeout(2000).get();
		    	break;
		    } catch (java.net.SocketTimeoutException e) {
		    }
		}
		String xml = doc.toString().replaceAll("&nbsp", "&#160");
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = builderFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		
		Document document = dBuilder.parse(is);
		NodeList newsList = document.getElementsByTagName("item");
		document.getDocumentElement().normalize();
		for (int i = 0; i < newsList.getLength(); i++) {
			Node n = newsList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element item = (Element) n;
				Node title = item.getElementsByTagName("title").item(0);
				Node link = item.getElementsByTagName("link").item(0);
				Node pubDate = item.getElementsByTagName("pubdate").item(0);
				Element elementTitle = (Element) title;
				Element elementLink = (Element) link;
				Element elementPubDate = (Element) pubDate;
				data.add(elementTitle.getTextContent().trim());
				data.add(elementLink.getTextContent().trim());
				data.add(elementPubDate.getTextContent().trim());
			}
		}
		return data;
	}
	
	private int checkDayLightSavingTime() {
		ZonedDateTime timeNow = ZonedDateTime.now(ZoneId.of("Europe/Sofia"));
		ZoneId z = timeNow.getZone();
		ZoneRules zoneRules = z.getRules();
		Boolean isDst = zoneRules.isDaylightSavings(timeNow.toInstant());
		if (isDst) {
			return 3;
		} else {
			return 2;
		}
	}

	private final String setExchangeName(String s) {
		String exchangeLetters = s.substring(0, 2);
		Set<String> nasdaqExchanges = new HashSet<String> (Arrays.asList("NM", "NG", "NI", "NC"));
		if (nasdaqExchanges.contains(exchangeLetters)) {
			return "NASDAQ";
		} else if (exchangeLetters.equals("AS")) {
			return "NYSEMKT";
		} else if (exchangeLetters.equals("PS") || exchangeLetters.equals("PC")) {
			return "NYSEARCA";
		} else {
			return "NYSE";
		}
	}

	public static void main(String... strings) throws SAXException, IOException, ParserConfigurationException {

		News news = new News("X", "NY");
		System.out.println(news.getGoogleNewsInHTML());
	}
}
