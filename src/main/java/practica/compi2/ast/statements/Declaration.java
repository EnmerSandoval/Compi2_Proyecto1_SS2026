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
import practica.compi2.c3d.helpers.CopyHelper;
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
    public void validate(SemanticContext context) {
        checkSizes(context);
        Type declaredType;
        if (type == null) {
            declaredType = inferType(context);
        } else {
            declaredType = context.resolveType(type);
            if (value != null) {
                new AssignmentChecker(context).check(value, declaredType, sizes);
            }
        }
        symbol = context.declareVariable(id, declaredType, position);
    }

    private Type inferType(SemanticContext context) {
        if (value == null) {
            context.error(position, id, "La variable '" + id + "' necesita un tipo o un valor inicial");
            return Type.ERROR;
        }
        if (value instanceof InitializerList) {
            context.error(position, id, "No se puede deducir el tipo de '" + id + "' a partir de una lista {...}");
            return Type.ERROR;
        }
        Type inferred = value.validate(context);
        if (inferred.isVoid() || inferred.isNull()) {
            context.error(position, id, "No se puede deducir el tipo de '" + id + "'");
            return Type.ERROR;
        }
        return inferred;
    }

    private void checkSizes(SemanticContext context) {
        for (Expression size : sizes) {
            Type sizeType = size.validate(context);
            if (!sizeType.isError() && !sizeType.is(TypeKind.INTEGER)) {
                context.error(size.getPosition(), id, "El tamaño del arreglo '" + id + "' debe ser entero");
            }
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        MemoryAccess initialValue = createInitialValue(utils);
        utils.getCuartetas().add(new Assignation3D(MemoryHelper.stackSlot(utils, symbol.getOffset()), initialValue));
    }

    private MemoryAccess createInitialValue(IntermediateCodeUtils utils) {
        if (value instanceof InitializerList) {
            return InitializerHelper.build(utils, (InitializerList) value, symbol.getType(), sizes);
        }
        if (value != null && isStructValue(symbol.getType())) {
            return CopyHelper.cloneStruct(utils, value.toIntermediateCode(utils), utils.getStruct(symbol.getType().getName()));
        }
        if (value != null) {
            return value.toIntermediateCode(utils);
        }
        return DefaultValueHelper.create(utils, symbol.getType(), sizes);
    }

    private boolean isStructValue(Type type) {
        return type.isStruct() && type.getDimensions() == 0;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", type);
        printer.value("id", id);
        printer.children("dimensiones", sizes);
        printer.child("valor", value);
    }
}
