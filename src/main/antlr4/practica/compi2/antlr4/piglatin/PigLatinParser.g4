parser grammar PigLatinParser;

options {
    tokenVocab = PigLatinLexer;
}

program
    : importStatement* variablesSection? mainSection EOF
    ;

importStatement
    : IMPORT ID (DOT ID)+ SEMICOLON?
    ;

variablesSection
    : VARIABILES declaration*
    ;

mainSection
    : MAIOR statement* FINIS_MAIOR SEMICOLON
    ;

declaration
    : ESTO ID COLON type? expression? SEMICOLON               # variableDeclaration
    | SERIES ID dimensions COLON type expression? SEMICOLON    # arrayDeclaration
    ;

dimensions
    : (LBRACKET expression RBRACKET)+
    ;

block
    : LBRACE statement* RBRACE
    ;

// sentencias
statement
    : declaration
    | simpleStatement
    | controlStatement
    ;

simpleStatement
    : assignment SEMICOLON                              # assignmentStatement
    | PRINT expression (PRINT expression)* SEMICOLON?   # printStatement
    | expression? READ SEMICOLON?                       # readStatement
    | PERGE SEMICOLON                                   # continueStatement
    | INTERRUMPE SEMICOLON                              # breakStatement
    | expression SEMICOLON                              # expressionStatement
    ;

assignment
    : expression ASSIGN expression
    ;

controlStatement
    : SI LPAREN expression RPAREN block elseIfClause* elseClause? FINIS SEMICOLON                        # ifStatement
    | DUM LPAREN expression RPAREN block FINIS SEMICOLON                                                  # whileStatement
    | FACERE block DUM LPAREN expression RPAREN SEMICOLON                                                 # doWhileStatement
    | PER LPAREN forInit? SEMICOLON expression? SEMICOLON forUpdate? RPAREN block (FINIS SEMICOLON)?      # forStatement
    ;

elseIfClause
    : ALITER LPAREN expression RPAREN block
    ;

elseClause
    : ALITER block
    ;

forInit
    : ESTO ID COLON type? expression    # forDeclaration
    | assignment                        # forAssignment
    ;

forUpdate
    : assignment
    | expression
    ;

// expresiones
expression
    : expression DOT ID LPAREN arguments? RPAREN      # methodCallExpr
    | expression DOT ID                               # fieldAccessExpr
    | expression LBRACKET expression RBRACKET         # arrayAccessExpr
    | expression op=(INC | DEC)                       # postIncDecExpr
    | op=(NOT | MINUS) expression                     # unaryExpr
    | expression op=(MULT | DIV) expression           # mulExpr
    | expression op=(PLUS | MINUS) expression         # addExpr
    | expression op=(LT | GT | LE | GE) expression    # relExpr
    | expression op=(EQ | NE) expression             # eqExpr
    | expression AND expression                       # andExpr
    | expression OR expression                        # orExpr
    | atom                                            # atomExpr
    ;

atom
    : LPAREN expression RPAREN                           # groupAtom
    | literal                                            # literalAtom
    | ID LPAREN arguments? RPAREN                        # callAtom
    | ID                                                 # idAtom
    | NOVUS ID LPAREN arguments? RPAREN                  # newObjectAtom
    | LBRACE (expression (COMMA expression)*)? RBRACE    # listAtom
    ;

arguments
    : expression (COMMA expression)*
    ;

literal
    : INT_LIT
    | DECIMAL_LIT
    | STRING_LIT
    | CHAR_LIT
    | VERUM
    | FALSUS
    ;

type
    : NUMERUS
    | DECIMALIS
    | TEXTUM
    | LITTERA
    | ID
    ;
