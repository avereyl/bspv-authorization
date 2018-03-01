DROP TABLE IF EXISTS users;
CREATE TABLE users (
	id UUID NOT NULL,
	version BIGINT NOT NULL DEFAULT 1,
	username VARCHAR(50) NOT NULL,
	secretkey  VARCHAR(128) NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT TRUE,
	email VARCHAR(50),
	PRIMARY KEY (id),
	CONSTRAINT uk_users__1 UNIQUE(username)
);

DROP TABLE IF EXISTS authorities;
CREATE TABLE authorities (
    user_id UUID NOT NULL,
    authority VARCHAR(50) NOT NULL,
    service VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority, service),
    CONSTRAINT fk_authorities__users FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE INDEX ix_users__username ON users(username);
CREATE INDEX ix_authorities__userid_service ON authorities(user_id, service);
