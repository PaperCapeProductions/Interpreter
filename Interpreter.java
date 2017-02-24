import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Scanner;

public class Interpreter {
 
  ArrayList<Token> program;
  int pc;
  Token currentToken;
  HashMap<String, Integer> symbols;
  int temp;
  boolean isLoop = false;
  
  public Interpreter(String filename) throws Exception {
    
    Lexer lexer = new Lexer(filename);
    this.program = new ArrayList<Token>();
    Token t;
    this.pc = 0;
    
    this.symbols = new HashMap<String, Integer>();
    
    // Read all of the program tokens into an ArrayList
    do {
      t = lexer.nextToken();
      this.program.add(t);
    } while (t.type != TokenType.EOF);
    
    // Get the first Token
    this.pc = 0;
    this.currentToken = this.program.get(this.pc);
  }
  
  
  public void consume(TokenType expected) throws Exception {
    
    // If the currentToken matches the specified token, consume it and
    // advance to the next token. If not, throw an error.
    
    if (this.currentToken.type != expected) {
      throw new Exception("Expected " + expected + ", but found " + currentToken.type + "."); 
    }
    
    this.pc++;
    if (this.pc < this.program.size()) {
      this.currentToken = this.program.get(this.pc);
    }
  }
  
  
  public int evalFactor() throws Exception {
    // A factor is either a literal number, a variable reference, or
    // another expression in parentheses
    int value = 0;
    
    if (this.currentToken.type == TokenType.NUMBER) {
      value = Integer.parseInt(this.currentToken.value);
      this.consume(TokenType.NUMBER);
    } else if (this.currentToken.type == TokenType.NAME) { // Variable
      String name = this.currentToken.value;
      this.consume(TokenType.NAME);
      
      if (this.symbols.containsKey(name)) {
        return this.symbols.get(name); 
      } else {
        throw new Exception("Unrecognized symbol: " + name); 
      }
      
    } else if (this.currentToken.type == TokenType.LEFT_PAREN) {
      this.consume(TokenType.LEFT_PAREN);
      value = this.evalExpression();
      this.consume(TokenType.RIGHT_PAREN);
    } else {
      throw new Exception("Expected number, variable name, or parenthesized expression, found " + this.currentToken.type); 
    }
    
    return value;
  }
  
  
  
  public int evalUnaryTerm() throws Exception {
    // A unary expression is either a factor or a factor
    // prefixed by a unary negation operator
    if (this.currentToken.type == TokenType.MINUS) {
      this.consume(TokenType.MINUS);
      int value = this.evalFactor();
      return -value;
    } else {
      return this.evalFactor(); 
    }
  }
  
  
  public int evalTerm() throws Exception {
    // A term consists of at least one factor ,followed by any 
    // number of terms separated by multiplication and division operations
    int value = this.evalUnaryTerm();
    
    while (this.currentToken.type == TokenType.TIMES ||
           this.currentToken.type == TokenType.DIVIDE ||
           this.currentToken.type == TokenType.MOD) {
      if (this.currentToken.type == TokenType.TIMES) {
        this.consume(TokenType.TIMES);
        value = value * this.evalUnaryTerm();
      } else if (this.currentToken.type == TokenType.DIVIDE) {
        this.consume(TokenType.DIVIDE); 
        value = value / this.evalUnaryTerm();
      } else {
        this.consume(TokenType.MOD); 
        value = value % this.evalUnaryTerm();
      }
    } 
    
    return value;
  }
  
  
  public int evalExpression() throws Exception {
    // An expression consists of at least one term ,followed
    // by any number of terms separated by plus and minus operations
    int value = this.evalTerm();
    
    while (this.currentToken.type == TokenType.PLUS || this.currentToken.type == TokenType.MINUS) {
      if (this.currentToken.type == TokenType.PLUS) {
        this.consume(TokenType.PLUS);
        value = value + this.evalTerm();
      } else {
        this.consume(TokenType.MINUS); 
        value = value - this.evalTerm();
      }
    }
    
    return value;
  }
  
  
	public int evalConditional() throws Exception {
		// An expression consists of at least one arithmetic expression,
		// possibly joined to a second by a relational operator
		int value = this.evalExpression();
		if (this.currentToken.type == TokenType.COLON) {
			this.consume(TokenType.COLON);
		}
		// Add additional cases for the other relational operators
		if (this.currentToken.type == TokenType.LESS_THAN || this.currentToken.type == TokenType.GREATER_THAN) {

			if (this.currentToken.type == TokenType.LESS_THAN) {
				this.consume(TokenType.LESS_THAN);
				int lhs = value;
				int rhs = this.evalExpression();
				if (this.currentToken.type != TokenType.COLON) {
					throw new Exception("Missing colon from Conditional Statment.");
				}
				this.consume(TokenType.COLON);

				if (lhs < rhs) {
					return 1;
				} else {
					return 0;
				}
			}

			else if (this.currentToken.type == TokenType.GREATER_THAN) {
				this.consume(TokenType.GREATER_THAN);
				int lhs = value;
				int rhs = this.evalExpression();
				if (this.currentToken.type != TokenType.COLON) {
					throw new Exception("Missing colon from Conditional Statment.");
				}
				this.consume(TokenType.COLON);

				if (lhs > rhs) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		if (this.currentToken.type == TokenType.LESS_THAN_OR_EQUAL
				|| this.currentToken.type == TokenType.GREATER_THAN_OR_EQUAL) {

			if (this.currentToken.type == TokenType.LESS_THAN_OR_EQUAL) {
				this.consume(TokenType.LESS_THAN_OR_EQUAL);
				int lhs = value;
				int rhs = this.evalExpression();
				if (this.currentToken.type != TokenType.COLON) {
					throw new Exception("Missing colon from Conditional Statment.");
				}

				this.consume(TokenType.COLON);

				if (lhs <= rhs) {
					return 1;
				} else {
					return 0;
				}
			}

			else if (this.currentToken.type == TokenType.GREATER_THAN_OR_EQUAL) {
				this.consume(TokenType.GREATER_THAN_OR_EQUAL);
				int lhs = value;
				int rhs = this.evalExpression();
				if (this.currentToken.type != TokenType.COLON) {
					throw new Exception("Missing colon from Conditional Statment.");
				}

				this.consume(TokenType.COLON);

				if (lhs >= rhs) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		if (this.currentToken.type == TokenType.NOT_EQUAL) {
			this.consume(TokenType.NOT_EQUAL);
			int lhs = value;
			int rhs = this.evalExpression();
			if (this.currentToken.type != TokenType.COLON) {
				throw new Exception("Missing colon from Conditional Statment.");
			}
			this.consume(TokenType.COLON);

			if (lhs != rhs) {
				return 1;
			} else {
				return 0;
			}
		}

		if (this.currentToken.type == TokenType.EQUAL) {
			this.consume(TokenType.EQUAL);
			int lhs = value;
			int rhs = this.evalExpression();
			if (this.currentToken.type != TokenType.COLON) {
				throw new Exception("Missing colon from Conditional Statment.");
			}
			this.consume(TokenType.COLON);

			if (lhs == rhs) {
				return 1;
			} else {
				return 0;
			}
		}
		return value;
	}

  public void evalLoop() throws Exception {
    if (this.evalConditional() == 0) {
		while (this.currentToken.type != TokenType.ENDWHILE) {
  			this.consume(this.currentToken.type);
  		}
		isLoop = false;
    } else {
   		isLoop = true;
   		this.evalStatementBlock();
   	}
  }
  
  public void evalAssignmentStatement() throws Exception {

    // An assignment has the form NAME := EXPRESSION
    String lhs = this.currentToken.value;
    this.consume(TokenType.NAME);
	if (this.currentToken.type != TokenType.ASSIGN) {
		throw new Exception("Syntax error: missing : from assign.");
	}

    this.consume(TokenType.ASSIGN);  // match the :=

    int value = this.evalExpression();
    this.symbols.put(lhs, value); 
    
  }
  
  
  public void evalPrintStatement() throws Exception {
    
    // Match the print token that got us here
    this.consume(TokenType.PRINT); 
    
    // Evaluate an expression and print its value
    System.out.println(this.evalExpression());
  }
  
  
  public void evalInputStatement() throws Exception {
    
    this.consume(TokenType.INPUT);
    
    String name = this.currentToken.value;
    this.consume(TokenType.NAME);
    
    Scanner scan = new Scanner(System.in);
    System.out.print("Enter a value for " + name + ": ");
    int value = scan.nextInt();
    
    this.symbols.put(name, value);
  }
  
  
  public void evalStatement() throws Exception {
    // There are different types of statements: select the 
    // appropriate case based on the currentToken
    switch(this.currentToken.type) {
      
      // Add new cases here for each new statement type:
      // INPUT, IF, WHILE, CALL, SUB, RETURN, FOR  
      case INPUT:
        this.evalInputStatement();
        break;
        
      case IF:
  		this.consume(TokenType.IF);
  		if (this.evalConditional() == 0) {
  			while (this.currentToken.type != TokenType.ENDIF) {
  				this.consume(this.currentToken.type);
  			}
  		} else {
  			this.evalStatementBlock();
  		}
  		
  		if (this.currentToken.type != TokenType.ENDIF) {
  			throw new Exception("Missing ENDIF");
  		}
    	break;
    	
      case WHILE:
    	temp = this.pc;
    	this.consume(TokenType.WHILE);
  		this.evalLoop();
    	break;
      
      case NAME:
        this.evalAssignmentStatement();
        break;
        
      case NEWLINE:  // Empty statement
        break;
      
      case PRINT:
        this.evalPrintStatement();
        break;
        
      case COMMENT:
		this.consume(TokenType.COMMENT);
    	break;
    	
      // Cases corresponding to the end of blocks
      // Treated as empty
      case END:
        break;
        
      case ENDIF:
    	this.consume(TokenType.ENDIF);
        break;
        
      case ENDWHILE:
        if (isLoop == true) {
     		this.pc = temp;
       	    this.currentToken = this.program.get(this.pc);
       	    this.evalStatement();
       	    break;
        }

      	this.consume(TokenType.ENDWHILE);
        break;
        
      case ENDSUB:
      	this.consume(TokenType.ENDSUB);
        break;
         
      // Unrecognized token error
      default:
        throw new Exception("Unexpected token: " + this.currentToken.type);
    }
  }
  
  
  public void evalStatementBlock() throws Exception {
    // A program is at least one statement followed
    // by any number of optional statements separated by newlines
    this.evalStatement();
    while (this.currentToken.type == TokenType.NEWLINE) {
      this.consume(TokenType.NEWLINE);
      this.evalStatement();
    }
  }
  
  public void evalProgram() throws Exception {
	  while (this.currentToken.type == TokenType.NEWLINE) {
		  this.consume(TokenType.NEWLINE);
	  }

	  if (this.currentToken.type == TokenType.COMMENT) {
		  this.consume(TokenType.COMMENT);
	  }
	  
	  while (this.currentToken.type == TokenType.NEWLINE) {
		  this.consume(TokenType.NEWLINE);
	  }
	  
	  this.consume(TokenType.PROGRAM);

	  this.consume(TokenType.NAME);
	  
	  this.consume(TokenType.COLON);
	  
	  while(this.currentToken.type != TokenType.END) {
		  this.evalStatementBlock();
	  }

	  this.consume(TokenType.END);
  }
  
  public static void main(String[] args) {
      
      try {
        Interpreter interpreter = new Interpreter("src/Test/Basic/DivisibleBy7.a");
        interpreter.evalProgram();  // Start with evalProgram, which you'll need to write
      } catch (Exception e) {
        System.out.println(e);
      }
  }
}