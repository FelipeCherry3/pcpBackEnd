package com.rubim.pcpBackEnd.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.rubim.pcpBackEnd.Entity.ContatoEntity;
import com.rubim.pcpBackEnd.Entity.EstoqueEntity;
import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoDeVendaEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoEntity;
import com.rubim.pcpBackEnd.Entity.SituacaoEntity;
import com.rubim.pcpBackEnd.Entity.VendedorEntity;
import com.rubim.pcpBackEnd.repository.ContatoRepository;
import com.rubim.pcpBackEnd.repository.EstoqueRepository;
import com.rubim.pcpBackEnd.repository.PedidosVendaRepository;
import com.rubim.pcpBackEnd.repository.ProdutoRepository;
import com.rubim.pcpBackEnd.repository.SituacaoRepository;
import com.rubim.pcpBackEnd.repository.VendedorRepository;
import com.rubim.pcpBackEnd.utils.JsonParserUtil;
import com.rubim.pcpBackEnd.utils.ParserDescricao;

@Service
public class BlingPedidoVendaService {
    
    private BlingAuthService blingAuthService;
    private WebClient.Builder webClientBuilder; // IMPORTANTE: org.springframework.web.reactive.function.client.WebClient

    private PedidosVendaRepository pedidosVendaRepository;
    private ContatoRepository contatoRepository;
    private SituacaoRepository situacaoRepository;
    private VendedorRepository vendedorRepository;
    private ProdutoRepository produtoRepository;
    private ParserDescricao parserDescricao;
    private EstoqueRepository estoqueRepository;

    @Autowired
    public BlingPedidoVendaService(BlingAuthService blingAuthService,
                                   WebClient.Builder webClientBuilder,
                                   PedidosVendaRepository pedidosVendaRepository,
                                   ContatoRepository contatoRepository,
                                   SituacaoRepository situacaoRepository,
                                   VendedorRepository vendedorRepository,
                                   ProdutoRepository produtoRepository,
                                   ParserDescricao parserDescricao,
                                   EstoqueRepository estoqueRepository) {
        this.blingAuthService = blingAuthService;
        this.webClientBuilder = webClientBuilder;
        this.pedidosVendaRepository = pedidosVendaRepository;
        this.contatoRepository = contatoRepository;
        this.situacaoRepository = situacaoRepository;
        this.vendedorRepository = vendedorRepository;
        this.produtoRepository = produtoRepository;
        this.parserDescricao = parserDescricao;
        this.estoqueRepository = estoqueRepository;
    }

    private WebClient blingClient() {
        return webClientBuilder
                .baseUrl("https://api.bling.com.br/Api/v3")
                .build();
    }

    /**
     * Busca um pedido pelo identificador na API do Bling e persiste seus dados.
     * @param idPedido identificador numérico do pedido no Bling
     */
    
    @Transactional
    public void sincronizarPedido(Long idPedido) {
        // Obtém um token de acesso válido
        String accessToken = blingAuthService.obterAccessTokenValido().block();
        if (accessToken == null) {
            throw new IllegalStateException("Não foi possível obter token de acesso");
        }

        // Faz a chamada HTTP e converte a resposta em um Map
        Map<String, Object> response = blingClient().get()
                .uri("/pedidos/vendas/" + idPedido)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("data")) {
            throw new IllegalStateException("Resposta da API do Bling inválida");
        }

        // Recupera o objeto 'data' do JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");

        // Instancia ou busca o pedido existente
        Long pedidoId = ((Number) data.get("id")).longValue();
        PedidosVendaEntity pedido = pedidosVendaRepository.findById(pedidoId)
                .orElseGet(PedidosVendaEntity::new);
        pedido.setId(pedidoId);
        // Número do pedido
        Object numeroObj = data.get("numero");
        if (numeroObj != null) {
            pedido.setNumero(((Number) numeroObj).longValue());
        }
        // Datas
        if (data.get("data") != null) {
            pedido.setData(JsonParserUtil.toLocalDateLenient(data.get("data")));
        }
        if (data.get("dataSaida") != null) {
            pedido.setDataSaida(JsonParserUtil.toLocalDateLenient(data.get("dataSaida")));
        }
        if (data.get("dataPrevista") != null) {
            pedido.setDataPrevista(JsonParserUtil.toLocalDateLenient(data.get("dataPrevista")));
        }
        // Totais
        if (data.get("totalProdutos") != null) {
            pedido.setTotalProdutos(new BigDecimal(data.get("totalProdutos").toString()));
        }
        if (data.get("total") != null) {
            pedido.setTotal(new BigDecimal(data.get("total").toString()));
        }
        // Observações do Pedido e Observações Internas
        pedido.setObservacoes((String) data.get("observacoes"));
        pedido.setObservacoesInternas((String) data.get("observacoesInternas"));

        // Contato (cliente)
        @SuppressWarnings("unchecked")
        Map<String, Object> contatoMap = (Map<String, Object>) data.get("contato");
        if (contatoMap != null) {
            Long contatoId = ((Number) contatoMap.get("id")).longValue();
            ContatoEntity contato = contatoRepository.findById(contatoId)
                    .orElseGet(ContatoEntity::new);
            contato.setId(contatoId);
            contato.setNome((String) contatoMap.get("nome"));
            contato.setTipoPessoa((String) contatoMap.get("tipoPessoa"));
            contato.setNumeroDocumento((String) contatoMap.get("numeroDocumento"));
            contatoRepository.save(contato);
            pedido.setContato(contato);
        }

        // Situação
        @SuppressWarnings("unchecked")
        Map<String, Object> situacaoMap = (Map<String, Object>) data.get("situacao");
        if (situacaoMap != null) {
            // ID da situação é String no entity, porém o JSON pode trazer número
            Long situacaoId = ((Number) situacaoMap.get("id")).longValue();
            SituacaoEntity situacao = situacaoRepository.findById(situacaoId)
                    .orElseGet(SituacaoEntity::new);
            situacao.setId(situacaoId);
            // Valor também é String no entity; converte para string
            situacao.setValor(String.valueOf(situacaoMap.get("valor")));
            situacaoRepository.save(situacao);
            pedido.setSituacao(situacao);
        }

        // Vendedor
        @SuppressWarnings("unchecked")
        Map<String, Object> vendedorMap = (Map<String, Object>) data.get("vendedor");
        if (vendedorMap != null) {
            Long vendedorId = ((Number) vendedorMap.get("id")).longValue();
            VendedorEntity vendedor = vendedorRepository.findById(vendedorId)
                    .orElseGet(VendedorEntity::new);
            vendedor.setId(vendedorId);
            // define um nome padrão se ainda não existir
            if (vendedor.getNome() == null) {
                vendedor.setNome("Vendedor " + vendedorId);
            }
            vendedorRepository.save(vendedor);
            pedido.setVendedor(vendedor);
        }

        // Processa os itens
        List<ProdutoDeVendaEntity> itensList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itens = (List<Map<String, Object>>) data.get("itens");
        if (itens != null) {
            for (Map<String, Object> itemMap : itens) {
                if (itemMap == null) continue;
                Long itemId = ((Number) itemMap.get("id")).longValue();
                ProdutoDeVendaEntity item = new ProdutoDeVendaEntity();
                item.setId(itemId);
                item.setPedido(pedido);
                item.setCodigo((String) itemMap.get("codigo"));
                item.setUnidade((String) itemMap.get("unidade"));
                // Quantidade, desconto e valor são BigDecimal
                if (itemMap.get("quantidade") != null) {
                    item.setQuantidade(new BigDecimal(itemMap.get("quantidade").toString()));
                }
                if (itemMap.get("desconto") != null) {
                    item.setDesconto(new BigDecimal(itemMap.get("desconto").toString()));
                }
                if (itemMap.get("valor") != null) {
                    item.setValor(new BigDecimal(itemMap.get("valor").toString()));
                }
                // Descrições
                item.setDescricao((String) itemMap.get("descricao"));
                item.setDescricaoDetalhada((String) itemMap.get("descricaoDetalhada"));

                // Aplica regex para preencher atributos adicionais
                Map<String, String> atributos = parserDescricao.parseDescription(
                    (String) item.getDescricaoDetalhada());
                item.setCorMadeira(atributos.get("madeira"));
                item.setCorRevestimento(atributos.get("revestimento"));
                item.setMedidasTampo(atributos.get("tamanho"));
                // Produto
                @SuppressWarnings("unchecked")
                Map<String, Object> prodMap = (Map<String, Object>) itemMap.get("produto");
                if (prodMap != null) {
                    Long prodId = ((Number) prodMap.get("id")).longValue();
                    ProdutoEntity produto = produtoRepository.findById(prodId)
                            .orElseGet(ProdutoEntity::new);
                    produto.setId(prodId);
                    // Se o produto existe no JSON, mas não temos nome/codigo, apenas salva o ID
                    produtoRepository.save(produto);
                    item.setProduto(produto);
                }
                itensList.add(item);
            }
        }
        // Substitui a lista de itens no pedido (orphanRemoval cuidará da exclusão de antigos)
        pedido.setItens(itensList);
        pedido.setPriority("normal");

        // Persiste o pedido e seus itens (cascateado)
        pedidosVendaRepository.save(pedido);
    }

    // A) Só busca os IDs por período (não salva)

    public List<Long> listarIdsPedidosPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {

        //Valida Parametros
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("dataInicial e dataFinal são obrigatórios.");
        }
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicial.");
        }

        // Obtém token de acesso válido
        String token = blingAuthService.obterAccessTokenValido().block();

        // Chama a API do Bling
        Map<?, ?> resp = blingClient().get()
                .uri(uri -> uri.path("/pedidos/vendas")
                        .queryParam("dataInicial", dataInicial.toString())
                        .queryParam("dataFinal",   dataFinal.toString())
                        .queryParam("idsSituacoes[]", 9)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) return Collections.emptyList();

        Object dataObj = resp.get("data");
        if (!(dataObj instanceof List<?> lista)) return Collections.emptyList();

        List<Long> ids = new ArrayList<>(lista.size());
        for (Object o : lista) {
            if (o instanceof Map<?, ?> m) {
                Object idObj = m.get("id");
                if (idObj != null) {
                    ids.add(JsonParserUtil.toLong(idObj)); //Json Parser utils/JsonParserUtil
                }
            }
        }
    return ids;
    }

    /////////////////////////////////////////////////////////////////////////
    // B) Chama sincronizarPedido(id) para cada ID retornado pelo método A
    /////////////////////////////////////////////////////////////////////////
    public int sincronizarPedidosDoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<Long> ids = listarIdsPedidosPorPeriodo(dataInicial, dataFinal);
        for (Long id : ids) {
            // seu método já implementado
            sincronizarPedido(id);
        }
        return ids.size();
    }

    /**
 * Sincroniza produtos do Bling do tipo "E" (Composições) e grava:
 * - Produto: id, nome, codigo, tipo, preco, precoCusto, situacao, descricaoCurta
 * - Estoque DEFAULT: quantidade = saldoVirtualTotal (ou saldoValorVirtual) do JSON
 *
 * @return quantidade de itens processados/persistidos
 */
@Transactional
public int sincronizarProdutos() {
    String token = blingAuthService.obterAccessTokenValido().block();
    if (token == null) {
        throw new IllegalStateException("Não foi possível obter token de acesso");
    }

    final int limite = 100;
    int pagina = 1;
    int totalPersistidos = 0;

    while (true) {
        // cópia efetivamente final para usar na lambda
        final int pageForCall = pagina;

        Map<?, ?> resp = blingClient().get()
                .uri(uri -> uri.path("/produtos")
                        .queryParam("pagina", pageForCall)
                        .queryParam("limite", limite)
                        .queryParam("tipo", "E") // apenas Composições
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) break;

        Object dataObj = resp.get("data");
        if (!(dataObj instanceof List<?> lista) || lista.isEmpty()) {
            break; // acabou a paginação
        }

        for (Object o : lista) {
            if (!(o instanceof Map<?, ?> m)) continue;

            // --- Produto ---
            Long id = JsonParserUtil.toLong(m.get("id"));
            if (id == null) continue;

            ProdutoEntity p = produtoRepository.findById(id).orElseGet(ProdutoEntity::new);
            p.setId(id);
            p.setNome((String) m.get("nome"));
            p.setCodigo((String) m.get("codigo"));
            p.setTipo((String) m.get("tipo"));
            p.setPreco(JsonParserUtil.toBigDecimal(m.get("preco")));
            p.setPrecoCusto(JsonParserUtil.toBigDecimal(JsonParserUtil.firstNonNull(
                    m.get("precoCusto"),
                    m.get("preco_custo")
            )));
            p.setSituacao((String) m.get("situacao"));
            p.setDescricaoCurta((String) m.get("descricaoCurta"));
            if (p.getDescricaoCurta() == null && m.get("descricao_curta") != null) {
                p.setDescricaoCurta(String.valueOf(m.get("descricao_curta")));
            }
            produtoRepository.save(p);

            // --- Estoque DEFAULT ---
            @SuppressWarnings("unchecked")
            Map<String, Object> estoqueMap = (Map<String, Object>) m.get("estoque");

            int quantidade = 0;
            if (estoqueMap != null) {
                quantidade = JsonParserUtil.toInt(JsonParserUtil.toBigDecimal(JsonParserUtil.firstNonNull(
                        estoqueMap.get("saldoVirtualTotal"),
                        estoqueMap.get("saldo_virtual_total"),
                        estoqueMap.get("saldoValorVirtual"),
                        estoqueMap.get("saldo_valor_virtual")
                )));
            }

            EstoqueEntity e = estoqueRepository.findByProdutoAndDeposito(p, "DEFAULT")
                    .orElseGet(() -> {
                        EstoqueEntity novo = new EstoqueEntity();
                        novo.setProduto(p);
                        novo.setDeposito("DEFAULT");
                        novo.setMinimo(-1000);
                        return novo;
                    });
            e.setSaldoVirtualTotal(quantidade); // <<< usa a coluna 'saldoVirtualTotal' da sua tabela
            estoqueRepository.save(e);

            totalPersistidos++;
        }

        // próxima página
        pagina++;
    }

    return totalPersistidos;
}

        
}
