package com.donty.gymapp.Enums;

import java.util.Optional;

public interface My_Enum
{
    String key();
    
    
    static <E extends Enum<E> & My_Enum>
    Optional<E> get_Enum_From_Key(Class<E> enumType, String key)
    {
        
        for (E e : enumType.getEnumConstants())
        {
            if (e.key().equals(key))
            {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}
