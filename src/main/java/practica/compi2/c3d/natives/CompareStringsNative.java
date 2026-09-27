package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;

public class CompareStringsNative extends NativeFunction {

    public static final String NAME = "nativeCompararCadenas";

    @Override
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 3);
        TemporalAccess first = parameter(u, 1);
        TemporalAccess second = parameter(u, 2);
        TemporalAccess firstChar = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess secondChar = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int equalLabel = u.getCounter().getAndIncrementLabelCount();
        int differentLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();

        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Assignation3D(firstChar, new HeapAccess(first)));
        u.getCuartetas().add(new Assignation3D(secondChar, new HeapAccess(second)));
        u.getCuartetas().add(new Conditional3D(firstChar, secondChar, differentLabel, "!="));
        u.getCuartetas().add(new Conditional3D(firstChar, new Literal3D(0), equalLabel, "=="));
        u.getCuartetas().add(new Operation3D(first, first, new Literal3D(1), "+"));
        u.getCuartetas().add(new Operation3D(second, second, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));

        u.getCuartetas().add(new LabelDefinition3D(equalLabel));
        returnValue(u, new Literal3D(1));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(differentLabel));
        returnValue(u, new Literal3D(0));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        u.endFunction();
    }
}
