package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
//import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Intro extends JFrame
{

    private final static String
            version_no = "00002",
            databaseName = "gymapp" + version_no,
            db_Script_Folder_Address = "src/main/java/Resources/Database_Scripts/Original_DB_Scripts";

    private MyJDBC db;
    private Container contentPane;
    private GridBagConstraints gbc = new GridBagConstraints();


    private String name = databaseName;

    private int jFrameWidth = 600, jFrameHeight = 600;

    public Intro(MyJDBC db)
    {
        this.db = db;

        //#############################################################################################################
        //   1. Create the
        //#############################################################################################################

        // Define Frame Properties
        setVisible(true);
        setResizable(false);
        setSize(jFrameWidth, jFrameHeight);
        setLocationRelativeTo(null); // Center JFrame on screen

        // Delete all temp data on close
        addWindowListener(new WindowAdapter()
        {
            @Override //HELLO Causes Error
            public void windowClosing(WindowEvent windowEvent)
            {

            }
        });

        // Container (ContentPane)
        contentPane = getContentPane();
        contentPane.setVisible(true);
        contentPane.setLayout(new GridLayout(1,1));

        //#############################################################################################################
        //   2. Add Picture to GUI
        //#############################################################################################################
        Image image = Toolkit.getDefaultToolkit().createImage("src/main/java/images/0.) Intro Screen/Background.jpeg")
                .getScaledInstance(jFrameWidth, jFrameHeight, Image.SCALE_DEFAULT);

        JPanel picturePanel = new JPanel(new GridLayout(1,1)){

            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                if (image != null)
                {
                    g.drawImage(image, 0, 0, this);
                }
            }
        };

       contentPane.add(picturePanel);

        //#############################################################################################################
        //   1. Create the
        //#############################################################################################################
        //checkIfTablesExist();
    }

    public void checkIfTablesExist()
    {
        db = new MyJDBC("root", "password", databaseName, null);
        if (!(db.get_DB_Connection_Status()))
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }
    }

    public static void main(String[] args)
    {
        MyJDBC db = new MyJDBC("root", "password", databaseName, db_Script_Folder_Address);
        new Intro(db);
    }
}
