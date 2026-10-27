package practica.compi2.visitors.y;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.antlr4.y.YParserBaseVisitor;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.FunctionDefinition;
import practica.compi2.ast.definitions.Parameter;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.ast.types.TypeReference;

public class YProgramVisitor extends YParserBaseVisitor<AstNode> {

    private final String fileName;
    private final YStatementVisitor statementVisitor;
    private final YTypeMapper typeMapper;

    public YProgramVisitor(String fileName) {
        this.fileName = fileName;
        this.statementVisitor = new YStatementVisitor();
        this.typeMapper = new YTypeMapper();
    }

    @Override
    public YProgram visitProgram(YParser.ProgramContext ctx) {
        YProgram program = new YProgram(Position.of(ctx), fileName);
        if (ctx.structSection() != null) {
            for (YParser.StructDefinitionContext structCtx : ctx.structSection().structDefinition()) {
                program.addStruct(statementVisitor.visitStructDefinition(structCtx));
            }
        }
        for (YParser.FunctionDefinitionContext functionCtx : ctx.functionSection().functionDefinition()) {
            program.addFunction(visitFunctionDefinition(functionCtx));
        }
        return program;
    }

    @Override
    public FunctionDefinition visitFunctionDefinition(YParser.FunctionDefinitionContext ctx) {
        TypeReference returnType = new TypeReference(Position.of(ctx), PrimitiveType.VOID);
        if (ctx.type() != null) {
            returnType = typeMapper.map(ctx.type());
            returnType.setDimensions(ctx.LBRACKET().size());
        }
        List<Parameter> parameters = createParameters(ctx.parameters());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new FunctionDefinition(Position.of(ctx), ctx.ID().getText(), parameters, returnType, body);
    }

    @Override
    public Parameter visitValueParameter(YParser.ValueParameterContext ctx) {
        TypeReference type = typeMapper.map(ctx.type());
        return new Parameter(Position.of(ctx), type, ctx.ID().getText(), false);
    }

    @Override
    public Parameter visitArrayParameter(YParser.ArrayParameterContext ctx) {
        TypeReference type = typeMapper.map(ctx.type());
        type.setDimensions(ctx.LBRACKET().size());
        return new Parameter(Position.of(ctx), type, ctx.ID().getText(), true);
    }

    @Override
    public Parameter visitStructParameter(YParser.StructParameterContext ctx) {
        TypeReference type = new TypeReference(Position.of(ctx), ctx.ID(0).getText());
        return new Parameter(Position.of(ctx), type, ctx.ID(1).getText(), true);
    }

    private List<Parameter> createParameters(YParser.ParametersContext ctx) {
        List<Parameter> parameters = new ArrayList<>();
        if (ctx != null) {
            for (YParser.ParameterContext parameterCtx : ctx.parameter()) {
                parameters.add((Parameter) visit(parameterCtx));
            }
        }
        return parameters;
    }
}
