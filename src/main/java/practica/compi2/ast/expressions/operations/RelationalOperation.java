package practica.compi2.ast.expressions.operations;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

public class RelationalOperation extends Operation {

    public RelationalOperation(Position position, Expression left, Expression right, String operator) {
        super(position, left, right, operator);
    }

    @Override
    protected Type resultType(TypeRules rules, Type leftType, Type rightType) {
        return rules.relational(operator, leftType, rightType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess leftMemoryAccess = left.toIntermediateCode(u);
        MemoryAccess rightMemoryAccess = right.toIntermediateCode(u);
        if (left.getType().isString() && right.getType().isString()) {
            TemporalAccess equal = StringHelper.equalsText(u, leftMemoryAccess, rightMemoryAccess);
            if (operator.equals("!=")) {
                TemporalAccess different = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
                u.getCuartetas().add(new Operation3D(different, new Literal3D(1), equal, "-"));
                return different;
            }
            return equal;
        }
        return ConditionHelper.compare(u, leftMemoryAccess, rightMemoryAccess, operator);
    }
}
