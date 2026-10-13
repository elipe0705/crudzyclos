package br.com.zyclos.controller;

import br.com.zyclos.model.entity.Seguidor;
import br.com.zyclos.model.services.SeguidorService;
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
@RequestMapping("/api/v1/seguidores")
public class SeguidorController {

    @Autowired
    private SeguidorService seguidorService;

    @GetMapping
    public ResponseEntity<List<Seguidor>> findAll() {
        return ResponseEntity.ok(seguidorService.findAll());
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody Seguidor seguidor) {
        try {
            Seguidor novo = seguidorService.save(seguidor);
            return ResponseEntity.status(HttpStatus.CREATED).body(novo);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.BAD_REQUEST, "Dados inválidos: confira os campos obrigatórios e os ids relacionados");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(seguidorService.findById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Seguidor não encontrado com o id " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Seguidor seguidor) {
        try {
            return ResponseEntity.ok(seguidorService.update(Long.parseLong(id), seguidor));
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.BAD_REQUEST, "Dados inválidos: confira os campos obrigatórios e os ids relacionados");
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Seguidor não encontrado com o id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        try {
            seguidorService.delete(Long.parseLong(id));
            return ResponseEntity.ok().body(
                    Map.of(
                            "status", 200,
                            "message", "Seguidor excluído com sucesso"
                    )
            );
        } catch (NumberFormatException e) {
            return erro(HttpStatus.BAD_REQUEST, "O id informado não é válido: " + id);
        } catch (DataIntegrityViolationException e) {
            return erro(HttpStatus.CONFLICT, "Não é possível excluir: existem registros vinculados ao id " + id);
        } catch (RuntimeException e) {
            return erro(HttpStatus.NOT_FOUND, "Seguidor não encontrado com o id " + id);
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
