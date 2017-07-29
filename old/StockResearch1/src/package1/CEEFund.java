package package1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

public class CEEFund extends Stock {
	
	private String[] cefConnectData;
	
	private String topHoldingsInHTML;
	private String topSectorsInHTML;
	private String countryAllocationInHTML;
	
	private Document cefConnect;
	
	public CEEFund(String ticker) throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {
		super(ticker);
		this.cefConnect = super.setDocument("http://www.cefconnect.com/fund/" + ticker);
		this.cefConnectData = this.setCefConnectData();
		this.topHoldingsInHTML = this.setTopHoldings();
		this.topSectorsInHTML = this.setTwoColumnTable("ContentPlaceHolder1_cph_main_cph_main_ucPortChar_pcSector_SectorGrid");
		this.countryAllocationInHTML = this.setTwoColumnTable("ContentPlaceHolder1_cph_main_cph_main_ucPortChar_pcCountry_CountryGrid");
	}
	
	String[] getCefConnectData() {
		return cefConnectData;
	}

	void setCefConnectData(String[] cefConnectData) {
		this.cefConnectData = cefConnectData;
	}

	String getTopHoldingsInHTML() {
		return topHoldingsInHTML;
	}

	void setTopHoldingsInHTML(String topHoldings) {
		this.topHoldingsInHTML = topHoldings;
	}

	String getTopSectorsInHTML() {
		return topSectorsInHTML;
	}

	void setTopSectorsInHTML(String topSectors) {
		this.topSectorsInHTML = topSectors;
	}

	String getCountryAllocationInHTML() {
		return countryAllocationInHTML;
	}

	void setCountryAllocationInHTML(String countryAllocation) {
		this.countryAllocationInHTML = countryAllocation;
	}

	Document getCefConnect() {
		return cefConnect;
	}

	void setCefConnect(Document cefConnect) {
		this.cefConnect = cefConnect;
	}

	private String[] setCefConnectData() {
		String[] cefConnectData = new String[9];
		cefConnectData[0] = this.setFullName(); //full name
		cefConnectData[1] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_DistrDetails") //yield
				.select("td").get(3).text();
		cefConnectData[2] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_DistrDetails") //dividend
				.select("td").get(5).text();
		cefConnectData[3] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_DistrDetails") //dividendFrequency
				.select("td").get(7).text();
		cefConnectData[4] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_ucFundBasics_dvCapitalStructure") //sharesOutstanding
				.select("td").get(5).text();
		cefConnectData[5] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_SummaryGrid") //CurrentPremiumDiscount
				.select("td").get(3).text();
		cefConnectData[6] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_SummaryGrid") //FiftyTwoWeekAveragePremiumDiscount
				.select("td").get(7).text();
		cefConnectData[7] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_SummaryGrid") //FiftyTwoWeekHighPremiumDiscoun
				.select("td").get(11).text();
		cefConnectData[8] = this.cefConnect.select("#ContentPlaceHolder1_cph_main_cph_main_SummaryGrid") //FiftyTwoWeekLowPremiumDiscount
				.select("td").get(15).text();
		
		return cefConnectData;
	}
	
	
	private String setFullName() {
		String name = this.getCefConnect().getElementById("ContentPlaceHolder1_cph_main_cph_main_FundNameLabel").text(); 
		int index = name.indexOf(':');
		return name.substring(0, index); //trim redundant part of the name
	}

	
	private String setTopHoldings() {
		String tableId = "ContentPlaceHolder1_cph_main_cph_main_ucPortChar_TopHoldingsGrid";
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>"
				+ "<html>"
				+ "<head>"
				+ "<style>"
				+ "table, td {"
				+ "padding: 2px;"
				+ "}"
				+ "td {"
				+ "border: 1px solid black;"
				+ "font-family: Arial, Sand-serif;"
				+ "font-style: normal;"
				+ "font-size: 11px;"
				+ "}"
				+ "</style>"
				+ "</head>"
				+ "<body>"
				+ "<table>");
		Element table = this.getCefConnect().getElementById(tableId);
		Elements rows = table.select("td");
		
		for (int i = 0; i < rows.size(); i += 3) {
			sb.append("<tr>"
					+ "<td>" + rows.get(i).text() + "</td>"
					+ "<td align=\"right\">" + rows.get(i + 1).text() + "</td>"
					+ "<td align=\"right\">" + rows.get(i + 2).text() + "</td>"
					+ "</tr>");
		}
		sb.append("</body>"
				+ "</html>"
				+ "</table>");
		return sb.toString();
	}
	
	private String setTwoColumnTable(String tableId) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>"
				+ "<html>"
				+ "<head>"
				+ "<style>"
				+ "table, td {"
				+ "padding: 2px;"
				+ "}"
				+ "td {"
				+ "border: 1px solid black;"
				+ "font-family: Arial, Sand-serif;"
				+ "font-style: normal;"
				+ "font-size: 11px;"
				+ "}"
				+ "</style>"
				+ "</head>"
				+ "<body>"
				+ "<table>");
		Element table = this.getCefConnect().getElementById(tableId);
		Elements rows = table.select("td");
		
		for (int i = 0; i < rows.size(); i += 2) {
			sb.append("<tr>"
					+ "<td>" + rows.get(i).text() + "</td>"
					+ "<td align=\"right\">" + rows.get(i + 1).text() + "</td>"
					+ "</tr>");
		}
		sb.append("</body>"
				+ "</html>"
				+ "</table>");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s", super.getTicker(), Arrays.toString(super.getFinanceYahooData()),
				Arrays.toString(super.getShortInterestData()), Arrays.toString(this.getCefConnectData()),
				this.getTopHoldingsInHTML(), this.getTopSectorsInHTML(), this.getCountryAllocationInHTML());
	}
	

}
