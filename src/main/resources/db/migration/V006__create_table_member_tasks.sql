create table member_tasks(
    member_id integer null references members(id),
    task_id integer null references tasks(id),
    PRIMARY KEY (member_id, task_id)
)