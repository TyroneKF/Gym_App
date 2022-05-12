package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class Intro extends JFrame
{

    private Container contentPane;
    private GridBagConstraints gbc = new GridBagConstraints();
    private MyJDBC db;
    String databaseName = "gymapp7";
    private String name = databaseName;

    private int jFramewidth = 710, jFrameheight = 850;

    public Intro()
    {
        //#############################################################################################################
        //   1. Create the
        //#############################################################################################################

        // Container (ContentPane)
        contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setVisible(true);


        //#########################################
        //   Define Frame Properties
        //#########################################

        setVisible(true);
        setResizable(true);
        setSize(jFramewidth, jFrameheight);
        setLocation(00, 0);

        //Delete all temp data on close
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override //HELLO Causes Error
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                // ##############################################
                // If targets have changed, save them?
                // ##############################################
            }
        });

        checkIfTablesExist();
    }

    public void checkIfTablesExist()
    {
        db = new MyJDBC("root", "password", databaseName, null);
        if (!(db.isDatabaseConnected()))
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }
    }

    public static void main(String[] args)
    {
        new Intro();
    }
}
