package App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS;

public final class Measurement_ID_OBJ extends Storable_IDS_Parent
{
    private String unit_Symbol;
    private Measurement_Material_Type_ID_OBJ measurement_material_type_id_obj;
    
    public Measurement_ID_OBJ(
            int id,
            boolean is_system,
            String unit_Name,
            String unit_Symbol,
            Measurement_Material_Type_ID_OBJ measurement_material_type_id_obj
    )
    {
        super(id, is_system, unit_Name);
        this.unit_Symbol = unit_Symbol;
        this.measurement_material_type_id_obj = measurement_material_type_id_obj;
    }
    
    public String get_Unit_Symbol()
    {
        return  unit_Symbol;
    }
    
    public Measurement_Material_Type_ID_OBJ get_Measured_Material_Type()
    {
        return measurement_material_type_id_obj;
    }
}
