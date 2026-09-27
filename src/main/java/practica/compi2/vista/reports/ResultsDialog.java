package practica.compi2.vista.reports;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.compiler.CompilationResult;
import practica.compi2.console.TypeReportPrinter;
import practica.compi2.semantic.symbols.Symbol;

public class ResultsDialog extends JDialog {

    private static final String[] SYMBOL_COLUMNS = {"Nombre", "Tipo", "Categoría", "Ámbito", "Posición", "Archivo"};
    private static final String[] CUARTETA_COLUMNS = {"#", "Operador", "Argumento 1", "Argumento 2", "Resultado"};

    public ResultsDialog(JFrame owner, CompilationResult compilation) {
        super(owner, "Reportes de compilación", false);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tabla de símbolos", symbolTable(compilation.getSymbols()));
        tabs.addTab("Tabla de tipos", TableFactory.createTextPanel(new TypeReportPrinter().print(compilation.getTypeTable())));
        tabs.addTab("Cuartetas", cuartetaTable(compilation.getCuartetas()));
        tabs.addTab("Código de tres direcciones", TableFactory.createTextPanel(compilation.getCode()));
        add(tabs);
        setSize(1000, 600);
        setLocationRelativeTo(owner);
    }

    private JScrollPane symbolTable(List<Symbol> symbols) {
        List<String[]> rows = new ArrayList<>();
        for (Symbol symbol : symbols) {
            String offset = "-";
            if (symbol.getOffset() >= 0) {
                offset = String.valueOf(symbol.getOffset());
            }
            rows.add(new String[]{
                symbol.getName(),
                String.valueOf(symbol.getType()),
                symbol.getKind().getDescription(),
                String.valueOf(symbol.getScope()),
                offset,
                symbol.getFile()
            });
        }
        JTable table = TableFactory.createTable(SYMBOL_COLUMNS, rows);
        TableFactory.setWidths(table, 240, 110, 90, 160, 60, 180);
        return new JScrollPane(table);
    }

    private JScrollPane cuartetaTable(List<Cuarteta> cuartetas) {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < cuartetas.size(); i++) {
            String[] row = cuartetas.get(i).toRow();
            rows.add(new String[]{String.valueOf(i), row[0], row[1], row[2], row[3]});
        }
        JTable table = TableFactory.createTable(CUARTETA_COLUMNS, rows);
        TableFactory.setWidths(table, 50, 120, 250, 250, 250);
        return new JScrollPane(table);
    }
}
