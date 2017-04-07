insert into bank(bacen_code, name) values(001, 'BANCO DO BRASIL S.A. Banco do Brasil ');
insert into bank(bacen_code, name) values(033, 'BANCO SANTANDER (BRASIL) S.A');
insert into bank(bacen_code, name) values(318, 'BANCO BMG S.A');
insert into bank(bacen_code, name) values(320, 'BANCO INDUSTRIAL E COMERCIAL S.A');
insert into bank(bacen_code, name) values(341, 'BANCO ITAÚ S.A');
insert into bank(bacen_code, name) values(473, 'BANCO CAIXA GERAL - BRASIL S.A');
insert into bank(bacen_code, name) values(477, 'CITIBANK N.A. Banco Comercial Estrangeiro ');
insert into bank(bacen_code, name) values(479, 'BANCO ITAUBANK S.A');


insert into bank_account(id, bank_bacen_code, agency,agency_dv, account_number, account_number_dv, account_type) values ('1', 341, '1234', 'A', '5467884', 'A1', 'CURRENT')
insert into bank_account(id, bank_bacen_code, agency,agency_dv, account_number, account_number_dv, account_type) values ('2', 341, '558', '2', '79789444', '1', 'CURRENT')
insert into payment_bank_account(id, bank_account_id, authorize_transfer,deposit_period) values ('1', '1', '1', 'WEEKLY')