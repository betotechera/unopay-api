alter table user_credit_card rename to person_credit_card;
alter table person_credit_card rename user_id to person_id;
alter table person_credit_card drop constraint fk_credit_card_user;
alter table person_credit_card add constraint fk_credit_card_person foreign key(person_id) references person(id);