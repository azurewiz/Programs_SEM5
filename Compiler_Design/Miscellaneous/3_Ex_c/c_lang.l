%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX_SYMBOLS 100

extern FILE *yyin;
%}

%option noyywrap

%%
"//".*                                  { /* Ignore single-line comments */ }
"/*"([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*"/"  { /* Ignore multi-line comments */ }
"int"|"float"|"if"|"else"|"for"|"while"   { printf("Keyword: %s\n", yytext); }
[0-9]+                                   { printf("Integer Constant: %s\n", yytext); }
[0-9]+"."[0-9]+                          { printf("Floating Constant: %s\n", yytext); }
[a-zA-Z_][a-zA-Z0-9_]*                   { printf("Identifier: %s\n", yytext);  }
"+"|"-"|"*"|"/"                          { printf("Arithmetic Operator: %s\n", yytext); }
"=="|"!="|"<="|">="|"<"|">"              { printf("Relational Operator: %s\n", yytext); }
"("|")"|"["|"]"|"{"|"}"                  { printf("Symbol: %s\n", yytext); }
"&&"|"||"|"!"                            { printf("Logical Operator: %s\n", yytext); }
[ \t\n]+                                 { /* Ignore whitespace */ }
.                                        { printf("Unknown character: %s\n", yytext); }
%%

int main(int argc, char **argv) {
  if (argc > 1) {
    FILE *file = fopen(argv[1], "r");
    if (!file) {
      perror("Could not open the file");
      return 1;
    }
    yyin = file;
  }
  yylex();
  return 0;
}

//int yywarp() {
  //return 1;
//}
