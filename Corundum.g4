grammar Corundum;

@members {
  public int SemanticErrorsNum = 0;
  public int NumStr = 1;
  java.util.LinkedList<String> definitions = new java.util.LinkedList<String>();

  public static boolean is_defined(java.util.LinkedList<String> definitions, String variable) {
       int index = definitions.indexOf(variable);
       if (index == -1) {
         return false;
       }
       return true;
  }
}

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
                             {
                              String func = $function_name.text;
                              if (is_defined(definitions, func)) {
                                System.out.println("line " + NumStr + " Error! Function " + func + " is already defined!");

                                SemanticErrorsNum++;
                              } 
                              else {
                                definitions.add(func);
                              }
                             }
                           | DEF function_name function_definition_params crlf
                             {
                              String func = $function_name.text;
                              if (is_defined(definitions, func)) {
                                System.out.println("line " + NumStr + " Error! Function " + func + " is already defined!");
                                SemanticErrorsNum++;
                              } 
                              else {
                                definitions.add(func);
                              }
                             }
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
                {
                  String func = $function_name.text;
                  if (!is_defined(definitions, func)) {
                    System.out.println("line " + NumStr + " Error! Undefined function " + func + "!");
                    SemanticErrorsNum++;
                  }
                } # FunctionCall
              | function_name function_call_param_list
                {
                  String func = $function_name.text;
                  if (!is_defined(definitions, func)) {
                    System.out.println("line " + NumStr + " Error! Undefined function " + func + "!");
                    SemanticErrorsNum++;
                  }
                } # FunctionCall
              | function_name LEFT_RBRACKET RIGHT_RBRACKET
                {
                  String func = $function_name.text;
                  if (!is_defined(definitions, func)) {
                    System.out.println("line " + NumStr + " Error! Undefined function " + func + "!");
                    SemanticErrorsNum++;
                  }
                } # FunctionCall
              ;

function_call_param_list : function_call_params;

function_call_params : rvalue
                     | function_call_params COMMA rvalue
                     ;

if_elsif_statement : ELSIF rvalue crlf elsif_expression_list
                   | ELSIF rvalue crlf elsif_expression_list ELSE crlf elsif_expression_list
                   | ELSIF rvalue crlf elsif_expression_list if_elsif_statement
                   ;

elsif_expression_list : expression_list;

if_statement : IF rvalue crlf if_expression_list END
             | IF rvalue THEN if_expression_list END
             | IF rvalue crlf if_expression_list ELSE crlf if_expression_list END
             | IF rvalue THEN if_expression_list ELSE if_expression_list END
             | IF rvalue crlf if_expression_list if_elsif_statement END
             ;

if_expression_list : expression_list;

unless_statement : UNLESS rvalue crlf unless_expression_list END;

unless_expression_list : expression_list;

while_statement : WHILE rvalue crlf while_expression_list END;

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

init_expression : expression;

cond_expression : expression;

loop_expression : expression;

for_expression_list : expression terminator
                    | RETRY terminator
                    | BREAK terminator
                    | for_expression_list expression terminator
                    | for_expression_list RETRY terminator
                    | for_expression_list BREAK terminator
                    ;

assignment : lvalue op=ASSIGN rvalue
             {
              definitions.add($lvalue.text);
             }
           | lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) rvalue
             {
              String variable = $lvalue.text;
              if (!is_defined(definitions, variable)) {
                System.out.println("line " + NumStr + " Error! Undefined variable " + variable + "!");
                SemanticErrorsNum++;
              }         
             }
           ;

dynamic_assignment : lvalue op=ASSIGN dynamic_result
                   | lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) dynamic_result
                   ;

int_assignment : var_id=lvalue op=ASSIGN int_result
               | var_id=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) int_result
               ;

float_assignment : lvalue op=ASSIGN float_result
                 | lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) float_result
                 ;

string_assignment : lvalue op=ASSIGN string_result
                  | lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) string_result
                  ;

array_assignment : lvalue array_definition ASSIGN rvalue
                   {
                    String variable = $lvalue.text;
                    if (!is_defined(definitions, variable)) {
                      System.out.println("line " + NumStr + " Error! Undefined variable " + variable + "!");
                      SemanticErrorsNum++;
                    }
                   }
                 | lvalue ASSIGN array_definition
                   {
                    definitions.add($lvalue.text);
                   }
                 ;

array_definition : LEFT_SBRACKET array_definition_elements RIGHT_SBRACKET
                 | LEFT_SBRACKET RIGHT_SBRACKET
                 ;

array_definition_elements : rvalue
                          | array_definition_elements COMMA rvalue
                          ;

array_selector : id LEFT_SBRACKET rvalue RIGHT_SBRACKET
               | id_global LEFT_SBRACKET rvalue RIGHT_SBRACKET
               | function_call LEFT_SBRACKET rvalue RIGHT_SBRACKET
               ;

dynamic_result : dynamic_result op=( MUL | DIV | MOD ) int_result
               | dynamic_result op=( PLUS | MINUS ) int_result
               | int_result op=( MUL | DIV | MOD ) dynamic_result
               | int_result op=( PLUS | MINUS ) dynamic_result
               | dynamic_result ( MUL | DIV | MOD ) float_result
               | dynamic_result ( PLUS | MINUS )  float_result
               | float_result ( MUL | DIV | MOD ) dynamic_result
               | float_result ( PLUS | MINUS )  dynamic_result
               | dynamic_result MUL string_result
               | string_result MUL dynamic_result
               | dynamic_result op=( MUL | DIV | MOD ) dynamic_result
               | dynamic_result op=( PLUS | MINUS ) dynamic_result
               | LEFT_RBRACKET dynamic_result RIGHT_RBRACKET
               | dynamic
               ;

dynamic : id
        | id_global
        | function_call
        ;

int_result : int_result op=( MUL | DIV | MOD ) int_result
           | int_result op=( PLUS | MINUS ) int_result
           | LEFT_RBRACKET int_result RIGHT_RBRACKET       
           | int_t
           ;

float_result : float_result ( MUL | DIV | MOD ) float_result
             | int_result ( MUL | DIV | MOD ) float_result
             | float_result ( MUL | DIV | MOD ) int_result
             | float_result ( PLUS | MINUS ) float_result
             | int_result ( PLUS | MINUS )  float_result
             | float_result ( PLUS | MINUS )  int_result
             | LEFT_RBRACKET float_result RIGHT_RBRACKET
             | float_t
             ;

string_result : string_result MUL int_result
              | int_result MUL string_result
              | literal_t
              ;

lvalue : id  
       | id_global        
       ;

rvalue : lvalue 
         {
          String variable = $lvalue.text;
          if (!is_defined(definitions, variable)) {
            System.out.println("line " + NumStr + " Error! Undefined variable " + variable + "!");
            SemanticErrorsNum++;
          }
         }       
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

crlf : CRLF
       {
        NumStr++;
       }
     ;

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
THEN : 'then';
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
