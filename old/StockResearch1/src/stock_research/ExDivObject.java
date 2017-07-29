package stock_research;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExDivObject {

	private String ticker;
	private double exDividendAmount;
	private LocalDate exDividendDate;

	public ExDivObject(String ticker, double exDividendAmount, LocalDate exDividendDate) {
		this.ticker = ticker;
		this.exDividendAmount = exDividendAmount;
		this.exDividendDate = exDividendDate;
	}

	public final String getTicker() {
		return ticker;
	}

	public final double getExDividendAmount() {
		return exDividendAmount;
	}

	public final LocalDate getExDividendDate() {
		return exDividendDate;
	}

	@Override
	public String toString() {
		return String.format("%s %f %s", ticker, exDividendAmount, exDividendDate);
	}

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		String testDate = "9/12/2016";
		String testDate1 = "9/1/2016";
		String testDate2 = "9/2/2016";
		String testDate3 = "9/12/2016";
		LocalDate date = LocalDate.parse(testDate, formatter);
		LocalDate date1 = LocalDate.parse(testDate1, formatter);
		LocalDate date2 = LocalDate.parse(testDate2, formatter);
		LocalDate date3 = LocalDate.parse(testDate3, formatter);
		ExDivObject ob = new ExDivObject("KOREMKOWOMAN", 0.42, date);
		ExDivObject ob1 = new ExDivObject("Yuli", 0.42, date1);
		ExDivObject ob2 = new ExDivObject("Plamen", 0.42, date2);
		ExDivObject ob3 = new ExDivObject("A", 0.42, date3);

		System.out.println(date);
		System.out.println(ob);
		List<ExDivObject> list = new ArrayList<ExDivObject>(Arrays.asList(ob, ob1, ob2, ob3));
		order(list);
		System.out.println(list);
	}
	
	private static void order(List<ExDivObject> exDivO) {
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
