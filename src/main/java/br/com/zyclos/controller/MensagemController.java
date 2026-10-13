package br.com.zyclos.controller;

import br.com.zyclos.model.entity.Mensagem;
import br.com.zyclos.model.services.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 ResponseEntity: Toda resposta HTTP (status, cabeçalhos e corpo), aqui temos mais controle sobre o que é devolvido
                 para o cliente
 1. Status HTTP: (200 OK, 201 CREATED, 404 NOT FOUND etc...)
 2. Headers: ( cabeçalhos extras, como Location, Authorization etc...)
 3. Body: ( o objeto que será convertido em JSON/XML para o cliente )

 @RequestBody : Corpo da requisição (Recebendo um objeto JSON )
*/

@RestController
@RequestMapping("/api/v1/mensagens")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @GetMapping
    public ResponseEntity<List<Mensagem>> findAll() {
        return ResponseEntity.ok(mensagemService.findAll());
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody Mensagem mensagem) {
        try {
            Mensagem novo = mensagemService.save(mensagem);
            return ResponseEntity.status(HttpStatus.CREATED).body(novo);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.BAD_REQUEST, "Dados inválidos: confira os campos obrigatórios e os ids relacionados");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(mensagemService.findById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Mensagem não encontrada com o id " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Mensagem mensagem) {
        try {
            return ResponseEntity.ok(mensagemService.update(Long.parseLong(id), mensagem));
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.BAD_REQUEST, "Dados inválidos: confira os campos obrigatórios e os ids relacionados");
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Mensagem não encontrada com o id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        try {
            mensagemService.delete(Long.parseLong(id));
            return ResponseEntity.ok().body(
                    Map.of(
                            "status", 200,
                            "message", "Mensagem excluída com sucesso"
                    )
            );
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.CONFLICT, "Não é possível excluir: existem registros vinculados ao id " + id);
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Mensagem não encontrada com o id " + id);
        }
    }

    private ResponseEntity<Object> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", mensagem
                )
        );
    }
}
