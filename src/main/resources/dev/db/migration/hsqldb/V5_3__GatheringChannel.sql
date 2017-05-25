create table establishment_gathering(
    establishment_id varchar(256) not null,
    gathering_channel varchar(256) not null,
    constraint fk_est_est_gath foreign key(establishment_id) references establishment(id)
);

alter table establishment
    drop column gathering_channel;