package package1;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
 
public class ErrorBox {
 
    public static void showDialog(String title, String details) {
        final JEditorPane textPane = new JEditorPane();
        textPane.setCaretPosition(0);
        textPane.setText(details);
        textPane.setEditable(false);
 
 
        final JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setAlignmentX(0);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(650, 500));
        scrollPane.setMinimumSize(new Dimension(10, 10));
 
        final JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
 
        final JDialog dialog = new JOptionPane(
                content,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION).createDialog(null, "Error!");
 
        JLabel message = new JLabel(title);
        message.setBorder(new EmptyBorder(20, 20, 20, 20));
        message.setAlignmentX(0);
        Dimension labelSize = message.getPreferredSize();
        labelSize.setSize(300, labelSize.height);
        message.setPreferredSize(labelSize);
        content.add(message);
 
        JToggleButton tb = new JToggleButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;
            {
                this.putValue(Action.SELECTED_KEY, false);
                this.putValue(Action.NAME, "Show Details");
            }
 
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Boolean) this.getValue(Action.SELECTED_KEY)) {
                    content.add(scrollPane);
                } else {
                    content.remove(scrollPane);
                }
                content.invalidate();
                dialog.invalidate();
                dialog.pack();
            }
        });
        content.add(tb);
 
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}