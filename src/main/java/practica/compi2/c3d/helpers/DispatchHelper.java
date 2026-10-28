package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.FunctionEnd3D;
import practica.compi2.c3d.cuartetas.FunctionJump3D;
import practica.compi2.c3d.cuartetas.FunctionStart3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.FunctionSymbol;

public class DispatchHelper {

    public static String target(IntermediateCodeUtils utils, ClassSymbol staticClass, FunctionSymbol method) {
        FunctionSymbol base = staticClass.findImplementation(method);
        if (!method.getAccess().isInheritable()) {
            return base.getCName();
        }
        Map<ClassSymbol, FunctionSymbol> overrides = overrides(utils, staticClass, base);
        if (overrides.isEmpty()) {
            return base.getCName();
        }
        String name = staticClass.getName() + "_virtual_" + base.getCName();
        if (!utils.getReturnLinker().hasDispatcher(name)) {
            generate(utils, name, base, overrides);
        }
        return name;
    }

    private static Map<ClassSymbol, FunctionSymbol> overrides(IntermediateCodeUtils utils, ClassSymbol staticClass,
            FunctionSymbol base) {
        Map<ClassSymbol, FunctionSymbol> overrides = new LinkedHashMap<>();
        for (ClassSymbol candidate : utils.getClasses()) {
            if (candidate == staticClass || !candidate.isSubclassOf(staticClass)) {
                continue;
            }
            FunctionSymbol implementation = candidate.findImplementation(base);
            if (implementation != base) {
                overrides.put(candidate, implementation);
            }
        }
        return overrides;
    }

    private static void generate(IntermediateCodeUtils utils, String name, FunctionSymbol base,
            Map<ClassSymbol, FunctionSymbol> overrides) {
        List<Cuarteta> code = new ArrayList<>();
        Set<String> targets = new LinkedHashSet<>();
        code.add(new FunctionStart3D(name, false, -1));
        TemporalAccess classId = loadClassId(utils, code);
        for (Map.Entry<ClassSymbol, FunctionSymbol> entry : overrides.entrySet()) {
            int next = utils.getCounter().getAndIncrementLabelCount();
            code.add(new Conditional3D(classId, new Literal3D(entry.getKey().getId()), next, "!="));
            code.add(new FunctionJump3D(entry.getValue().getCName()));
            code.add(new LabelDefinition3D(next));
            targets.add(entry.getValue().getCName());
        }
        code.add(new FunctionJump3D(base.getCName()));
        code.add(new FunctionEnd3D(name, false));
        targets.add(base.getCName());
        utils.getReturnLinker().addDispatcher(name, targets, code);
    }

    private static TemporalAccess loadClassId(IntermediateCodeUtils utils, List<Cuarteta> code) {
        TemporalAccess thisAddress = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess thisPointer = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess classId = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        code.add(new Operation3D(thisAddress, PointerAccess.STACK, new Literal3D(1), "+"));
        code.add(new Assignation3D(thisPointer, new StackAccess(thisAddress)));
        code.add(new Assignation3D(classId, new HeapAccess(thisPointer)));
        return classId;
    }
}
