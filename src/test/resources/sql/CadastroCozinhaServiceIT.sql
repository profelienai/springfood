insert into cozinha (id, nome) values (1, 'Italiana');
insert into cozinha (id, nome) values (2, 'Árabe');

insert into estado (id, nome) values (1, 'Minas Gerais');
insert into cidade (id, nome, estado_id) values (1, 'Uberlândia', 1);

insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao, endereco_cidade_id, endereco_cep, endereco_logradouro, endereco_numero, endereco_bairro) values (1, 'Thai Gourmet', 10, 2, utc_timestamp, utc_timestamp, 1, '38400-999', 'Rua João Pinheiro', '1000', 'Centro');