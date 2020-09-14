alter table user_credit_card rename to person_credit_card;
alter table person_credit_card rename user_id to person_id;
alter table person_credit_card drop constraint fk_credit_card_user;
update person_credit_card set person_id = c.person_id
from person_credit_card cc
inner join oauth_user_details u on cc.person_id = u.id
inner join contractor c on u.contractor_id = c."id";
alter table person_credit_card add constraint fk_credit_card_person foreign key(person_id) references person(id);
