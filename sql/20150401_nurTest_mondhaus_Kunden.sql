USE mondhaus;

DROP PROCEDURE IF EXISTS k_f_getItemKunden;
DELIMITER $$
CREATE PROCEDURE k_f_getItemKunden (
  IN
  p_pnr INT
)

BEGIN

  SELECT
	  p_personen.p_pnr,
      p_personen.p_nachname,
	  p_personen.p_vorname,
	  p_personen.p_a_anr,
	  p_personen.p_geschlecht,

      k_kunden.k_seit,
      k_kunden.k_status,
      k_kunden.k_p_pnr
      
    FROM p_personen, k_kunden
    WHERE k_kunden.k_p_pnr=p_pnr AND p_personen.p_pnr=p_pnr
  ;
    
END$$
DELIMITER ;

--  1000002, NachnameK, VornameK, 1000002, 1, 2015-03-29, 1, 1000002 
call mondhaus.k_f_addKunden (
  1000006, -- p_pnr INT,
  'NachnameK', -- p_nachname VARCHAR(20),
  'VornameK', -- p_vorname VARCHAR(15),
  1000006, -- p_a_anr INT,
  1, -- p_geschlecht TINYINT(1)
  
  '2015-03-29', -- k_seit DATE,
  1, -- k_status TINYINT(1),
  1000006 -- k_p_pnr INT
);


CALL mondhaus.k_f_getItemKunden(1000000);

CALL mondhaus.k_f_getItemsKunden();

CALL mondhaus.k_f_removeKunden(1000003);

SELECT * FROM mondhaus.p_personen;