package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.c3d.CallSite;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.FrameOffset3D;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Call3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;

public class CallHelper {

    public static TemporalAccess call(IntermediateCodeUtils utils, String name, MemoryAccess thisPointer,
            List<MemoryAccess> arguments) {
        int frameSize = utils.getFrameSize();
        FrameOffset3D offset = new FrameOffset3D(frameSize + 1);
        TemporalAccess newFrame = newTemporal(utils);
        Operation3D start = new Operation3D(newFrame, PointerAccess.STACK, offset, "+");
        utils.getCuartetas().add(start);
        storeValues(utils, newFrame, thisPointer, arguments);
        int returnLabel = storeReturnLabel(utils, newFrame);
        utils.getCuartetas().add(new Operation3D(PointerAccess.STACK, PointerAccess.STACK, offset, "+"));
        utils.getCuartetas().add(new Call3D(name, returnLabel));
        utils.getCuartetas().add(new LabelDefinition3D(returnLabel));
        TemporalAccess result = MemoryHelper.load(utils, new StackAccess(PointerAccess.STACK));
        Operation3D end = new Operation3D(PointerAccess.STACK, PointerAccess.STACK, offset, "-");
        utils.getCuartetas().add(end);
        utils.registerCall(name, returnLabel, new CallSite(start, end, offset, frameSize));
        return result;
    }

    private static void storeValues(IntermediateCodeUtils utils, TemporalAccess frame, MemoryAccess thisPointer,
            List<MemoryAccess> arguments) {
        List<MemoryAccess> values = new ArrayList<>();
        if (thisPointer != null) {
            values.add(thisPointer);
        }
        values.addAll(arguments);
        for (int i = 0; i < values.size(); i++) {
            TemporalAccess address = newTemporal(utils);
            utils.getCuartetas().add(new Operation3D(address, frame, new Literal3D(i + 1), "+"));
            utils.getCuartetas().add(new Assignation3D(new StackAccess(address), values.get(i)));
        }
    }

    private static int storeReturnLabel(IntermediateCodeUtils utils, TemporalAccess frame) {
        int returnLabel = utils.getCounter().getAndIncrementLabelCount();
        TemporalAccess address = newTemporal(utils);
        utils.getCuartetas().add(new Operation3D(address, frame, new Literal3D(1), "-"));
        utils.getCuartetas().add(new Assignation3D(new StackAccess(address), new Literal3D(returnLabel)));
        return returnLabel;
    }

    private static TemporalAccess newTemporal(IntermediateCodeUtils utils) {
        return new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
    }
}
