package com.rubim.pcpBackEnd.Services;

import org.springframework.beans.factory.annotation.Autowired;

import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.Entity.MovimentacaoSetorEntity;
import com.rubim.pcpBackEnd.repository.MovimentacaoSetorRepository;

public class MovimentacaoSetorService {
    @Autowired
    private MovimentacaoSetorRepository movimentacaoSetorRepository;

    public void realizarMovimentacao(PedidoVendaResponseDTO pedidoVenda, Long idSetorNovo) {
        MovimentacaoSetorEntity movimentacao = new MovimentacaoSetorEntity();
        movimentacaoSetorRepository.save(movimentacao);
    }
    
}
