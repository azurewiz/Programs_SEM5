package Symbol_Table;

import java.util.*;
import java.util.regex.*;

class Symbol {
  String name;
  String type;
  String category; // Variable, Function, Constant
  String scope; // Global, Local, etc.

  public Symbol(String name, String type, String category, String scope) {
    this.name = name;
    this.type = type;
    this.category = category;
    this.scope = scope;
  }

  @Override
  public String toString() {
    return "Name: " + name + ", Type: " + type + ", Category: " + category + ", Scope: " + scope;
  }
}

public class CSymbolTable {
  // HashMap for storing symbols
  private static Map<String, Symbol> symbolTable = new HashMap<>();

  // Recognize C program components and create the symbol table
  public static void recognizeAndBuildSymbolTable(String code) {
    // Pattern for variable declaration (supports int, float, char, double, etc.)
    String varPattern = "\\b(int|float|double|char)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*;";
    String funcPattern = "\\b(int|float|double|char|void)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(.*\\)\\s*\\{";
    String constPattern = "#define\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+([0-9]+)";

    // Match variable declarations
    Pattern variable = Pattern.compile(varPattern);
    Matcher varMatcher = variable.matcher(code);
    while (varMatcher.find()) {
      String type = varMatcher.group(1);
      String name = varMatcher.group(2);
      symbolTable.put(name, new Symbol(name, type, "Variable", "Global"));
    }

    // Match function definitions
    Pattern function = Pattern.compile(funcPattern);
    Matcher funcMatcher = function.matcher(code);
    while (funcMatcher.find()) {
      String returnType = funcMatcher.group(1);
      String funcName = funcMatcher.group(2);
      symbolTable.put(funcName, new Symbol(funcName, returnType, "Function", "Global"));
    }

    // Match constants
    Pattern constant = Pattern.compile(constPattern);
    Matcher constMatcher = constant.matcher(code);
    while (constMatcher.find()) {
      String constName = constMatcher.group(1);
      String constValue = constMatcher.group(2);
      symbolTable.put(constName, new Symbol(constName, constValue, "Constant", "Global"));
    }
  }

  // Display the symbol table
  public static void displaySymbolTable() {
    System.out.println("Symbol Table:");
    for (Symbol symbol : symbolTable.values()) {
      System.out.println(symbol);
    }
  }

  public static void main(String[] args) {
    // Example C code
    String cProgram = """
            #include <stdio.h>
            #define MAX 100

            int main() {
                int x;
                float y;
                char z;
                return 0;
            }

            void foo() {
                int a;
            }
        """;

    recognizeAndBuildSymbolTable(cProgram);
    displaySymbolTable();
  }
}
