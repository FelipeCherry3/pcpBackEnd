package com.rubim.pcpBackEnd.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rubim.pcpBackEnd.utils.JsonParserUtil;
import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesDTO;
import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesResponseDTO;
import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesResumoDTO;
import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesRowDTO;
import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;
import com.rubim.pcpBackEnd.repository.RelatorioRepository;

@Service
public class RelatoriosService {

    @Autowired
    private RelatorioRepository relatorioRepository;
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    public RelatoriosService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    public RelatorioPedidosEntreguesResponseDTO relatorioPedidosEntregues(LocalDate dataInicial, LocalDate dataFinal) {
        
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("Período inválido: dataFinal < dataInicial");
        }

        OffsetDateTime dtInicial = dataInicial.atStartOfDay(ZONE).toOffsetDateTime();
        OffsetDateTime dtFinal = dataFinal.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime();

        List<Object[]> rows = relatorioRepository.listarPedidosEntreguesNoPeriodo(dtInicial, dtFinal);

        List<RelatorioPedidosEntreguesRowDTO> itens = new ArrayList<>(rows.size());

        long totalPecas = 0;
        long somaLeadTimeDias = 0;
        
        for(Object[] r : rows) {
            Long id = JsonParserUtil.toLong(r[0]);
            Long numero = JsonParserUtil.toLong(r[1]);
            String nomeCliente = (String) r[2];

            OffsetDateTime dataPedido = JsonParserUtil.toOffsetDateTime(r[3], ZONE);
            OffsetDateTime data_entrega = JsonParserUtil.toOffsetDateTime(r[4], ZONE);
            Long   quantidadePecas     = JsonParserUtil.toLong(r[5]);

            long leadTimeDias = 0;
            if (dataPedido != null && data_entrega != null) {
                leadTimeDias = ChronoUnit.DAYS.between(
                        dataPedido.toLocalDate(),
                        data_entrega.toLocalDate()
                );
            }

            itens.add(new RelatorioPedidosEntreguesRowDTO(
                    numero,
                    nomeCliente,
                    dataPedido,
                    data_entrega,
                    quantidadePecas,
                    leadTimeDias
            ));

            totalPecas += (quantidadePecas != null ? quantidadePecas : 0);
            somaLeadTimeDias += leadTimeDias;
        }

        long totalPedidos = itens.size();
        BigDecimal mediaDias = BigDecimal.ZERO;
        if (totalPedidos > 0) {
            mediaDias = BigDecimal.valueOf(somaLeadTimeDias)
                    .divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP);
        }

        RelatorioPedidosEntreguesResumoDTO resumo = new RelatorioPedidosEntreguesResumoDTO(
                totalPedidos,
                totalPecas,
                mediaDias
        );

        return new RelatorioPedidosEntreguesResponseDTO(
                dataInicial,
                dataFinal,
                itens,
                resumo
        );
    }

}
