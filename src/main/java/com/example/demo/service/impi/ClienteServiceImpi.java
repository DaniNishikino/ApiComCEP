package com.example.demo.service.impi;

import com.example.demo.model.Cliente;
import com.example.demo.model.ClienteRepository;
import com.example.demo.model.Endereco;
import com.example.demo.model.EnderecoRepository;
import com.example.demo.service.ClienteService;
import com.example.demo.service.ViaCepService;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpi implements ClienteService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final ViaCepService viaCepService;

    public ClienteServiceImpi(EnderecoRepository enderecoRepository, ClienteRepository clienteRepository, ViaCepService viaCepService) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
        this.viaCepService = viaCepService;
    }

    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null);
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        Optional<Cliente> cliente1 = clienteRepository.findById(id);
        if (cliente1.isPresent()){
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
        String cep  =  cliente.getEndereco().getCep();
        Endereco endereco =  enderecoRepository.findById(cep).orElseGet(() ->{
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            novoEndereco.setCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }
}
