package practica.compi2.visitors.zetariano;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.antlr4.zetariano.ZetarianoParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Assignation;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.BreakStmt;
import practica.compi2.ast.statements.ContinueStmt;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.PrintStmt;
import practica.compi2.ast.statements.ReturnStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.statements.ConstructorCall;
import practica.compi2.ast.types.TypeReference;

public class ZetarianoStatementVisitor extends ZetarianoParserBaseVisitor<Statement> {

    private final ZetarianoExpressionVisitor expressionVisitor;
    private final ZetarianoControlFlowVisitor controlFlowVisitor;
    private final ZetarianoTypeMapper typeMapper;

    public ZetarianoStatementVisitor() {
        this.expressionVisitor = new ZetarianoExpressionVisitor();
        this.controlFlowVisitor = new ZetarianoControlFlowVisitor(this, expressionVisitor);
        this.typeMapper = new ZetarianoTypeMapper();
    }

    public ZetarianoExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    @Override
    public Block visitBlock(ZetarianoParser.BlockContext ctx) {
        Block block = new Block(Position.of(ctx));
        for (ZetarianoParser.StatementContext stmtCtx : ctx.statement()) {
            block.addStatement(visit(stmtCtx));
        }
        return block;
    }

    @Override
    public Statement visitStatement(ZetarianoParser.StatementContext ctx) {
        if (ctx.controlStatement() != null) {
            return controlFlowVisitor.visit(ctx.controlStatement());
        }
        if (ctx.block() != null) {
            return visitBlock(ctx.block());
        }
        if (ctx.simpleStatement() != null) {
            return visit(ctx.simpleStatement());
        }
        return new Block(Position.of(ctx));
    }

    @Override
    public Statement visitDeclarationStatement(ZetarianoParser.DeclarationStatementContext ctx) {
        return visit(ctx.localDeclaration());
    }

    @Override
    public Declaration visitLocalDeclaration(ZetarianoParser.LocalDeclarationContext ctx) {
        TypeReference type = typeMapper.map(ctx.type());
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), new ArrayList<>(), value);
    }

    @Override
    public Statement visitAssignmentStatement(ZetarianoParser.AssignmentStatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Assignation visitAssignment(ZetarianoParser.AssignmentContext ctx) {
        Expression target = expressionVisitor.visit(ctx.expression(0));
        Expression value = expressionVisitor.visit(ctx.expression(1));
        return new Assignation(Position.of(ctx), target, ctx.op.getText(), value);
    }

    @Override
    public PrintStmt visitPrintStatement(ZetarianoParser.PrintStatementContext ctx) {
        List<Expression> values = new ArrayList<>();
        if (ctx.expression() != null) {
            values.add(expressionVisitor.visit(ctx.expression()));
        }
        boolean newLine = ctx.PRINTLN() != null;
        return new PrintStmt(Position.of(ctx), values, newLine);
    }

    @Override
    public ConstructorCall visitThisCallStatement(ZetarianoParser.ThisCallStatementContext ctx) {
        return new ConstructorCall(Position.of(ctx), true, expressionVisitor.createArguments(ctx.arguments()));
    }

    @Override
    public ConstructorCall visitSuperCallStatement(ZetarianoParser.SuperCallStatementContext ctx) {
        return new ConstructorCall(Position.of(ctx), false, expressionVisitor.createArguments(ctx.arguments()));
    }

    @Override
    public ReturnStmt visitReturnStatement(ZetarianoParser.ReturnStatementContext ctx) {
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        return new ReturnStmt(Position.of(ctx), value);
    }

    @Override
    public BreakStmt visitBreakStatement(ZetarianoParser.BreakStatementContext ctx) {
        return new BreakStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public ContinueStmt visitContinueStatement(ZetarianoParser.ContinueStatementContext ctx) {
        return new ContinueStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public ExpressionStmt visitExpressionStatement(ZetarianoParser.ExpressionStatementContext ctx) {
        Expression expression = expressionVisitor.visit(ctx.expression());
        return new ExpressionStmt(Position.of(ctx), expression);
    }
}
