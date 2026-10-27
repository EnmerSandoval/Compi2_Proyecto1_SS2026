package practica.compi2.vista.reports;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import practica.compi2.console.CompatibilityPrinter;
import practica.compi2.console.TypeTablePrinter;
import practica.compi2.semantic.types.CompatibilityTable;
import practica.compi2.semantic.types.PigLatinCompatibilityTable;
import practica.compi2.semantic.types.TypeEntry;
import practica.compi2.semantic.types.TypeKind;
import practica.compi2.semantic.types.TypeTable;
import practica.compi2.semantic.types.YCompatibilityTable;
import practica.compi2.semantic.types.ZetarianoCompatibilityTable;

public class TypeTablesPanel extends JTabbedPane {

    private static final String[] TYPE_COLUMNS = {"Tipo", "Categoría", "Tamaño", "Cómo se guarda", "Miembros (posición)"};
    private static final Color VALID = new Color(214, 240, 214);
    private static final Color INVALID = new Color(245, 214, 214);

    private final TypeTablePrinter typePrinter;
    private final CompatibilityPrinter compatibilityPrinter;

    public TypeTablesPanel(TypeTable typeTable) {
        this.typePrinter = new TypeTablePrinter();
        this.compatibilityPrinter = new CompatibilityPrinter();
        addTab("Tipos", typesTable(typeTable));
        for (CompatibilityTable table : List.of(new YCompatibilityTable(), new ZetarianoCompatibilityTable(),
                new PigLatinCompatibilityTable())) {
            addTab(table.getLanguage(), compatibilityTables(table));
        }
    }

    private JScrollPane typesTable(TypeTable typeTable) {
        List<String[]> rows = new ArrayList<>();
        for (TypeEntry entry : typeTable.getEntries()) {
            rows.add(new String[]{
                entry.getName(),
                typePrinter.categoryOf(entry),
                String.valueOf(entry.getSize()),
                typePrinter.storageOf(entry),
                typePrinter.membersOf(entry)
            });
        }
        JTable table = TableFactory.createTable(TYPE_COLUMNS, rows);
        TableFactory.setWidths(table, 120, 160, 60, 380, 420);
        return new JScrollPane(table);
    }

    private JScrollPane compatibilityTables(CompatibilityTable table) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        for (String group : CompatibilityTable.GROUPS) {
            if (table.hasRules(group)) {
                content.add(groupPanel(table, group));
            }
        }
        JScrollPane scroll = new JScrollPane(content);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel groupPanel(CompatibilityTable table, String group) {
        String axes = group.equals("=")
                ? "fila: tipo de la variable, columna: tipo del valor"
                : "fila: operando izquierdo, columna: operando derecho";
        JLabel title = new JLabel("Operador " + group + "   (" + axes + ")");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(10, 4, 4, 4));
        JTable grid = TableFactory.createTable(header(table), rows(table, group));
        grid.setDefaultRenderer(Object.class, new CompatibilityRenderer());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(grid.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(grid, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private String[] header(CompatibilityTable table) {
        List<String> columns = new ArrayList<>();
        columns.add("");
        for (TypeKind kind : CompatibilityTable.PRIMITIVES) {
            columns.add(table.nameOf(kind));
        }
        return columns.toArray(new String[0]);
    }

    private List<String[]> rows(CompatibilityTable table, String group) {
        List<String[]> rows = new ArrayList<>();
        for (TypeKind left : CompatibilityTable.PRIMITIVES) {
            List<String> row = new ArrayList<>();
            row.add(table.nameOf(left));
            for (TypeKind right : CompatibilityTable.PRIMITIVES) {
                row.add(compatibilityPrinter.cellText(table, group, left, right));
            }
            rows.add(row.toArray(new String[0]));
        }
        return rows;
    }

    private static class CompatibilityRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
                int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setHorizontalAlignment(column == 0 ? LEFT : CENTER);
            if (column == 0) {
                cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                cell.setBackground(table.getTableHeader().getBackground());
            } else if (!selected) {
                cell.setBackground("error".equals(value) ? INVALID : VALID);
            }
            return cell;
        }
    }
}
