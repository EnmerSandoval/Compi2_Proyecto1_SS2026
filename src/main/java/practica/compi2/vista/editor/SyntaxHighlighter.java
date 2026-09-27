package practica.compi2.vista.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public class SyntaxHighlighter implements DocumentListener, ActionListener {

    private final JTextPane editor;
    private final TokenStyles styles;
    private final Timer timer;
    private String extension;

    public SyntaxHighlighter(JTextPane editor) {
        this.editor = editor;
        this.styles = new TokenStyles(editor);
        this.timer = new Timer(250, this);
        this.timer.setRepeats(false);
        this.extension = "";
        editor.getDocument().addDocumentListener(this);
    }

    public void setExtension(String extension) {
        this.extension = extension;
        highlight();
    }

    public void highlight() {
        StyledDocument document = editor.getStyledDocument();
        String text;
        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            return;
        }
        document.setCharacterAttributes(0, text.length(), styles.getNormal(), true);
        Lexer lexer = LexerFactory.create(extension, text);
        if (lexer == null) {
            return;
        }
        Token token = lexer.nextToken();
        while (token.getType() != Token.EOF) {
            int start = token.getStartIndex();
            int stop = token.getStopIndex();
            if (stop >= start && stop < text.length()) {
                document.setCharacterAttributes(start, stop - start + 1,
                        styles.styleFor(lexer.getVocabulary(), token.getType()), true);
            }
            token = lexer.nextToken();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        highlight();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        timer.restart();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        timer.restart();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
    }
}
