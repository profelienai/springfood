insert into cozinha (id, nome) values (1, 'Italiana');
insert into cozinha (id, nome) values (2, 'Arabe');

insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao) values (1, 'Aramad', 10.50, 2, utc_timestamp, utc_timestamp);
insert into restaurante (id, nome, taxa_frete, cozinha_id, data_cadastro, data_atualizacao) values (2, 'Di Napoli', 9.50, 1, utc_timestamp, utc_timestamp);