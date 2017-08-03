alter table product drop column maximum_credit_insertion;
alter table product drop column minimum_credit_insertion;
alter table payment_rule_group add column maximum_credit_insertion decimal(*,2);
alter table payment_rule_group add column minimum_credit_insertion decimal(*,2);

