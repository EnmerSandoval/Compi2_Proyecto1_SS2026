package practica.compi2.semantic.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SymbolKind {
    VARIABLE("variable"),
    PARAMETER("parámetro"),
    ATTRIBUTE("atributo"),
    FIELD("campo"),
    STRUCT("estructura"),
    FUNCTION("función"),
    CLASS("clase"),
    METHOD("método"),
    CONSTRUCTOR("constructor");

    private final String description;
}
