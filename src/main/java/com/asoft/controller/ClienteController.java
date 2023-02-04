package com.asoft.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.asoft.exception.CodigoNaoExisteException;
import com.asoft.exception.ErroChaveEstrangueiraEmUsoException;
import com.asoft.model.Cidade;
import com.asoft.model.Cliente;
import com.asoft.model.Endereco;
import com.asoft.model.Estado;
import com.asoft.service.CidadeService;
import com.asoft.service.ClienteService;
import com.asoft.service.EnderecoService;
import com.asoft.service.EstadoService;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EnderecoService enderecoService;
	
	@Autowired
	private CidadeService cidadeService;
	
	@Autowired	
	private EstadoService estadoService;
	
	
	@RequestMapping
	public ModelAndView listarTodos(){
		
		ModelAndView clienteView = new ModelAndView("cliente/clientescon");
		
		List<Cliente> listaCliente = clienteService.consultarTodos();
		
		clienteView.addObject("clientes", listaCliente );
		
		return clienteView ;
	}
	
	@RequestMapping("/{clienteId}")
	public Optional<Cliente> listarId(@PathVariable Long clienteId) {
		
		return clienteService.consultaId(clienteId);
	}
	
	@GetMapping("/por-nome")
	public ResponseEntity<List<Cliente>> consultarLikeNome(String nome) {
		
		return ResponseEntity.ok().body(clienteService.consultaLikeNome(nome));
	}
	
	@GetMapping("/por-datanasc")
	public ResponseEntity<?> consultaDataNasc(LocalDate dataNasc){

			return ResponseEntity.ok().body(clienteService.consultaDataNasc(dataNasc));
	}
	
	@GetMapping("/por-telefone")
	public ResponseEntity<Cliente> consultarPorTelefone(String telefone){
		
		return ResponseEntity.ok().body(clienteService.consultaPorTelefone(telefone));
	}
	
	
	@GetMapping("/novo-cliente")
	public ModelAndView novoCliente() {
		
		ModelAndView clienteNovo = new ModelAndView("cliente/clientescad");
		
		clienteNovo.addObject("clienteNovo", new Cliente());
		clienteNovo.addObject("enderecoNovo", new Endereco());
		clienteNovo.addObject("cidadeNovo", new Cidade());
		clienteNovo.addObject("estadoNovo", new Estado());
		
		return clienteNovo ;
	}
	
	@PostMapping
	public ModelAndView adicionar(@ModelAttribute Cliente cliente, 
			@ModelAttribute Endereco endereco, @ModelAttribute Cidade cidade, @ModelAttribute Estado estado){
		
			cidade.setEstado(estadoService.salvar(estado) );
			
			endereco.setCidade(cidadeService.salvar(cidade) );
		
			cliente.setEndereco( enderecoService.salvar(endereco) );
			
			clienteService.salvar(cliente);
			
			return listarTodos();
	
	}
	
	@PutMapping("/{clienteId}")
	public ResponseEntity<Cliente> atualizar(@RequestBody Cliente cliente, @PathVariable Long clienteId){
		
		Optional<Cliente> clienteAtual = clienteService.consultaId(clienteId);
		
		if(clienteAtual.isPresent()) {
			
			BeanUtils.copyProperties(cliente, clienteAtual.get(), "id");
			
			Cliente clienteSalvo = clienteService.salvar( clienteAtual.get() );
			
			return ResponseEntity.ok().body(clienteSalvo);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{clienteId}")
	public ResponseEntity<?> remover(@PathVariable Long clienteId){
		
		try {
			
			clienteService.excluir(clienteId);
			
		}catch (CodigoNaoExisteException e) {
	    	return ResponseEntity.badRequest().body(e.getMessage());
		} 
		catch (ErroChaveEstrangueiraEmUsoException e) {
	    	return ResponseEntity.badRequest().body(e.getMessage());
		}

		return ResponseEntity.ok().build();
	}
	
	
}
