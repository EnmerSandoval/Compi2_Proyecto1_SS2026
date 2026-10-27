package practica.compi2.c3d;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.Operation3D;

public class TemporalSaver {

    private final IdsCounter counter;
    private final List<CallSite> calls;

    public TemporalSaver(IdsCounter counter) {
        this.counter = counter;
        this.calls = new ArrayList<>();
    }

    public void addCall(CallSite call) {
        calls.add(call);
    }

    public void saveAroundCalls(List<Cuarteta> cuartetas, int functionStart) {
        for (int i = calls.size() - 1; i >= 0; i--) {
            saveAround(cuartetas, functionStart, calls.get(i));
        }
        calls.clear();
    }

    private void saveAround(List<Cuarteta> cuartetas, int functionStart, CallSite call) {
        int start = cuartetas.indexOf(call.getStart());
        int end = cuartetas.indexOf(call.getEnd());
        TreeSet<Integer> live = temporalsBetween(cuartetas, functionStart, start);
        live.retainAll(temporalsBetween(cuartetas, end + 1, cuartetas.size()));
        call.getOffset().setValue(call.getFrameSize() + live.size() + 1);

        List<Cuarteta> saves = new ArrayList<>();
        List<Cuarteta> restores = new ArrayList<>();
        int position = call.getFrameSize();
        for (Integer temporal : live) {
            TemporalAccess value = new TemporalAccess(temporal);
            saves.add(new Assignation3D(stackSlot(saves, position), value));
            restores.add(new Assignation3D(value, stackSlot(restores, position)));
            position++;
        }
        cuartetas.addAll(end + 1, restores);
        cuartetas.addAll(start, saves);
    }

    private StackAccess stackSlot(List<Cuarteta> code, int position) {
        TemporalAccess address = new TemporalAccess(counter.getAndIncrementTemporalCount());
        code.add(new Operation3D(address, PointerAccess.STACK, new Literal3D(position), "+"));
        return new StackAccess(address);
    }

    private TreeSet<Integer> temporalsBetween(List<Cuarteta> cuartetas, int from, int to) {
        TreeSet<Integer> temporals = new TreeSet<>();
        for (int i = from; i < to; i++) {
            cuartetas.get(i).collectTemporals(temporals);
        }
        return temporals;
    }
}
