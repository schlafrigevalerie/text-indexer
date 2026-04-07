package com.valerix.textindexer.app;

import com.valerix.textindexer.core.tokenizer.SimpleTokenizer;
import com.valerix.textindexer.core.tokenizer.Tokenizer;

public class Main {
    public static void main(String[] args) {
        Tokenizer tokenizer = new SimpleTokenizer();
        System.out.println(tokenizer.tokenize("Hello, world! Hello!!!"));
    }
}