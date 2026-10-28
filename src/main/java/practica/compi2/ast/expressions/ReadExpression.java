package practica.compi2.ast.expressions;

import java.util.ArrayList;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.c3d.natives.ReadLineNative;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class ReadExpression extends Expression {

    public ReadExpression(Position position) {
        super(position);
    }

    @Override
    public Type validate(SemanticContext context) {
        type = Type.STRING;
        return type;
    }

    public boolean convertTo(Type target) {
        if (target.getDimensions() != 0
                || !(target.is(TypeKind.INTEGER) || target.is(TypeKind.FLOAT) || target.is(TypeKind.CHAR))) {
            return false;
        }
        type = target;
        return true;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        MemoryAccess text = CallHelper.call(utils, ReadLineNative.NAME, null, new ArrayList<>());
        return StringHelper.fromText(utils, text, type);
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
