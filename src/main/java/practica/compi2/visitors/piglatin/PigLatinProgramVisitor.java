package practica.compi2.visitors.piglatin;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.tree.TerminalNode;
import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.antlr4.piglatin.PigLatinParserBaseVisitor;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.ImportDeclaration;
import practica.compi2.ast.programs.PigLatinProgram;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.Declaration;

public class PigLatinProgramVisitor extends PigLatinParserBaseVisitor<AstNode> {

    private final String fileName;
    private final PigLatinStatementVisitor statementVisitor;

    public PigLatinProgramVisitor(String fileName) {
        this.fileName = fileName;
        this.statementVisitor = new PigLatinStatementVisitor();
    }

    @Override
    public PigLatinProgram visitProgram(PigLatinParser.ProgramContext ctx) {
        PigLatinProgram program = visitImports(ctx);
        if (ctx.variablesSection() != null) {
            for (PigLatinParser.DeclarationContext declarationCtx : ctx.variablesSection().declaration()) {
                program.addGlobal((Declaration) statementVisitor.visit(declarationCtx));
            }
        }
        program.setMain(visitMainSection(ctx.mainSection()));
        return program;
    }

    public PigLatinProgram visitImports(PigLatinParser.ProgramContext ctx) {
        PigLatinProgram program = new PigLatinProgram(Position.of(ctx), fileName);
        for (PigLatinParser.ImportStatementContext importCtx : ctx.importStatement()) {
            if (isComplete(importCtx)) {
                program.addImport(visitImportStatement(importCtx));
            }
        }
        return program;
    }

    private boolean isComplete(PigLatinParser.ImportStatementContext ctx) {
        if (ctx.exception != null) {
            return false;
        }
        for (TerminalNode id : ctx.ID()) {
            if (id.getSymbol().getTokenIndex() < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ImportDeclaration visitImportStatement(PigLatinParser.ImportStatementContext ctx) {
        List<String> path = new ArrayList<>();
        for (TerminalNode id : ctx.ID()) {
            path.add(id.getText());
        }
        return new ImportDeclaration(Position.of(ctx), path);
    }

    @Override
    public Block visitMainSection(PigLatinParser.MainSectionContext ctx) {
        Block main = new Block(Position.of(ctx));
        for (PigLatinParser.StatementContext stmtCtx : ctx.statement()) {
            main.addStatement(statementVisitor.visit(stmtCtx));
        }
        return main;
    }
}
