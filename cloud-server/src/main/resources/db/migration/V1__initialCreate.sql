CREATE TABLE IF NOT EXISTS public.users
(
    user_password character varying(64) NOT NULL,
    user_name character varying(64) NOT NULL,
    id integer NOT NULL,
    home_dir character varying(128) NOT NULL,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.users
    OWNER to postgres;
