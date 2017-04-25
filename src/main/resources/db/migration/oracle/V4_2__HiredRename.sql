ALTER table oauth_user_details DROP CONSTRAINT fk_user_hired;
ALTER TABLE oauth_user_details DROP COLUMN hired_id;
UPDATE authority set name = 'ROLE_LIST_CONTRACTOR' where name = 'ROLE_LIST_HIRED';
UPDATE authority set name = 'ROLE_MANAGE_CONTRACTOR' where name = 'ROLE_MANAGE_HIRED';
UPDATE oauth_group_authorities set authority = 'ROLE_LIST_CONTRACTOR' where authority = 'ROLE_LIST_HIRED';
UPDATE oauth_group_authorities set authority = 'ROLE_MANAGE_CONTRACTOR' where authority = 'ROLE_MANAGE_HIRED';

ALTER TABLE hired RENAME TO contractor;

ALTER TABLE oauth_user_details ADD contractor_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_contractor FOREIGN KEY(contractor_id) REFERENCES contractor(id);
