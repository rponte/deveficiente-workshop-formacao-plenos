package com.deveficiente.pix.registra;

import com.deveficiente.pix.registra.service.NovaChavePixService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class RegistraChaveController {

    private final NovaChavePixService service;

    public RegistraChaveController(NovaChavePixService service) {
        this.service = service;
    }

    @PostMapping("/api/pix/keys")
    public ResponseEntity<?> registra(@RequestBody RegistraChavePixRequest request) {

        ChavePix novaChave = request.toModel();
        ChavePix chaveCriada = service.registra(novaChave);

        RegistraChavePixResponse response = new RegistraChavePixResponse(
            chaveCriada.getClienteId(),
            chaveCriada.getId()
        );

        return ResponseEntity.ok(response);
    }

}

record RegistraChavePixRequest(
    @NotBlank @ValidUUID String clienteId,
    @NotNull TipoDeChave tipoDeChave,
    @Size(max = 77) String chave,
    @NotNull TipoDeConta tipoDeConta
) {

    public ChavePix toModel() {
        if (!this.tipoDeChave.valida(this.chave))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chave informada inválida para o tipo selecionado");

        return new ChavePix(
            UUID.fromString(this.clienteId),
            this.tipoDeChave,
            this.chave,
            this.tipoDeConta
        );
    }
}

record RegistraChavePixResponse(
    UUID clienteId,
    UUID pixId
) {}
