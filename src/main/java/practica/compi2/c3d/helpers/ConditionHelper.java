package practica.compi2.c3d.helpers;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;

public class ConditionHelper {

    public static void jumpIfTrue(IntermediateCodeUtils u, MemoryAccess condition, int trueLabel, int falseLabel) {
        u.getCuartetas().add(new Conditional3D(condition, new Literal3D(1), trueLabel, "=="));
        u.getCuartetas().add(new Goto3D(falseLabel));
    }

    public static TemporalAccess compare(IntermediateCodeUtils u, MemoryAccess left, MemoryAccess right,
            String operator) {
        TemporalAccess temp = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int trueLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(left, right, trueLabel, operator));
        u.getCuartetas().add(new Assignation3D(temp, new Literal3D(0)));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(trueLabel));
        u.getCuartetas().add(new Assignation3D(temp, new Literal3D(1)));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
