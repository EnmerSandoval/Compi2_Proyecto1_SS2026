package practica.compi2.ast.expressions;

import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class ThisAccess extends Expression {

    public ThisAccess(Position position) {
        super(position);
    }

    @Override
    public Type validate(SemanticContext sc) {
        if (sc.getCurrentClass() == null) {
            sc.error(position, "this", "'this' solo puede usarse dentro de una clase");
            type = Type.ERROR;
            return type;
        }
        type = new Type(TypeKind.CLASS, sc.getCurrentClass().getName(), 0);
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        return MemoryHelper.thisPointer(u);
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
