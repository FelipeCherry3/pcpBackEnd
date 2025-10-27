package com.rubim.pcpBackEnd.utils;

import java.util.Map;

public class OrdemSetor {
    public static final Map<Long, Integer> ORDEM_SETOR = Map.of(
            1L, 1,  // Usinagem
            2L, 2,  // Marcenaria (se tiver)
            3L, 3,  // Montagem
            4L, 4,  // Tapeçaria
            5L, 5,  // Lustração
            6L, 6,  // Expedição
            7L, 7   // Entregue
            
    );
}
