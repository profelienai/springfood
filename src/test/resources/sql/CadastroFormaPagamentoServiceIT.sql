insert into forma_pagamento (id, descricao) values (1, 'Cartao de credito');
insert into forma_pagamento (id, descricao) values (2, 'Cartao de debito');
insert into forma_pagamento (id, descricao) values (3, 'Dinheiro');

insert into cozinha (id, nome) values (2, 'Arabe');

insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao, ativo) values (1, 'Aramad', 10.50, 2, utc_timestamp, utc_timestamp, true);

insert into restaurante_forma_pagamento (restaurante_id, forma_pagamento_id) values (1, 1), (1, 2);
