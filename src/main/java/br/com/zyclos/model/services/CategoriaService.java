package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Categoria;
import br.com.zyclos.model.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired // Injeção de dependência automática
    private CategoriaRepository categoriaRepository;

    // Método responsável em listar todas as categorias cadastradas no banco de dados

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    // Método responsável em criar a categoria no banco de dados

    public Categoria save(Categoria categoria) {
        categoria.setCodStatus(true);
        return categoriaRepository.save(categoria);
    }

    // Método responsável em listar a categoria por ID

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o id " + id));
    }

    // Método responsável em atualizar a categoria

    public Categoria update(Long id, Categoria categoria) {
        Categoria categoriaExistente = findById(id);
        categoriaExistente.setNome(categoria.getNome());
        categoriaExistente.setSlug(categoria.getSlug());
        categoriaExistente.setDescricao(categoria.getDescricao());
        return categoriaRepository.save(categoriaExistente);
    }

    // Método responsável em excluir a categoria ( exclusão física )

    public void delete(Long id) {
        Categoria categoriaExistente = findById(id);
        categoriaRepository.delete(categoriaExistente);
    }
}
