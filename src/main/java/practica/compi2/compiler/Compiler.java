package practica.compi2.compiler;

import java.nio.file.Path;
import practica.compi2.ast.Ast;
import practica.compi2.c3d.CCodeGenerator;
import practica.compi2.c3d.IntermediateCodeGenerator;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.errors.CompilationError;
import practica.compi2.errors.ErrorManager;
import practica.compi2.errors.ErrorType;
import practica.compi2.parsers.ProgramLoader;
import practica.compi2.riscv.RiscVGenerator;
import practica.compi2.semantic.SemanticAnalyzer;

public class Compiler {

    public CompilationResult compile(Path mainFile) {
        CompilationResult compilation = new CompilationResult();
        ErrorManager errorManager = new ErrorManager();
        compilation.setErrors(errorManager.getErrors());
        try {
            run(mainFile, compilation, errorManager);
        } catch (StackOverflowError e) {
            compilation.setCode(null);
            errorManager.addError(new CompilationError(ErrorType.SINTACTICO, "", mainFile.getFileName().toString(), 0, 0,
                    "El programa tiene demasiados niveles de anidamiento para poder compilarse"));
        }
        return compilation;
    }

    private void run(Path mainFile, CompilationResult compilation, ErrorManager errorManager) {
        ProgramLoader loader = new ProgramLoader(mainFile.getParent(), errorManager);
        Ast ast = loader.load(mainFile);
        compilation.setAst(ast);
        if (errorManager.hasSyntaxErrors()) {
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorManager);
        analyzer.analyze(ast);
        compilation.setSymbols(analyzer.getSymbols());
        compilation.setTypeTable(analyzer.getTypeTable());
        if (errorManager.hasErrors() || ast.getMain() == null) {
            return;
        }

        IntermediateCodeUtils utils = new IntermediateCodeGenerator().generate(ast, analyzer.getTypeTable());
        compilation.setCuartetas(utils.getCuartetas());
        compilation.setCode(new CCodeGenerator().generateCode(utils));
        compilation.setRiscVCode(new RiscVGenerator().generateCode(utils));
    }
}
