package com.donty.gymapp.ui.meta.ids.storableIDs;

public final class Measurement_ID_OBJ extends Storable_IDS_Parent
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final String unit_Symbol;
    private final Measurement_Material_Type_ID_OBJ measurement_material_type_id_obj;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
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
}
