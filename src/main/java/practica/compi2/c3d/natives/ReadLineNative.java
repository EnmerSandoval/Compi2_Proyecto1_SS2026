package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.ReadChar3D;
import practica.compi2.c3d.helpers.MemoryHelper;

public class ReadLineNative extends NativeFunction {

    public static final String NAME = "nativeLeerLinea";

    @Override
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 1);
        TemporalAccess temp = MemoryHelper.load(utils, PointerAccess.HEAP);
        TemporalAccess character = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new ReadChar3D(character));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(10), endLabel, "=="));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(-1), endLabel, "=="));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(13), loopLabel, "=="));
        MemoryHelper.pushToHeap(utils, character);
        MemoryHelper.checkHeapLimit(utils);
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        MemoryHelper.pushToHeap(utils, new Literal3D(0));
        returnValue(utils, temp);
        utils.endFunction();
    }
}
