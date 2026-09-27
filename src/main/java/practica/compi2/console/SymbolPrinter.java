package practica.compi2.console;

import java.util.List;
import practica.compi2.semantic.symbols.Symbol;

public class SymbolPrinter {

    private static final String FORMAT = "%-38s | %-14s | %-11s | %-30s | %-8s | %s%n";

    public String print(List<Symbol> symbols) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT, "Nombre", "Tipo", "Categoría", "Ámbito", "Posición", "Archivo"));
        for (Symbol symbol : symbols) {
            String offset = "-";
            if (symbol.getOffset() >= 0) {
                offset = String.valueOf(symbol.getOffset());
            }
            sb.append(String.format(FORMAT,
                    symbol.getName(),
                    symbol.getType(),
                    symbol.getKind().getDescription(),
                    symbol.getScope(),
                    offset,
                    symbol.getFile()));
        }
        return sb.toString();
    }
}
