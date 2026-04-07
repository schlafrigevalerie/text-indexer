package com.valerix.textindexer.core.tokenizer;

import java.util.Arrays;
import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(s -> !s.isBlank())
                .toList();
    }
}
