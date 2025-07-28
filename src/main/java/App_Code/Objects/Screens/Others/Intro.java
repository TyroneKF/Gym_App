package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
//import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class Intro extends JFrame
{

    private final static String
            version_no = "00002",
            databaseName = "gymapp" + version_no,
            db_Script_List_Folder_Path = "/data/database_scripts/",
            db_Script_List_Name = "0.) Script_List.txt";

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
        URL imageUrl = getClass().getResource("/images/0.) Intro Screen/Background.jpeg");
        Image image = Toolkit.getDefaultToolkit()
                .createImage(imageUrl)
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
        /*db = new MyJDBC("root", "password", databaseName, db_Script_List_Folder_Path, db_Script_List_Name);
        if (!(db.get_DB_Connection_Status()))
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }*/
    }

    public static void main(String[] args)
    {
       /* MyJDBC db = new MyJDBC("root", "password", databaseName, db_Script_List_Folder_Path, db_Script_List_Name);
        new Intro(db);*/
    }
}
