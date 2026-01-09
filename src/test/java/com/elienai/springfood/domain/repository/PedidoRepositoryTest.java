package com.elienai.springfood.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource( locations = "/application-test.properties",
	                 properties = {"spring.jpa.properties.hibernate.generate_statistics=true"})
@Sql(scripts = "/sql/EmissaoPedidoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }    
    
    @BeforeEach
    void setUp() {
        SessionFactory sessionFactory =
            entityManager.getEntityManagerFactory()
                         .unwrap(SessionFactory.class);

        sessionFactory.getStatistics().clear();
    }    
    
    @Test
    void deveExecutarApenasUmaQuery_quandoListarTodosPedidos() {
        SessionFactory sessionFactory =
                entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

        sessionFactory.getStatistics().clear();

        var pedidos = pedidoRepository.findAll();
        
        assertEquals(2, pedidos.size());
        
        var pedido1 = pedidos.get(0);
        var pedido2 = pedidos.get(1);
        
        assertEquals(1L, pedido1.getId());
        assertEquals(2L, pedido2.getId());
        
        pedidos.forEach(pedido -> {
            assertNotNull(pedido.getCliente().getNome());
            assertNotNull(pedido.getRestaurante().getNome());
            assertNotNull(pedido.getRestaurante().getCozinha().getNome());
            assertNotNull(pedido.getEnderecoEntrega().getCidade().getNome());
            assertNotNull(pedido.getEnderecoEntrega().getCidade().getEstado().getNome());
        }); 

        long quantidadeQueries = sessionFactory
        		.getStatistics()
                .getPrepareStatementCount();

        assertEquals(1, quantidadeQueries);
    }
}
