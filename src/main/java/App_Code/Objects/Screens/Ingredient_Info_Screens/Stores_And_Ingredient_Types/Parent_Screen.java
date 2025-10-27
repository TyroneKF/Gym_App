package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.Image_JPanel;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;


public abstract class Parent_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // String
    protected String
            sql_File_Path,
            process;
    
    //############################
    // Integer
    //############################
    protected int
            frameHeight,
            frameWidth;
    
    //############################
    // Boolean
    //############################
    protected final boolean[] isEditMode = { false };
    
    //############################
    // Collections
    //############################
    protected Collection<String> jComboBox_List;
    
    //############################
    // Objects
    //############################
    protected MyJDBC db;
    protected GridBagConstraints gbc = new GridBagConstraints();
    
    //############################
    // Screen Objects
    //############################
    protected Ingredients_Info_Screen ingredient_Info_Screen;
    protected Edit_Screen edit_Screen;
    protected Add_Screen add_Screen;
    
    // JButton
    protected IconButton delete_Icon_Btn;
    
    // JPanels
    protected Image_JPanel screenImage;
    protected JPanel addScreen_Divider;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Screen(MyJDBC db, Ingredients_Info_Screen ingredients_Info_Screen,
                         String process, Collection<String> jComboBox_List, String sql_File_Path)
    {
        super(null, false);
        
        //####################################
        // Variables
        //####################################
        // Object
        this.db = db;
        
        // Screen Objects
        this.ingredient_Info_Screen = ingredients_Info_Screen;
        
        // Collections
        this.jComboBox_List = jComboBox_List;
        
        // String
        this.process = process;
        this.sql_File_Path = sql_File_Path;
        
        // Integer
        frameWidth = ingredients_Info_Screen.getWidth();
        frameHeight = ingredients_Info_Screen.getHeight();
    }
    
    protected abstract void initialize_Screens(MyJDBC db);
    
    //##################################################################################################################
    // Create GUI Methods
    //##################################################################################################################
    protected void create_Interface() // 850
    {
        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################
        get_ScrollPane_JPanel().setLayout(new BorderLayout());

        // Create Main Centre Screen for Interface
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        get_ScrollPane_JPanel().add(mainCentreScreen, BorderLayout.CENTER);
        
        //##################################################################################
        // Icon & ToggleSwitch JPanel Setup
        //##################################################################################
        int height = 55;
        
        //###########################
        // JPanel Creation
        //###########################
        JPanel icon_And_ToggleSwitch_JP = new JPanel(new BorderLayout());
        icon_And_ToggleSwitch_JP.setPreferredSize(new Dimension(500, height));
        
        //###########################
        // West JPanel
        //###########################
        icon_And_ToggleSwitch_JP.add(create_Space_Divider(325, height, new java.awt.Color(0, 0, 0, 0)), BorderLayout.WEST);  // West
        
        //###########################
        // East JPanel
        //###########################
        JPanel east_JP = create_Space_Divider(325, height, java.awt.Color.WHITE);  // East
        icon_And_ToggleSwitch_JP.add(east_JP, BorderLayout.EAST);
        create_IconPanel(east_JP);
        
        //################################################
        // JavaFX Toggle Switch Creation
        //#################################################
        JFXPanel fxPanel = new JFXPanel();  // <-- Swing wrapper for FX scene
        fxPanel.setSize(new Dimension(150, height));
        
        icon_And_ToggleSwitch_JP.add(fxPanel, BorderLayout.CENTER);  // add to Swing layout
        
        /**
         *  JavaFX Application Thread
         *  JAVAFX components can only be handled in a javaFX thread because it uses its own UI properties
         */
        Platform.runLater(() -> {
            
            //###########################
            // ADD Label & Size
            //###########################
            Label add_Label = new Label("ADD");
            add_Label.setTextFill(Color.WHITE);
            add_Label.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            
            //###########################
            // BG (Track) OF Toggle
            //###########################
            StackPane background = new StackPane();
            background.setPrefSize(105, 40); // THIS defines the toggle's visible size
            background.setStyle("-fx-background-color: #ccc; -fx-background-radius: 30px;");
            
            //###########################
            // Circle Knob
            //###########################
            Circle circle = new Circle(14, Color.WHITE); // Defines radius of  white circle, Circumference = x2
            circle.setTranslateX(- 35); // Positions Circle left within the track
            
            //###########################
            // Horizontal Padding
            //###########################
            // Add padding for the label text, the padding isn't for the container itself
            StackPane.setMargin(add_Label, new Insets(0, 35, 0, 35));
            
            //###########################
            // Create Toggle
            //###########################
            /**
             *   Create toggle Set
             *   The toggle itself takes its size from its largest child, which is the background
             *   (since it has setPrefSize(110, 40)).
             */
            
            StackPane toggle = getStackPane(background, add_Label, circle);
            
            //###########################
            //
            //###########################
            fxPanel.setScene(new Scene(new StackPane(toggle), Color.TRANSPARENT));
        });
        
        //##################################################################################
        // Adding Component To GUI
        //##################################################################################
        // Screen Image
        add_To_Container(mainCentreScreen, screenImage, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //############################
        // ToggleSwitch & Icons
        //############################
        add_To_Container(mainCentreScreen, icon_And_ToggleSwitch_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //############################
        // Add Screen
        //############################
        add_To_Container(mainCentreScreen, add_Screen, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.1, "horizontal", 0, 0, null);
        
        // Add Screen Divider to fill bottom of GUI Space
        add_To_Container(mainCentreScreen, addScreen_Divider = create_Space_Divider(10, 50, java.awt.Color.WHITE), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.1, "horizontal", 0, 0, null);
        
        //############################
        // Edit Screen
        //############################
        edit_Screen.setVisible(false);
        add_To_Container(mainCentreScreen, edit_Screen, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.20, "both", 0, 0, null);
        
        //##################################################################################
        // resize
        //##################################################################################
        resize_GUI();
    }
    
    private void create_IconPanel(JPanel add_To_Panel)
    {
        int width = 38;
        int height = 40;
        
        //#####################################################
        // Creating area for North JPanel (Refresh Icon)
        //#####################################################
        JPanel iconArea = new JPanel(new GridBagLayout());
        add_To_Container(add_To_Panel, iconArea, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //##########################
        // Refresh Icon
        //##########################
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        iconPanelInsert.add(refresh_Icon_Btn);
        
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            refresh_BTN_Action();
        });
        
        //##########################
        // Delete Icon
        //##########################
        delete_Icon_Btn = new IconButton("/images/x/x.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton delete_Btn = delete_Icon_Btn.returnJButton();
        iconPanelInsert.add(delete_Icon_Btn);
        
        delete_Icon_Btn.makeBTntransparent();
        delete_Icon_Btn.set_Btn_Visibility(false);
        
        delete_Btn.addActionListener(ae -> {
            delete_BTN_Action();
        });
        
        //#####################################################
        //
        //#####################################################
        additional_Icon_Setup(iconPanelInsert);
    }
    
    protected abstract void additional_Icon_Setup(JPanel iconPanelInsert);
    
    private StackPane getStackPane(StackPane background, Label add_Label, Circle circle)
    {
        StackPane toggle = new StackPane(background, add_Label, circle);
        
        //#####################################################
        // Define Toggle Movements
        //#####################################################
        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(0.25), circle);
        moveRight.setToX(35);
        
        TranslateTransition moveLeft = new TranslateTransition(Duration.seconds(0.25), circle);
        moveLeft.setToX(- 35);
        
        //#####################################################
        // Toggle ActionListener EVT
        //#####################################################
        
        toggle.setOnMouseClicked(e -> {
            if (isEditMode[0])
            {
                background.setStyle("-fx-background-color: #ccc; -fx-background-radius: 30px;");
                add_Label.setText("ADD");
                moveLeft.play();
            }
            else
            {
                background.setStyle("-fx-background-color: #4cd964; -fx-background-radius: 30px;");
                add_Label.setText("EDIT");
                moveRight.play();
            }
            
            //#####################################################
            //
            //#####################################################
            isEditMode[0] = ! isEditMode[0];
            switchPanel();
        });
        
        return toggle;
    }
    
    //#######################################################################
    // Icon BTN Actions
    //#######################################################################
    private void refresh_BTN_Action()
    {
        if(is_Editable())
        {
            edit_Screen.refresh_Btn_Action();
            return;
        }
        
        add_Screen.refresh_Btn_Action();
    }
    
    private void delete_BTN_Action()
    {
        edit_Screen.delete_Btn_Action_Listener();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected void reset_Actions()
    {
        add_Screen.refresh_Btn_Action();
        edit_Screen.load_JComboBox();
    }
    
    //####################################################
    // ToggleSwitch Methods
    //####################################################
    protected boolean is_Editable()
    {
        return isEditMode[0];
    }
    
    protected void switchPanel()
    {
        if ( is_Editable()) // Edit Mode
        {
            add_Screen.setVisible(false);          // Set Add_Screen to invisible
            addScreen_Divider.setVisible(false);  //  Set Add_Screen Divider to invisible
            edit_Screen.setVisible(true);         // Set Edit_Screen to Visible
            
            delete_Icon_Btn.set_Btn_Visibility(true);  // Set delete_Btn to Visible
        }
        else // ADD Mode
        {
            add_Screen.setVisible(true);          // Set Add_Screen to visible
            addScreen_Divider.setVisible(true);   //  Set Add_Screen Divider to visible
            edit_Screen.setVisible(false);        // Set Edit_Screen to invisible
            
            delete_Icon_Btn.set_Btn_Visibility(false);  // Set delete_Btn to  invisible
        }
        
        resize_GUI();
    }
    
    //####################################################
    // Get Methods
    //####################################################
    // Objects
    public JPanel get_Container()
    {
        return this;
    }
    
    // Screen Objects
    public Ingredients_Info_Screen get_Ingredient_Info_Screen()
    {
        return ingredient_Info_Screen;
    }
    
    // String
    public String get_SQL_File_Path()
    {
        return sql_File_Path;
    }
    
    public String get_Process()
    {
        return process;
    }
    
    // Integer
    public int get_FrameWidth()
    {
        return frameWidth;
    }
    
    public int get_FrameHeight()
    {
        return frameHeight;
    }
    
    // Collections
    public Collection<String> get_JComboBox_List()
    {
        return jComboBox_List;
    }
}
