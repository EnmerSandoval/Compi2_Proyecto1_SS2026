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
public class WhileStmt extends Statement {

    private Expression condition;
    private Statement body;

    public WhileStmt(Position position, Expression condition, Statement body) {
        super(position);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void validate(SemanticContext context) {
        context.checkCondition(condition, "mientras");
        context.enterLoop();
        body.validate(context);
        context.exitLoop();
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        int startLabel = utils.getCounter().getAndIncrementLabelCount();
        int bodyLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new LabelDefinition3D(startLabel));
        MemoryAccess conditionAccess = condition.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, conditionAccess, bodyLabel, endLabel);
        utils.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        utils.enterLoop(startLabel, endLabel);
        body.toIntermediateCode(utils);
        utils.exitLoop();
        utils.getCuartetas().add(new Goto3D(startLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("condicion", condition);
        printer.child("cuerpo", body);
    }
}
