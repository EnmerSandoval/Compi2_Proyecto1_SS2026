package practica.compi2.ast.expressions.operations;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

public class NotOperation extends UnaryOperation {

    public NotOperation(Position position, Expression operand) {
        super(position, operand, "!");
    }

    @Override
    protected Type resultType(TypeRules rules, Type operandType) {
        return rules.not(operandType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess value = operand.toIntermediateCode(u);
        TemporalAccess temp = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(temp, new Literal3D(1), value, "-"));
        return temp;
    }
}
