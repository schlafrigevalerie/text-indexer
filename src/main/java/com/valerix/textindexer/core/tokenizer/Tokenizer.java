package com.valerix.textindexer.core.tokenizer;

import java.util.List;

public interface Tokenizer {
    List<String> tokenize(String text);
}
