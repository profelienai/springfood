insert into usuario (id, nome, email, senha, data_cadastro) values
(1, 'João da Silva', 'joao@gmail.com', '123', utc_timestamp),
(2, 'Maria Joaquina', 'maria@gmail.com', '456', utc_timestamp);

insert into grupo (id, nome) values (1, 'Gerente'), (2, 'Vendedor');

insert into usuario_grupo (usuario_id, grupo_id) values (1, 1);