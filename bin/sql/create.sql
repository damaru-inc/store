drop table if exists item;
drop table if exists category;

create table category (
id bigint not null auto_increment primary key,
description varchar(255)
);

create table item (
id bigint not null auto_increment primary key,
description varchar(255),
category_id bigint not null,
foreign key item_category_fk (category_id) references category(id)
);

