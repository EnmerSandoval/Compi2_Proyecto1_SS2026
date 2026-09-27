package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Operation3D;

public class StringToIntNative extends NativeFunction {

    public static final String NAME = "nativeCadenaAEntero";

    @Override
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 2);
        TemporalAccess ptr = parameter(u, 1);
        TemporalAccess sign = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess value = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        readSign(u, ptr, sign);
        readIntegerPart(u, ptr, value);
        u.getCuartetas().add(new Operation3D(value, value, sign, "*"));
        returnValue(u, value);
        u.endFunction();
    }
}
