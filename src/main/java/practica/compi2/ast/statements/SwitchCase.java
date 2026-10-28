package practica.compi2.ast.statements;

import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class SwitchCase extends AstNode {

    private Expression value;
    private List<Statement> statements;

    public SwitchCase(Position position, Expression value, List<Statement> statements) {
        super(position);
        this.value = value;
        this.statements = statements;
    }

    public boolean isDefault() {
        return value == null;
    }

    public void validate(SemanticContext context, Type switchType) {
        if (value != null) {
            Type valueType = value.validate(context);
            if (!value.isConstant()) {
                context.error(value.getPosition(), "", "El valor de un caso debe ser constante");
            } else if (!context.getRules().canCompare(switchType, valueType)) {
                context.error(value.getPosition(), "", "El caso de tipo " + valueType
                        + " no se puede comparar con " + switchType);
            }
        }
        for (Statement statement : statements) {
            statement.validate(context);
        }
    }

    public void jumpIfMatches(IntermediateCodeUtils utils, MemoryAccess switchValue, Type switchType, int label) {
        MemoryAccess caseValue = value.toIntermediateCode(utils);
        if (switchType.isString()) {
            MemoryAccess equal = StringHelper.equalsText(utils, switchValue, caseValue);
            utils.getCuartetas().add(new Conditional3D(equal, new Literal3D(1), label, "=="));
        } else {
            utils.getCuartetas().add(new Conditional3D(switchValue, caseValue, label, "=="));
        }
    }

    public void toIntermediateCode(IntermediateCodeUtils utils) {
        for (Statement statement : statements) {
            statement.toIntermediateCode(utils);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        if (isDefault()) {
            printer.value("caso", "por defecto");
        }
        printer.child("valor", value);
        printer.children("sentencias", statements);
    }
}
