package com.rubim.pcpBackEnd.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ParserDescricao {
    
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(
        "\\[MADEIRA:\\s*([^\\]]*)\\]\\s*\\[REVESTIMENTO:\\s*([^\\]]*)\\]\\s*\\[TAMANHO:\\s*([^\\]]*)\\]\\s*\\[DETALHES:\\s*([^\\]]*)\\]"
        );

    public static Map<String, String> parseDescription(String descricaoDetalhada) {
        Map<String, String> result = new HashMap<>();
        result.put("madeira", null);
        result.put("revestimento", null);
        result.put("tamanho", null);
        result.put("detalhes", null);

        if (descricaoDetalhada == null || descricaoDetalhada.trim().isEmpty()) {
            return result;
        }

        Matcher matcher = DESCRIPTION_PATTERN.matcher(descricaoDetalhada.trim());
        if (matcher.matches()) {
            result.put("madeira", matcher.group(1).trim().isEmpty() ? null : matcher.group(1).trim());
            result.put("revestimento", matcher.group(2).trim().isEmpty() ? null : matcher.group(2).trim());
            result.put("tamanho", matcher.group(3).trim().isEmpty() ? null : matcher.group(3).trim());
            result.put("detalhes", matcher.group(4).trim().isEmpty() ? null : matcher.group(4).trim());
        }

        return result;
    }
    
}
