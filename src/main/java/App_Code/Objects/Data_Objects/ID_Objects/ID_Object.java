package App_Code.Objects.Data_Objects.ID_Objects;

import java.util.Objects;

public class ID_Object implements Comparable<ID_Object>
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private String  name;
    private Integer id;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    protected ID_Object(Integer id, String name)
    {
        //############################################
        // Variables
        //############################################
        this.id = id;
        set_Name(name);
    }
    
    // #################################################################################################################
    // Default Methods
    // #################################################################################################################
    public void set_Name(String name)
    {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }
    
    // #################################
    // Accessor
    // #################################
    public String get_Name()
    {
        return name;
    }
    
    public Integer get_ID()
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
        if (other == null) return 1;
        return this.get_Name().compareToIgnoreCase(other.get_Name());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        
        ID_Object other = (ID_Object) obj;
        return Objects.equals(get_ID(), other.get_ID());
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
