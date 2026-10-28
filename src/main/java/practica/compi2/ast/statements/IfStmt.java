package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.SemanticContext;

@Getter
public class IfStmt extends Statement {

    private Expression condition;
    private Statement body;
    private Statement elseBranch;

    public IfStmt(Position position, Expression condition, Statement body, Statement elseBranch) {
        super(position);
        this.condition = condition;
        this.body = body;
        this.elseBranch = elseBranch;
    }

    @Override
    public void validate(SemanticContext context) {
        context.checkCondition(condition, "si");
        body.validate(context);
        if (elseBranch != null) {
            elseBranch.validate(context);
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int falseLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        MemoryAccess conditionAccess = condition.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, conditionAccess, trueLabel, falseLabel);
        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        body.toIntermediateCode(utils);
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(falseLabel));
        if (elseBranch != null) {
            elseBranch.toIntermediateCode(utils);
        }
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("condicion", condition);
        printer.child("cuerpo", body);
        printer.child("sino", elseBranch);
    }
}
