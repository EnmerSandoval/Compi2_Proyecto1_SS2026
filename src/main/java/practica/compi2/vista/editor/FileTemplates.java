package practica.compi2.vista.editor;

public class FileTemplates {

    public static String forFile(String fileName) {
        if (fileName.endsWith(".pig")) {
            return """
                    VARIABILES>

                    MAIOR>
                    >> "Hola mundo";
                    FINIS;
                    """;
        }
        if (fileName.endsWith(".y")) {
            return """
                    %funciones
                    definir saludar():
                        imprimir("Hola desde Y?")
                    """;
        }
        if (fileName.endsWith(".z")) {
            String className = fileName.substring(0, fileName.length() - 2);
            return "public class " + className + " {\n\n    public " + className + "() {\n    }\n}\n";
        }
        return "";
    }
}
