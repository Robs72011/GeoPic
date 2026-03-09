BEGIN;

-- 1. UTENTI (IDUtente: CHAR(5))
INSERT INTO galleria.UTENTE (IDUtente, Username, Password, IsAdmin, IsSoggetto) VALUES
('00001', 'admin_user', 'pass1', TRUE, FALSE),
('00002', 'luca_b', 'pass2', FALSE, FALSE),
('00003', 'maria_v', 'pass3', FALSE, TRUE);

-- 2. LUOGHI (Coordinate: CHAR(14))
INSERT INTO galleria.LUOGO (Coordinate, Toponimo) VALUES
('+41.90,+012.45', 'Città del Vaticano'),
('+45.43,+012.33', 'Piazza San Marco');

-- 3. GALLERIE (Personali obbligatorie + una Condivisa)
-- IDGalleria: CHAR(10)
INSERT INTO galleria.GALLERIA (IDGalleria, NomeGalleria, Condivisa, Proprietario) VALUES
('0000000001', 'Privata Admin', FALSE, '00001'), --
('0000000002', 'Privata Luca', FALSE, '00002'),  --
('0000000003', 'Privata Maria', FALSE, '00003'), --
('0000000004', 'Viaggio di Gruppo', TRUE, '00001'); -- Proprietario: 00001

-- 4. FOTOGRAFIE
INSERT INTO galleria.FOTOGRAFIA (IDFoto, Dispositivo, Autore, Coordinate, Visibilita, DataScatto) VALUES
('0000000001', 'iPhone 15', '00001', '+41.90,+012.45', TRUE, '2024-01-01'), -- Autore Admin
('0000000002', 'Samsung S23', '00002', '+45.43,+012.33', TRUE, '2024-01-02'), -- Autore Luca
('0000000003', 'Nikon Z6', '00003', NULL, TRUE, '2024-01-03');           -- Autore Maria

-- 5. VIDEO (Solo in galleria personale dell'utente)
INSERT INTO galleria.VIDEO (IDVideo, TitoloVideo, Descrizione, Galleria) VALUES
('0000000001', 'Ricordi Vaticano', 'Clip privata', '0000000001'); -- Legato a galleria privata

-- 6. PARTECIPA (Solo i partecipanti, NON il proprietario)
-- La galleria 04 è di Admin (00001), quindi inseriamo solo Luca e Maria.
INSERT INTO galleria.PARTECIPA (IDGalleria, IDUtente) VALUES
('0000000004', '00002'), -- Partecipante
('0000000004', '00003'); -- Partecipante

-- 7. CONTIENE (Associazione Foto -> Galleria)
-- Ogni foto nella condivisa (04) è presente anche nella privata del rispettivo autore.
INSERT INTO galleria.CONTIENE (IDGalleria, IDFoto) VALUES
('0000000001', '0000000001'), -- Privata Admin (Autore)
('0000000004', '0000000001'), -- Condivisa
('0000000002', '0000000002'), -- Privata Luca (Autore)
('0000000004', '0000000002'), -- Condivisa
('0000000003', '0000000003'); -- Solo Privata Maria (Autore)

-- 8. COMPONE (Foto -> Video)
INSERT INTO galleria.COMPONE (IDVideo, IDFoto) VALUES
('0000000001', '0000000001'); --

-- 9. SOGGETTI (Utenti e Paesaggi)
INSERT INTO galleria.SOGGETTO (NomeSoggetto, Categoria, IDUtente) VALUES
('Maria Verdi', 'Persona', '00003'),      -- Soggetto = Utente
('Tramonto Veneziano', 'Paesaggio', NULL); -- Soggetto != Utente

-- 10. MOSTRA (Soggetto -> Foto)
INSERT INTO galleria.MOSTRA (NomeSoggetto, IDFoto) VALUES
('Maria Verdi', '0000000003'),
('Tramonto Veneziano', '0000000002'); --

COMMIT;
