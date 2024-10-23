%{
#include <stdio.h>
#include <stdlib.h>
%}

%token NUMBER

%left '+' '-'
%left '*' '/'

%%

expr:
    expr '+' expr   { printf("Addition\n"); }
    | expr '-' expr   { printf("Subtraction\n"); }
    | expr '*' expr   { printf("Multiplication\n"); }
    | expr '/' expr   { printf("Division\n"); }
    | '(' expr ')'   { /* Parenthesized expression */ }
    | NUMBER         { /* Single number */ }
    ;

%%

int main() {
    printf("Enter an arithmetic expression: ");
    yyparse();
    return 0;
}

int yyerror(char *s) {
    printf("Error: %s\n", s);
    return 0;
}

int yylex() {
    int c;
    while((c = getchar()) == ' ' || c == '\t');  // skip whitespaces

    if (c == EOF)
        return 0;
    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')')
        return c;
    if (isdigit(c)) {
        yylval = c - '0';
        return NUMBER;
    }
    return 0;
}

