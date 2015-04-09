-- *************************
-- Unit-Tests mondhaus Da - Script
-- *************************



-- ---------------------
-- Anbieter
-- ---------------------
call mondhaus.an_f_addAnbieter (
  999999, -- p_pnr INT,
  'Nachname', -- p_nachname VARCHAR(20),
  'Vorname', -- p_vorname VARCHAR(15),
  999999, -- p_a_anr INT,
  1 -- p_geschlecht TINYINT(1)
  );

call mondhaus.an_f_setAnbieterAt (
  999999, -- p_pnr INT,
  'NachnameUpdate', -- p_nachname VARCHAR(20),
  'VornameUpdate', -- p_vorname VARCHAR(15),
  999999, -- p_a_anr INT,
  2 -- p_geschlecht TINYINT(1)
  );

-- Prüfen ob der Datensatz in der View sichtbar ist:
SELECT * 
  FROM p_personen INNER JOIN an_anbieter 
    ON p_personen.p_pnr = an_anbieter.an_p_pnr;

call g_f_addGenre (
  'kom', -- g_gid CHAR(3),
  'Kommödie' -- g_bezeichnung VARCHAR(20)
);

call g_f_setGenreAt (
  'kom', -- g_gid CHAR(3),
  'KommödieUpdate' -- g_bezeichnung VARCHAR(20)
);

SELECT * FROM mondhaus.g_genre;


call mondhaus.s_f_addSaele (
  -- IN
  999999, -- s_snr INT,
  'TestSaal', -- s_name VARCHAR(20),
  999999, -- s_an_annr INT,
  999999 -- s_an_p_pnr INT
);

call mondhaus.s_f_setSaeleAt (
  -- IN
  999999, -- s_snr INT,
  'TestSaalUpdate', -- s_name VARCHAR(20),
  999999, -- s_an_annr INT,
  999999 -- s_an_p_pnr INT
);

SELECT * FROM mondhaus.s_saele;

call mondhaus.ka_f_addKategorien(
  '99999', -- `ka_kaid` CHAR(5) NOT NULL,
  'Kategorien', -- `ka_bezeichnung` VARCHAR(10) NULL,
  '999' -- `ka_preis` DECIMAL NULL,
);

call mondhaus.ka_f_setKategorienAt(
  '99999', -- `ka_kaid` CHAR(5) NOT NULL,
  'KatUpdate', -- `ka_bezeichnung` VARCHAR(10) NULL,
  '999' -- `ka_preis` DECIMAL NULL,
);

SELECT * FROM mondhaus.ka_kategorien;

call mondhaus.sit_f_addSitzplaetze(
  9999, -- sit_reihe INT,
  9999, -- sit_sitnr INT,
  999999, -- sit_s_snr INT,
  '99999' -- sit_ka_kaid CHAR(5)
);

call mondhaus.sit_f_setSitzplaetzeAt(
  9999, -- sit_reihe INT,
  9999, -- sit_sitnr INT,
  999999, -- sit_s_snr INT,
  '99999' -- sit_ka_kaid CHAR(5)
);

SELECT * FROM mondhaus.sit_sitzplaetze;

call mondhaus.st_f_addStuecke (
  'Titel', -- st_titel VARCHAR(30),
  'Regisseur', -- st_regisseur VARCHAR(15),
  'kom' -- st_g_gid CHAR(3)
);

call mondhaus.st_f_setStueckeAt (
  'Titel', -- st_titel VARCHAR(30),
  'RegisseurUpdate', -- st_regisseur VARCHAR(15),
  'kom' -- st_g_gid CHAR(3)
);

SELECT * FROM mondhaus.st_stuecke;

call mondhaus.v_f_addVorstellungen (
  999999, -- v_vnr INT,
  'Titel', -- v_st_titel VARCHAR(30),
  999999, -- v_s_snr INT,
  999999, -- v_s_an_annr INT,
  '2015-03-29', -- v_datum DATE,
  '20:00', -- v_beginn CHAR(5),
  '22:10' -- v_ende CHAR(5)
);

call mondhaus.v_f_setVorstellungenAt (
  999999, -- v_vnr INT,
  'Titel', -- v_st_titel VARCHAR(30),
  999999, -- v_s_snr INT,
  999999, -- v_s_an_annr INT,
  '2015-03-30', -- v_datum DATE,
  '20:00', -- v_beginn CHAR(5),
  '22:10' -- v_ende CHAR(5)
);

SELECT * FROM mondhaus.v_vorstellungen;

-- ---------------------
-- Kunde add
-- ---------------------


call mondhaus.k_f_addKunden (
  1000000, -- p_pnr INT,
  'NachnameK', -- p_nachname VARCHAR(20),
  'VornameK', -- p_vorname VARCHAR(15),
  1000000, -- p_a_anr INT,
  1, -- p_geschlecht TINYINT(1)
  
  '2015-03-29', -- k_seit DATE,
  1, -- k_status TINYINT(1),
  1000000 -- k_p_pnr INT
);

call mondhaus.k_f_setKundenAt (
  1000000, -- p_pnr INT,
  'NachnameKUpdate', -- p_nachname VARCHAR(20),
  'VornameKUpdate', -- p_vorname VARCHAR(15),
  1000000, -- p_a_anr INT,
  1, -- p_geschlecht TINYINT(1)
  
  '2015-03-30', -- k_seit DATE,
  1, -- k_status TINYINT(1),
  1000000 -- k_p_pnr INT
);

SELECT * FROM mondhaus.k_kunden;

call i_f_addInteressen (
  'kom',  -- i_g_gid CHAR(3),
   1000000 -- i_k_p_pnr INT
);

call i_f_setInteressenAt (
  'kom',  -- i_g_gid CHAR(3),
   1000000 -- i_k_p_pnr INT
);

SELECT * FROM mondhaus.i_interessen;

-- ---------------------
-- Kunde del
-- ---------------------

call i_f_removeInteressen (
  'kom',  -- i_g_gid CHAR(3),
   1000000 -- i_k_p_pnr INT
);

call mondhaus.k_f_removeKunden (
  1000000 -- p_pnr INT,
);

-- ---------------------
-- Anbieter del
-- ---------------------

call mondhaus.v_f_removeVorstellungen (
  999999 -- v_vnr INT,
);

call mondhaus.st_f_removeStuecke (
  'Titel' -- st_titel VARCHAR(30),
);

call mondhaus.sit_f_removeSitzplaetze(
  9999, -- sit_reihe INT,
  9999, -- sit_sitnr INT,
  999999 -- sit_s_snr INT,
);

call mondhaus.ka_f_removeKategorien(
  '99999' -- `ka_kaid` CHAR(5) NOT NULL,
);

call mondhaus.s_f_removeSaele (
  -- IN
  999999 -- s_snr INT,
);

call mondhaus.g_f_removeGenre (
  'kom'
);

call mondhaus.an_f_removeAnbieter (
  999999 -- p_pnr INT,
  );
