package App_Code.Objects.Database_Objects.JDBC;

public class Null_MYSQL_Field
{
    private final int sqlType;
    
    public Null_MYSQL_Field (int sqlType)
    {
        this.sqlType = sqlType;
    }
    
    public int getSqlType()
    {
        return sqlType;
    }
    
    @Override
    public String toString()
    {
        return "Null_MYSQL_Field(" + sqlType + ")";
    }
}
