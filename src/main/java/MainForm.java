import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.Objects;


public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JLabel lbImage;


    public MainForm() {
        super();

        JMenuBar menu = generateMenu();
        setJMenuBar(menu);
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setResizable(true);
        setVisible(true);
    }


    private JMenuBar generateMenu() {
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(e -> {
            JFileChooser openFile = new JFileChooser();
            int option = openFile.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    loadImage(openFile.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


        });
        JMenuItem save = new JMenuItem("Settings...");
        JMenuItem quit = new JMenuItem("Run");
        JMenu file = new JMenu("File");
        file.add(open);
        file.add(save);
        file.add(quit);
        JMenuBar menu = new JMenuBar();
        menu.add(file);
        return menu;
    }

    private void loadImage(String filename) throws IOException {

        //TODO ADD OTHER EXTENSIONS & IMPROVE ERROR MESSAGE
        if (!Objects.equals(FilenameUtils.getExtension(filename), "jpg")) {
            JOptionPane.showMessageDialog(this, "Error");
        }
        //TODO FIX CODE REPETITION
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(filename).getImage().getScaledInstance(mainPanel.getWidth(), mainPanel.getHeight(), Image.SCALE_DEFAULT));
        lbImage.setIcon(imageIcon);
        ComponentListener e = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(filename).getImage().getScaledInstance(mainPanel.getWidth(), mainPanel.getHeight(), Image.SCALE_DEFAULT));
                lbImage.setIcon(imageIcon);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        };
        mainPanel.addComponentListener(e);

    }


}
