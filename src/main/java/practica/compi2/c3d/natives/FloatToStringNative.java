package practica.compi2.c3d.natives;

import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.IntegerCast3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.MemoryHelper;

public class FloatToStringNative extends NativeFunction {

    public static final String NAME = "nativeDecimalACadena";

    @Override
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 2);
        TemporalAccess number = parameter(utils, 1);
        TemporalAccess temp = MemoryHelper.load(utils, PointerAccess.HEAP);
        int positiveLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(number, new Literal3D(0), positiveLabel, ">="));
        MemoryHelper.pushToHeap(utils, new Literal3D(45));
        utils.getCuartetas().add(new Operation3D(number, new Literal3D(0), number, "-"));
        utils.getCuartetas().add(new LabelDefinition3D(positiveLabel));

        TemporalAccess integerPart = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess cents = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new IntegerCast3D(integerPart, number));
        utils.getCuartetas().add(new Operation3D(cents, number, integerPart, "-"));
        utils.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(100), "*"));
        utils.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(0.5), "+"));
        utils.getCuartetas().add(new IntegerCast3D(cents, cents));
        int noCarryLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(cents, new Literal3D(100), noCarryLabel, "<"));
        utils.getCuartetas().add(new Operation3D(integerPart, integerPart, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(100), "-"));
        utils.getCuartetas().add(new LabelDefinition3D(noCarryLabel));

        CallHelper.call(utils, IntToStringNative.NAME, null, List.of(integerPart));
        utils.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, new Literal3D(1), "-"));
        MemoryHelper.pushToHeap(utils, new Literal3D(46));
        writeCents(utils, cents);
        MemoryHelper.pushToHeap(utils, new Literal3D(0));
        MemoryHelper.checkHeapLimit(utils);
        returnValue(utils, temp);
        utils.endFunction();
    }

    private void writeCents(IntermediateCodeUtils utils, TemporalAccess cents) {
        TemporalAccess tens = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess units = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Operation3D(tens, cents, new Literal3D(10), "/"));
        utils.getCuartetas().add(new IntegerCast3D(tens, tens));
        utils.getCuartetas().add(new Operation3D(units, tens, new Literal3D(10), "*"));
        utils.getCuartetas().add(new Operation3D(units, cents, units, "-"));
        utils.getCuartetas().add(new Operation3D(tens, tens, new Literal3D(48), "+"));
        utils.getCuartetas().add(new Operation3D(units, units, new Literal3D(48), "+"));
        MemoryHelper.pushToHeap(utils, tens);
        MemoryHelper.pushToHeap(utils, units);
    }
}
