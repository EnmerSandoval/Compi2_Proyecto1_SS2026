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
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 3);
        TemporalAccess first = parameter(utils, 1);
        TemporalAccess second = parameter(utils, 2);
        TemporalAccess firstChar = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess secondChar = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int equalLabel = utils.getCounter().getAndIncrementLabelCount();
        int differentLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();

        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Assignation3D(firstChar, new HeapAccess(first)));
        utils.getCuartetas().add(new Assignation3D(secondChar, new HeapAccess(second)));
        utils.getCuartetas().add(new Conditional3D(firstChar, secondChar, differentLabel, "!="));
        utils.getCuartetas().add(new Conditional3D(firstChar, new Literal3D(0), equalLabel, "=="));
        utils.getCuartetas().add(new Operation3D(first, first, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Operation3D(second, second, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));

        utils.getCuartetas().add(new LabelDefinition3D(equalLabel));
        returnValue(utils, new Literal3D(1));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(differentLabel));
        returnValue(utils, new Literal3D(0));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        utils.endFunction();
    }
}
