delete from item;
delete from category;

insert into category(description) values ("Tea");
insert into category(description) values ("Coffee");

insert into item(description, category_id) values ("Oolong", 1);
insert into item(description, category_id) values ("Kimodo Dragon", 2);
