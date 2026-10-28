package practica.compi2.ast.expressions;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class Literal extends Expression {

    private PrimitiveType primitive;
    private Object value;

    public Literal(Position position, PrimitiveType primitive, Object value) {
        super(position);
        this.primitive = primitive;
        this.value = value;
    }

    @Override
    public Type validate(SemanticContext context) {
        if (primitive == PrimitiveType.INTEGER && value instanceof String) {
            context.error(position, value.toString(), "El número " + value + " está fuera del rango de un entero");
        }
        type = Type.fromPrimitive(primitive);
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        switch (primitive) {
            case STRING -> {
                return StringHelper.allocateString(utils, value.toString());
            }
            case CHAR -> {
                return new Literal3D((int) (Character) value);
            }
            case BOOLEAN -> {
                return new Literal3D((Boolean) value ? 1 : 0);
            }
            default -> {
                return new Literal3D(value);
            }
        }
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", primitive);
        if (primitive == PrimitiveType.STRING) {
            printer.value("valor", "\"" + value + "\"");
        } else if (primitive == PrimitiveType.CHAR) {
            printer.value("valor", "'" + value + "'");
        } else {
            printer.value("valor", value);
        }
    }
}
