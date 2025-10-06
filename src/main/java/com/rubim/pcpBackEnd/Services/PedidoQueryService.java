package com.rubim.pcpBackEnd.Services;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoDeVendaEntity;
import com.rubim.pcpBackEnd.Entity.SetorEntity;
import com.rubim.pcpBackEnd.Entity.ContatoEntity;
import com.rubim.pcpBackEnd.repository.PedidosVendaRepository;
import com.rubim.pcpBackEnd.repository.ProdutoRepository;
import com.rubim.pcpBackEnd.repository.SetorRepository;
import com.rubim.pcpBackEnd.utils.JsonParserUtil;
// DTOs:
import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.DTO.ProdutoDTO;
import com.rubim.pcpBackEnd.DTO.SetorDTO;
import com.rubim.pcpBackEnd.DTO.ItemPedidoResponseDTO;
import com.rubim.pcpBackEnd.DTO.AtualizarSetorDTO;
import com.rubim.pcpBackEnd.DTO.ClienteDTOResponse; 
import com.rubim.pcpBackEnd.Entity.ProdutoEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoQueryService {

    private final PedidosVendaRepository pedidosRepo;
    private final SetorRepository setorRepo;

    private final ProdutoRepository produtoRepo;

    public PedidoQueryService(PedidosVendaRepository pedidosRepo, SetorRepository setorRepo, ProdutoRepository produtoRepo) {
        this.pedidosRepo = pedidosRepo;
        this.setorRepo = setorRepo;
        this.produtoRepo = produtoRepo;
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
        dto.setPriority(p.getPriority());

        // Setor 
        dto.setSetor(toSetorDTO(p.getSetor()));

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

        ProdutoDTO produtoDTO = toProdutoDTO(it.getProduto());
        dto.setProduto(produtoDTO);

        dto.setPedidoVenda(incluirPedidoNoItem ? pedidoPaiParaReferenciarOuNull : null);

        return dto;
    }
    private ClienteDTOResponse toClienteDTO(ContatoEntity c) {
        if (c == null) return null;
        ClienteDTOResponse cli = new ClienteDTOResponse();
        cli.setId(c.getId());
        cli.setNome(c.getNome());
        return cli;
    }

    private ProdutoDTO toProdutoDTO(ProdutoEntity p) {
        if (p == null) return null;
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setCodigo(p.getCodigo());
        dto.setDescricaoCurta(p.getDescricaoCurta());
        dto.setPreco(p.getPreco());
        dto.setPrecoCusto(p.getPrecoCusto());   
        dto.setSituacao(p.getSituacao());
        dto.setTipo(p.getTipo());
        return dto;
    }
    private SetorDTO toSetorDTO(SetorEntity s) {
        if (s == null) return null;
        SetorDTO dto = new SetorDTO();
        dto.setId(s.getId());
        dto.setNome(s.getNome());
        return dto;
    }

    @Transactional
    public boolean atualizarSetorDePedido(Long idPedido, Long idSetorNovo) {
        PedidosVendaEntity pedido = pedidosRepo.findById(idPedido).orElse(null);
        if (pedido == null) return false;

        SetorEntity novoSetor = setorRepo.findById(idSetorNovo).orElse(null);
        if (novoSetor == null) return false;

        pedido.setSetor(novoSetor);
        //@Transactional, o flush acontece no commit.
        pedidosRepo.save(pedido);
        return true;
    }

    @Transactional
    public boolean atualizarDadosPedido(PedidoVendaResponseDTO dto) {
        if (dto == null || dto.getId() == null) return false;

        PedidosVendaEntity pedido = pedidosRepo.findById(dto.getId()).orElse(null);
        if (pedido == null) return false;

        // Patch de campos simples (só aplica se vier não-nulo)
        applyIfNotNull(dto.getDataEmissao(), pedido::setData);
        applyIfNotNull(dto.getDataEntrega(), pedido::setDataSaida);
        applyIfNotNull(dto.getDataPrevista(), pedido::setDataPrevista);
        applyIfNotNull(dto.getPriority(), pedido::setPriority);

        // Itens: NÂO substituir coleção; apenas sobrescrever os existentes por id
        if (dto.getItens() != null && pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            Map<Long, ProdutoDeVendaEntity> existentes = pedido.getItens().stream()
                .filter(it -> it.getId() != null)
                .collect(java.util.stream.Collectors.toMap(ProdutoDeVendaEntity::getId, it -> it));

            for (ItemPedidoResponseDTO itemDTO : dto.getItens()) {
                if (itemDTO.getId() == null) continue; // não cria
                ProdutoDeVendaEntity alvo = existentes.get(itemDTO.getId());
                if (alvo != null) {
                    patchItemFromDTO(alvo, itemDTO); // só sobrescreve campos enviados
                }
            }
            // Importante: não fazer pedido.setItens(...)
        }

        pedidosRepo.save(pedido);
        return true;
    }

    private static <T> void applyIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private void patchItemFromDTO(ProdutoDeVendaEntity entity, ItemPedidoResponseDTO dto) {
        applyIfNotNull(dto.getCodigo(), entity::setCodigo);
        applyIfNotNull(dto.getUnidade(), entity::setUnidade);
        applyIfNotNull(dto.getDescricao(), entity::setDescricao);
        applyIfNotNull(dto.getDescricaoDetalhada(), entity::setDescricaoDetalhada);
        applyIfNotNull(dto.getCorMadeira(), entity::setCorMadeira);
        applyIfNotNull(dto.getCorRevestimento(), entity::setCorRevestimento);
        applyIfNotNull(dto.getDetalhesMedidas(), entity::setMedidasTampo);

        // numéricos via seu util
        applyIfNotNull(JsonParserUtil.toBigDecimal(dto.getQuantidade()), entity::setQuantidade);
        applyIfNotNull(JsonParserUtil.toBigDecimal(dto.getDesconto()), entity::setDesconto);
        applyIfNotNull(JsonParserUtil.toBigDecimal(dto.getValor()), entity::setValor);

        // Produto relacionado: só troca se vier id (não cria nada)
        if (dto.getProduto() != null && dto.getProduto().getId() != null) {
            produtoRepo.findById(dto.getProduto().getId()).ifPresent(entity::setProduto);
        }
    }
}