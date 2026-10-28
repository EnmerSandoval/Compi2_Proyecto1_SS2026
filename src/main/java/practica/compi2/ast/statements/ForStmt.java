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
public class ForStmt extends Statement {

    private Statement init;
    private Expression condition;
    private Statement update;
    private Statement body;

    public ForStmt(Position position, Statement init, Expression condition, Statement update, Statement body) {
        super(position);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    @Override
    public void validate(SemanticContext context) {
        context.enterScope();
        if (init != null) {
            init.validate(context);
        }
        if (condition != null) {
            context.checkCondition(condition, "para");
        }
        if (update != null) {
            update.validate(context);
        }
        context.enterLoop();
        body.validate(context);
        context.exitLoop();
        context.exitScope();
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        int conditionLabel = utils.getCounter().getAndIncrementLabelCount();
        int bodyLabel = utils.getCounter().getAndIncrementLabelCount();
        int updateLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        if (init != null) {
            init.toIntermediateCode(utils);
        }
        utils.getCuartetas().add(new LabelDefinition3D(conditionLabel));
        if (condition != null) {
            MemoryAccess conditionAccess = condition.toIntermediateCode(utils);
            ConditionHelper.jumpIfTrue(utils, conditionAccess, bodyLabel, endLabel);
        }
        utils.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        utils.enterLoop(updateLabel, endLabel);
        body.toIntermediateCode(utils);
        utils.exitLoop();
        utils.getCuartetas().add(new LabelDefinition3D(updateLabel));
        if (update != null) {
            update.toIntermediateCode(utils);
        }
        utils.getCuartetas().add(new Goto3D(conditionLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("inicio", init);
        printer.child("condicion", condition);
        printer.child("actualizacion", update);
        printer.child("cuerpo", body);
    }
}
