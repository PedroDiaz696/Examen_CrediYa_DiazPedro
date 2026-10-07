package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao.ClienteDAO;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.ValidacionException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.FileManager;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.validation.Validador;
import java.util.List;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class ClienteService {
    private final ClienteDAO clienteDAO;
    private final FileManager fileManager;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
        this.fileManager = new FileManager();
    }

    public void registrarCliente(Cliente cliente) {
        Validador.validarTextoObligatorio(cliente.getNombre(), "nombre", 80);
        Validador.validarTextoObligatorio(cliente.getDocumento(), "documento", 30);
        Validador.validarCorreo(cliente.getCorreo());
        Validador.validarTelefono(cliente.getTelefono());
        validarNoDuplicado(cliente);

        clienteDAO.guardar(cliente);
        fileManager.guardarClientes(clienteDAO.listar());
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = clienteDAO.buscarPorId(id);
        Validador.validarExistencia(cliente, "No existe un cliente con ID " + id + ".");
        return cliente;
    }

    /**
     * El documento y el correo de un cliente no se pueden repetir.
     */
    private void validarNoDuplicado(Cliente nuevo) {
        boolean duplicado = clienteDAO.listar().stream()
                .anyMatch(c -> nuevo.getDocumento().equalsIgnoreCase(c.getDocumento())
                        || nuevo.getCorreo().equalsIgnoreCase(c.getCorreo()));
        if (duplicado) {
            throw new ValidacionException("Ya existe un cliente registrado con ese documento o correo.");
        }
    }
}
