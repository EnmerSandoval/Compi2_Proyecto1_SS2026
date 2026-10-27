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
        ErrorManager em = new ErrorManager();
        compilation.setErrors(em.getErrors());
        try {
            run(mainFile, compilation, em);
        } catch (StackOverflowError e) {
            compilation.setCode(null);
            em.addError(new CompilationError(ErrorType.SINTACTICO, "", mainFile.getFileName().toString(), 0, 0,
                    "El programa tiene demasiados niveles de anidamiento para poder compilarse"));
        }
        return compilation;
    }

    private void run(Path mainFile, CompilationResult compilation, ErrorManager em) {
        ProgramLoader loader = new ProgramLoader(mainFile.getParent(), em);
        Ast ast = loader.load(mainFile);
        compilation.setAst(ast);
        if (em.hasSyntaxErrors()) {
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer(em);
        analyzer.analyze(ast);
        compilation.setSymbols(analyzer.getSymbols());
        compilation.setTypeTable(analyzer.getTypeTable());
        if (em.hasErrors() || ast.getMain() == null) {
            return;
        }

        IntermediateCodeUtils u = new IntermediateCodeGenerator().generate(ast, analyzer.getTypeTable());
        compilation.setCuartetas(u.getCuartetas());
        compilation.setCode(new CCodeGenerator().generateCode(u));
        compilation.setRiscVCode(new RiscVGenerator().generateCode(u));
    }
}
