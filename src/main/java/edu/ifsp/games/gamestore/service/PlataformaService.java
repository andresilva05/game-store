package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.PlataformaDAO;
import edu.ifsp.games.gamestore.model.Plataforma;

import java.util.List;

public class PlataformaService {

    private final PlataformaDAO plataformaDAO;

    public PlataformaService() {
        this.plataformaDAO = new PlataformaDAO();
    }

    public PlataformaService(PlataformaDAO plataformaDAO) {
        this.plataformaDAO = plataformaDAO;
    }

    public void salvar(Plataforma plataforma) {
        if (plataforma == null || plataforma.getNome() == null || plataforma.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome da plataforma é obrigatório.");
        }

        if (plataforma.getId() == 0) {
            plataformaDAO.inserir(plataforma);
        } else {
            plataformaDAO.atualizar(plataforma);
        }
    }

    public List<Plataforma> listarTodas() {
        return plataformaDAO.listarTodas();
    }

    public Plataforma buscarPorId(int id) {
        Plataforma p = plataformaDAO.buscarPorId(id);
        if (p == null) {
            throw new RegraNegocioException("Plataforma não encontrada para o ID: " + id);
        }
        return p;
    }

    public void excluir(int id) {
        buscarPorId(id);

        if (plataformaDAO.possuiProdutos(id)) {
            throw new RegraNegocioException("Não é possível excluir uma plataforma que possui Gift Cards cadastrados.");
        }

        plataformaDAO.excluir(id);
    }
}