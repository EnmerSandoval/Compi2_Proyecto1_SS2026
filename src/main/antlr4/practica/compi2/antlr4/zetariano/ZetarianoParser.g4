parser grammar ZetarianoParser;

options {
    tokenVocab = ZetarianoLexer;
}

compilationUnit
    : classDeclaration EOF
    ;

classDeclaration
    : accessModifier? CLASS ID (EXTENDS ID)? LBRACE classMember* RBRACE
    ;

classMember
    : fieldDeclaration
    | constructorDeclaration
    | methodDeclaration
    ;

accessModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    ;

fieldDeclaration
    : accessModifier? type ID (ASSIGN expression)? SEMICOLON
    ;

constructorDeclaration
    : accessModifier? ID LPAREN parameters? RPAREN block
    ;

methodDeclaration
    : OVERRIDE? accessModifier? (type | VOID) ID LPAREN parameters? RPAREN block
    ;

parameters
    : parameter (COMMA parameter)*
    ;

parameter
    : type ID
    ;

type
    : (primitiveType | ID) (LBRACKET RBRACKET)*
    ;

primitiveType
    : INT
    | DOUBLE
    | CHAR
    | BOOLEAN
    | STRING
    ;

block
    : LBRACE statement* RBRACE
    ;

statement
    : block
    | simpleStatement SEMICOLON
    | controlStatement
    | SEMICOLON
    ;

simpleStatement
    : localDeclaration                               # declarationStatement
    | assignment                                     # assignmentStatement
    | (PRINTLN | PRINT) LPAREN expression? RPAREN     # printStatement
    | SUPER LPAREN arguments? RPAREN                   # superCallStatement
    | THIS LPAREN arguments? RPAREN                    # thisCallStatement
    | RETURN expression?                             # returnStatement
    | BREAK                                          # breakStatement
    | CONTINUE                                       # continueStatement
    | expression                                     # expressionStatement
    ;

localDeclaration
    : type ID (ASSIGN expression)?
    ;

assignment
    : expression op=(ASSIGN | ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN) expression
    ;

controlStatement
    : IF LPAREN expression RPAREN statement (ELSE statement)?                            # ifStatement
    | SWITCH LPAREN expression RPAREN LBRACE switchSection* RBRACE                      # switchStatement
    | FOR LPAREN forInit? SEMICOLON expression? SEMICOLON forUpdate? RPAREN statement   # forStatement
    | WHILE LPAREN expression RPAREN statement                                          # whileStatement
    | DO statement WHILE LPAREN expression RPAREN SEMICOLON                             # doWhileStatement
    ;

switchSection
    : (CASE expression | DEFAULT) COLON statement*
    ;

forInit
    : localDeclaration
    | assignment
    ;

forUpdate
    : assignment
    | expression
    ;

expression
    : expression DOT ID LPAREN arguments? RPAREN                      # methodCallExpr
    | expression DOT ID                                               # fieldAccessExpr
    | expression LBRACKET expression RBRACKET                         # arrayAccessExpr
    | expression op=(INC | DEC)                                       # postIncDecExpr
    | op=(INC | DEC) expression                                       # preIncDecExpr
    | op=(NOT | MINUS) expression                                     # unaryExpr
    | expression op=(MULT | DIV | MOD) expression                     # mulExpr
    | expression op=(PLUS | MINUS) expression                         # addExpr
    | expression op=(LT | GT | LE | GE) expression                    # relExpr
    | expression op=(EQ | NE) expression                              # eqExpr
    | expression AND expression                                       # andExpr
    | expression OR expression                                        # orExpr
    | <assoc=right> expression QUESTION expression COLON expression    # ternaryExpr
    | atom                                                            # atomExpr
    ;

atom
    : LPAREN expression RPAREN                                  # groupAtom
    | literal                                                   # literalAtom
    | THIS                                                      # thisAtom
    | SUPER                                                     # superAtom
    | NULL                                                      # nullAtom
    | ID LPAREN arguments? RPAREN                               # callAtom
    | ID                                                        # idAtom
    | NEW ID LPAREN arguments? RPAREN                           # newObjectAtom
    | NEW (primitiveType | ID) (LBRACKET expression RBRACKET)+  # newArrayAtom
    | LBRACE (expression (COMMA expression)*)? RBRACE           # listAtom
    | READLN LPAREN RPAREN                                      # readAtom
    ;

arguments
    : expression (COMMA expression)*
    ;

literal
    : INT_LIT
    | DECIMAL_LIT
    | STRING_LIT
    | CHAR_LIT
    | TRUE
    | FALSE
    ;
