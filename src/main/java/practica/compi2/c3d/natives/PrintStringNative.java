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
import practica.compi2.c3d.cuartetas.Print3D;

public class PrintStringNative extends NativeFunction {

    public static final String NAME = "nativeImprimirCadena";

    @Override
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 2);
        TemporalAccess ptr = parameter(utils, 1);
        TemporalAccess character = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(0), endLabel, "=="));
        utils.getCuartetas().add(new Print3D(character, "%c"));
        utils.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        utils.endFunction();
    }
}
