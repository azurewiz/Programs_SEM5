%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX_SYMBOLS 100

// Symbol table structure
struct symbol_table {
    char identifier[100];
} symbols[MAX_SYMBOLS];

int symbol_count = 0;

// Function to add identifier to symbol table
void add_to_symbol_table(char *identifier) {
    for (int i = 0; i < symbol_count; i++) {
        if (strcmp(symbols[i].identifier, identifier) == 0) {
            return; // Identifier already in symbol table
        }
    }
    strcpy(symbols[symbol_count++].identifier, identifier);
}

// Function to print the symbol table
void print_symbol_table() {
    printf("\nSymbol Table:\n");
    for (int i = 0; i < symbol_count; i++) {
        printf("%s\n", symbols[i].identifier);
    }
}

%}

%%
"//".*                                  { /* Ignore single-line comments */ }
"/*"([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*"/"  { /* Ignore multi-line comments */ }
"int"|"float"|"if"|"else"|"for"|"while"   { printf("Keyword: %s\n", yytext); }
[0-9]+                                   { printf("Integer Constant: %s\n", yytext); }
[0-9]+"."[0-9]+                          { printf("Floating Constant: %s\n", yytext); }
[a-zA-Z_][a-zA-Z0-9_]*                   { printf("Identifier: %s\n", yytext); add_to_symbol_table(yytext); }
"+"|"-"|"*"|"/"                          { printf("Arithmetic Operator: %s\n", yytext); }
"=="|"!="|"<="|">="|"<"|">"              { printf("Relational Operator: %s\n", yytext); }
"&&"|"||"|"!"                            { printf("Logical Operator: %s\n", yytext); }
[ \t\n]+                                 { /* Ignore whitespace */ }
.                                        { printf("Unknown character: %s\n", yytext); }
%%

int main() {
    printf("Enter code to analyze (CTRL+D to end):\n");
    yylex();  // Start lexical analysis
    print_symbol_table();  // Print the symbol table at the end
    return 0;
}

