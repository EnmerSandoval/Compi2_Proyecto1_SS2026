package practica.compi2.visitors.piglatin;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.antlr4.piglatin.PigLatinParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.ArrayAccess;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.FieldAccess;
import practica.compi2.ast.expressions.MethodCall;
import practica.compi2.ast.expressions.operations.IncDecOperation;
import practica.compi2.ast.expressions.operations.NegativeOperation;
import practica.compi2.ast.expressions.operations.NotOperation;
import practica.compi2.ast.expressions.operations.Operation;

public class PigLatinExpressionVisitor extends PigLatinParserBaseVisitor<Expression> {

    private final PigLatinAtomVisitor atomVisitor;

    public PigLatinExpressionVisitor() {
        this.atomVisitor = new PigLatinAtomVisitor(this);
    }

    public List<Expression> visitList(List<PigLatinParser.ExpressionContext> contexts) {
        List<Expression> expressions = new ArrayList<>();
        for (PigLatinParser.ExpressionContext ctx : contexts) {
            expressions.add(visit(ctx));
        }
        return expressions;
    }

    public Expression visitOptional(PigLatinParser.ExpressionContext ctx) {
        if (ctx == null) {
            return null;
        }
        return visit(ctx);
    }

    public List<Expression> createArguments(PigLatinParser.ArgumentsContext ctx) {
        if (ctx == null) {
            return new ArrayList<>();
        }
        return visitList(ctx.expression());
    }

    @Override
    public Expression visitMethodCallExpr(PigLatinParser.MethodCallExprContext ctx) {
        Expression target = visit(ctx.expression());
        List<Expression> arguments = createArguments(ctx.arguments());
        return new MethodCall(Position.of(ctx), target, ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitFieldAccessExpr(PigLatinParser.FieldAccessExprContext ctx) {
        Expression target = visit(ctx.expression());
        return new FieldAccess(Position.of(ctx), target, ctx.ID().getText());
    }

    @Override
    public Expression visitArrayAccessExpr(PigLatinParser.ArrayAccessExprContext ctx) {
        Expression target = visit(ctx.expression(0));
        Expression index = visit(ctx.expression(1));
        return new ArrayAccess(Position.of(ctx), target, index);
    }

    @Override
    public Expression visitPostIncDecExpr(PigLatinParser.PostIncDecExprContext ctx) {
        Expression operand = visit(ctx.expression());
        return new IncDecOperation(Position.of(ctx), operand, ctx.op.getText(), false);
    }

    @Override
    public Expression visitUnaryExpr(PigLatinParser.UnaryExprContext ctx) {
        Expression operand = visit(ctx.expression());
        if (ctx.op.getType() == PigLatinParser.NOT) {
            return new NotOperation(Position.of(ctx), operand);
        }
        return new NegativeOperation(Position.of(ctx), operand);
    }

    @Override
    public Expression visitMulExpr(PigLatinParser.MulExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAddExpr(PigLatinParser.AddExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitRelExpr(PigLatinParser.RelExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitEqExpr(PigLatinParser.EqExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAndExpr(PigLatinParser.AndExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "&&");
    }

    @Override
    public Expression visitOrExpr(PigLatinParser.OrExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "||");
    }

    @Override
    public Expression visitAtomExpr(PigLatinParser.AtomExprContext ctx) {
        return atomVisitor.visit(ctx.atom());
    }

    private Expression binaryOperation(ParserRuleContext ctx, PigLatinParser.ExpressionContext leftCtx,
            PigLatinParser.ExpressionContext rightCtx, String operator) {
        Expression left = visit(leftCtx);
        Expression right = visit(rightCtx);
        return Operation.create(Position.of(ctx), left, right, operator);
    }
}
