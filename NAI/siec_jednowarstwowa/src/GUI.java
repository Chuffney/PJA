import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class GUI {
    private static JTextArea textInput;
    private static final JFrame mainWindow = new JFrame();
    private static final LanguageRenderer languageRenderer = new LanguageRenderer();

    public static void startGUI() {
        SwingUtilities.invokeLater(GUI::createGUI);
    }

    private static void createGUI() {
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        mainWindow.setLocation(centerPoint.x - 300, centerPoint.y - 300);

        mainWindow.setResizable(true);

        mainWindow.setSize(600, 600);
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setTitle("Language recognition");
        mainWindow.setVisible(true);
        mainWindow.setLayout(new BorderLayout());

        textInput = new JTextArea("input text here");
        textInput.setLineWrap(true);

        JScrollPane scrollTextArea = new JScrollPane(textInput);
        scrollTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainWindow.add(scrollTextArea, BorderLayout.CENTER);

        JList<String> languageList = new JList<>(new Vector<>(Enumerator.getAllNames()));
        languageList.setFixedCellWidth(100);
        languageList.setCellRenderer(languageRenderer);

        JPanel languagePanel = new JPanel(new BorderLayout());
        languagePanel.add(languageList, BorderLayout.CENTER);

        JButton confirmButton = new JButton("confirm");
        confirmButton.addActionListener(GUI::confirmButtonClicked);
        languagePanel.add(confirmButton, BorderLayout.PAGE_END);

        languagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        mainWindow.add(languagePanel, BorderLayout.LINE_END);
    }

    private static void confirmButtonClicked(ActionEvent event) {
        languageRenderer.setHighlight(Main.recogniseLanguage(textInput.getText()));
        mainWindow.repaint();
    }

    static class LanguageRenderer extends JLabel implements ListCellRenderer<String> {
        private Collection<Integer> highlights = new ArrayList<>();

        LanguageRenderer() {
            setOpaque(true);
            highlights.add(-1);
        }

        void setHighlight(Collection<Integer> highlightedIdx) {
            highlights = highlightedIdx;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value);

            if (highlights.isEmpty()) {
                setBackground(Color.RED);
                return this;
            }

            if (highlights.contains(index))
                setBackground(Color.GREEN);
            else
                setBackground(Color.WHITE);

            return this;
        }
    }
}
