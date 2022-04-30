import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
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
        setResizable(false);
        setVisible(true);


    }


    private JMenuBar generateMenu() {
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(e -> {
            JFileChooser openFile = new JFileChooser();
            int option = openFile.showOpenDialog(null);
            if(option == JFileChooser.APPROVE_OPTION){
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
        if(!Objects.equals(FilenameUtils.getExtension(filename) , "jpg")){
            JOptionPane.showMessageDialog(this, "Erro");
        }
        lbImage.setIcon(new ImageIcon(filename));
    }
}
