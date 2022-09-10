package App_Code.Objects.Database_Objects.JTable_JDBC.MyJTable_JDBC.ViewDataTables.Parent;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;

import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID = 1;
    protected boolean setIconsUp;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                              String tableName, ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering)
    {
        super.db = db;

        super.data = data;
        super.columnNames = columnNames;


        this.planID = planID;

        super.parentContainer = parentContainer;
        super.tableName = tableName;


        super.unEditableColumns = unEditableColumns;
        super.colAvoidCentering = colAvoidCentering;

        this.setIconsUp = false;


        setUp();
    }

    private void setUp()
    {
        setLayout(new GridBagLayout());
        if (db.isDatabaseConnected())
        {
            if (data != null)
            {

                //###############################
                // Table Data
                //###############################
                super.data = data;

                super.columnNames = columnNames;
                super.columnDataTypes = db.getColumnDataTypes(tableName); //Column Data Types

                super.columnsInTable = columnNames.length;
                super.rowsInTable = data.length;

                //##############################
                // Table Setup
                //##############################
                super.tableSetup(data, columnNames, setIconsUp);

            }
            else
            {
                super.tableSetup(new Object[0][0], columnNames, setIconsUp);
            }
        }
    }


    public void refreshData()
    {
        //tableSetup(getData(), getColumnNames());
        tableModel_Setup(super.getData(), super.getColumnNames());
    }
}
