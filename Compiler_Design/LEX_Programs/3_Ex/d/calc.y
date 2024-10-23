%{
  #include <stdio.h>
  #include <stdlib.h>
%}

%token NUM
%left "+" "-"
%left "*" "/"

%%
expr: expr "+" expr { printf("%d\n", $1 + $3); }
    | expr "-" expr { printf("%d\n", $1 + $3); }
    | expr "*" expr { printf("%d\n", $1 + $3); }
    | expr "/" expr { printf("%d\n", $1 + $3); }
    | "(" expr ")"  { $$ = $2; }
    | NUM           { $$ = $1; }
%%

int main() {
  yyparse();
  return 0;
}

int yyerror(char *s) {
  fprintf(stderr, "%s\n", s);
  return 0;
}
