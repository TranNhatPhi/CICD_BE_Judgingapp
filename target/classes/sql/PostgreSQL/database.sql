-- Creating school table
CREATE TABLE IF NOT EXISTS school (
id serial PRIMARY KEY,
school_name varchar(50) NOT NULL,
description varchar(256),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64)
);

-- Updating semester table
CREATE TABLE IF NOT EXISTS semester (
id serial PRIMARY KEY,
semester_name varchar(50) NOT NULL,
year_semester int NOT NULL,
school_id int,
FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE
);

-- Updating judge table
CREATE TABLE IF NOT EXISTS judge (
id serial PRIMARY KEY,
account varchar(64) NOT NULL,
pwd varchar(64) NOT NULL,
first_name varchar(64) NOT NULL,
last_name varchar(64) NOT NULL,
email varchar(64) NOT NULL UNIQUE,
phone varchar(64) NOT NULL UNIQUE,
round_enable boolean NOT NULL DEFAULT false,
description varchar(256),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64),
user_role varchar(64) NOT NULL,
semester_id int,
school_id int,
FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE,
FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE
);

-- Updating project table
CREATE TABLE IF NOT EXISTS project (
id serial PRIMARY KEY,
group_name varchar(50) NOT NULL,
title varchar(20) NOT NULL,
client varchar(20),
average_mark_v1 double,
average_mark_v2 double,
description varchar(512),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64),
semester_id int,
school_id int,
FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE,
FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE
);

-- Updating criteria table
CREATE TABLE IF NOT EXISTS criteria (
id serial PRIMARY KEY,
criteria_name varchar(50) NOT NULL,
description varchar(100),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64),
semester_id int,
school_id int,
FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE,
FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE
);

-- Updating student table
CREATE TABLE IF NOT EXISTS student (
id serial PRIMARY KEY,
first_name varchar(20) NOT NULL,
last_name varchar(20) NOT NULL,
project_id int,
description varchar(256),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64),
FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE SET NULL
);

-- Updating judge_project table
CREATE TABLE IF NOT EXISTS judge_project (
 judge_id int NOT NULL,
 project_id int NOT NULL,
 PRIMARY KEY (judge_id, project_id),
FOREIGN KEY (judge_id) REFERENCES judge(id) ON DELETE CASCADE,
FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

-- Updating marking_round table
CREATE TABLE IF NOT EXISTS marking_round (
 id serial PRIMARY KEY,
 round int NOT NULL,
 criteria_id int,
 judge_id int NOT NULL,
 mark int NOT NULL,
 is_marked boolean NOT NULL DEFAULT false,
 project_id int NOT NULL,
 description varchar(256),
create_at timestamp,
modify_at timestamp,
modify_by varchar(64),
create_by varchar(64),
CONSTRAINT UK_marking_round UNIQUE (round, criteria_id, judge_id, project_id),
FOREIGN KEY (criteria_id) REFERENCES criteria(id) ON DELETE CASCADE,
FOREIGN KEY (judge_id) REFERENCES judge(id) ON DELETE CASCADE,
FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
