package practica.compi2.ast.expressions.operations;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

public class AndOperation extends Operation {

    public AndOperation(Position position, Expression left, Expression right) {
        super(position, left, right, "&&");
    }

    @Override
    protected Type resultType(TypeRules rules, Type leftType, Type rightType) {
        return rules.logical(leftType, rightType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        TemporalAccess temp = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int checkRightLabel = u.getCounter().getAndIncrementLabelCount();
        int trueLabel = u.getCounter().getAndIncrementLabelCount();
        int falseLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();

        MemoryAccess leftMemoryAccess = left.toIntermediateCode(u);
        ConditionHelper.jumpIfTrue(u, leftMemoryAccess, checkRightLabel, falseLabel);
        u.getCuartetas().add(new LabelDefinition3D(checkRightLabel));
        MemoryAccess rightMemoryAccess = right.toIntermediateCode(u);
        ConditionHelper.jumpIfTrue(u, rightMemoryAccess, trueLabel, falseLabel);

        u.getCuartetas().add(new LabelDefinition3D(trueLabel));
        u.getCuartetas().add(new Assignation3D(temp, new Literal3D(1)));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(falseLabel));
        u.getCuartetas().add(new Assignation3D(temp, new Literal3D(0)));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
