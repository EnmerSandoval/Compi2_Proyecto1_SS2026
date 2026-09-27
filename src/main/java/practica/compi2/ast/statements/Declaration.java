package practica.compi2.ast.statements;

import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.helpers.DefaultValueHelper;
import practica.compi2.c3d.helpers.InitializerHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.AssignmentChecker;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class Declaration extends Statement {

    private TypeReference type;
    private String id;
    private List<Expression> sizes;
    private Expression value;
    private Symbol symbol;

    public Declaration(Position position, TypeReference type, String id, List<Expression> sizes, Expression value) {
        super(position);
        this.type = type;
        this.id = id;
        this.sizes = sizes;
        this.value = value;
    }

    public boolean isArray() {
        return !sizes.isEmpty();
    }

    @Override
    public void validate(SemanticContext sc) {
        checkSizes(sc);
        Type declaredType;
        if (type == null) {
            declaredType = inferType(sc);
        } else {
            declaredType = sc.resolveType(type);
            if (value != null) {
                new AssignmentChecker(sc).check(value, declaredType, sizes);
            }
        }
        symbol = sc.declareVariable(id, declaredType, position);
    }

    private Type inferType(SemanticContext sc) {
        if (value == null) {
            sc.error(position, id, "La variable '" + id + "' necesita un tipo o un valor inicial");
            return Type.ERROR;
        }
        if (value instanceof InitializerList) {
            sc.error(position, id, "No se puede deducir el tipo de '" + id + "' a partir de una lista {...}");
            return Type.ERROR;
        }
        Type inferred = value.validate(sc);
        if (inferred.isVoid() || inferred.isNull()) {
            sc.error(position, id, "No se puede deducir el tipo de '" + id + "'");
            return Type.ERROR;
        }
        return inferred;
    }

    private void checkSizes(SemanticContext sc) {
        for (Expression size : sizes) {
            Type sizeType = size.validate(sc);
            if (!sizeType.isError() && !sizeType.is(TypeKind.INTEGER)) {
                sc.error(size.getPosition(), id, "El tamaño del arreglo '" + id + "' debe ser entero");
            }
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess initialValue = createInitialValue(u);
        u.getCuartetas().add(new Assignation3D(MemoryHelper.stackSlot(u, symbol.getOffset()), initialValue));
    }

    private MemoryAccess createInitialValue(IntermediateCodeUtils u) {
        if (value instanceof InitializerList) {
            return InitializerHelper.build(u, (InitializerList) value, symbol.getType(), sizes);
        }
        if (value != null) {
            return value.toIntermediateCode(u);
        }
        return DefaultValueHelper.create(u, symbol.getType(), sizes);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", type);
        printer.value("id", id);
        printer.children("dimensiones", sizes);
        printer.child("valor", value);
    }
}
