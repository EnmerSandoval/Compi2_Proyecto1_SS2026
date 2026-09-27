package practica.compi2.visitors.zetariano;

import java.util.List;
import org.antlr.v4.runtime.Token;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.antlr4.zetariano.ZetarianoParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.expressions.MethodCall;
import practica.compi2.ast.expressions.NewArray;
import practica.compi2.ast.expressions.NewObject;
import practica.compi2.ast.expressions.NullLiteral;
import practica.compi2.ast.expressions.ReadExpression;
import practica.compi2.ast.expressions.ThisAccess;
import practica.compi2.ast.expressions.VariableAccess;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.visitors.LiteralFactory;

public class ZetarianoAtomVisitor extends ZetarianoParserBaseVisitor<Expression> {

    private final ZetarianoExpressionVisitor expressionVisitor;
    private final ZetarianoTypeMapper typeMapper;

    public ZetarianoAtomVisitor(ZetarianoExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.typeMapper = new ZetarianoTypeMapper();
    }

    @Override
    public Expression visitGroupAtom(ZetarianoParser.GroupAtomContext ctx) {
        return expressionVisitor.visit(ctx.expression());
    }

    @Override
    public Expression visitLiteralAtom(ZetarianoParser.LiteralAtomContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Expression visitThisAtom(ZetarianoParser.ThisAtomContext ctx) {
        return new ThisAccess(Position.of(ctx));
    }

    @Override
    public Expression visitNullAtom(ZetarianoParser.NullAtomContext ctx) {
        return new NullLiteral(Position.of(ctx));
    }

    @Override
    public Expression visitCallAtom(ZetarianoParser.CallAtomContext ctx) {
        ThisAccess owner = new ThisAccess(Position.of(ctx));
        List<Expression> arguments = expressionVisitor.createArguments(ctx.arguments());
        return new MethodCall(Position.of(ctx), owner, ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitIdAtom(ZetarianoParser.IdAtomContext ctx) {
        return new VariableAccess(Position.of(ctx), ctx.ID().getText());
    }

    @Override
    public Expression visitNewObjectAtom(ZetarianoParser.NewObjectAtomContext ctx) {
        List<Expression> arguments = expressionVisitor.createArguments(ctx.arguments());
        return new NewObject(Position.of(ctx), ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitNewArrayAtom(ZetarianoParser.NewArrayAtomContext ctx) {
        TypeReference type = typeMapper.mapBase(Position.of(ctx), ctx.primitiveType(), ctx.ID());
        List<Expression> sizes = expressionVisitor.visitList(ctx.expression());
        type.setDimensions(sizes.size());
        return new NewArray(Position.of(ctx), type, sizes);
    }

    @Override
    public Expression visitListAtom(ZetarianoParser.ListAtomContext ctx) {
        List<Expression> elements = expressionVisitor.visitList(ctx.expression());
        return new InitializerList(Position.of(ctx), elements);
    }

    @Override
    public Expression visitReadAtom(ZetarianoParser.ReadAtomContext ctx) {
        return new ReadExpression(Position.of(ctx));
    }

    @Override
    public Expression visitLiteral(ZetarianoParser.LiteralContext ctx) {
        Position position = Position.of(ctx);
        Token token = ctx.getStart();
        switch (token.getType()) {
            case ZetarianoParser.INT_LIT -> {
                return LiteralFactory.integer(position, token.getText());
            }
            case ZetarianoParser.DECIMAL_LIT -> {
                return LiteralFactory.decimal(position, token.getText());
            }
            case ZetarianoParser.STRING_LIT -> {
                return LiteralFactory.string(position, token.getText());
            }
            case ZetarianoParser.CHAR_LIT -> {
                return LiteralFactory.character(position, token.getText());
            }
            default -> {
                return LiteralFactory.bool(position, token.getType() == ZetarianoParser.TRUE);
            }
        }
    }
}
