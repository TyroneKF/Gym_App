package App_Code.Objects.Data_Objects;

import java.util.Objects;

public class ID_Object implements Comparable<ID_Object>
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private String type, name;
    private int id;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    protected ID_Object(int id, String name, String type)
    {
        //############################################
        // Variables
        //############################################
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    // #################################################################################################################
    // Default Methods
    // #################################################################################################################
    public void set_Name(String name)
    {
        this.name = name;
    }
    
    // #################################
    // Accessor
    // #################################
    public String get_Name()
    {
        return name;
    }
    
    public String get_Type()
    {
        return type;
    }
    
    public int get_ID()
    {
        return id;
    }
    
    // #################################################################################################################
    // Default Methods
    // #################################################################################################################
    @Override
    public int compareTo(ID_Object other)
    {
        return CharSequence.compare(this.get_Name(), other.get_Name());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        
        ID_Object other = (ID_Object) obj;
        return get_ID() == other.get_ID() && get_Type().equals(other.get_Type());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(get_ID());
    }
    
    @Override
    public String toString()
    {
        return get_Name();
    }
}
