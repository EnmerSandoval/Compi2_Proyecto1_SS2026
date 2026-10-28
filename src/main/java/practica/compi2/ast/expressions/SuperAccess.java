package practica.compi2.ast.expressions;

import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class SuperAccess extends Expression {

    public SuperAccess(Position position) {
        super(position);
    }

    @Override
    public Type validate(SemanticContext context) {
        type = Type.ERROR;
        if (context.getCurrentClass() == null) {
            context.error(position, "super", "'super' solo puede usarse dentro de una clase");
            return type;
        }
        if (context.getCurrentClass().getParent() == null) {
            context.error(position, "super", "La clase " + context.getCurrentClass().getName() + " no hereda de ninguna clase");
            return type;
        }
        type = new Type(TypeKind.CLASS, context.getCurrentClass().getParent().getName(), 0);
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        return MemoryHelper.thisPointer(utils);
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
