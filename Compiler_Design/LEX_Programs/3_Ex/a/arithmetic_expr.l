%{
#include "arithmetic_expr.tab.h"
%}

%%

[0-9]+   { yylval = atoi(yytext); return NUMBER; }

[ \t]+   ;   // ignore whitespace

.        { return yytext[0]; }   // return characters like '+', '-', '*', etc.

%%

int yywrap() {
    return 1;
}

