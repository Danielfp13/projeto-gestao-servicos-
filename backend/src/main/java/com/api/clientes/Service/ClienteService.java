package com.api.clientes.Service;

import com.api.clientes.model.entity.Cliente;
import com.api.clientes.repository.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repositoty;


    public Cliente find(Integer id) {
        return repositoty
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não existe cliente com id = " + id + "."));
    }

    public List<Cliente> findAll() {
        return repositoty.findAll();
    }

    public Page<Cliente> findPage(Integer page, Integer linePerPage, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, linePerPage, Sort.Direction.valueOf(direction), orderBy);
        return repositoty.findAll(pageRequest);
    }

    public void delete(Integer id) {
        find(id);
        try {
            repositoty.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não se pode excluir clientes com serviços.");
        }
    }

    public Cliente update(Cliente cliente, Integer id) {
        Cliente newCliente = find(id);
        BeanUtils.copyProperties(cliente, newCliente);
        newCliente.setId(id);
        return repositoty.save(newCliente);
    }

    public Cliente insert(Cliente cliente) {
        cliente.setId(null);
        return repositoty.save(cliente);
    }
}
