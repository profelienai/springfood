insert into estado (id, nome) VALUES (1, 'Rio de Janeiro');
insert into cidade (id, nome, estado_id) values (1, 'Campo Grande', 1);

insert into cozinha (id, nome) values (1, 'Italiana');
insert into cozinha (id, nome) values (2, 'Arabe');

insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao, ativo, aberto, endereco_cidade_id, endereco_cep, endereco_logradouro, endereco_numero, endereco_bairro) values (1, 'Aramad', 10.50, 2, utc_timestamp, utc_timestamp, true, true, 1, '38400-999', 'Rua João Pinheiro', '1000', 'Centro');
insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao, ativo, aberto) values (2, 'Di Napoli', 9.50, 1, utc_timestamp, utc_timestamp, false, false);

insert into forma_pagamento (id, descricao) values (1, 'Cartão de crédito');
insert into forma_pagamento (id, descricao) values (2, 'Cartão de débito');
insert into forma_pagamento (id, descricao) values (3, 'Dinheiro');

insert into restaurante_forma_pagamento (restaurante_id, forma_pagamento_id) values (1, 2), (1, 3), (2, 3);

insert into usuario (id, nome, email, senha, data_cadastro) values
(1, 'João da Silva', 'joao.ger@gmail.com', '123', utc_timestamp),
(2, 'Maria Joaquina', 'maria.vnd@gmail.com', '123', utc_timestamp);

insert into restaurante_usuario_responsavel (restaurante_id, usuario_id) values (1, 1), (2, 1);