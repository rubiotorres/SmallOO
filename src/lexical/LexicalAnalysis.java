package lexical;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.PushbackInputStream;

import java.util.*;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() throws IOException {
        int estado = 1; //estado inicial do diagrama
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        while (estado != 9 && estado != 10) {
            // getc:
            int c = input.read();
            // System.out.println("[" + estado + " '" + (char)(c) + "'" + "]");
                switch (estado){
                    case 1: // estado inicial
                        if (c == ' ' || c == '\r' || c == '\n' || c == '\t'){
                            
                        } else if (c == '\n'){
                            line++;
                        } else if (c == '/'){
                            lex.token += (char)c;
                            estado = 2;
                        } else if (Character.isDigit(c)){
                            lex.token += (char)c;
                            lex.type = TokenType.NUMBER;
                            estado = 5;
                        } else if (c == '<' || c == '>' || c == '!' || c == '='){
                            lex.token += (char)c;
                            estado = 6;
                        } else if (Character.isLetter(c)){
                            lex.token += (char)c;
                            estado = 7;
                        } else if (c == '\"'){
                            lex.type = TokenType.STRING;
                            estado = 8;
                        } else if (c == ';' || c == ',' || c == '.' || c == '(' || c == ')' || c == '{' || 
                                   c == '}' || c == '+' || c == '-' || c == '*' || c == '%' || c == '&' || c == '|') {
                            lex.token += (char)c;
                            estado = 9;
                        } else if (c == -1){
                            lex.type = TokenType.END_OF_FILE;
                            estado = 10;
                        } else {
                            lex.token += (char)c;
                            lex.type = TokenType.INVALID_TOKEN;
                            estado = 10;
                        }
                        break;
                    case 2: // caracter '/'
                        if (c == '*'){
                            lex.token = "";
                            estado = 3;
                        }
                        else {
                            // ungetc:
                            if (c != -1){
                                input.unread(c);
                                lex.type = TokenType.DIV;
                            }
                            estado = 9;
                        }
                        break;
                        
                    case 3: // caracter '*'
                        if (c == '*'){
                            estado = 4;
                        }
                        else {
                            estado = 3;
                        }
                        break;
                        
                    case 4: // caracteres ['*' '/']
                        if (c == '*'){
                            estado = 4;
                            lex.token += (char)c;
                        }
                        else if (c == '/'){
                            estado = 1;
                            lex.token += (char)c;
                        }
                        else{
                            estado = 3;
                        }
                        break;
                        
                    case 5: // dígitos
                        if (Character.isDigit(c)){
                            lex.token += (char)c;
                            estado = 5;
                        } else {
                            // ungetc:
                            if (c != -1)
                                input.unread(c);
                            estado = 10;
                        }
                        break;
                        
                    case 6: // caracter de atribuição ['<' '>' '!' '=']
                        // ungetc:
                        if (c == '=') {
                            lex.token += (char)c;
                        } else if (c != -1){
                            input.unread(c);
                        }
                        estado = 9;
                        break;
                    
                    case 7: // letras
                        if (Character.isLetter(c)){
                            lex.token += (char)c;
                            estado = 7;
                        } else {
                            // ungetc:
                            if (c != -1)
                                input.unread(c);
                            estado = 9;
                        }
                        break;
                    
                    case 8: // aspas
                        if (c == '\"'){
                            lex.token += (char)c;
                        }
                        else{
                            lex.token += (char)c;
                            estado = 10;
                        }
                        break;
                    default:
                           break;
                }
                
                // Se o estado for 9, consultar a tabela de símbolos
                if (estado == 9){
                    if (st.contains(lex.token)){
                        lex.type = st.find(lex.token);
                        }
                    else {
                    lex.type = TokenType.NAME;
                    //Consultar tabela, definir o tipo
                    }
                }
                else if (estado == 10){
                    //lex.type = TokenType.END_OF_FILE;
                    //estado = 1;
                }
         }
         return lex;
    }
}
