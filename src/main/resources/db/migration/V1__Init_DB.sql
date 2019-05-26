create table hibernate_sequence
(
    next_val bigint
) engine = InnoDB;

insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);

create table stage
(
    id                   bigint  not null,
    code                 varchar(255),
    name                 varchar(255),
    required_teams_count integer not null,
    primary key (id)
) engine = InnoDB;

create table team
(
    id   bigint not null,
    name varchar(32),
    primary key (id)
) engine = InnoDB;

create table tournament
(
    id                   bigint   not null,
    description          varchar(2048),
    end_date             datetime not null,
    location             varchar(256),
    name                 varchar(64),
    start_date           datetime not null,
    current_stage        bigint,
    tournament_winner_id bigint,
    primary key (id)
) engine = InnoDB;

create table tournament_match
(
    id                 bigint           not null,
    first_team_result  double precision not null,
    second_team_result double precision not null,
    first_team_id      bigint,
    second_team_id     bigint,
    stage_id           bigint,
    tournament_id      bigint,
    winner_id          bigint,
    primary key (id)
) engine = InnoDB;

create table tournament_team
(
    id             bigint not null,
    sequent_number integer,
    team_id        bigint not null,
    tournament_id  bigint,
    primary key (id)
) engine = InnoDB;

create table user
(
    id       bigint not null,
    active   INTEGER,
    password varchar(512),
    username varchar(16),
    primary key (id)
) engine = InnoDB;

create table user_role
(
    user_id bigint not null,
    roles   varchar(255)
) engine = InnoDB;

alter table team
    add constraint UK_g2l9qqsoeuynt4r5ofdt1x2td unique (name);
alter table user
    add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);
alter table tournament
    add constraint FKapprx1dvakvj9oqewt8g65s2k foreign key (current_stage) references stage (id);
alter table tournament
    add constraint FKjsv2xt7leixdltn3sdlus2wfx foreign key (tournament_winner_id) references team (id);
alter table tournament_match
    add constraint FK7rq0bq155qo4iwokuk603n8qv foreign key (first_team_id) references tournament_team (id);
alter table tournament_match
    add constraint FKstipaud6mfgnpaw4mu4y2n1bi foreign key (second_team_id) references tournament_team (id);
alter table tournament_match
    add constraint FKouj73fje4biqlbhay1c90gp4s foreign key (stage_id) references stage (id);
alter table tournament_match
    add constraint FKdkuifcmini0m4kng459h696cy foreign key (tournament_id) references tournament (id);
alter table tournament_match
    add constraint FKju1fr0koxjupisdqahjr1dkpo foreign key (winner_id) references tournament_team (id);
alter table tournament_team
    add constraint FKe2nr7bhms79ni7aa6jjdf3ioj foreign key (team_id) references team (id);
alter table tournament_team
    add constraint FKnpt2r8h2uwq1j0iesu13so87p foreign key (tournament_id) references tournament (id);
alter table user_role
    add constraint FK859n2jvi8ivhui0rl0esws6o foreign key (user_id) references user (id);