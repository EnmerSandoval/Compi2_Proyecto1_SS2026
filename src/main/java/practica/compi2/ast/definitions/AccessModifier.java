package practica.compi2.ast.definitions;

import practica.compi2.semantic.symbols.ClassSymbol;

public enum AccessModifier {
    PUBLIC,
    PRIVATE,
    PROTECTED,
    DEFAULT;

    public boolean isInheritable() {
        return this == PUBLIC || this == PROTECTED;
    }

    public boolean allowsAccess(ClassSymbol from, ClassSymbol owner) {
        return switch (this) {
            case PUBLIC -> true;
            case PRIVATE, DEFAULT -> from == owner;
            case PROTECTED -> from != null && from.isSubclassOf(owner);
        };
    }

    public String describe() {
        return this == DEFAULT ? "sin modificador (se trata como private)" : name().toLowerCase();
    }
}
