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

public class StringToFloatNative extends NativeFunction {

    public static final String NAME = "nativeCadenaADecimal";

    @Override
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 2);
        TemporalAccess ptr = parameter(utils, 1);
        TemporalAccess sign = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess value = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        readSign(utils, ptr, sign);
        TemporalAccess character = readIntegerPart(utils, ptr, value);

        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        TemporalAccess factor = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(46), endLabel, "!="));
        utils.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Assignation3D(factor, new Literal3D(0.1)));
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(48), endLabel, "<"));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(57), endLabel, ">"));
        utils.getCuartetas().add(new Operation3D(character, character, new Literal3D(48), "-"));
        utils.getCuartetas().add(new Operation3D(character, character, factor, "*"));
        utils.getCuartetas().add(new Operation3D(value, value, character, "+"));
        utils.getCuartetas().add(new Operation3D(factor, factor, new Literal3D(10), "/"));
        utils.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));

        utils.getCuartetas().add(new Operation3D(value, value, sign, "*"));
        returnValue(utils, value);
        utils.endFunction();
    }
}
