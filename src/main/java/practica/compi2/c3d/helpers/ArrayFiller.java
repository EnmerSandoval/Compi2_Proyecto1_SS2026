package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.semantic.types.Type;

public class ArrayFiller {

    private final IntermediateCodeUtils utils;
    private final TemporalAccess ptr;
    private final int dimensionCount;
    private final Type elementType;

    public ArrayFiller(IntermediateCodeUtils utils, TemporalAccess ptr, int dimensionCount, Type elementType) {
        this.utils = utils;
        this.ptr = ptr;
        this.dimensionCount = dimensionCount;
        this.elementType = elementType;
    }

    public void fill(InitializerList list, List<MemoryAccess> path) {
        List<Expression> elements = list.getElements();
        for (int i = 0; i < elements.size(); i++) {
            List<MemoryAccess> elementPath = new ArrayList<>(path);
            elementPath.add(new Literal3D(i));
            Expression element = elements.get(i);
            if (elementPath.size() < dimensionCount && element instanceof InitializerList) {
                fill((InitializerList) element, elementPath);
            } else if (elementPath.size() == dimensionCount) {
                store(element, elementPath);
            }
        }
    }

    private void store(Expression element, List<MemoryAccess> elementPath) {
        MemoryAccess value = InitializerHelper.valueOf(utils, element, elementType);
        MemoryAccess address = ArrayHelper.elementAddress(utils, ptr, elementPath, dimensionCount);
        utils.getCuartetas().add(new Assignation3D(address, value));
    }
}
