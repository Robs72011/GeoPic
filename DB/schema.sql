DROP SCHEMA IF EXISTS galleria CASCADE;

CREATE SCHEMA galleria AUTHORIZATION postgres;


------------------------------------------------------------------------------------
--dominio dedicato agli identificativi degli utenti. dt sta per "data type"
CREATE DOMAIN galleria.id_user_dt CHAR(5)
    CONSTRAINT id_user_dt_alnum CHECK (VALUE ~ '^[A-Z0-9]+$');


--dominio dedicato agli identificati degli oggetti, per oggetti s'intende foto, video gallerie, etc.
CREATE DOMAIN galleria.id_object_dt CHAR(10)
    CONSTRAINT id_object_dt_alnum CHECK (VALUE ~ '^[A-Z0-9]+$');


--dominio dedicato alle coordinate, espresse in modo decimale, con una precisione fino a 2 decimali.
CREATE DOMAIN galleria.coo_dt CHAR(14)
    CONSTRAINT coordinate_format CHECK (VALUE ~ '^[+-][0-9]{2}\.[0-9]{2},[+-][0-9]{3}\.[0-9]{2}$');

CREATE DOMAIN galleria.string VARCHAR(50);

------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS galleria.UTENTE(
    IDUtente galleria.id_user_dt NOT NULL,
    Username galleria.string NOT NULL,
	Password galleria.string NOT NULL,
    IsAdmin BOOLEAN NOT NULL DEFAULT FALSE,
    
	IsSoggetto BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT utente_pk PRIMARY KEY (IDUtente)
);

CREATE TABLE IF NOT EXISTS galleria.LUOGO(
    Coordinate  galleria.coo_dt NOT NULL,
    Toponimo galleria.string UNIQUE,

    CONSTRAINT luogo_pk PRIMARY KEY (Coordinate)

);

CREATE TABLE IF NOT EXISTS galleria.GALLERIA(
    IDGalleria galleria.id_object_dt  NOT NULL,
    NomeGalleria galleria.string  NOT NULL,
    Condivisa BOOLEAN NOT NULL DEFAULT FALSE,
    Proprietario galleria.id_user_dt  NOT NULL,

    CONSTRAINT galleria_pk PRIMARY KEY (IDGalleria),

    CONSTRAINT proprietario_fk FOREIGN KEY (Proprietario) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.FOTOGRAFIA( 
	IDFoto galleria.id_object_dt NOT NULL,
	Dispositivo galleria.string NOT NULL DEFAULT 'Nameless', --il dispositivo non e' specificato nel caso l'utente non lo inserisce
    Autore galleria.id_user_dt NOT NULL,
    Coordinate galleria.coo_dt,
	Visibilita	BOOLEAN NOT NULL DEFAULT TRUE,
	DataScatto DATE NOT NULL,
	DataEliminazione DATE DEFAULT NULL,

    CONSTRAINT foto_pk PRIMARY KEY (IDFoto),

    CONSTRAINT autore_fk FOREIGN KEY (Autore) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT coordinate_fk FOREIGN KEY (Coordinate) REFERENCES galleria.LUOGO(Coordinate) ON UPDATE CASCADE ON DELETE NO ACTION
    );



CREATE TABLE IF NOT EXISTS galleria.VIDEO(
    IDVideo galleria.id_object_dt NOT NULL,
    TitoloVideo galleria.string NOT NULL,
    Descrizione TEXT,

    Galleria galleria.id_object_dt NOT NULL,


    CONSTRAINT video_pk PRIMARY KEY (IDVideo),

    CONSTRAINT galleria_fk FOREIGN KEY (Galleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.SOGGETTO(
    NomeSoggetto galleria.string NOT NULL,
    Categoria galleria.string NOT NULL,

	IDUtente galleria.id_user_dt UNIQUE DEFAULT NULL,

    CONSTRAINT soggetto_pk PRIMARY KEY (NomeSoggetto),

    CONSTRAINT idutente_fk FOREIGN KEY (IDUtente) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);





CREATE TABLE IF NOT EXISTS galleria.PARTECIPA(
    IDGalleria galleria.id_object_dt  NOT NULL,
    IDUtente galleria.id_user_dt  NOT NULL,

    CONSTRAINT partecipa_pk PRIMARY KEY (IDGalleria, IDUtente),

    CONSTRAINT galleria_fk FOREIGN KEY (IDGalleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT utente_partecipante_fk FOREIGN KEY (IDUtente) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.CONTIENE(
    IDGalleria galleria.id_object_dt  NOT NULL,
    IDFoto galleria.id_object_dt  NOT NULL,

    CONSTRAINT contiene_pk PRIMARY KEY (IDGalleria, IDFoto),

    CONSTRAINT galleria_contenitrice_fk FOREIGN KEY (IDGalleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT foto_contenuta_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.COMPONE(
    IDVideo galleria.id_object_dt  NOT NULL,
    IDFoto galleria.id_object_dt  NOT NULL,

    CONSTRAINT compone_pk PRIMARY KEY (IDVideo, IDFoto),

    CONSTRAINT video_fk FOREIGN KEY (IDVideo) REFERENCES galleria.VIDEO(IDVideo)
    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT foto_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto)
    ON UPDATE CASCADE ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS galleria.MOSTRA(
    NomeSoggetto galleria.string NOT NULL,
	IDFoto galleria.id_object_dt NOT NULL,

	CONSTRAINT mostra_pk PRIMARY KEY (NomeSoggetto, IDFoto),

    CONSTRAINT nome_soggetto_fk FOREIGN KEY (NomeSoggetto) REFERENCES galleria.SOGGETTO(NomeSoggetto)ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT foto_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto) ON UPDATE CASCADE ON DELETE NO ACTION
);
