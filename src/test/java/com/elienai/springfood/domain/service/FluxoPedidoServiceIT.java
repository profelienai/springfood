package com.elienai.springfood.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.StatusPedidoEnum;
import com.elienai.springfood.domain.repository.PedidoRepository;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(
	scripts = "/sql/EmissaoPedidoServiceIT.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class FluxoPedidoServiceIT {

	@Autowired
	private FluxoPedidoService fluxoPedidoService;

	@Autowired
	private PedidoRepository pedidoRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;
	
    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }	
    
	@Test
	void deveConfirmarPedidoComSucesso() {
		fluxoPedidoService.confirmar(1L);
		
		// Confirma persistência no banco via Repository
		Optional<Pedido> optPedido = pedidoRepository.findById(1L);
		assertThat(optPedido.isPresent()).isTrue();
		
		Pedido pedido = optPedido.get();
		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.CONFIRMADO);
		assertThat(pedido.getDataConfirmacao()).isNotNull();
	}

	@Test
	void deveFalharAoConfirmarPedidoQueNaoEstaCriado() {
		fluxoPedidoService.confirmar(1L); // confirma uma vez

		assertThatThrownBy(() -> fluxoPedidoService.confirmar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessageContaining("Status do pedido 1 não pode ser alterado");
	}

	@Test
	void deveCancelarPedidoComSucesso() {
		fluxoPedidoService.cancelar(1L);

		// Confirma persistência no banco via Repository
		Optional<Pedido> optPedido = pedidoRepository.findById(1L);
		assertThat(optPedido.isPresent()).isTrue();
		
		Pedido pedido = optPedido.get();
		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.CANCELADO);
		assertThat(pedido.getDataCancelamento()).isNotNull();		
	}

	@Test
	void deveFalharAoCancelarPedidoQueNaoEstaCriado() {
		fluxoPedidoService.confirmar(1L);

		assertThatThrownBy(() -> fluxoPedidoService.cancelar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessageContaining("Status do pedido 1 não pode ser alterado");
	}

	@Test
	void deveEntregarPedidoComSucesso() {
		fluxoPedidoService.confirmar(1L);
		fluxoPedidoService.entregar(1L);

		// Confirma persistência no banco via Repository
		Optional<Pedido> optPedido = pedidoRepository.findById(1L);
		assertThat(optPedido.isPresent()).isTrue();
		
		Pedido pedido = optPedido.get();
		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.ENTREGUE);
		assertThat(pedido.getDataEntrega()).isNotNull();			
	}

	@Test
	void deveFalharAoEntregarPedidoNaoConfirmado() {
		assertThatThrownBy(() -> fluxoPedidoService.entregar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessageContaining("Status do pedido 1 não pode ser alterado");
	}
}
