package App_Code.Objects.Database_Objects.MyJDBC.Batch_Objects;

import App_Code.Objects.Database_Objects.MyJDBC.Statements.Fetch_Statement;

import java.util.LinkedHashSet;

public class Batch_Fetch_Statements
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected final LinkedHashSet<Fetch_Statement> fetch_Queries_And_Params = new LinkedHashSet<>();
    private final String error_msg;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Batch_Fetch_Statements(String error_msg)
    {
        this.error_msg = error_msg;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void add_Fetches(Fetch_Statement fetch_statement)
    {
        fetch_Queries_And_Params.add(fetch_statement);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public LinkedHashSet<Fetch_Statement> get_Fetch_Queries_And_Params()
    {
        return fetch_Queries_And_Params;
    }

    public String get_Error_MSG()
    {
        return error_msg;
    }
}
