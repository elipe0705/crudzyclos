package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Denuncia;
import br.com.zyclos.model.repository.DenunciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DenunciaService {

    @Autowired // Injeção de dependência automática
    private DenunciaRepository denunciaRepository;

    // Método responsável em listar todas as denúncias cadastradas no banco de dados

    public List<Denuncia> findAll() {
        return denunciaRepository.findAll();
    }

    // Método responsável em criar a denúncia no banco de dados

    public Denuncia save(Denuncia denuncia) {
        if (denuncia.getStatus() == null) {
            denuncia.setStatus("PENDENTE");
        }
        denuncia.setDataCadastro(LocalDateTime.now());
        denuncia.setCodStatus(true);
        return denunciaRepository.save(denuncia);
    }

    // Método responsável em listar a denúncia por ID

    public Denuncia findById(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada com o id " + id));
    }

    // Método responsável em atualizar a denúncia

    public Denuncia update(Long id, Denuncia denuncia) {
        Denuncia denunciaExistente = findById(id);
        denunciaExistente.setMotivo(denuncia.getMotivo());
        denunciaExistente.setDescricao(denuncia.getDescricao());
        denunciaExistente.setStatus(denuncia.getStatus());
        denunciaExistente.setRespostaModeracao(denuncia.getRespostaModeracao());
        denunciaExistente.setDataResolucao(denuncia.getDataResolucao());
        denunciaExistente.setDenuncianteId(denuncia.getDenuncianteId());
        denunciaExistente.setAnuncioId(denuncia.getAnuncioId());
        denunciaExistente.setUsuarioDenunciadoId(denuncia.getUsuarioDenunciadoId());
        return denunciaRepository.save(denunciaExistente);
    }

    // Método responsável em excluir a denúncia ( exclusão física )

    public void delete(Long id) {
        Denuncia denunciaExistente = findById(id);
        denunciaRepository.delete(denunciaExistente);
    }
}
