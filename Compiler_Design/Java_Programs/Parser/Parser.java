package Parser;

import java.util.List;

class Parser {
  private List<Token> tokens;
  private int currentTokenIndex = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  // Start parsing
  public void parse() {
    while (currentTokenIndex < tokens.size()) {
      Token currentToken = tokens.get(currentTokenIndex);
      switch (currentToken.type) {
        case "INCLUDE":
          parseIncludeDirective();
          break;
        case "DEFINE":
          parseDefineDirective();
          break;
        case "KEYWORD":
          if (currentToken.value.equals("int") || currentToken.value.equals("float")
              || currentToken.value.equals("char")) {
            parseVariableDeclaration();
          } else if (currentToken.value.equals("void")) {
            parseFunctionDeclaration();
          }
          break;
        default:
          System.out.println("Syntax Error: Unexpected token " + currentToken.value);
          currentTokenIndex++;
      }
    }
  }

  private void parseIncludeDirective() {
    System.out.println("Parsing Include Directive...");
    currentTokenIndex++; // Skip the INCLUDE token
    System.out.println("#include <" + tokens.get(currentTokenIndex - 1).value + "> recognized.");
  }

  private void parseDefineDirective() {
    System.out.println("Parsing Define Directive...");
    // Current token is DEFINE
    String constName = tokens.get(currentTokenIndex).value;
    currentTokenIndex++; // Move to constant value
    String constValue = tokens.get(currentTokenIndex).value;
    System.out.println("#define " + constName + " " + constValue + " recognized.");
    currentTokenIndex++;
  }

  private void parseVariableDeclaration() {
    System.out.println("Parsing Variable Declaration...");
    // Example: `int x;`
    currentTokenIndex++; // Move to identifier
    if (tokens.get(currentTokenIndex).type.equals("IDENTIFIER")) {
      String varName = tokens.get(currentTokenIndex).value;
      currentTokenIndex++;
      if (tokens.get(currentTokenIndex).value.equals(";")) {
        System.out.println("Variable " + varName + " declared successfully.");
        currentTokenIndex++; // Skip ';'
      } else {
        System.out.println("Syntax Error: Missing semicolon after variable declaration.");
        currentTokenIndex++;
      }
    } else {
      System.out.println("Syntax Error: Expected identifier.");
    }
  }

  private void parseFunctionDeclaration() {
    System.out.println("Parsing Function Declaration...");
    // Example: `void foo() { }`
    currentTokenIndex++; // Move to function name
    if (tokens.get(currentTokenIndex).type.equals("IDENTIFIER")) {
      String funcName = tokens.get(currentTokenIndex).value;
      currentTokenIndex++; // Move to '('
      if (tokens.get(currentTokenIndex).value.equals("(")) {
        currentTokenIndex++; // Skip '('
        if (tokens.get(currentTokenIndex).value.equals(")")) {
          currentTokenIndex++; // Skip ')'
          if (tokens.get(currentTokenIndex).value.equals("{")) {
            System.out.println("Function " + funcName + " defined successfully.");
            currentTokenIndex++; // Skip '{'
            // Skip until we hit closing brace '}' for simplicity
            while (!tokens.get(currentTokenIndex).value.equals("}")) {
              currentTokenIndex++;
            }
            currentTokenIndex++; // Skip '}'
          } else {
            System.out.println("Syntax Error: Expected '{' after function declaration.");
          }
        } else {
          System.out.println("Syntax Error: Expected ')' after function parameters.");
        }
      } else {
        System.out.println("Syntax Error: Expected '(' after function name.");
      }
    } else {
      System.out.println("Syntax Error: Expected function name.");
    }
  }
}
