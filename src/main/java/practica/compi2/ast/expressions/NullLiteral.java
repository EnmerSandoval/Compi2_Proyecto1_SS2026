package practica.compi2.ast.expressions;

import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

public class NullLiteral extends Expression {

    public NullLiteral(Position position) {
        super(position);
    }

    @Override
    public Type validate(SemanticContext context) {
        type = Type.NULL;
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        return new Literal3D(0);
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
