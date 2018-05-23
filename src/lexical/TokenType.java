package lexical;

public enum TokenType {
    // special tokens
    INVALID_TOKEN,
    UNEXPECTED_EOF,
    END_OF_FILE,
    // symbols
    OPEN_CUR,
    CLOSE_CUR,
    DOT_COMMA,
    DOT,
    ASSIGN,
    OPEN_PAR,
    COMMA,
    CLOSE_PAR,
    // keywords
    IF,
    ELSE,
    WHILE,
    FUNCTION,
    RETURN,
    SYSTEM,
    SELF,
    ARGS,
    // operators
    NOT,
    AND,
    OR,
    EQUAL,
    DIFF,
    LOWER,
    GREATER,
    LOWER_EQ,
    GREATER_EQ,
    ADD,
    SUB,
    MULT,
    DIV,
    MOD,
    // others

    NAME,
    NUMBER,
    STRING,

};
