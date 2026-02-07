package com.donty.gymapp.persistence.database.Batch;

import com.donty.gymapp.persistence.database.Statements.Fetch_Statement;

import java.util.LinkedHashSet;

public class Batch_Upload_And_Fetch_Statements extends Batch_Upload_Statements
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected final LinkedHashSet<Fetch_Statement> fetch_Queries_And_Params = new LinkedHashSet<>();


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Batch_Upload_And_Fetch_Statements(String error_msg)
    {
        super(error_msg);
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
}
