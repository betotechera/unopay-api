create table authority (
  name VARCHAR(256),
  description VARCHAR(256)
);

create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4000),
  autoapprove VARCHAR(256)
);

create table oauth_user_details (
  id VARCHAR(256) PRIMARY KEY,
  email VARCHAR(256),
  name VARCHAR(256),
  password VARCHAR(256),
  version integer,
  CONSTRAINT oauth_user_details_uk UNIQUE (email)
);

create table oauth_user_authorities (
  user_id VARCHAR(256),
  authority VARCHAR(256)
);

create table oauth_client_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication BLOB,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication BLOB
);

create table oauth_code (
  code VARCHAR(256), authentication BLOB
);

create table oauth_approvals (
	userId VARCHAR(256),
	clientId VARCHAR(256),
	scope VARCHAR(256),
	status VARCHAR(10),
	expiresAt TIMESTAMP,
	lastModifiedAt TIMESTAMP
);

create table oauth_groups (
	 id VARCHAR(256) PRIMARY KEY,
	group_name varchar(50) not null,
	description VARCHAR(256),
	version integer,
	 CONSTRAINT oauth_group_uk UNIQUE (group_name)
);

create table oauth_group_authorities (
	group_id varchar(256) not null,
	authority varchar(50) not null,
	constraint fk_group_authorities_group foreign key(group_id) references oauth_groups(id)
);

create table oauth_group_members (
	user_id varchar(256) not null,
	group_id varchar(256) not null,
	constraint fk_oauth_group_members foreign key(group_id) references oauth_groups(id)
);
