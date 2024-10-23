%{
  #include "calc.tab.h"
  #include <stdlib.h>
%}

%option noyywrap

%%
  [0-9]+ { yylval = atoi(yytext); return NUM; }
  [ \t]  {/* ignore whitespace */}
  \n     { return 0; }
  .      { return yytext[0]; }
%%  
