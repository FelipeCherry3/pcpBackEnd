package com.rubim.pcpBackEnd.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutosResponseDTO {
    public List<Item> data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        public Long id;
        public String nome;
        public String codigo;
        public String tipo;           // "E"
        public BigDecimal preco;
        @JsonAlias({"precoCusto","preco_custo"})
        public BigDecimal precoCusto;
        public String situacao;
        @JsonAlias({"descricaoCurta","descricao_curta"})
        public String descricaoCurta;

        public Estoque estoque;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Estoque {
            // cobrimos variações de nome que costumam aparecer
            @JsonAlias({"saldoVirtualTotal","saldo_virtual_total","saldoValorVirtual","saldo_valor_virtual"})
            public BigDecimal saldoVirtualTotal;
        }
    }
}