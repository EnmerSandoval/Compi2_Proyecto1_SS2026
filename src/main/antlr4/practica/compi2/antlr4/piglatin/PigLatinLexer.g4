lexer grammar PigLatinLexer;

IMPORT      : 'import';
VARIABILES  : 'VARIABILES>';
MAIOR       : 'MAIOR>';
FINIS_MAIOR : 'FINIS';
ESTO        : 'esto';
SERIES      : 'series';
NOVUS       : 'novus';
NULL        : 'null';
SI          : 'si';
ALITER      : 'aliter';
FINIS       : 'finis';
DUM         : 'dum';
FACERE      : 'facere';
PER         : 'per';
PERGE       : 'perge';
INTERRUMPE  : 'interrumpe';
VERUM       : 'verum';
FALSUS      : 'falsus';

NUMERUS   : 'numerus';
DECIMALIS : 'decimalis';
TEXTUM    : 'textum';
LITTERA   : 'littera';
BOOL      : 'bool';

PRINT     : '>>';
READ      : '<<';
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
LPAREN    : '(';
RPAREN    : ')';
LBRACKET  : '[';
RBRACKET  : ']';
LBRACE    : '{';
RBRACE    : '}';
COMMA     : ',';
COLON     : ':';
SEMICOLON : ';';
DOT       : '.';

DECIMAL_LIT : [0-9]+ '.' [0-9]+;
INT_LIT     : [0-9]+;
CHAR_LIT    : '\'' (ESCAPE | ~['\\\r\n]) '\'';
STRING_LIT  : '"' (ESCAPE | ~["\\\r\n])* '"'
            | '“' ~[”\r\n]* '”'
            ;
ID          : [a-zA-Z_] [a-zA-Z0-9_]*;

BLOCK_COMMENT : '##' .*? '##' -> channel(HIDDEN);
LINE_COMMENT  : '//' ~[\r\n]* -> channel(HIDDEN);
WS            : [ \t\r\n]+ -> skip;

fragment ESCAPE : '\\' [nrt'"\\];
