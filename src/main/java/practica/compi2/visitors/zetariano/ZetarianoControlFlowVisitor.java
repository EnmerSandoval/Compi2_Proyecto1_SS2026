package practica.compi2.visitors.zetariano;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.antlr4.zetariano.ZetarianoParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.DoWhileStmt;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.ForStmt;
import practica.compi2.ast.statements.IfStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.statements.SwitchCase;
import practica.compi2.ast.statements.SwitchStmt;
import practica.compi2.ast.statements.WhileStmt;

public class ZetarianoControlFlowVisitor extends ZetarianoParserBaseVisitor<Statement> {

    private final ZetarianoStatementVisitor statementVisitor;
    private final ZetarianoExpressionVisitor expressionVisitor;

    public ZetarianoControlFlowVisitor(ZetarianoStatementVisitor statementVisitor,
            ZetarianoExpressionVisitor expressionVisitor) {
        this.statementVisitor = statementVisitor;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public IfStmt visitIfStatement(ZetarianoParser.IfStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Statement body = statementVisitor.visit(ctx.statement(0));
        Statement elseBranch = null;
        if (ctx.ELSE() != null) {
            elseBranch = statementVisitor.visit(ctx.statement(1));
        }
        return new IfStmt(Position.of(ctx), condition, body, elseBranch);
    }

    @Override
    public SwitchStmt visitSwitchStatement(ZetarianoParser.SwitchStatementContext ctx) {
        Expression expression = expressionVisitor.visit(ctx.expression());
        List<SwitchCase> cases = new ArrayList<>();
        for (ZetarianoParser.SwitchSectionContext sectionCtx : ctx.switchSection()) {
            cases.add(createSwitchCase(sectionCtx));
        }
        return new SwitchStmt(Position.of(ctx), expression, cases);
    }

    @Override
    public ForStmt visitForStatement(ZetarianoParser.ForStatementContext ctx) {
        Statement init = null;
        if (ctx.forInit() != null) {
            init = statementVisitor.visit(ctx.forInit().getChild(0));
        }
        Expression condition = expressionVisitor.visitOptional(ctx.expression());
        Statement update = createForUpdate(ctx.forUpdate());
        Statement body = statementVisitor.visit(ctx.statement());
        return new ForStmt(Position.of(ctx), init, condition, update, body);
    }

    @Override
    public WhileStmt visitWhileStatement(ZetarianoParser.WhileStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Statement body = statementVisitor.visit(ctx.statement());
        return new WhileStmt(Position.of(ctx), condition, body);
    }

    @Override
    public DoWhileStmt visitDoWhileStatement(ZetarianoParser.DoWhileStatementContext ctx) {
        Statement body = statementVisitor.visit(ctx.statement());
        Expression condition = expressionVisitor.visit(ctx.expression());
        return new DoWhileStmt(Position.of(ctx), body, condition);
    }

    private SwitchCase createSwitchCase(ZetarianoParser.SwitchSectionContext ctx) {
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        List<Statement> statements = new ArrayList<>();
        for (ZetarianoParser.StatementContext stmtCtx : ctx.statement()) {
            statements.add(statementVisitor.visit(stmtCtx));
        }
        return new SwitchCase(Position.of(ctx), value, statements);
    }

    private Statement createForUpdate(ZetarianoParser.ForUpdateContext ctx) {
        if (ctx == null) {
            return null;
        }
        if (ctx.assignment() != null) {
            return statementVisitor.visit(ctx.assignment());
        }
        Expression expression = expressionVisitor.visit(ctx.expression());
        return new ExpressionStmt(Position.of(ctx), expression);
    }
}
