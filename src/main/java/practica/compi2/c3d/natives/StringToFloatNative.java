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
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 2);
        TemporalAccess ptr = parameter(u, 1);
        TemporalAccess sign = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess value = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        readSign(u, ptr, sign);
        TemporalAccess character = readIntegerPart(u, ptr, value);

        int endLabel = u.getCounter().getAndIncrementLabelCount();
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        TemporalAccess factor = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(46), endLabel, "!="));
        u.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        u.getCuartetas().add(new Assignation3D(factor, new Literal3D(0.1)));
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(48), endLabel, "<"));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(57), endLabel, ">"));
        u.getCuartetas().add(new Operation3D(character, character, new Literal3D(48), "-"));
        u.getCuartetas().add(new Operation3D(character, character, factor, "*"));
        u.getCuartetas().add(new Operation3D(value, value, character, "+"));
        u.getCuartetas().add(new Operation3D(factor, factor, new Literal3D(10), "/"));
        u.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));

        u.getCuartetas().add(new Operation3D(value, value, sign, "*"));
        returnValue(u, value);
        u.endFunction();
    }
}
