create table if not exists auth_log (
                                        id uuid primary key default uuid_generate_v4(),
                                        actor_type text not null,
                                        actor_id text,
                                        event text not null,
                                        ip text,
                                        user_agent text,
                                        at timestamptz not null default now()
);
