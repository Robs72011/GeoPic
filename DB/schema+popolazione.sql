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
    IDUtente SERIAL NOT NULL,
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
    IDGalleria SERIAL NOT NULL,
    NomeGalleria galleria.string  NOT NULL,
    Condivisa BOOLEAN NOT NULL DEFAULT FALSE,
	Proprietario INT NOT NULL,

    CONSTRAINT galleria_pk PRIMARY KEY (IDGalleria),

    CONSTRAINT proprietario_fk FOREIGN KEY (Proprietario) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.FOTOGRAFIA(
    IDFoto SERIAL NOT NULL,
    Dispositivo galleria.string,
    DataScatto DATE NOT NULL,
    DataEliminazione DATE,
    Visibilita BOOLEAN NOT NULL DEFAULT TRUE,
    Autore INT, -- Tolto NOT NULL per gestire la regola dell'eccezione
    Coordinate galleria.coo_dt,

    CONSTRAINT foto_pk PRIMARY KEY (IDFoto),
    
    CONSTRAINT autore_fk FOREIGN KEY (Autore) 
        REFERENCES galleria.UTENTE(IDUtente) 
        ON UPDATE CASCADE ON DELETE NO ACTION, 
        
    CONSTRAINT luogo_fk FOREIGN KEY (Coordinate) 
        REFERENCES galleria.LUOGO(Coordinate) 
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS galleria.VIDEO(
    IDVideo SERIAL NOT NULL,
    TitoloVideo galleria.string NOT NULL,
    Descrizione TEXT,

    Galleria INT NOT NULL,

    CONSTRAINT video_pk PRIMARY KEY (IDVideo),

    CONSTRAINT galleria_fk FOREIGN KEY (Galleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.SOGGETTO(
    NomeSoggetto galleria.string NOT NULL,
    Categoria galleria.string NOT NULL,

	IDUtente INT UNIQUE DEFAULT NULL,

    CONSTRAINT soggetto_pk PRIMARY KEY (NomeSoggetto),

    CONSTRAINT idutente_fk FOREIGN KEY (IDUtente) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.PARTECIPA(
    IDGalleria INT NOT NULL,
    IDUtente INT NOT NULL,

    CONSTRAINT partecipa_pk PRIMARY KEY (IDGalleria, IDUtente),

    CONSTRAINT galleria_fk FOREIGN KEY (IDGalleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT utente_partecipante_fk FOREIGN KEY (IDUtente) REFERENCES galleria.UTENTE(IDUtente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.CONTIENE(
    IDGalleria INT NOT NULL,
    IDFoto INT NOT NULL,

    CONSTRAINT contiene_pk PRIMARY KEY (IDGalleria, IDFoto),

    CONSTRAINT galleria_contenitrice_fk FOREIGN KEY (IDGalleria) REFERENCES galleria.GALLERIA(IDGalleria) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT foto_contenuta_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleria.COMPONE(
    IDVideo INT NOT NULL,
    IDFoto INT NOT NULL,

    CONSTRAINT compone_pk PRIMARY KEY (IDVideo, IDFoto),

    CONSTRAINT video_fk FOREIGN KEY (IDVideo) REFERENCES galleria.VIDEO(IDVideo)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT foto_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto)
    ON UPDATE CASCADE ON DELETE CASCADE 
);

CREATE TABLE IF NOT EXISTS galleria.MOSTRA(
    NomeSoggetto galleria.string NOT NULL,
	IDFoto INT NOT NULL,

	CONSTRAINT mostra_pk PRIMARY KEY (NomeSoggetto, IDFoto),

    CONSTRAINT nome_soggetto_fk FOREIGN KEY (NomeSoggetto) REFERENCES galleria.SOGGETTO(NomeSoggetto)ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT foto_fk FOREIGN KEY (IDFoto) REFERENCES galleria.FOTOGRAFIA(IDFoto) ON UPDATE CASCADE ON DELETE CASCADE 
);

BEGIN;

-- 1. UTENTI (IDUtente: CHAR(5))
INSERT INTO galleria.UTENTE (Username, Password, IsAdmin, IsSoggetto) VALUES
('admin', 'pass1', TRUE, FALSE),
('luca_b', 'pass2', FALSE, FALSE),
('maria_v', 'pass3', FALSE, TRUE),
('paolo', 'paolo', false, false),
('vale_p', 'kiki', false, false),
('luca_p', 'ai', false, false),
('gigio', 'tasso', false, true),
('gabbo', 'dsq', false, false),
('manuela', 'rossa', false, true);

-- 2. LUOGHI (Coordinate: CHAR(14))
INSERT INTO galleria.LUOGO (Coordinate, Toponimo) VALUES
('+41.90,+012.45', 'Città del Vaticano'),
('+45.43,+012.33', 'Piazza San Marco'),
('+40.85,+014.26', 'Piazza del Plebiscito'),
('+43.76,+011.25', 'Ponte Vecchio'),
('+41.89,+012.49', 'Colosseo');

-- 3. GALLERIE (Personali obbligatorie + Condivise)
-- IDGalleria: CHAR(10)
INSERT INTO galleria.GALLERIA (NomeGalleria, Condivisa, Proprietario) VALUES
('Privata Admin', FALSE, '00001'),
('Privata Luca', FALSE, '00002'),
('Privata Maria', FALSE, '00003'),
('Viaggio di Gruppo Roma', TRUE, '00001'),
('Gita a Napoli', TRUE, '00002'), -- Proprietario: 00002
('Galleria privata di Paolo', false, 4),
('Galleria privata di vale_p', false, 5),
('Galleria privata di luca_p', false, 6),
('Galleria privata di Gigio', false, 7),
('Galleria privata di Gabbo', false, 8),
('Galleria privata di M	nuela', false, 9);

-- 4. FOTOGRAFIE
INSERT INTO galleria.FOTOGRAFIA (Dispositivo, Autore, Coordinate, Visibilita, DataScatto) VALUES
('iPhone 15', '00001', '+41.90,+012.45', TRUE, '2024-01-01'), -- Autore Admin
('Samsung S23', '00002', '+45.43,+012.33', TRUE, '2024-01-02'), -- Autore Luca
('Nikon Z6', '00003', NULL, TRUE, '2024-01-03'),               -- Autore Maria
('iPhone 15', '00001', '+41.89,+012.49', TRUE, '2024-02-15'), -- Autore Admin, Colosseo
('GoPro Hero 11', '00002', '+40.85,+014.26', TRUE, '2024-03-10'), -- Autore Luca, Napoli
('Nikon Z6', '00003', '+43.76,+011.25', TRUE, '2024-04-20'), -- Autore Maria, Firenze
('Canon EOS', '00001', '+40.85,+014.26', TRUE, '2024-03-11'); -- Autore Admin, Napoli

-- 5. VIDEO (Solo in galleria personale dell'utente)
INSERT INTO galleria.VIDEO (TitoloVideo, Descrizione, Galleria) VALUES
('Ricordi Vaticano', 'Clip privata admin al vaticano', '0000000001'),
('Volo su Napoli', 'Riprese col drone di Luca su Napoli', '0000000002'),
('Timelaps Firenze', 'Tramonto meraviglioso a Firenze Maria', '0000000003'),
('Viaggio a Roma Highlights', 'I migliori momenti a Roma di Admin', '0000000001');

-- 6. PARTECIPA (Solo i partecipanti, NON il proprietario)
INSERT INTO galleria.PARTECIPA (IDGalleria, IDUtente) VALUES
('0000000004', '00002'), -- Luca partecipa alla galleria di Admin
('0000000004', '00003'), -- Maria partecipa alla galleria di Admin
('0000000005', '00001'), -- Admin partecipa alla gita di Luca
('0000000005', '00003'); -- Maria partecipa alla gita di Luca

-- 7. CONTIENE (Associazione Foto -> Galleria)
INSERT INTO galleria.CONTIENE (IDGalleria, IDFoto) VALUES
('0000000001', '0000000001'), -- Privata Admin
('0000000004', '0000000001'), -- Condivisa Gruppo Roma
('0000000002', '0000000002'), -- Privata Luca
('0000000005', '0000000002'), -- Condivisaita Napoli (Luca la mette qui anche se non è di napoli per errore!)
('0000000003', '0000000003'), -- Solo Privata Maria
('0000000001', '0000000004'), -- Privata Admin
('0000000004', '0000000004'), -- Condivisa Gruppo Roma
('0000000002', '0000000005'), -- Privata Luca
('0000000005', '0000000005'), -- Condivisa Napoli
('0000000003', '0000000006'), -- Privata Maria
('0000000001', '0000000007'), -- Privata Admin
('0000000005', '0000000007'); -- Admin aggiunge la sua foto di napoli alla galleria condivisa di Luca

-- 8. COMPONE (Foto -> Video) - i Video sono diapositive/slideshow composti dalle foto
INSERT INTO galleria.COMPONE (IDVideo, IDFoto) VALUES
('0000000001', '0000000001'), -- Video Vaticano di Admin ha foto vaticano
('0000000002', '0000000005'), -- Video Napoli di Luca ha foto gopro napoli
('0000000003', '0000000006'), -- Video firenze di Maria ha foto firenze
('0000000004', '0000000001'), -- Video Roma Admin ha foto Vaticano
('0000000004', '0000000004'); -- Video Roma Admin ha foto Colosseo

-- 9. SOGGETTI (Utenti e Paesaggi)
INSERT INTO galleria.SOGGETTO (NomeSoggetto, Categoria, IDUtente) VALUES
('maria_v', 'Utente', '00003'),
('Tramonto Veneziano', 'Paesaggio', NULL),
('Vesuvio', 'Paesaggio', NULL),
('Cupolone', 'Monumento', NULL);

-- 10. MOSTRA (Soggetto -> Foto)
INSERT INTO galleria.MOSTRA (NomeSoggetto, IDFoto) VALUES
('maria_v', '0000000003'),
('Tramonto Veneziano', '0000000002'),
('Cupolone', '0000000001'),
('Vesuvio', '0000000005'),
('maria_v', '0000000006'); -- Maria si è fatta un selfie a Firenze

COMMIT;
