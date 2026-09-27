package practica.compi2.ast.expressions;

import java.util.ArrayList;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.natives.ReadLineNative;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

public class ReadExpression extends Expression {

    public ReadExpression(Position position) {
        super(position);
    }

    @Override
    public Type validate(SemanticContext sc) {
        type = Type.STRING;
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        return CallHelper.call(u, ReadLineNative.NAME, null, new ArrayList<>());
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
