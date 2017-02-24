// A Token recognized by the lexical analyzer
// DSM, 2017

enum TokenType {
   NAME, NUMBER, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL,
   EQUAL, NOT_EQUAL, ASSIGN, PLUS, MINUS, TIMES, DIVIDE, EOF, IF, WHILE, THEN, DO,
   NEWLINE, UNKNOWN, COMMENT, COLON, COMMA, ENDIF, ENDSUB, ENDWHILE, INPUT, LEFT_PAREN, 
   RIGHT_PAREN, MOD, AND, NOT, OR, PRINT, RETURN, SUB, VAR, END, PROGRAM
}

public class Token {
   TokenType type;
   String value;  // Some tokens, like IDENT, have an associated value
   
   public Token(TokenType type) {
     this.type = type;
     this.value = null;
   }
   
   public Token(TokenType type, String value) {
     this.type = type;
     this.value = value;
   }
   
   public String toString() {
     if (this.type == TokenType.NAME || this.type == TokenType.NUMBER
    		 || this.type == TokenType.UNKNOWN) {
       return "<" + this.type + ", " + this.value + ">";
     } else {
        return "<" + this.type + ">"; 
     }
   }
}