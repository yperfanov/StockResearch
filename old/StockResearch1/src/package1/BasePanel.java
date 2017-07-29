package package1;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import package1.Stock.APITag;

public final class BasePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4888909997200702877L;

	static JTextField textField = new JTextField();
	private JButton btnShowInfo;

	
	BasePanel(int x, int y) {
		super(null);
		
		this.setBounds(x, y, 873, 231);
		 
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField.setBounds(69, 1, 61, 28);
		textField.setColumns(10);
		textField.setToolTipText("Enter CEE Fund");
		
		JLabel fullName = CreatorHelper.createLabel(308, 1, 565, 37, null, Font.BOLD | Font.ITALIC);
		fullName.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));;
		
		JLabel labelTimeOfLastTrade = CreatorHelper.createLabel(308, 49, 175, 25, null, Font.ITALIC);
		labelTimeOfLastTrade.setFont(new Font("Arial", Font.ITALIC, 14));
		labelTimeOfLastTrade.setToolTipText("15 minutes delayed");
		
		JLabel lblTicker = CreatorHelper.createLabel(5, 1, 54, 29,
				"Ticker", Font.BOLD);
		
		JLabel lblBasics = CreatorHelper.createLabel(5, 45, 67, 29,
				"Basics", Font.BOLD);
		
		JLabel lblUsefulLinks = CreatorHelper.createLabel(605, 132, 90, 14,
				"Useful Links:", Font.ITALIC);
		lblUsefulLinks.setFont(new Font("Arial", Font.ITALIC, 14));
		
		JSeparator separator = new JSeparator();
		separator.setBounds(5, 41, 863, 2);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 240, 863, 2);
		
		this.btnShowInfo = new JButton("Show Info");
		this.btnShowInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.btnShowInfo.setBounds(146, 1, 114, 28);
		
		
		JButton liveFeedsUpdateButton = CreatorHelper.createUpdateButton(482, 49, "intra day data");
		
		liveFeedsUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ticker = textField.getText().toUpperCase();
				try {
					Stock stockWithOnlyLiveFeeds = new Stock(ticker, null, true, APITag.DEFAULT);
					System.out.println(stockWithOnlyLiveFeeds);
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//setLiveFeedsTable(stockWithOnlyLiveFeeds);
			}
		});
		
		this.add(textField);
		this.add(fullName);
		this.add(labelTimeOfLastTrade);
		this.add(lblTicker);
		this.add(lblBasics);
		this.add(lblUsefulLinks);
		this.add(separator);
		this.add(separator_2);
		this.add(btnShowInfo);
		this.add(liveFeedsUpdateButton);
		
	}
	
	JTextField getTextField() {
		return BasePanel.textField;
	}
}
