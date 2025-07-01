package workshop.arquitetura.pix;

import com.deveficiente.integration.bcb.BancoCentralClient;
import com.deveficiente.integration.bcb.*;
import com.deveficiente.integration.legado.ContasDeClientesNoLegadoClient;
import com.deveficiente.integration.legado.*;
import com.deveficiente.pix.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class NovaChavePixService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NovaChavePixService.class);

    private final ChavePixRepository repository;
    private final ContasDeClientesNoLegadoClient legadoClient; // http client
    private final BancoCentralClient bcbClient; // http client

    public NovaChavePixService(ChavePixRepository repository,
                               ContasDeClientesNoLegadoClient legadoClient,
                               BancoCentralClient bcbClient) {
        this.repository = repository;
        this.legadoClient = legadoClient;
        this.bcbClient = bcbClient;
    }

    @Transactional
    public ChavePix registra(@NotNull ChavePix novaChave) {

        // 1. Verifica se a chave já existe
        if (repository.existsByChave(novaChave.getChave())) {
            throw new ChavePixExistenteException("Chave Pix informada já existente");
        }

        // 2. Busca dados da conta no ERP do legado e associa a chave Pix
        DadosDaContaResponse response = legadoClient.buscaContaPorTipo(novaChave.getClienteId(), novaChave.getTipoDeConta().name());
        if (response == null) {
            throw new ContaNaoEncontradaException("Conta não encontrada no sistema legado da instituição bancária");
        }

        ContaAssociada conta = response.toModel();
        novaChave.associaConta(conta);

        // 3. Grava chave Pix no banco de dados
        ChavePix chave = repository.save(novaChave);

        // 4. Registra chave Pix no BCB
        CreatePixKeyRequest bcbRequest = CreatePixKeyRequest.of(chave);
        LOGGER.info("Registrando chave Pix no Banco Central do Brasil (BCB): {}", bcbRequest);

        CreatePixKeyResponse bcbResponse = bcbClient.create(bcbRequest);
        if (bcbResponse.statusCode != HttpStatus.CREATED) {
            throw new BancoCentralException("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)");
        }

        // 5. Atualiza a chave com a resposta do BCB
        chave.atualizaChave(bcbResponse.getBody().getKey());
        repository.save(chave);

        return chave;
    }
}
