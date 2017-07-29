package package1;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public final class CreatorHelper {
	
	static Map<String, CEEFund> CEEFundsContainer = new HashMap<String, CEEFund>();
	static Map<String, Stock> commonStockContainer = new HashMap<String, Stock>();

	public static JLabel createLabel(int x, int y, int width, int height, String lblText, int fontStyle) {
		JLabel label = new JLabel(lblText);
		label.setFont(new Font("Arial", fontStyle, 18));
		label.setBounds(x, y, width, height);
		return label;
	}
	
	public static JButton createUpdateButton(int x, int y, String tipText) {
		JButton button = new JButton("Update");
		button.setFont(new Font("Arial", Font.PLAIN, 10));
		button.setBounds(x, y, 70, 20);
		button.setToolTipText("Click to update " + tipText);
		return button;
	}
	
	public static JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(SystemColor.menu);
		editorPane.setContentType("text/html");
		return editorPane;
	}
	
	public static JEditorPane createEditorPaneWithHyperlink() {
		JEditorPane editorPane = createEditorPane();
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
			}
		});
		return editorPane;
		
	}
	
	public static JScrollPane createScrollPane(int x, int y, int width, int height, JEditorPane editorPane) {
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBounds(x, y, width, height);
		scrollPane.setPreferredSize(new Dimension(250, 145));
		scrollPane.setMinimumSize(new Dimension(10, 10));
		return scrollPane;
	}
	
}
