CREATE TABLE ENTITY(
entity_inte int(4) primary key auto_increment,
entity_lon bigint(8) not null,
entity_str varchar(4),
entity_fl float not null,
entity_dou double not null,
entity_date date not null
);