package practica.compi2.semantic.types;

import java.util.Objects;
import lombok.Getter;
import practica.compi2.ast.types.PrimitiveType;

@Getter
public class Type {

    public static final Type INTEGER = new Type(TypeKind.INTEGER, null, 0);
    public static final Type FLOAT = new Type(TypeKind.FLOAT, null, 0);
    public static final Type STRING = new Type(TypeKind.STRING, null, 0);
    public static final Type CHAR = new Type(TypeKind.CHAR, null, 0);
    public static final Type BOOLEAN = new Type(TypeKind.BOOLEAN, null, 0);
    public static final Type VOID = new Type(TypeKind.VOID, null, 0);
    public static final Type NULL = new Type(TypeKind.NULL, null, 0);
    public static final Type ERROR = new Type(TypeKind.ERROR, null, 0);

    private TypeKind kind;
    private String name;
    private int dimensions;

    public Type(TypeKind kind, String name, int dimensions) {
        this.kind = kind;
        this.name = name;
        this.dimensions = dimensions;
    }

    public static Type fromPrimitive(PrimitiveType primitive) {
        switch (primitive) {
            case INTEGER -> {
                return INTEGER;
            }
            case FLOAT -> {
                return FLOAT;
            }
            case STRING -> {
                return STRING;
            }
            case CHAR -> {
                return CHAR;
            }
            case BOOLEAN -> {
                return BOOLEAN;
            }
            default -> {
                return VOID;
            }
        }
    }

    public static Type fromKind(TypeKind kind) {
        switch (kind) {
            case INTEGER -> {
                return INTEGER;
            }
            case FLOAT -> {
                return FLOAT;
            }
            case STRING -> {
                return STRING;
            }
            case CHAR -> {
                return CHAR;
            }
            case BOOLEAN -> {
                return BOOLEAN;
            }
            default -> {
                return ERROR;
            }
        }
    }

    public Type withDimensions(int newDimensions) {
        return new Type(kind, name, newDimensions);
    }

    public Type elementType() {
        return new Type(kind, name, dimensions - 1);
    }

    public boolean is(TypeKind other) {
        return kind == other && dimensions == 0;
    }

    public boolean isArray() {
        return dimensions > 0 && kind != TypeKind.ERROR;
    }

    public boolean isNumeric() {
        return is(TypeKind.INTEGER) || is(TypeKind.FLOAT) || is(TypeKind.CHAR);
    }

    public boolean isPrimitive() {
        return isNumeric() || is(TypeKind.STRING) || is(TypeKind.BOOLEAN);
    }

    public boolean isString() {
        return is(TypeKind.STRING);
    }

    public boolean isBoolean() {
        return is(TypeKind.BOOLEAN);
    }

    public boolean isStruct() {
        return is(TypeKind.STRUCT);
    }

    public boolean isClass() {
        return is(TypeKind.CLASS);
    }

    public boolean isNull() {
        return kind == TypeKind.NULL;
    }

    public boolean isVoid() {
        return kind == TypeKind.VOID;
    }

    public boolean isError() {
        return kind == TypeKind.ERROR;
    }

    public boolean sameAs(Type other) {
        return kind == other.kind && dimensions == other.dimensions && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        String base = kind.getDescription();
        if (name != null) {
            base = name;
        }
        return base + "[]".repeat(dimensions);
    }
}
