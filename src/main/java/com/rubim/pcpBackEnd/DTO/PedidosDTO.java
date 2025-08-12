package com.rubim.pcpBackEnd.DTO;

import lombok.Data;

@Data
public class PedidosDTO {
    private Long id;
	private Long numero;
	private String numeroLoja;
	private String data;
	private String dataSaida;
	private String dataPrevista;
	private Long totalProdutos;
	private Long total;
	private SituacaoDTO situacao;
	private LojaDTO loja;
}
