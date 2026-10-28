package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Operation3D;

public class StringToIntNative extends NativeFunction {

    public static final String NAME = "nativeCadenaAEntero";

    @Override
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 2);
        TemporalAccess ptr = parameter(utils, 1);
        TemporalAccess sign = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess value = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        readSign(utils, ptr, sign);
        readIntegerPart(utils, ptr, value);
        utils.getCuartetas().add(new Operation3D(value, value, sign, "*"));
        returnValue(utils, value);
        utils.endFunction();
    }
}
