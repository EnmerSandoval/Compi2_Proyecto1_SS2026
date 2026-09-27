package practica.compi2.visitors.piglatin;

import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.ast.types.TypeReference;

public class PigLatinTypeMapper {

    public TypeReference map(PigLatinParser.TypeContext ctx) {
        Position position = Position.of(ctx);
        switch (ctx.getStart().getType()) {
            case PigLatinParser.NUMERUS -> {
                return new TypeReference(position, PrimitiveType.INTEGER);
            }
            case PigLatinParser.DECIMALIS -> {
                return new TypeReference(position, PrimitiveType.FLOAT);
            }
            case PigLatinParser.TEXTUM -> {
                return new TypeReference(position, PrimitiveType.STRING);
            }
            case PigLatinParser.LITTERA -> {
                return new TypeReference(position, PrimitiveType.CHAR);
            }
            default -> {
                return new TypeReference(position, ctx.getText());
            }
        }
    }
}
