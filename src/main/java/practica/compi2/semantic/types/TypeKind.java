package practica.compi2.semantic.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeKind {
    INTEGER("entero"),
    FLOAT("decimal"),
    STRING("cadena"),
    CHAR("caracter"),
    BOOLEAN("booleano"),
    VOID("vacio"),
    STRUCT("estructura"),
    CLASS("clase"),
    NULL("nulo"),
    ERROR("error");

    private final String description;
}
