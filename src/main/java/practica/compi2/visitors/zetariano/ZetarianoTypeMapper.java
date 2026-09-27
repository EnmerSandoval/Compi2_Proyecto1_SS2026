package practica.compi2.visitors.zetariano;

import org.antlr.v4.runtime.tree.TerminalNode;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.ast.types.TypeReference;

public class ZetarianoTypeMapper {

    public TypeReference map(ZetarianoParser.TypeContext ctx) {
        TypeReference type = mapBase(Position.of(ctx), ctx.primitiveType(), ctx.ID());
        type.setDimensions(ctx.LBRACKET().size());
        return type;
    }

    public TypeReference mapBase(Position position, ZetarianoParser.PrimitiveTypeContext primitiveCtx,
            TerminalNode id) {
        if (primitiveCtx == null) {
            return new TypeReference(position, id.getText());
        }
        switch (primitiveCtx.getStart().getType()) {
            case ZetarianoParser.INT -> {
                return new TypeReference(position, PrimitiveType.INTEGER);
            }
            case ZetarianoParser.DOUBLE -> {
                return new TypeReference(position, PrimitiveType.FLOAT);
            }
            case ZetarianoParser.CHAR -> {
                return new TypeReference(position, PrimitiveType.CHAR);
            }
            case ZetarianoParser.BOOLEAN -> {
                return new TypeReference(position, PrimitiveType.BOOLEAN);
            }
            default -> {
                return new TypeReference(position, PrimitiveType.STRING);
            }
        }
    }
}
