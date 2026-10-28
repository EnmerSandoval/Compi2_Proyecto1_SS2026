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
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.types.Type;

public class InitializerHelper {

    public static MemoryAccess build(IntermediateCodeUtils utils, InitializerList list, Type type,
            List<Expression> sizes) {
        if (type.isStruct()) {
            return buildStruct(utils, list, utils.getStruct(type.getName()));
        }
        return buildArray(utils, list, type, sizes);
    }

    private static MemoryAccess buildStruct(IntermediateCodeUtils utils, InitializerList list, StructSymbol struct) {
        int size = utils.getTypeTable().sizeOf(struct.getName());
        TemporalAccess ptr = MemoryHelper.allocate(utils, new Literal3D(size));
        List<Expression> elements = list.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Type fieldType = struct.getFields().get(i).getType();
            MemoryAccess value = valueOf(utils, elements.get(i), fieldType);
            utils.getCuartetas().add(new Assignation3D(MemoryHelper.heapSlot(utils, ptr, new Literal3D(i)), value));
        }
        return ptr;
    }

    private static MemoryAccess buildArray(IntermediateCodeUtils utils, InitializerList list, Type type,
            List<Expression> sizes) {
        List<MemoryAccess> dimensions = new ArrayList<>();
        if (sizes.isEmpty()) {
            dimensions = shapeOf(list, type.getDimensions());
        } else {
            for (Expression size : sizes) {
                dimensions.add(size.toIntermediateCode(utils));
            }
        }
        TemporalAccess ptr = ArrayHelper.allocate(utils, dimensions);
        ArrayFiller filler = new ArrayFiller(utils, ptr, dimensions.size(), type.withDimensions(0));
        filler.fill(list, new ArrayList<>());
        return ptr;
    }

    public static MemoryAccess valueOf(IntermediateCodeUtils utils, Expression element, Type type) {
        if (element instanceof InitializerList) {
            return build(utils, (InitializerList) element, type, new ArrayList<>());
        }
        return copyOf(utils, element.toIntermediateCode(utils), type);
    }

    public static MemoryAccess copyOf(IntermediateCodeUtils utils, MemoryAccess value, Type type) {
        if (type.isArray()) {
            return CopyHelper.cloneArray(utils, value, type);
        }
        if (type.isStruct()) {
            return CopyHelper.cloneStruct(utils, value, utils.getStruct(type.getName()));
        }
        return value;
    }

    private static List<MemoryAccess> shapeOf(InitializerList list, int dimensionCount) {
        List<MemoryAccess> shape = new ArrayList<>();
        Expression current = list;
        for (int i = 0; i < dimensionCount; i++) {
            int size = 0;
            if (current instanceof InitializerList) {
                List<Expression> elements = ((InitializerList) current).getElements();
                size = elements.size();
                current = elements.isEmpty() ? null : elements.get(0);
            }
            shape.add(new Literal3D(size));
        }
        return shape;
    }
}
