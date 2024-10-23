package Parser;

import java.util.*;
import java.util.regex.*;

class Token {
  String type;
  String value;

  public Token(String type, String value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Token[type=" + type + ", value=" + value + "]";
  }
}

class Lexer {
  private static final String[] keywords = { "int", "float", "char", "double", "return", "void", "if", "else", "while",
      "for" };
  private static final String[] operators = { "+", "-", "*", "/", "=", "==" };
  private static final String[] delimiters = { ";", ",", "(", ")", "{", "}", "[", "]" };

  private static List<Token> tokens = new ArrayList<>();

  // Regular expressions for various token types
  private static final String idPattern = "[a-zA-Z_][a-zA-Z0-9_]*";
  private static final String numPattern = "\\d+";

  // Handle #include, #define and other preprocessor directives
  private static final String includePattern = "#include\\s+[<\"]([a-zA-Z0-9_.]+)[>\"]";
  private static final String definePattern = "#define\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+(\\d+)";

  public static void lex(String code) {
    // Remove comments from code
    code = code.replaceAll("//.*|/\\*.*\\*/", "");

    // Handle #include directives first
    Pattern includeRegex = Pattern.compile(includePattern);
    Matcher includeMatcher = includeRegex.matcher(code);
    while (includeMatcher.find()) {
      String header = includeMatcher.group(1);
      tokens.add(new Token("INCLUDE", header));
    }

    // Handle #define directives
    Pattern defineRegex = Pattern.compile(definePattern);
    Matcher defineMatcher = defineRegex.matcher(code);
    while (defineMatcher.find()) {
      String constName = defineMatcher.group(1);
      String constValue = defineMatcher.group(2);
      tokens.add(new Token("DEFINE", constName));
      tokens.add(new Token("NUMBER", constValue));
    }

    // Tokenize the rest of the code
    String[] tokensArray = code.split("\\s+|(?=[;,+\\-*/(){}=])|(?<=[;,+\\-*/(){}=])");

    for (String token : tokensArray) {
      if (token.isEmpty())
        continue;

      if (isKeyword(token)) {
        tokens.add(new Token("KEYWORD", token));
      } else if (isOperator(token)) {
        tokens.add(new Token("OPERATOR", token));
      } else if (isDelimiter(token)) {
        tokens.add(new Token("DELIMITER", token));
      } else if (token.matches(numPattern)) {
        tokens.add(new Token("NUMBER", token));
      } else if (token.matches(idPattern)) {
        tokens.add(new Token("IDENTIFIER", token));
      } else {
        tokens.add(new Token("UNKNOWN", token));
      }
    }
  }

  public static List<Token> getTokens() {
    return tokens;
  }

  private static boolean isKeyword(String token) {
    return Arrays.asList(keywords).contains(token);
  }

  private static boolean isOperator(String token) {
    return Arrays.asList(operators).contains(token);
  }

  private static boolean isDelimiter(String token) {
    return Arrays.asList(delimiters).contains(token);
  }
}
