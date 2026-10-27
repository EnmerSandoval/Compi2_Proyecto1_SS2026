package practica.compi2.c3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.FunctionEnd3D;

public class ReturnLinker {

    private final Map<String, List<Integer>> returnLabels;
    private final Map<FunctionEnd3D, TemporalAccess> returnAddresses;
    private final Map<String, Set<String>> dispatchTargets;
    private final List<Cuarteta> dispatchers;

    public ReturnLinker() {
        this.returnLabels = new HashMap<>();
        this.returnAddresses = new HashMap<>();
        this.dispatchTargets = new HashMap<>();
        this.dispatchers = new ArrayList<>();
    }

    public void addCall(String function, int returnLabel) {
        labelsOf(function).add(returnLabel);
    }

    public void addReturnAddress(FunctionEnd3D end, TemporalAccess address) {
        returnAddresses.put(end, address);
    }

    public boolean hasDispatcher(String name) {
        return dispatchTargets.containsKey(name);
    }

    public void addDispatcher(String name, Set<String> targets, List<Cuarteta> code) {
        dispatchTargets.put(name, targets);
        dispatchers.addAll(code);
    }

    public List<Cuarteta> link(List<Cuarteta> cuartetas) {
        List<Cuarteta> all = new ArrayList<>(cuartetas);
        all.addAll(dispatchers);
        shareDispatcherLabels();
        List<Cuarteta> linked = new ArrayList<>();
        for (Cuarteta cuarteta : all) {
            if (cuarteta instanceof FunctionEnd3D end && returnAddresses.containsKey(end)) {
                addReturnJumps(linked, end);
            }
            linked.add(cuarteta);
        }
        return linked;
    }

    private void shareDispatcherLabels() {
        for (String dispatcher : dispatchTargets.keySet()) {
            List<Integer> labels = returnLabels.getOrDefault(dispatcher, List.of());
            for (String target : dispatchTargets.get(dispatcher)) {
                labelsOf(target).addAll(labels);
            }
        }
    }

    private void addReturnJumps(List<Cuarteta> code, FunctionEnd3D end) {
        TemporalAccess address = returnAddresses.get(end);
        for (Integer label : returnLabels.getOrDefault(end.getName(), List.of())) {
            code.add(new Conditional3D(address, new Literal3D(label), label, "=="));
        }
    }

    private List<Integer> labelsOf(String function) {
        return returnLabels.computeIfAbsent(function, key -> new ArrayList<>());
    }
}
