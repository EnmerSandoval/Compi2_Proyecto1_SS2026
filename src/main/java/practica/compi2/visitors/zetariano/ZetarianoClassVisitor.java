package practica.compi2.visitors.zetariano;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.antlr4.zetariano.ZetarianoParserBaseVisitor;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.AccessModifier;
import practica.compi2.ast.definitions.AttributeDefinition;
import practica.compi2.ast.definitions.ClassDefinition;
import practica.compi2.ast.definitions.ConstructorDefinition;
import practica.compi2.ast.definitions.MethodDefinition;
import practica.compi2.ast.definitions.Parameter;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.ast.types.TypeReference;

public class ZetarianoClassVisitor extends ZetarianoParserBaseVisitor<AstNode> {

    private final String fileName;
    private final ZetarianoStatementVisitor statementVisitor;
    private final ZetarianoTypeMapper typeMapper;

    public ZetarianoClassVisitor(String fileName) {
        this.fileName = fileName;
        this.statementVisitor = new ZetarianoStatementVisitor();
        this.typeMapper = new ZetarianoTypeMapper();
    }

    @Override
    public ZetarianoProgram visitCompilationUnit(ZetarianoParser.CompilationUnitContext ctx) {
        ClassDefinition classDefinition = visitClassDeclaration(ctx.classDeclaration());
        return new ZetarianoProgram(Position.of(ctx), fileName, classDefinition);
    }

    @Override
    public ClassDefinition visitClassDeclaration(ZetarianoParser.ClassDeclarationContext ctx) {
        AccessModifier access = accessOf(ctx.accessModifier());
        ClassDefinition classDefinition = new ClassDefinition(Position.of(ctx), access, ctx.ID(0).getText());
        if (ctx.EXTENDS() != null) {
            classDefinition.setParentName(ctx.ID(1).getText());
        }
        for (ZetarianoParser.ClassMemberContext memberCtx : ctx.classMember()) {
            if (memberCtx.fieldDeclaration() != null) {
                classDefinition.addAttribute(visitFieldDeclaration(memberCtx.fieldDeclaration()));
            } else if (memberCtx.constructorDeclaration() != null) {
                classDefinition.addConstructor(visitConstructorDeclaration(memberCtx.constructorDeclaration()));
            } else {
                classDefinition.addMethod(visitMethodDeclaration(memberCtx.methodDeclaration()));
            }
        }
        return classDefinition;
    }

    @Override
    public AttributeDefinition visitFieldDeclaration(ZetarianoParser.FieldDeclarationContext ctx) {
        AccessModifier access = accessOf(ctx.accessModifier());
        TypeReference type = typeMapper.map(ctx.type());
        Expression value = statementVisitor.getExpressionVisitor().visitOptional(ctx.expression());
        return new AttributeDefinition(Position.of(ctx), access, type, ctx.ID().getText(), value);
    }

    @Override
    public ConstructorDefinition visitConstructorDeclaration(ZetarianoParser.ConstructorDeclarationContext ctx) {
        AccessModifier access = accessOf(ctx.accessModifier());
        List<Parameter> parameters = createParameters(ctx.parameters());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new ConstructorDefinition(Position.of(ctx), access, ctx.ID().getText(), parameters, body);
    }

    @Override
    public MethodDefinition visitMethodDeclaration(ZetarianoParser.MethodDeclarationContext ctx) {
        AccessModifier access = accessOf(ctx.accessModifier());
        TypeReference returnType = new TypeReference(Position.of(ctx), PrimitiveType.VOID);
        if (ctx.type() != null) {
            returnType = typeMapper.map(ctx.type());
        }
        List<Parameter> parameters = createParameters(ctx.parameters());
        Block body = statementVisitor.visitBlock(ctx.block());
        MethodDefinition method = new MethodDefinition(Position.of(ctx), access, ctx.ID().getText(), parameters,
                returnType, body);
        method.setOverride(ctx.OVERRIDE() != null);
        return method;
    }

    @Override
    public Parameter visitParameter(ZetarianoParser.ParameterContext ctx) {
        TypeReference type = typeMapper.map(ctx.type());
        boolean reference = type.isArray() || !type.isPrimitive();
        return new Parameter(Position.of(ctx), type, ctx.ID().getText(), reference);
    }

    private List<Parameter> createParameters(ZetarianoParser.ParametersContext ctx) {
        List<Parameter> parameters = new ArrayList<>();
        if (ctx != null) {
            for (ZetarianoParser.ParameterContext parameterCtx : ctx.parameter()) {
                parameters.add(visitParameter(parameterCtx));
            }
        }
        return parameters;
    }

    private AccessModifier accessOf(ZetarianoParser.AccessModifierContext ctx) {
        if (ctx == null) {
            return AccessModifier.DEFAULT;
        }
        switch (ctx.getStart().getType()) {
            case ZetarianoParser.PUBLIC -> {
                return AccessModifier.PUBLIC;
            }
            case ZetarianoParser.PRIVATE -> {
                return AccessModifier.PRIVATE;
            }
            default -> {
                return AccessModifier.PROTECTED;
            }
        }
    }
}
