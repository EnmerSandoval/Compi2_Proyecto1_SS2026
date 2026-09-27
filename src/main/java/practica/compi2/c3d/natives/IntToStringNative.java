package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.IntegerCast3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.MemoryHelper;

public class IntToStringNative extends NativeFunction {

    public static final String NAME = "nativeEnteroACadena";

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
        u.getCuartetas().add(new IntegerCast3D(number, number));
        TemporalAccess divisor = findDivisor(u, number);
        writeDigits(u, number, divisor);
        MemoryHelper.pushToHeap(u, new Literal3D(0));
        returnValue(u, temp);
        u.endFunction();
    }

    private TemporalAccess findDivisor(IntermediateCodeUtils u, TemporalAccess number) {
        TemporalAccess divisor = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess quotient = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Assignation3D(divisor, new Literal3D(1)));
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Operation3D(quotient, number, divisor, "/"));
        u.getCuartetas().add(new Conditional3D(quotient, new Literal3D(10), endLabel, "<"));
        u.getCuartetas().add(new Operation3D(divisor, divisor, new Literal3D(10), "*"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return divisor;
    }

    private void writeDigits(IntermediateCodeUtils u, TemporalAccess number, TemporalAccess divisor) {
        TemporalAccess digit = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess code = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess used = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Conditional3D(divisor, new Literal3D(1), endLabel, "<"));
        u.getCuartetas().add(new Operation3D(digit, number, divisor, "/"));
        u.getCuartetas().add(new IntegerCast3D(digit, digit));
        u.getCuartetas().add(new Operation3D(code, digit, new Literal3D(48), "+"));
        MemoryHelper.pushToHeap(u, code);
        u.getCuartetas().add(new Operation3D(used, digit, divisor, "*"));
        u.getCuartetas().add(new Operation3D(number, number, used, "-"));
        u.getCuartetas().add(new Operation3D(divisor, divisor, new Literal3D(10), "/"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }
}
