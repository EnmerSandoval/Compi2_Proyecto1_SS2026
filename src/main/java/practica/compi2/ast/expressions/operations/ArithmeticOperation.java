package practica.compi2.ast.expressions.operations;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.IntegerCast3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;
import practica.compi2.semantic.types.TypeRules;

public class ArithmeticOperation extends Operation {

    public ArithmeticOperation(Position position, Expression left, Expression right, String operator) {
        super(position, left, right, operator);
    }

    @Override
    protected Type resultType(TypeRules rules, Type leftType, Type rightType) {
        return rules.arithmetic(operator, leftType, rightType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        MemoryAccess leftMemoryAccess = left.toIntermediateCode(utils);
        MemoryAccess rightMemoryAccess = right.toIntermediateCode(utils);
        return operate(utils, leftMemoryAccess, rightMemoryAccess);
    }

    public MemoryAccess operate(IntermediateCodeUtils utils, MemoryAccess leftMemoryAccess, MemoryAccess rightMemoryAccess) {
        if (type.isString()) {
            MemoryAccess leftText = StringHelper.toText(utils, leftMemoryAccess, left.getType());
            MemoryAccess rightText = StringHelper.toText(utils, rightMemoryAccess, right.getType());
            return StringHelper.concat(utils, leftText, rightText);
        }
        TemporalAccess temp = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Operation3D(temp, leftMemoryAccess, rightMemoryAccess, operator));
        if (operator.equals("/") && type.is(TypeKind.INTEGER)) {
            utils.getCuartetas().add(new IntegerCast3D(temp, temp));
        }
        return temp;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }
}
