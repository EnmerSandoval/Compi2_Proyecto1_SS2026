package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.SemanticContext;

@Getter
public class DoWhileStmt extends Statement {

    private Statement body;
    private Expression condition;

    public DoWhileStmt(Position position, Statement body, Expression condition) {
        super(position);
        this.body = body;
        this.condition = condition;
    }

    @Override
    public void validate(SemanticContext context) {
        context.enterLoop();
        body.validate(context);
        context.exitLoop();
        context.checkCondition(condition, "hacer-mientras");
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        int bodyLabel = utils.getCounter().getAndIncrementLabelCount();
        int conditionLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        utils.enterLoop(conditionLabel, endLabel);
        body.toIntermediateCode(utils);
        utils.exitLoop();
        utils.getCuartetas().add(new LabelDefinition3D(conditionLabel));
        MemoryAccess conditionAccess = condition.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, conditionAccess, bodyLabel, endLabel);
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("cuerpo", body);
        printer.child("condicion", condition);
    }
}
