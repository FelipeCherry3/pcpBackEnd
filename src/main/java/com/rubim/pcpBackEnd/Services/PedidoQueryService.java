package com.rubim.pcpBackEnd.Services;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoDeVendaEntity;
import com.rubim.pcpBackEnd.Entity.SetorEntity;
import com.rubim.pcpBackEnd.Entity.ContatoEntity;
import com.rubim.pcpBackEnd.repository.PedidosVendaRepository;
import com.rubim.pcpBackEnd.utils.JsonParserUtil;
// seus DTOs:
import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.DTO.ItemPedidoResponseDTO;
import com.rubim.pcpBackEnd.DTO.ClienteDTOResponse; // se existir
import com.rubim.pcpBackEnd.Entity.ProdutoEntity;         // ajuste se for ProdutoEntity

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoQueryService {

    private final PedidosVendaRepository pedidosRepo;

    public PedidoQueryService(PedidosVendaRepository pedidosRepo) {
        this.pedidosRepo = pedidosRepo;
    }

    /**
     * Lista pedidos por período [dataInicial, dataFinal].
     * Retorna em DTO pronto para o front.
     */
    @Transactional(readOnly = true)
    public List<PedidoVendaResponseDTO> listarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<PedidosVendaEntity> pedidos = pedidosRepo.findAllByDataBetween(dataInicial, dataFinal);
        List<PedidoVendaResponseDTO> dtoList = new ArrayList<>(pedidos.size());
        for (PedidosVendaEntity p : pedidos) {
            dtoList.add(toDTO(p, /*incluirPedidoNoItem*/ false));
        }
        return dtoList;
    }

    /**
     * Busca 1 pedido por id e devolve DTO.
     */
    @Transactional(readOnly = true)
    public PedidoVendaResponseDTO detalhar(Long id) {
        PedidosVendaEntity p = pedidosRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
        return toDTO(p, /*incluirPedidoNoItem*/ true);
    }

    /* ===================== MAPEAMENTO ===================== */

    private PedidoVendaResponseDTO toDTO(PedidosVendaEntity p, boolean incluirPedidoNoItem) {
        PedidoVendaResponseDTO dto = new PedidoVendaResponseDTO();
        dto.setId(p.getId());
        dto.setNumero(p.getNumero());
        dto.setDataEmissao(JsonParserUtil.toLocalDate(p.getData()));
        dto.setDataEntrega(JsonParserUtil.toLocalDate(p.getDataSaida()));
        dto.setDataPrevista(JsonParserUtil.toLocalDate(p.getDataPrevista()));
        dto.setTotal(JsonParserUtil.toInt(p.getTotal()));

        // Setor (se existir na sua entidade)
        SetorEntity setor = null;
        try {
            // se sua entidade PedidosVendaEntity tiver getSetor():
            setor = (SetorEntity) PedidosVendaEntity.class
                    .getMethod("getSetor")
                    .invoke(p);
        } catch (Exception ignore) {}
        dto.setSetor(setor);

        // Cliente/Contato → ClienteDTOResponse (preencha conforme seu DTO)
        dto.setCliente(toClienteDTO(p.getContato()));

        // Itens
        List<ItemPedidoResponseDTO> itensDTO = new ArrayList<>();
        if (p.getItens() != null) {
            for (ProdutoDeVendaEntity it : p.getItens()) {
                itensDTO.add(toItemDTO(it, incluirPedidoNoItem ? null : dto, incluirPedidoNoItem));
            }
        }
        dto.setItens(itensDTO);

        return dto;
    }

    private ItemPedidoResponseDTO toItemDTO(ProdutoDeVendaEntity it,
                                            PedidoVendaResponseDTO pedidoPaiParaReferenciarOuNull,
                                            boolean incluirPedidoNoItem) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setId(it.getId());
        dto.setCodigo(it.getCodigo());
        dto.setUnidade(it.getUnidade());
        dto.setDesconto(JsonParserUtil.toInt(it.getDesconto()));
        dto.setValor(JsonParserUtil.toInt(it.getValor())); // se preferir, mude DTO para BigDecimal
        dto.setDescricao(it.getDescricao());
        dto.setQuantidade(JsonParserUtil.toInt(it.getQuantidade()));
        dto.setDescricaoDetalhada(it.getDescricaoDetalhada());

        // ====== GANCHO PARA REGEX ======
        // Aqui você aplica seus regex em descricao / descricaoDetalhada
        // para preencher corMadeira, corRevestimento, detalhesMedidas.
        aplicarRegexDeAtributos(dto);
        // ================================

        // Produto: mapeie conforme seu tipo (Produto vs ProdutoEntity)
        // Se seu DTO espera "Produto" (domínio), adapte aqui.
        ProdutoEntity produto = null;
        try {
            // exemplo simples: se sua entidade do item tiver getProduto() e isso for atribuível a Produto
            Object prodEntity = ProdutoDeVendaEntity.class.getMethod("getProduto").invoke(it);
            if (prodEntity instanceof ProdutoEntity prod) {
                produto = prod;
            }
        } catch (Exception ignore) {}
        dto.setProduto(produto);

        // Evitar ciclo infinito na serialização:
        // Só setar pedido no item quando explicitamente solicitado.
        dto.setPedidoVenda(incluirPedidoNoItem ? pedidoPaiParaReferenciarOuNull : null);

        return dto;
    }

    /** PREENCHA AQUI SUA LÓGICA DE REGEX — exemplo com TODOs */
    private void aplicarRegexDeAtributos(ItemPedidoResponseDTO dto) {
        String base = (dto.getDescricaoDetalhada() != null && !dto.getDescricaoDetalhada().isBlank())
                ? dto.getDescricaoDetalhada()
                : (dto.getDescricao() != null ? dto.getDescricao() : "");

        // TODO: Exemplo de padrões — ajuste para o seu caso real.
        // Pattern pMadeira = Pattern.compile("Madeira:\\s*([\\w\\s]+)", Pattern.CASE_INSENSITIVE);
        // Matcher m1 = pMadeira.matcher(base);
        // if (m1.find()) dto.setCorMadeira(m1.group(1).trim());

        // Pattern pRevest = Pattern.compile("Revestimento:\\s*([\\w\\s]+)", Pattern.CASE_INSENSITIVE);
        // Matcher m2 = pRevest.matcher(base);
        // if (m2.find()) dto.setCorRevestimento(m2.group(1).trim());

        // Pattern pMedidas = Pattern.compile("Medidas?:\\s*([^;\\n]+)", Pattern.CASE_INSENSITIVE);
        // Matcher m3 = pMedidas.matcher(base);
        // if (m3.find()) dto.setDetalhesMedidas(m3.group(1).trim());

        // Por enquanto, zere para evitar lixo:
        if (dto.getCorMadeira() == null) dto.setCorMadeira(null);
        if (dto.getCorRevestimento() == null) dto.setCorRevestimento(null);
        if (dto.getDetalhesMedidas() == null) dto.setDetalhesMedidas(null);
    }
    private ClienteDTOResponse toClienteDTO(ContatoEntity c) {
        if (c == null) return null;
        // TODO: preencha conforme os campos do seu ClienteDTOResponse.
        // Exemplo genérico (se tiver setters):
        // ClienteDTOResponse cli = new ClienteDTOResponse();
        // cli.setId(c.getId());
        // cli.setNome(c.getNome());
        // cli.setDocumento(c.getNumeroDocumento());
        // return cli;
        return null; // por hora, retorne null até mapear seus campos reais
    }
}
