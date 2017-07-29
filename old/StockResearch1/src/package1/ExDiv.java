package package1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
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

public class ExDiv {

	public static void main(String... strings)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		long startTime = System.nanoTime();
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getCookieManager().clearCookies();
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.waitForBackgroundJavaScript(1000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		HtmlPage exDivsPage = webClient.getPage("http://www.dividend.com/ex-dividend-dates.php");
		HtmlForm form = exDivsPage.getFormByName("searchbox");
		HtmlSubmitInput button = form.getInputByValue("Search Ex-Dividend Dates");
		HtmlTextInput unputDate_1 = form.getInputByName("ExDividendDate1");
		HtmlTextInput inputDate_2 = form.getInputByName("ExDividendDate2");
	
		unputDate_1.setValueAttribute("2016-10-12");
		inputDate_2.setValueAttribute("2016-10-12");
		exDivsPage = button.click();

		HtmlDivision  sss = (HtmlDivision) exDivsPage.getByXPath("//div[@class='calcBox']").get(1);
		System.err.println(sss.getTextContent());
		DomElement ss = exDivsPage.getFirstByXPath("//div[@class='pageof']");
		String t = ss.getTextContent();
		int index = t.lastIndexOf(" ");
		int i = Integer.parseInt(t.substring(index + 1));
		
		Map<String, Double> map = new TreeMap<String, Double>();
		ArrayList<String> list = new ArrayList<String>();
		for (int j = 1; j <= i; j++) {
			webClient.getCookieManager().clearCookies();

			System.out.println("page" + j);
			if (j == 5) {
				//System.out.println(newPage.asText());
			}
			HtmlAnchor myAnchor = exDivsPage.getFirstByXPath("//a[@href='javascript:page("+ j +")']");
			System.err.println(myAnchor.asXml());
			exDivsPage = myAnchor.click();
			HtmlTable table = (HtmlTable) exDivsPage.getElementsByTagName("table").get(1);
			System.out.println(table.asText());
			String s = table.asXml();
			// System.out.println(page.asText());
			// System.err.printf("\n\n\n-----------------------------------\n\n\n");
			Document doc = Jsoup.parse(s);
			Elements rows = doc.getElementsByClass("yield");

			for (Element el : rows) {
				String ticker = el.select("td").get(0).text();
				String div = el.select("td").get(5).text();
				String tempDate = el.select("td").get(3).text();
				list.add(tempDate);
				double divNumb = Double.parseDouble(div);
				map.put(ticker, divNumb);
				//for (Map.Entry<String, Double> text : map.entrySet()) {
				//	sb.append(String.format("%s - %.3f\n", text.getKey(), text.getValue()));
				//}

			}
		}
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.printf("%s - %f\n", entry.getKey(), entry.getValue());
		}
		webClient.close();
		long endTime = System.nanoTime();
		long result = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
		Collections.sort(list);
		System.err.println(result);
		System.out.println(list);
	}
}
