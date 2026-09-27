package practica.compi2.visitors.y;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.antlr4.y.YParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.DoWhileStmt;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.ForStmt;
import practica.compi2.ast.statements.IfStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.statements.SwitchCase;
import practica.compi2.ast.statements.SwitchStmt;
import practica.compi2.ast.statements.WhileStmt;

public class YControlFlowVisitor extends YParserBaseVisitor<Statement> {

    private final YStatementVisitor statementVisitor;
    private final YExpressionVisitor expressionVisitor;

    public YControlFlowVisitor(YStatementVisitor statementVisitor, YExpressionVisitor expressionVisitor) {
        this.statementVisitor = statementVisitor;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Statement visitControlStatement(YParser.ControlStatementContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public IfStmt visitIfStatement(YParser.IfStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Block body = statementVisitor.visitBlock(ctx.block());
        Statement elseBranch = createElseBranch(ctx, 0);
        return new IfStmt(Position.of(ctx), condition, body, elseBranch);
    }

    @Override
    public SwitchStmt visitSwitchStatement(YParser.SwitchStatementContext ctx) {
        Expression expression = expressionVisitor.visit(ctx.expression());
        List<SwitchCase> cases = new ArrayList<>();
        for (YParser.CaseClauseContext caseCtx : ctx.caseClause()) {
            Expression value = expressionVisitor.visit(caseCtx.expression());
            Block block = statementVisitor.visitBlock(caseCtx.block());
            cases.add(new SwitchCase(Position.of(caseCtx), value, block.getStatements()));
        }
        if (ctx.defaultClause() != null) {
            Block block = statementVisitor.visitBlock(ctx.defaultClause().block());
            cases.add(new SwitchCase(Position.of(ctx.defaultClause()), null, block.getStatements()));
        }
        return new SwitchStmt(Position.of(ctx), expression, cases);
    }

    @Override
    public ForStmt visitForStatement(YParser.ForStatementContext ctx) {
        Statement init = null;
        if (ctx.forInit() != null) {
            init = statementVisitor.visit(ctx.forInit().getChild(0));
        }
        Expression condition = expressionVisitor.visitOptional(ctx.expression());
        Statement update = createForUpdate(ctx.forUpdate());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new ForStmt(Position.of(ctx), init, condition, update, body);
    }

    @Override
    public WhileStmt visitWhileStatement(YParser.WhileStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new WhileStmt(Position.of(ctx), condition, body);
    }

    @Override
    public DoWhileStmt visitDoWhileStatement(YParser.DoWhileStatementContext ctx) {
        Block body = statementVisitor.visitBlock(ctx.block());
        Expression condition = expressionVisitor.visit(ctx.expression());
        return new DoWhileStmt(Position.of(ctx), body, condition);
    }

    private Statement createElseBranch(YParser.IfStatementContext ctx, int index) {
        if (index < ctx.elseIfClause().size()) {
            YParser.ElseIfClauseContext elseIfCtx = ctx.elseIfClause(index);
            Expression condition = expressionVisitor.visit(elseIfCtx.expression());
            Block body = statementVisitor.visitBlock(elseIfCtx.block());
            return new IfStmt(Position.of(elseIfCtx), condition, body, createElseBranch(ctx, index + 1));
        }
        if (ctx.elseClause() != null) {
            return statementVisitor.visitBlock(ctx.elseClause().block());
        }
        return null;
    }

    private Statement createForUpdate(YParser.ForUpdateContext ctx) {
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
