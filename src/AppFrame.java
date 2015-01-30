import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by Marvin on 29.01.15.
 */
public class AppFrame extends JFrame {

    private final String HOWTO_STRING = "1. Get your API key from http://steamcommunity.com/dev/apikey\n" +
            "2. Right click the item of interest in an inventory and copy the link\n" +
            "3. Paste API Key and item link into the application\n"+
            "4. Press the \"Request\" button";

    private final String ABOUT_STRING = "Made by Marvin from http://marvk.net/\n" +
            "Made with ♥ for /r/GlobalOffensiveTrade\n" +
            "Made using the JSON Simple library\n" +
            "This Application is not affiliated with Valve Software";

    private JButton requestButton, copyToClipboardButton;
    private JTextField apiKey, itemURL;
    private JLabel result;

    public AppFrame(Controller controller) {
        super("Wear Checker");

        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        bar.add(file);

        JMenu help = new JMenu("Help");
        JMenuItem howto = new JMenuItem("How to use");
        howto.addActionListener(e -> JOptionPane.showMessageDialog(this, HOWTO_STRING));
        help.add(howto);
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, ABOUT_STRING));
        help.add(about);
        bar.add(help);

        setJMenuBar(bar);

        JPanel panel = new JPanel(new GridLayout(0, 1));

        requestButton = new JButton("Request");
        copyToClipboardButton = new JButton("Copy to clipboard");
        apiKey = new JTextField("Paste your API Key here");
        itemURL = new JTextField("Paste your item URL here");
        result = new JLabel("?", JLabel.CENTER);

        requestButton.addActionListener(e -> {
            result.setText("Fetching...");
            copyToClipboardButton.setEnabled(false);
            requestButton.setEnabled(false);
            new Thread(() -> {
                String response = controller.request(apiKey.getText(), itemURL.getText());
                result.setText(response);
                if (!response.contains("Error"))
                    copyToClipboardButton.setEnabled(true);
                requestButton.setEnabled(true);
            }).start();
        });

        copyToClipboardButton.setEnabled(false);
        copyToClipboardButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(result.getText());
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        });

        panel.add(new JLabel("API Key:", JLabel.CENTER));
        panel.add(apiKey);

        panel.add(new JLabel("Item URL:", JLabel.CENTER));
        panel.add(itemURL);

        panel.add(requestButton);

        panel.add(new JLabel("Weapon Wear:", JLabel.CENTER));
        panel.add(result);

        panel.add(copyToClipboardButton);

        add(panel);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
