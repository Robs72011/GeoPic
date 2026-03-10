BEGIN;

-- 1. UTENTI (IDUtente: CHAR(5))
INSERT INTO galleria.UTENTE (IDUtente, Username, Password, IsAdmin, IsSoggetto) VALUES
('00001', 'admin_user', 'pass1', TRUE, FALSE),
('00002', 'luca_b', 'pass2', FALSE, FALSE),
('00003', 'maria_v', 'pass3', FALSE, TRUE);

-- 2. LUOGHI (Coordinate: CHAR(14))
INSERT INTO galleria.LUOGO (Coordinate, Toponimo) VALUES
('+41.90,+012.45', 'Città del Vaticano'),
('+45.43,+012.33', 'Piazza San Marco'),
('+40.85,+014.26', 'Piazza del Plebiscito'),
('+43.76,+011.25', 'Ponte Vecchio'),
('+41.89,+012.49', 'Colosseo');

-- 3. GALLERIE (Personali obbligatorie + Condivise)
-- IDGalleria: CHAR(10)
INSERT INTO galleria.GALLERIA (IDGalleria, NomeGalleria, Condivisa, Proprietario) VALUES
('0000000001', 'Privata Admin', FALSE, '00001'),
('0000000002', 'Privata Luca', FALSE, '00002'),
('0000000003', 'Privata Maria', FALSE, '00003'),
('0000000004', 'Viaggio di Gruppo Roma', TRUE, '00001'),
('0000000005', 'Gita a Napoli', TRUE, '00002'); -- Proprietario: 00002

-- 4. FOTOGRAFIE
INSERT INTO galleria.FOTOGRAFIA (IDFoto, Dispositivo, Autore, Coordinate, Visibilita, DataScatto) VALUES
('0000000001', 'iPhone 15', '00001', '+41.90,+012.45', TRUE, '2024-01-01'), -- Autore Admin
('0000000002', 'Samsung S23', '00002', '+45.43,+012.33', TRUE, '2024-01-02'), -- Autore Luca
('0000000003', 'Nikon Z6', '00003', NULL, TRUE, '2024-01-03'),               -- Autore Maria
('0000000004', 'iPhone 15', '00001', '+41.89,+012.49', TRUE, '2024-02-15'), -- Autore Admin, Colosseo
('0000000005', 'GoPro Hero 11', '00002', '+40.85,+014.26', TRUE, '2024-03-10'), -- Autore Luca, Napoli
('0000000006', 'Nikon Z6', '00003', '+43.76,+011.25', TRUE, '2024-04-20'), -- Autore Maria, Firenze
('0000000007', 'Canon EOS', '00001', '+40.85,+014.26', TRUE, '2024-03-11'); -- Autore Admin, Napoli

-- 5. VIDEO (Solo in galleria personale dell'utente)
INSERT INTO galleria.VIDEO (IDVideo, TitoloVideo, Descrizione, Galleria) VALUES
('0000000001', 'Ricordi Vaticano', 'Clip privata admin al vaticano', '0000000001'),
('0000000002', 'Volo su Napoli', 'Riprese col drone di Luca su Napoli', '0000000002'),
('0000000003', 'Timelaps Firenze', 'Tramonto meraviglioso a Firenze Maria', '0000000003'),
('0000000004', 'Viaggio a Roma Highlights', 'I migliori momenti a Roma di Admin', '0000000001');

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
('Maria Verdi', 'Persona', '00003'),
('Tramonto Veneziano', 'Paesaggio', NULL),
('Vesuvio', 'Paesaggio', NULL),
('Cupolone', 'Monumento', NULL);

-- 10. MOSTRA (Soggetto -> Foto)
INSERT INTO galleria.MOSTRA (NomeSoggetto, IDFoto) VALUES
('Maria Verdi', '0000000003'),
('Tramonto Veneziano', '0000000002'),
('Cupolone', '0000000001'),
('Vesuvio', '0000000005'),
('Maria Verdi', '0000000006'); -- Maria si è fatta un selfie a Firenze

COMMIT;
