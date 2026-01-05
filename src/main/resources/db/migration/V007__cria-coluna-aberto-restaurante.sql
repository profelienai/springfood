alter table restaurante add aberto tinyint not null;
update restaurante set aberto = false;