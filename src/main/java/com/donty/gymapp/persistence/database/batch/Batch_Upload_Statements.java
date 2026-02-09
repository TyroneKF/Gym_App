package com.donty.gymapp.persistence.database.batch;

import com.donty.gymapp.persistence.database.statements.Upload_Statement;

import java.util.LinkedHashSet;

public class Batch_Upload_Statements
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected final LinkedHashSet<Upload_Statement> upload_Queries_And_Params = new LinkedHashSet<>();
    private final String error_msg;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Batch_Upload_Statements(String error_msg)
    {
        this.error_msg = error_msg;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void add_Uploads(Upload_Statement upload_statement)
    {
        upload_Queries_And_Params.add(upload_statement);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public String get_Error_MSG()
    {
        return error_msg;
    }

    public LinkedHashSet<Upload_Statement> get_Upload_Queries_And_Params()
    {
        return upload_Queries_And_Params;
    }
}
