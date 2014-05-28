grammar Corundum;

prog : expression_list;

expression_list : expression terminator
                | expression_list expression terminator
                ;

expression : function_definition
           | require_block
           | if_statement
           | unless_statement
           | rvalue
           | return_statement
           | while_statement
           | for_statement
           ;

require_block : REQUIRE literal_t;

function_definition : function_definition_header function_definition_body END;

function_definition_body : expression_list;

function_definition_header : DEF function_name crlf
                           | DEF function_name function_definition_params crlf
                           ;

function_name : id_function
              | id
              ;

function_definition_params : LEFT_RBRACKET function_definition_params_list RIGHT_RBRACKET
                           | function_definition_params_list
                           ;

function_definition_params_list : id
                                | function_definition_params_list COMMA id
                                ;

return_statement : RETURN rvalue;

function_call : function_name LEFT_RBRACKET function_call_param_list RIGHT_RBRACKET
              | function_name function_call_param_list
              | function_name LEFT_RBRACKET RIGHT_RBRACKET
              ;

function_call_param_list : function_call_params;

function_call_params : rvalue
                     | function_call_params COMMA rvalue
                     ;

if_elsif_statement : ELSIF comparison_list crlf if_expression_list
                   | ELSIF comparison_list crlf if_expression_list else_token crlf if_expression_list
                   | ELSIF comparison_list crlf if_expression_list if_elsif_statement
                   ;

if_statement : IF comparison_list crlf if_expression_list END
             | IF comparison_list crlf if_expression_list else_token crlf if_expression_list END
             | IF comparison_list crlf if_expression_list if_elsif_statement END
             ;

if_expression_list : expression terminator
                   | RETRY terminator
                   | BREAK terminator
                   | if_expression_list expression terminator
                   | if_expression_list RETRY terminator
                   | if_expression_list BREAK terminator
                   ;

unless_statement : UNLESS comparison_list crlf unless_expression_list END;

unless_expression_list : expression_list;

while_statement : WHILE comparison_list crlf while_expression_list END;

while_expression_list : expression terminator
                      | RETRY terminator
                      | BREAK terminator
                      | while_expression_list expression terminator
                      | while_expression_list RETRY terminator
                      | while_expression_list BREAK terminator
                      ;

for_statement : FOR LEFT_RBRACKET init_expression SEMICOLON cond_expression SEMICOLON loop_expression RIGHT_RBRACKET crlf for_expression_list END
              | FOR init_expression SEMICOLON cond_expression SEMICOLON loop_expression crlf for_expression_list END
              ;

init_expression : ( int_assignment | float_assignment | string_assignment | dynamic_assignment );

cond_expression : comparison_list;

loop_expression : expression;

for_expression_list : expression terminator
                    | RETRY terminator
                    | BREAK terminator
                    | for_expression_list expression terminator
                    | for_expression_list RETRY terminator
                    | for_expression_list BREAK terminator
                    ;

assignment : var_id=lvalue op=ASSIGN rvalue
           | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) rvalue
           ;

dynamic_assignment : var_id=lvalue op=ASSIGN dynamic_result
                   | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) dynamic_result
                   ;

int_assignment : var_id=lvalue op=ASSIGN int_result
               | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) int_result
               ;

float_assignment : var_id=lvalue op=ASSIGN float_result
                 | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) float_result
                 ;

string_assignment : var_id=lvalue op=ASSIGN string_result
                  | var_id=lvalue op=PLUS_ASSIGN string_result
                  ;

initial_array_assignment : var_id=lvalue op=ASSIGN LEFT_SBRACKET RIGHT_SBRACKET;

array_assignment : var_id=lvalue array_definition op=ASSIGN array_value;

array_definition : LEFT_SBRACKET array_definition_elements RIGHT_SBRACKET;

array_definition_elements : int_t
                          | array_definition_elements COMMA int_t
                          ;

array_value : ( int_t | float_t | literal_t )
            | dynamic
            ;

array_selector : id LEFT_SBRACKET int_t RIGHT_SBRACKET
               | id_global LEFT_SBRACKET int_t RIGHT_SBRACKET
               ;

dynamic_result : dynamic_result op=( MUL | DIV | MOD ) int_result
               | int_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=( MUL | DIV | MOD ) float_result
               | float_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=MUL string_result
               | string_result op=MUL dynamic_result
               | dynamic_result op=( PLUS | MINUS ) int_result
               | int_result op=( PLUS | MINUS ) dynamic_result
               | dynamic_result op=( PLUS | MINUS )  float_result               
               | float_result op=( PLUS | MINUS )  dynamic_result                      
               | dynamic_result op=( PLUS | MINUS ) dynamic_result
               | LEFT_RBRACKET dynamic_result RIGHT_RBRACKET
               | dynamic
               ;

dynamic : id
        | id_global
        | function_call
        | array_selector
        ;

int_result : int_result op=( MUL | DIV | MOD ) int_result
           | int_result op=( PLUS | MINUS ) int_result
           | LEFT_RBRACKET int_result RIGHT_RBRACKET       
           | int_t
           ;

float_result : float_result op=( MUL | DIV | MOD ) float_result
             | int_result op=( MUL | DIV | MOD ) float_result
             | float_result op=( MUL | DIV | MOD ) int_result
             | float_result op=( PLUS | MINUS ) float_result
             | int_result op=( PLUS | MINUS )  float_result
             | float_result op=( PLUS | MINUS )  int_result
             | LEFT_RBRACKET float_result RIGHT_RBRACKET
             | float_t
             ;

string_result : string_result op=MUL int_result
              | int_result op=MUL string_result
              | string_result op=PLUS string_result
              | literal_t
              ;

comparison_list : comparison BIT_AND comparison_list
                | comparison AND comparison_list
                | comparison BIT_OR comparison_list
                | comparison OR comparison_list
                | LEFT_RBRACKET comparison_list RIGHT_RBRACKET
                | comparison
                ;

comparison : left=rvalue ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) right=rvalue
           | left=rvalue ( EQUAL | NOT_EQUAL ) right=rvalue
           ;

lvalue : id  
       | id_global        
       ;

rvalue : lvalue 
        
       | initial_array_assignment
       | array_assignment

       | int_result
       | float_result
       | string_result

       | dynamic_assignment
       | string_assignment
       | float_assignment
       | int_assignment
       | assignment    

       | function_call
       | literal_t
       | bool_t
       | float_t
       | int_t
       | nil_t 

       | rvalue EXP rvalue

       | ( NOT | BIT_NOT )rvalue

       | rvalue ( MUL | DIV | MOD ) rvalue
       | rvalue ( PLUS | MINUS ) rvalue

       | rvalue ( BIT_SHL | BIT_SHR ) rvalue

       | rvalue BIT_AND rvalue

       | rvalue ( BIT_OR | BIT_XOR )rvalue

       | rvalue ( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) rvalue

       | rvalue ( EQUAL | NOT_EQUAL ) rvalue

       | rvalue ( OR | AND ) rvalue

       | LEFT_RBRACKET rvalue RIGHT_RBRACKET    
       ;

literal_t : LITERAL;

float_t : FLOAT;

int_t : INT;

bool_t : TRUE
       | FALSE
       ;

nil_t : NIL;

id : ID;

id_global : ID_GLOBAL;

id_function : ID_FUNCTION;

terminator : terminator SEMICOLON
           | terminator crlf
           | SEMICOLON
           | crlf
           ;

else_token : ELSE;

crlf : CRLF;

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"'
        | '\'' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '\'';

COMMA : ',';  
SEMICOLON : ';';
CRLF : '\n';

REQUIRE : 'require';
END : 'end';
DEF : 'def';
RETURN : 'return';

IF: 'if';
ELSE : 'else';
ELSIF : 'elsif';
UNLESS : 'unless';
WHILE : 'while';
RETRY : 'retry';
BREAK : 'break';
FOR : 'for';

TRUE : 'true';
FALSE : 'false';

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EXP : '**';

EQUAL : '==';
NOT_EQUAL : '!=';
GREATER : '>';
LESS : '<';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';

ASSIGN : '=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
MOD_ASSIGN : '%=';
EXP_ASSIGN : '**=';

BIT_AND : '&';
BIT_OR : '|';
BIT_XOR : '^';
BIT_NOT : '~';
BIT_SHL : '<<';
BIT_SHR : '>>';

AND : 'and' | '&&';
OR : 'or' | '||';
NOT : 'not' | '!';

LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';

NIL : 'nil';

SL_COMMENT : ('#' ~('\r' | '\n')* '\n') -> skip;
ML_COMMENT : ('=begin' .*? '=end\n') -> skip;
WS : (' '|'\t')+ -> skip;

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]+;
ID : [a-zA-Z_][a-zA-Z0-9_]*;
ID_GLOBAL : '$'ID;
ID_FUNCTION : ID[!?];
