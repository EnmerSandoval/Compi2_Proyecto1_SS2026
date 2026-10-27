package practica.compi2.ast.statements;

import java.util.ArrayList;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.expressions.operations.ArithmeticOperation;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.helpers.CopyHelper;
import practica.compi2.c3d.helpers.InitializerHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.AssignmentChecker;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class Assignation extends Statement {

    private Expression target;
    private String operator;
    private Expression value;
    private ArithmeticOperation compound;

    public Assignation(Position position, Expression target, String operator, Expression value) {
        super(position);
        this.target = target;
        this.operator = operator;
        this.value = value;
        if (!operator.equals("=")) {
            this.compound = new ArithmeticOperation(position, target, value, operator.substring(0, 1));
        }
    }

    @Override
    public void validate(SemanticContext sc) {
        if (compound != null) {
            validateCompound(sc);
            return;
        }
        Type targetType = target.validate(sc);
        checkAssignable(sc);
        new AssignmentChecker(sc).check(value, targetType, new ArrayList<>());
    }

    private void validateCompound(SemanticContext sc) {
        Type operationType = compound.validate(sc);
        checkAssignable(sc);
        Type targetType = target.getType();
        if (!operationType.isError() && !sc.getRules().canAssign(targetType, operationType)) {
            sc.error(position, operator, "No se puede aplicar '" + operator + "' entre "
                    + targetType + " y " + value.getType());
        }
    }

    private void checkAssignable(SemanticContext sc) {
        if (!target.isAssignable()) {
            sc.error(position, operator, "Solo se puede asignar a variables, campos o posiciones de un arreglo");
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess location = target.toAddress(u);
        MemoryAccess newValue = createValue(u, location);
        u.getCuartetas().add(new Assignation3D(location, newValue));
    }

    private MemoryAccess createValue(IntermediateCodeUtils u, MemoryAccess location) {
        if (value instanceof InitializerList) {
            return InitializerHelper.build(u, (InitializerList) value, target.getType(), new ArrayList<>());
        }
        MemoryAccess access = value.toIntermediateCode(u);
        if (compound == null && target.getType().isStruct() && target.getType().getDimensions() == 0) {
            MemoryAccess current = MemoryHelper.load(u, location);
            CopyHelper.copyStruct(u, current, access, u.getStruct(target.getType().getName()));
            return current;
        }
        if (compound == null) {
            return access;
        }
        MemoryAccess current = MemoryHelper.load(u, location);
        return compound.operate(u, current, access);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("destino", target);
        printer.value("operador", operator);
        printer.child("valor", value);
    }
}
