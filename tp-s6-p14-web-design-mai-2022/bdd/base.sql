create database final_web;
create role final_web login password 'final_web';
alter database final_web owner to final_web;

-- \q
-- psql -U final_web final_web
-- final_web

CREATE  TABLE "public"."admin" (
                                   id                   serial  NOT NULL  ,
                                   nomadmin             varchar  NOT NULL  ,
                                   mdp                  varchar  NOT NULL  ,
                                   CONSTRAINT pk_tbl PRIMARY KEY ( id )
);

CREATE  TABLE "public".contenu (
                                   id                   integer DEFAULT nextval('contenu_id_seq'::regclass) NOT NULL  ,
                                   titre                varchar  NOT NULL  ,
                                   description          text  NOT NULL  ,
                                   datecreation         timestamp DEFAULT CURRENT_TIMESTAMP   ,
                                   idcategorie          integer    ,
                                   image                varchar    ,
                                   CONSTRAINT pk_contenu PRIMARY KEY ( id )
);

ALTER TABLE "public".contenu ADD CONSTRAINT fk_contenu_categorie FOREIGN KEY ( idcategorie ) REFERENCES "public".categorie( id );


CREATE  TABLE "public".contenusupprime (
    idcontenu            integer
);

ALTER TABLE "public".contenusupprime ADD CONSTRAINT fk_contenusupprime_contenu FOREIGN KEY ( idcontenu ) REFERENCES "public".contenu( id );

CREATE  TABLE "public".categorie (
                                     id                   serial  NOT NULL  ,
                                     nomcategorie         varchar  NOT NULL  ,
                                     CONSTRAINT pk_categorie PRIMARY KEY ( id )
);

CREATE OR REPLACE VIEW "public".v_contenu
AS SELECT contenu.*
   FROM contenu
            JOIN categorie
                 ON contenu.idcategorie = categorie.id;;

