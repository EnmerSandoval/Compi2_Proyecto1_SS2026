lexer grammar YLexer;

options {
    superClass = YLexerBase;
}

tokens {
    INDENT,
    DEDENT
}

SECCION_ESTRUCTURAS : '%estructuras';
SECCION_FUNCIONES   : '%funciones';

ESTRUCTURA : 'estructura';
DEFINIR    : 'definir';
RETORNAR   : 'retornar';
SI         : 'si';
ENTONCES   : 'entonces';
SINO       : 'sino';
CONTRARIO  : 'contrario';
ELEGIR     : 'elegir';
CASO       : 'caso';
SIEMPRE    : 'siempre';
ROMPER     : 'romper';
CONTINUAR  : 'continuar';
PARA       : 'para';
MIENTRAS   : 'mientras';
HACER      : 'hacer';
IMPRIMIR   : 'imprimir';
LEER       : 'leer';
VERDADERO  : 'verdadero';
FALSO      : 'falso';

ENTERO   : 'entero';
FLOTANTE : 'flotante';
CADENA   : 'cadena';
CARACTER : 'caracter';
BOOL     : 'bool';

ARROW     : '->';
INC       : '++';
DEC       : '--';
EQ        : '==';
NE        : '!=';
LE        : '<=';
GE        : '>=';
LT        : '<';
GT        : '>';
AND       : '&&';
OR        : '||';
NOT       : '!';
ASSIGN    : '=';
PLUS      : '+';
MINUS     : '-';
MULT      : '*';
DIV       : '/';
LPAREN    : '(' {openBracket();};
RPAREN    : ')' {closeBracket();};
LBRACKET  : '[' {openBracket();};
RBRACKET  : ']' {closeBracket();};
LBRACE    : '{' {openBracket();};
RBRACE    : '}' {closeBracket();};
COMMA     : ',';
COLON     : ':';
SEMICOLON : ';';
DOT       : '.';

DECIMAL_LIT : [0-9]+ '.' [0-9]+;
INT_LIT     : [0-9]+;
CHAR_LIT    : '\'' (ESCAPE | ~['\\\r\n]) '\'';
STRING_LIT  : '"' (ESCAPE | ~["\\\r\n])* '"';
ID          : [a-zA-Z_] [a-zA-Z0-9_]*;

NEWLINE : ('\r'? '\n' | '\r') [ \t]* {onNewLine();};

LINE_COMMENT  : '//' ~[\r\n]* -> channel(HIDDEN);
BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN);
WS            : [ \t]+ -> skip;

fragment ESCAPE : '\\' [nrt'"\\];
