parser grammar YParser;

options {
    tokenVocab = YLexer;
}

program
    : structSection? functionSection EOF
    ;

structSection
    : SECCION_ESTRUCTURAS NEWLINE structDefinition*
    ;

functionSection
    : SECCION_FUNCIONES NEWLINE functionDefinition*
    ;

structDefinition
    : ESTRUCTURA ID COLON NEWLINE INDENT fieldDefinition+ DEDENT
    ;

fieldDefinition
    : type ID dimensions? SEMICOLON? NEWLINE
    ;

functionDefinition
    : DEFINIR ID LPAREN parameters? RPAREN (ARROW type)? COLON block
    ;

parameters
    : parameter (COMMA parameter)*
    ;

parameter
    : type ID                         # valueParameter
    | (LBRACKET RBRACKET)+ type ID    # arrayParameter
    | LBRACE RBRACE ID ID             # structParameter
    ;

block
    : NEWLINE INDENT statement+ DEDENT
    ;

// sentencias
statement
    : structDefinition
    | simpleStatement SEMICOLON? NEWLINE
    | controlStatement
    ;

simpleStatement
    : declaration                            # declarationStatement
    | assignment                             # assignmentStatement
    | IMPRIMIR LPAREN expression RPAREN      # printStatement
    | RETORNAR expression?                   # returnStatement
    | ROMPER                                 # breakStatement
    | CONTINUAR                              # continueStatement
    | expression                             # expressionStatement
    ;

declaration
    : type ID dimensions? (ASSIGN expression)?
    ;

assignment
    : expression ASSIGN expression
    ;

dimensions
    : (LBRACKET expression RBRACKET)+
    ;

controlStatement
    : ifStatement
    | switchStatement
    | forStatement
    | whileStatement
    | doWhileStatement
    ;

ifStatement
    : SI LPAREN expression RPAREN ENTONCES block elseIfClause* elseClause?
    ;

elseIfClause
    : SINO LPAREN expression RPAREN ENTONCES block
    ;

elseClause
    : CONTRARIO block
    ;

switchStatement
    : ELEGIR LPAREN expression RPAREN LBRACE NEWLINE INDENT caseClause* defaultClause? DEDENT RBRACE NEWLINE
    ;

caseClause
    : CASO expression COLON block
    ;

defaultClause
    : SIEMPRE COLON block
    ;

forStatement
    : PARA LPAREN forInit? SEMICOLON expression? SEMICOLON forUpdate? RPAREN COLON block
    ;

forInit
    : declaration
    | assignment
    ;

forUpdate
    : assignment
    | expression
    ;

whileStatement
    : MIENTRAS LPAREN expression RPAREN HACER block
    ;

doWhileStatement
    : HACER COLON block MIENTRAS LPAREN expression RPAREN SEMICOLON? NEWLINE
    ;

// expresiones
expression
    : expression DOT ID                               # fieldAccessExpr
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
    | LEER LPAREN RPAREN                                 # readAtom
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
    | VERDADERO
    | FALSO
    ;

type
    : ENTERO
    | FLOTANTE
    | CADENA
    | CARACTER
    | BOOL
    | ID
    ;
