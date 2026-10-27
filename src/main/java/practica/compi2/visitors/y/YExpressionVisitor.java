package practica.compi2.visitors.y;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.antlr4.y.YParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.ArrayAccess;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.FieldAccess;
import practica.compi2.ast.expressions.operations.IncDecOperation;
import practica.compi2.ast.expressions.operations.NegativeOperation;
import practica.compi2.ast.expressions.operations.NotOperation;
import practica.compi2.ast.expressions.operations.Operation;

public class YExpressionVisitor extends YParserBaseVisitor<Expression> {

    private final YAtomVisitor atomVisitor;

    public YExpressionVisitor() {
        this.atomVisitor = new YAtomVisitor(this);
    }

    public List<Expression> visitList(List<YParser.ExpressionContext> contexts) {
        List<Expression> expressions = new ArrayList<>();
        for (YParser.ExpressionContext ctx : contexts) {
            expressions.add(visit(ctx));
        }
        return expressions;
    }

    public Expression visitOptional(YParser.ExpressionContext ctx) {
        if (ctx == null) {
            return null;
        }
        return visit(ctx);
    }

    @Override
    public Expression visitFieldAccessExpr(YParser.FieldAccessExprContext ctx) {
        Expression target = visit(ctx.expression());
        return new FieldAccess(Position.of(ctx.ID().getSymbol()), target, ctx.ID().getText());
    }

    @Override
    public Expression visitArrayAccessExpr(YParser.ArrayAccessExprContext ctx) {
        Expression target = visit(ctx.expression(0));
        Expression index = visit(ctx.expression(1));
        return new ArrayAccess(Position.of(ctx), target, index);
    }

    @Override
    public Expression visitPostIncDecExpr(YParser.PostIncDecExprContext ctx) {
        Expression operand = visit(ctx.expression());
        return new IncDecOperation(Position.of(ctx), operand, ctx.op.getText(), false);
    }

    @Override
    public Expression visitUnaryExpr(YParser.UnaryExprContext ctx) {
        Expression operand = visit(ctx.expression());
        if (ctx.op.getType() == YParser.NOT) {
            return new NotOperation(Position.of(ctx), operand);
        }
        return new NegativeOperation(Position.of(ctx), operand);
    }

    @Override
    public Expression visitMulExpr(YParser.MulExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAddExpr(YParser.AddExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitRelExpr(YParser.RelExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitEqExpr(YParser.EqExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAndExpr(YParser.AndExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "&&");
    }

    @Override
    public Expression visitOrExpr(YParser.OrExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "||");
    }

    @Override
    public Expression visitAtomExpr(YParser.AtomExprContext ctx) {
        return atomVisitor.visit(ctx.atom());
    }

    private Expression binaryOperation(ParserRuleContext ctx, YParser.ExpressionContext leftCtx,
            YParser.ExpressionContext rightCtx, String operator) {
        Expression left = visit(leftCtx);
        Expression right = visit(rightCtx);
        return Operation.create(Position.of(ctx), left, right, operator);
    }
}
