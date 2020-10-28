create table projects (
    id serial primary key,
    project_name varchar(100) not null,
    project_desc varchar(200),
    project_status boolean default true
)