CREATE TABLE IF NOT EXISTS school (
    id serial PRIMARY KEY,
    school_name varchar(50) NOT NULL,
    description varchar(256),
    create_at timestamp,
    modify_at timestamp,
    modify_by varchar(64),
    create_by varchar(64)
);

-- Creating semester table
CREATE TABLE IF NOT EXISTS semester (
    id serial PRIMARY KEY,
    semester_name varchar(50) NOT NULL,
    year_semester int NOT NULL,
    event_name varchar(10000),
    description varchar(10000),
    school_id int
);

-- Creating project table
CREATE TABLE IF NOT EXISTS project (
   id serial PRIMARY KEY,
   group_name varchar(256) NOT NULL,
    title varchar(256) NOT NULL,
    client varchar(256),
    average_mark_v1 DOUBLE PRECISION,
    average_mark_v2 DOUBLE PRECISION,
    is_round1_closed boolean DEFAULT false,
    is_round2_closed boolean DEFAULT false,
    description varchar(10000),
    semester_id int,
    rank varchar(10),
    student varchar(4096),
    create_at timestamp,
    modify_at timestamp,
    modify_by varchar(64),
    create_by varchar(64)
    );

-- Creating judge table
CREATE TABLE IF NOT EXISTS judge (
     id serial PRIMARY KEY,
     account varchar(64),
     plain_pwd varchar(64), -- Saving plain password (Draft)
     pwd varchar(64),
     first_name varchar(64),
     last_name varchar(64),
     email varchar(64),
     phone varchar(64),
     description varchar(256),
     semester_id int,
     create_at timestamp,
     modify_at timestamp,
     modify_by varchar(64),
     create_by varchar(64),
     user_role varchar(64) NOT NULL
);

-- Creating student table
CREATE TABLE IF NOT EXISTS student (
   id serial PRIMARY KEY,
   first_name varchar(20) NOT NULL,
   last_name varchar(20) NOT NULL,
   project_id int,
   description varchar(256),
   create_at timestamp,
   modify_at timestamp,
   modify_by varchar(64),
   create_by varchar(64)
);

-- Creating judge_project table
CREATE TABLE IF NOT EXISTS judge_project (
     judge_id int NOT NULL,
     project_id int NOT NULL,
     PRIMARY KEY (judge_id, project_id)
);

-- Creating criteria table
CREATE TABLE IF NOT EXISTS criteria (
    id serial PRIMARY KEY,
    criteria_name varchar(256) NOT NULL,
    description varchar(1000),
    semester_id int,
    create_at timestamp,
    modify_at timestamp,
    modify_by varchar(64),
    create_by varchar(64)
);

-- Creating marking_round table
CREATE TABLE IF NOT EXISTS marking_round (
     id serial PRIMARY KEY,
     round int NOT NULL,
     criteria_id int,
     judge_id int NOT NULL,
     mark int NOT NULL,
     is_marked boolean NOT NULL DEFAULT false,
     project_id int NOT NULL,
     description text,
     create_at timestamp,
     modify_at timestamp,
     modify_by varchar(64),
     create_by varchar(64)
);

ALTER TABLE semester
    ADD CONSTRAINT FK_school FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE,
    ADD CONSTRAINT UK_semester UNIQUE (semester_name, school_id);

ALTER TABLE judge
    ADD CONSTRAINT FK_semester FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE;

ALTER TABLE project
    ADD CONSTRAINT FK_semester FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE;

ALTER TABLE criteria
    ADD CONSTRAINT FK_semester FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE;

ALTER TABLE student
    ADD CONSTRAINT FK_student_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE SET NULL;

ALTER TABLE judge_project
    ADD CONSTRAINT FK_judge FOREIGN KEY (judge_id) REFERENCES judge(id) ON DELETE CASCADE,
    ADD CONSTRAINT FK_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

ALTER TABLE marking_round
    ADD CONSTRAINT UK_marking_round UNIQUE (round, criteria_id, judge_id, project_id),
    ADD CONSTRAINT FK_marking_round_criteria FOREIGN KEY (criteria_id) REFERENCES criteria(id) ON DELETE CASCADE,
    ADD CONSTRAINT FK_marking_round_judge FOREIGN KEY (judge_id) REFERENCES judge(id) ON DELETE CASCADE,
    ADD CONSTRAINT FK_marking_round_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;

