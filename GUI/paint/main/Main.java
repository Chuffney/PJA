package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static main.Brush.Mode.*;
import static java.awt.event.KeyEvent.*;

public class Main {
    private static final JFrame mainWindow = new JFrame();
    private static final JLabel modeLabel = new JLabel("Pen");
    private static final JLabel saveLabel = new JLabel("New");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createGUI);
    }

    private static void createGUI() {
        mainWindow.setTitle("Simple Draw");

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        mainWindow.setLocation(centerPoint.x - (DrawingPane.DRAWING_DIMENSIONS.width / 2), centerPoint.y - (DrawingPane.DRAWING_DIMENSIONS.height / 2));

        mainWindow.setResizable(false);
        mainWindow.getContentPane().setLayout(new BorderLayout());


        setupMenuBar();

        mainWindow.add(DrawingPane.singleton);

        setupToolBar();


        mainWindow.pack();

        mainWindow.setVisible(true);
    }

    private static void setupMenuBar() {
        JMenuBar topBar = new JMenuBar();
        {
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic('F');

            JMenuItem openFile = new JMenuItem("Open", 'O');
            openFile.setAccelerator(KeyStroke.getKeyStroke(VK_O, CTRL_DOWN_MASK));
            openFile.addActionListener(FileManager::openFile);
            fileMenu.add(openFile);

            JMenuItem saveFile = new JMenuItem("Save", 'S');
            saveFile.setAccelerator(KeyStroke.getKeyStroke(VK_S, CTRL_DOWN_MASK));
            saveFile.addActionListener(FileManager::saveFile);
            fileMenu.add(saveFile);

            JMenuItem saveAs = new JMenuItem("Save As...", 'a');
            saveAs.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
            saveAs.addActionListener(FileManager::saveAs);
            fileMenu.add(saveAs);

            JMenuItem quit = new JMenuItem("Quit", 'Q');
            quit.setAccelerator(KeyStroke.getKeyStroke(VK_Q, CTRL_DOWN_MASK));
            quit.addActionListener(Main::quit);
            fileMenu.addSeparator();
            fileMenu.add(quit);

            topBar.add(fileMenu);
        }

        {
            JMenu drawMenu = new JMenu("Draw");
            drawMenu.setMnemonic('D');

            {
                JMenu figureMenu = new JMenu("Figure");
                figureMenu.setMnemonic('F');

                ButtonGroup buttonGroup = new ButtonGroup();

                JRadioButtonMenuItem circle = new JRadioButtonMenuItem("Circle");
                circle.addActionListener(e -> setMode(Circle));
                circle.setMnemonic('C');
                circle.setAccelerator(KeyStroke.getKeyStroke(VK_C, CTRL_DOWN_MASK));
                buttonGroup.add(circle);
                figureMenu.add(circle);

                JRadioButtonMenuItem square = new JRadioButtonMenuItem("Square");
                square.addActionListener(e -> setMode(Square));
                square.setMnemonic('r');
                square.setAccelerator(KeyStroke.getKeyStroke(VK_R, CTRL_DOWN_MASK));
                buttonGroup.add(square);
                figureMenu.add(square);

                JRadioButtonMenuItem pen = new JRadioButtonMenuItem("Pen", true);
                pen.addActionListener(e -> setMode(Pen));
                pen.setMnemonic('e');
                pen.setAccelerator(KeyStroke.getKeyStroke(VK_E, CTRL_DOWN_MASK));
                buttonGroup.add(pen);
                figureMenu.add(pen);

                drawMenu.add(figureMenu);
            }

            JMenuItem color = new JMenuItem("Color", 'C');
            color.setAccelerator(KeyStroke.getKeyStroke("control shift C"));
            color.addActionListener(DrawingPane::setColor);
            drawMenu.add(color);

            JMenuItem clear = new JMenuItem("Clear", 'e');
            clear.setAccelerator(KeyStroke.getKeyStroke("control shift N"));
            clear.addActionListener(DrawingPane::clearCanvas);
            drawMenu.addSeparator();
            drawMenu.add(clear);

            topBar.add(drawMenu);
        }

        mainWindow.setJMenuBar(topBar);
    }

    private static void setupToolBar() {
        JToolBar bottomBar = new JToolBar();
        bottomBar.setLayout(new BorderLayout());

        bottomBar.add(modeLabel, BorderLayout.LINE_START);

        bottomBar.add(saveLabel, BorderLayout.LINE_END);

        mainWindow.add(bottomBar, BorderLayout.PAGE_END);
    }

    private static void setMode(Brush.Mode mode) {
        Brush.setMode(mode);
        modeLabel.setText(mode.toString());
    }

    public static void setSaveState(FileManager.SaveState saveState) {
        saveLabel.setText(saveState.toString());
    }

    public static void setAssociatedFile(File file) {
        mainWindow.setTitle("Simple Draw: " + file.getName());
    }

    public static void quit(ActionEvent ignored) {
        if (FileManager.currentSaveState != FileManager.SaveState.Modified)
            System.exit(0);

        switch (Dialog.askForSave(mainWindow)) {
            case YES:
                FileManager.saveFile();
                if (FileManager.currentSaveState != FileManager.SaveState.Saved)
                    break;
                //no break here - should fall through
            case NO:
                System.exit(0);
            case CANCEL:
        }
    }

    public static JFrame getMainWindow() {
        return mainWindow;
    }
}
