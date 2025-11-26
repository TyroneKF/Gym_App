package App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS;

public final class Measurement_ID_OBJ extends Storable_IDS_Parent
{
    private String unit_Symbol, measured_material_type;
    
    public Measurement_ID_OBJ(int id, String unit_Name, String unit_Symbol, String measured_material_type)
    {
        super(id, unit_Name);
        this.unit_Symbol = unit_Symbol;
        this.measured_material_type = measured_material_type;
    }
    
    public String get_Unit_Symbol()
    {
        return  unit_Symbol;
    }
    
    public String get_Measured_Material_Type()
    {
        return measured_material_type;
    }
}
