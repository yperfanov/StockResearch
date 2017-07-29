package package1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

public class Stock {

	private String ticker;
	private String realTimeQuotesInHTML;
	private String[] financeYahooData;
	private String[] shortInterestData;
	private News news;
	private String tags;

	/**
	 * @param DEFAULT_YAHOO_FINANCE_API_TAGS
	 * 0 - n(name)
	 * 1 - l1(price)
	 * 2 - t1(time)
	 * 3 - d1(date)
	 * 4 - c1(change)
	 * 5 - b(bid)
	 * 6 - b6(bid size)
	 * 7 - a(ask)
	 * 8 - a5(ask size)
	 * 9 - v(volume)
	 * 10 - m(days range)
	 * 11 - o(open)
	 * 12 - p(close)
	 * 13 - x(exchange)
	 * 14 - d(div per share)
	 * 15 - q(ex-div date)
	 * 16 - y(yield)
	 * 17- j2(shares outstanding)
	 * 18 - k(52week high)
	 * 19 - j(52week low)
	 * 20 - a2(3 month average volume)
	 * 
	 * @param DEFAULT_REAL_TIME_DATA_YAHOO_FINANCE_API_TAGS
	 * 0 - x(exchange)
	 * 1 - l1(price)
	 * 2 - t1(time)
	 * 3 - d1(date)
	 * 4 - c1(change)
	 * 5 - b(bid)
	 * 6 - b6(bid size)
	 * 7 - a(ask)
	 * 8 - a5(ask size)
	 * 9 - v(volume)
	 * 10 - m(days range)
	 * 11 - o(open)
	 * 12 - p(close)
	 */
	public final static String DEFAULT_YAHOO_FINANCE_API_TAGS = "nl1t1d1c1bb6aa5vmopxdqyj2kja2";
	public final static String DEFAULT_REAL_TIME_DATA_YAHOO_FINANCE_API_TAGS = "xl1t1d1c1bb6aa5vmop";

	enum APITag {
		DEFAULT, DEFAULT_AND_NEW, NEW;
	}

	Stock(String ticker) throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {
		this(ticker, null, false, APITag.DEFAULT);
	}

	Stock(String ticker, Boolean OnlyRealTimeData)
			throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {
		this(ticker, null, OnlyRealTimeData, APITag.DEFAULT);
	}

	Stock(String ticker, String yahooFinanceAPITags, Boolean OnlyRealTimeData, APITag yahooFinanceAPITagsOption)
			throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {

		if (OnlyRealTimeData) {
			switch (yahooFinanceAPITagsOption) {
			case DEFAULT:
				tags = Stock.DEFAULT_REAL_TIME_DATA_YAHOO_FINANCE_API_TAGS;
				setConstructorWithDefaultTagsAndOnlyRealTimeData(ticker, tags);
				break;
			case DEFAULT_AND_NEW:
				tags = Stock.DEFAULT_REAL_TIME_DATA_YAHOO_FINANCE_API_TAGS + yahooFinanceAPITags;
				setConstructorWithDefaultTagsAndOnlyRealTimeData(ticker, tags);
				break;
			case NEW:
				this.financeYahooData = setFinanceYahooData(ticker, yahooFinanceAPITags);
			}
		} else {
			switch (yahooFinanceAPITagsOption) {
			case DEFAULT:
				tags = Stock.DEFAULT_YAHOO_FINANCE_API_TAGS;
				setConstructorWithDefaultTagsAndAllData(ticker, tags);
				break;
			case DEFAULT_AND_NEW:
				tags = Stock.DEFAULT_YAHOO_FINANCE_API_TAGS + yahooFinanceAPITags;
				setConstructorWithDefaultTagsAndAllData(ticker, tags);
				break;
			case NEW:
				tags = yahooFinanceAPITags;
				this.ticker = ticker.toUpperCase();
				this.financeYahooData = setFinanceYahooData(ticker, tags);
			}

		}
	}

	private final void setConstructorWithDefaultTagsAndOnlyRealTimeData(String ticker, String tags)
			throws UnsupportedEncodingException, IOException {
		this.financeYahooData = setFinanceYahooData(ticker, tags);
		this.realTimeQuotesInHTML = setRealTimeQuotesInHTML(this.getFinanceYahooData()[5],
				this.getFinanceYahooData()[6], this.getFinanceYahooData()[7], this.getFinanceYahooData()[8]);
	}

	private final void setConstructorWithDefaultTagsAndAllData(String ticker, String tags)
			throws IOException, SAXException, ParserConfigurationException {
		this.ticker = ticker.toUpperCase();
		this.financeYahooData = setFinanceYahooData(ticker, tags);
		this.realTimeQuotesInHTML = setRealTimeQuotesInHTML(this.getFinanceYahooData()[5],
				this.getFinanceYahooData()[6], this.getFinanceYahooData()[7], this.getFinanceYahooData()[8]);
		this.shortInterestData = this.setShortInterestData();
		this.news = new News(ticker, this.getFinanceYahooData()[13]);
	}
	// http://finance.yahoo.com/d/quotes.csv?s=&f=l1t1d1c1bb6aa5vmopxndqyj2kja2

	String getTicker() {
		return this.ticker;
	}

	void setTicker(String ticker) {
		this.ticker = ticker;
	}

	String getRealTimeQuotesInHTML() {
		return this.realTimeQuotesInHTML;
	}

	String[] getFinanceYahooData() {
		return this.financeYahooData;
	}

	String[] getShortInterestData() {
		return this.shortInterestData;
	}

	News getNews() {
		return this.news;
	}

	private final String[] setFinanceYahooData(String ticker, String yahooFinanceAPItags) throws IOException {
	/* Using java URL classes gives "java.net.SocketTimeoutException: Read timed out" or 
	 * "java.net.SocketTimeoutException: connect timed out" very often.
	 * 	URL yahooFinance = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + ticker + "&f=" + yahooFinanceAPItags);
		HttpURLConnection connection = (HttpURLConnection) yahooFinance.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
		connection.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
																		//yahooFinance.openStream()
	 */
		String url = "http://finance.yahoo.com/d/quotes.csv?s=" + ticker + "&f=" + yahooFinanceAPItags;
		String rawData = setDocument(url).text();
		String yahooData = null;
		if (yahooFinanceAPItags.charAt(0) == 'n') {
			yahooData = eraseRedundantCommasFromName(rawData).replaceAll("\"", "");
		} else {
			yahooData = rawData.replaceAll("\"", "");
		}
		
		String[] data = yahooData.split("[,]");
		//System.out.println(java.util.Arrays.toString(data));

		if (tags.equals(DEFAULT_REAL_TIME_DATA_YAHOO_FINANCE_API_TAGS)) {

			if (!data[9].equalsIgnoreCase("N/A")) {
				String convertedString = new DecimalFormat("#,###").format(Double.parseDouble(data[9]));
				data[9] = convertedString;
			}

		} else if (tags.contains(DEFAULT_YAHOO_FINANCE_API_TAGS)) {
			if (!data[9].equalsIgnoreCase("N/A")) {
				String convertedString = new DecimalFormat("#,###").format(Double.parseDouble(data[9]));
				data[9] = convertedString;
			}
			if (!data[17].equalsIgnoreCase("N/A")) {
				String convertedString = new DecimalFormat("#,###").format(Double.parseDouble(data[17]));
				data[17] = convertedString;
			}

			if (!data[20].equalsIgnoreCase("N/A")) {
				String convertedString = new DecimalFormat("#,###").format(Double.parseDouble(data[20]));
				data[20] = convertedString;
			}

		}
		//reader.close();
		return data;
	}

	private String[] setShortInterestData() throws IOException {
		String[] data = new String[7];
		String exchangeName = setExchangeName(this.getFinanceYahooData()[13]);
		char firstLetterOfTheName = setFirstLetter();
		String url = "http://www.wsj.com/mdc/public/page/2_3062-sht" + exchangeName + "_" + firstLetterOfTheName
				+ "-listing.html";
		Document doc = setDocument(url);
		int count = 0;
		try {
			data[0] = doc.select("tr:matches(\\bCompany\\b)").select("td").get(2).text();
			count++;
			data[1] = doc.select("tr:matches(\\bCompany\\b)").select("td").get(3).text();
			count++;
			data[2] = doc.select("tr:matches(\\b" + this.getTicker() + "\\b)").select("td").get(2).text()
					.replaceAll(",", " ");
			count++;
			data[3] = doc.select("tr:matches(\\b" + this.getTicker() + "\\b)").select("td").get(3).text()
					.replaceAll(",", " ");
			count++;
			data[4] = doc.select("tr:matches(\\b" + this.getTicker() + "\\b)").select("td").get(4).text()
					.replaceAll(",", " ");
			count++;
			data[5] = doc.select("tr:matches(\\b" + this.getTicker() + "\\b)").select("td").get(6).text();
			count++;
			data[6] = doc.select("tr:matches(\\b" + this.getTicker() + "\\b)").select("td").get(7).text();
		} catch (Exception e) {
			for (int i = count; i < 7; i++) {
				data[i] = "N/A";
			}

		}

		return data;
	}

	private char setFirstLetter() {
		String[] nameSplit = (this.financeYahooData[0].split("[ -]+"));
		int lastArrayString = nameSplit.length - 1;
		char c = nameSplit[0].toUpperCase().charAt(0);
		if (this.financeYahooData[0].contains("SPDR Select Sector Fund")) {
			c = nameSplit[lastArrayString].toUpperCase().charAt(0);
		}
		return c;
	}

	private final String setRealTimeQuotesInHTML(String bid, String bidSize, String ask, String askSize) {
		String realTimeQuotesInHTML = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>" + "td {"
				+ "font-family: Arial, Sand-serif;" + "font-style: normal;"
				// + "table {padding: 1px;}"
				+ "font-size: 14px;}" + "</style>" + "</head>" + "<body>" + "<table>" + "<tr>" + "<th>Bid</th>"
				+ "<th>Size</th>" + "<th>Ask</th>" + "<th>Size</th>" + "</tr>" + "<tr>" + "<td>" + bid + "</td>"
				+ "<td>" + bidSize + "</td>" + "<td>" + ask + "</td>" + "<td>" + askSize + "</td>" + "</tr>"
				+ "</table>" + "</body>" + "</html>";
		return realTimeQuotesInHTML;
	}

	protected final Document setDocument(String url) throws IOException {
		Connection connection = null;
		Document doc = null;

		for (int retries = 0; retries < 5; retries++) {
			try {
				connection = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
						.ignoreContentType(true)
						.referrer("http://www.bing.com").timeout(2000);
				Connection.Response resp = connection.execute();

				if (resp.statusCode() == 200) {
					doc = connection.get();
				}
				break;
			} catch (java.net.SocketTimeoutException e) {
			}
		}
		return doc;
	}

	private final String eraseRedundantCommasFromName(String data) throws IOException {
		StringBuilder sb = new StringBuilder(data);
		int index = sb.indexOf("\"", 1);
		String s = sb.substring(1, index).replaceAll(",", "");
		sb.delete(0, index + 1);
		sb.insert(0, s);
		/*if (sb.substring(0, index).contains(",")) {
			for (int i = 1; i < index; i++) {
				System.out.println(sb.charAt(i));
				if (sb.charAt(i) == ',') {
					System.err.println(sb.charAt(i));
					sb.deleteCharAt(i);
				}
			}
		}*/
		return sb.toString();
	}

	private final String setExchangeName(String s) {
		String exchangeLetters = s.substring(0, 2);
		Set<String> nasdaqExchanges = new HashSet<String> (Arrays.asList("NM", "NG", "NI", "NC"));
		if (nasdaqExchanges.contains(exchangeLetters)) {
			return "nasdaq";
		} else if (exchangeLetters.equals("AS")) {
			return "amex";
		} else {
			return "nyse";
		}
	}

	public static void main(String... strings)
			throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {
		Stock stock = new Stock("AMDA");
		System.out.println(java.util.Arrays.toString(stock.getShortInterestData()));
	}
}
