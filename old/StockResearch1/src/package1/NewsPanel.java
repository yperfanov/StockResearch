package package1;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class NewsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3069895363032267573L;
	
	private JEditorPane yahooNews;
	private JEditorPane seekingAlphaNews;
	private JEditorPane googleNews;
	
	
	NewsPanel(int x, int y, String ticker, Map<String, Stock> map) {
		super(null);
		
		this.setBounds(x, y, 330, 705);
		
		JLabel lblNews = CreatorHelper.createLabel(124, 14, 93, 29,
				"News", Font.BOLD);
		
		
		JLabel lblYahooFinance = CreatorHelper.createLabel(10, 51, 143, 29,
				"Yahoo Finance:", Font.ITALIC);
		
		
		JLabel lblSeekingAlpha = CreatorHelper.createLabel(10, 237, 143, 29,
				"Seeking Alpha:", Font.ITALIC);
		
		
		JLabel lblGoogleFinance = CreatorHelper.createLabel(10, 424, 143, 29,
				"Google Finance:", Font.ITALIC);
		
		
		yahooNews = CreatorHelper.createEditorPaneWithHyperlink();
		JScrollPane yahooNewsScroll = CreatorHelper.createScrollPane(10, 81, 310, 154, yahooNews);
		
		
		seekingAlphaNews = CreatorHelper.createEditorPaneWithHyperlink();
		JScrollPane seekingAlphaNewsScroll = CreatorHelper.createScrollPane(10, 269, 310, 154, seekingAlphaNews);
		
		
		googleNews = CreatorHelper.createEditorPaneWithHyperlink();
		JScrollPane googleNewsScroll = CreatorHelper.createScrollPane(10, 457, 310, 154, googleNews);
		
		
		JButton buttonNewsUpdate = CreatorHelper.createUpdateButton(250, 54, "news");
		buttonNewsUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				News updatedNews = null;
				try {
					updatedNews = new News(ticker, map.get(ticker).getFinanceYahooData()[13]);
				} catch (SAXException | IOException | ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setNewsPanels(updatedNews);
			}
		});
		
		this.add(lblNews);
		this.add(lblYahooFinance);
		this.add(lblSeekingAlpha);
		this.add(lblGoogleFinance);
		this.add(yahooNewsScroll);
		this.add(seekingAlphaNewsScroll);
		this.add(googleNewsScroll);
		this.add(buttonNewsUpdate);
	}
	
	void setYahooNews(String text) {
		this.yahooNews.setText(text);
		this.yahooNews.setCaretPosition(0);
	}
	
	void setSeekingAlphaNews(String text) {
		this.seekingAlphaNews.setText(text);
		this.seekingAlphaNews.setCaretPosition(0);
	}
	
	void setGoogleNews(String text) {
		this.googleNews.setText(text);
		this.googleNews.setCaretPosition(0);
	}
	
	private void setNewsPanels(News news) {
		this.yahooNews.setText(news.getYahooNewsInHTML());
		this.yahooNews.setCaretPosition(0);
		this.seekingAlphaNews.setText(news.getSeekingAlphaNewsInHTML());
		this.seekingAlphaNews.setCaretPosition(0);
		this.googleNews.setText(news.getGoogleNewsInHTML());
		this.googleNews.setCaretPosition(0);
	}
}
