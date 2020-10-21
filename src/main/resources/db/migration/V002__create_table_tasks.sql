create table tasks (
    id serial primary key,
    task_name varchar(100) not null,
    task_desc varchar(200)
)