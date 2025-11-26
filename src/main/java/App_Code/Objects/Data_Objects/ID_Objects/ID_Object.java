package App_Code.Objects.Data_Objects.ID_Objects;

import java.util.Objects;

public class ID_Object implements Comparable<ID_Object>
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private String class_Type, name;
    private int id;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    protected ID_Object(int id, String name)
    {
        //############################################
        // Variables
        //############################################
        this.id = id;
        this.name = name;
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
    
    public String get_Class_Type()
    {
        return class_Type;
    }
    
    public int get_ID()
    {
        return id;
    }
    
    public String get_String_Values()
    {
        return String.format("%s - %s", id, name);
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
        return get_ID() == other.get_ID();
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
    
    @Override
    public String toString()
    {
        return get_Name();
    }
}
