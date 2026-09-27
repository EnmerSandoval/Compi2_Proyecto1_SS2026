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
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 2);
        TemporalAccess number = parameter(u, 1);
        TemporalAccess temp = MemoryHelper.load(u, PointerAccess.HEAP);
        int positiveLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(number, new Literal3D(0), positiveLabel, ">="));
        MemoryHelper.pushToHeap(u, new Literal3D(45));
        u.getCuartetas().add(new Operation3D(number, new Literal3D(0), number, "-"));
        u.getCuartetas().add(new LabelDefinition3D(positiveLabel));

        TemporalAccess integerPart = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess cents = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new IntegerCast3D(integerPart, number));
        u.getCuartetas().add(new Operation3D(cents, number, integerPart, "-"));
        u.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(100), "*"));
        u.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(0.5), "+"));
        u.getCuartetas().add(new IntegerCast3D(cents, cents));
        int noCarryLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(cents, new Literal3D(100), noCarryLabel, "<"));
        u.getCuartetas().add(new Operation3D(integerPart, integerPart, new Literal3D(1), "+"));
        u.getCuartetas().add(new Operation3D(cents, cents, new Literal3D(100), "-"));
        u.getCuartetas().add(new LabelDefinition3D(noCarryLabel));

        CallHelper.call(u, IntToStringNative.NAME, null, List.of(integerPart));
        u.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, new Literal3D(1), "-"));
        MemoryHelper.pushToHeap(u, new Literal3D(46));
        writeCents(u, cents);
        MemoryHelper.pushToHeap(u, new Literal3D(0));
        returnValue(u, temp);
        u.endFunction();
    }

    private void writeCents(IntermediateCodeUtils u, TemporalAccess cents) {
        TemporalAccess tens = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess units = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(tens, cents, new Literal3D(10), "/"));
        u.getCuartetas().add(new IntegerCast3D(tens, tens));
        u.getCuartetas().add(new Operation3D(units, tens, new Literal3D(10), "*"));
        u.getCuartetas().add(new Operation3D(units, cents, units, "-"));
        u.getCuartetas().add(new Operation3D(tens, tens, new Literal3D(48), "+"));
        u.getCuartetas().add(new Operation3D(units, units, new Literal3D(48), "+"));
        MemoryHelper.pushToHeap(u, tens);
        MemoryHelper.pushToHeap(u, units);
    }
}
