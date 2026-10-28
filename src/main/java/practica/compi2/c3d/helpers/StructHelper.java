package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;

public class StructHelper {

    public static TemporalAccess allocate(IntermediateCodeUtils utils, StructSymbol struct) {
        return allocate(utils, struct, new ArrayList<>());
    }

    private static TemporalAccess allocate(IntermediateCodeUtils utils, StructSymbol struct, List<String> chain) {
        List<Symbol> fields = struct.getFields();
        List<Declaration> declarations = struct.getDefinition().getFields();
        int size = utils.getTypeTable().sizeOf(struct.getName());
        TemporalAccess ptr = MemoryHelper.allocate(utils, new Literal3D(size));
        chain.add(struct.getName());
        for (int i = 0; i < fields.size(); i++) {
            MemoryAccess value = defaultField(utils, fields.get(i).getType(), declarations.get(i), chain);
            if (value != null) {
                utils.getCuartetas().add(new Assignation3D(MemoryHelper.heapSlot(utils, ptr, new Literal3D(i)), value));
            }
        }
        chain.remove(chain.size() - 1);
        return ptr;
    }

    private static MemoryAccess defaultField(IntermediateCodeUtils utils, Type type, Declaration declaration,
            List<String> chain) {
        if (declaration.isArray()) {
            List<MemoryAccess> dimensions = new ArrayList<>();
            for (Expression size : declaration.getSizes()) {
                dimensions.add(size.toIntermediateCode(utils));
            }
            return ArrayHelper.allocate(utils, dimensions);
        }
        if (type.isStruct() && !chain.contains(type.getName())) {
            return allocate(utils, utils.getStruct(type.getName()), chain);
        }
        return null;
    }
}
