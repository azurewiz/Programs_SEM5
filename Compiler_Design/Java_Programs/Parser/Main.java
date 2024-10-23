package Parser;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    // Example C program code to test
    String cProgram = """
            #include <stdio.h>
            #include "myHeader.h"

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

    // Run the lexical analyzer
    Lexer.lex(cProgram);
    List<Token> tokens = Lexer.getTokens();

    // Output tokens (can be suppressed if running in the background)
    System.out.println("Tokens:");
    for (Token token : tokens) {
      System.out.println(token);
    }

    // Run the parser on the token list
    Parser parser = new Parser(tokens);
    parser.parse();
  }
}
