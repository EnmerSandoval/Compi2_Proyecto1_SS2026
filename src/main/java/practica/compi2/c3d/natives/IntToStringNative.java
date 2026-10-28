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
    public void generate(IntermediateCodeUtils utils) {
        utils.startFunction(NAME, 2);
        TemporalAccess number = parameter(utils, 1);
        TemporalAccess temp = MemoryHelper.load(utils, PointerAccess.HEAP);
        int positiveLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(number, new Literal3D(0), positiveLabel, ">="));
        MemoryHelper.pushToHeap(utils, new Literal3D(45));
        utils.getCuartetas().add(new Operation3D(number, new Literal3D(0), number, "-"));
        utils.getCuartetas().add(new LabelDefinition3D(positiveLabel));
        utils.getCuartetas().add(new IntegerCast3D(number, number));
        TemporalAccess divisor = findDivisor(utils, number);
        writeDigits(utils, number, divisor);
        MemoryHelper.pushToHeap(utils, new Literal3D(0));
        MemoryHelper.checkHeapLimit(utils);
        returnValue(utils, temp);
        utils.endFunction();
    }

    private TemporalAccess findDivisor(IntermediateCodeUtils utils, TemporalAccess number) {
        TemporalAccess divisor = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess quotient = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Assignation3D(divisor, new Literal3D(1)));
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Operation3D(quotient, number, divisor, "/"));
        utils.getCuartetas().add(new Conditional3D(quotient, new Literal3D(10), endLabel, "<"));
        utils.getCuartetas().add(new Operation3D(divisor, divisor, new Literal3D(10), "*"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return divisor;
    }

    private void writeDigits(IntermediateCodeUtils utils, TemporalAccess number, TemporalAccess divisor) {
        TemporalAccess digit = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess code = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess used = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Conditional3D(divisor, new Literal3D(1), endLabel, "<"));
        utils.getCuartetas().add(new Operation3D(digit, number, divisor, "/"));
        utils.getCuartetas().add(new IntegerCast3D(digit, digit));
        utils.getCuartetas().add(new Operation3D(code, digit, new Literal3D(48), "+"));
        MemoryHelper.pushToHeap(utils, code);
        utils.getCuartetas().add(new Operation3D(used, digit, divisor, "*"));
        utils.getCuartetas().add(new Operation3D(number, number, used, "-"));
        utils.getCuartetas().add(new Operation3D(divisor, divisor, new Literal3D(10), "/"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }
}
