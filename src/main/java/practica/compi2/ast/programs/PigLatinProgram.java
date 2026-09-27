package practica.compi2.ast.programs;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.ImportDeclaration;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Frame;
import practica.compi2.semantic.types.PigLatinCompatibilityTable;
import practica.compi2.semantic.types.TypeRules;

@Getter
public class PigLatinProgram extends Program {

    private List<ImportDeclaration> imports;
    private List<Declaration> globals;
    @Setter
    private Block main;
    private int frameSize;

    public PigLatinProgram(Position position, String fileName) {
        super(position, fileName);
        this.imports = new ArrayList<>();
        this.globals = new ArrayList<>();
    }

    public void addImport(ImportDeclaration importDeclaration) {
        imports.add(importDeclaration);
    }

    public void addGlobal(Declaration declaration) {
        globals.add(declaration);
    }

    public void validate(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new PigLatinCompatibilityTable()));
        Frame frame = new Frame("main", 0);
        sc.enterMain(frame);
        for (Declaration global : globals) {
            global.validate(sc);
        }
        main.validateStatements(sc);
        frameSize = frame.getSize();
        sc.exitFunction();
    }

    public void toIntermediateCode(IntermediateCodeUtils u) {
        u.useBooleanWords("verum", "falsus");
        u.startMain(frameSize);
        for (Declaration global : globals) {
            global.toIntermediateCode(u);
        }
        main.toIntermediateCode(u);
        u.endFunction();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("archivo", fileName);
        printer.children("importaciones", imports);
        printer.children("variablesGlobales", globals);
        printer.child("principal", main);
    }
}
