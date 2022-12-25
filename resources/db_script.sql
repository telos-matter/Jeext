-- /!\ DON'T USE PRIMITVE TYPES IN THE MODELS, THEY CAN'T TAKE NULL /!\
-- Don't forget to name the references xxx_id, gender_id for example. But also dont forget that in the model it should just be named xxx.
-- VSCode may show some names highlighted but they work just fine with mySQL.
-- /!\ Don't use `foo` in names to overcome the limitation tho, you will get Exceptions in the JPA.. 
-- Weirdly, you can't use comments like this "--foo", only "-- foo"

-- DROP
DROP TABLE permission;
DROP TABLE user;

-- CREATE
CREATE TABLE user ( 
      id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
      username VARCHAR(32) NOT NULL UNIQUE,
      password VARCHAR(256) NOT NULL,
      email VARCHAR(64),
      first_name VARCHAR(64),
      last_name VARCHAR(64),
      isMale BOOLEAN,
      creation_date DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE permission (
      name VARCHAR(32) NOT NULL,
      user_id INT NOT NULL,

      constraint FK_permission_user foreign key (user_id) references user(id),

      constraint UQ_permission unique (name, user_id)
);

-- INSERT
INSERT INTO user (username, password, first_name, last_name, isMale) VALUES ('root',"root",'ROOT','ROOT', 1);
INSERT INTO permission (name, user_id) values ('ROOT', 1);

