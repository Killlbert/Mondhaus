
USE mondhaus;

DROP PROCEDURE IF EXISTS DropUserIfExists;
DELIMITER $$
CREATE PROCEDURE DropUserIfExists(
  userName VARCHAR(100),
  hostName VARCHAR(100)
)
BEGIN
  DECLARE foo BIGINT DEFAULT 0 ;
  SELECT COUNT(*)
  INTO foo
    FROM mysql.user
      WHERE User = userName AND Host = hostName;
   IF foo > 0 THEN
         SET @A = (SELECT Result FROM (SELECT GROUP_CONCAT("DROP USER"," ",userName,"@",hostName) AS Result) AS Q LIMIT 1);
         PREPARE STMT FROM @A;
         EXECUTE STMT;
         FLUSH PRIVILEGES;
   END IF;
END ;$$
DELIMITER ;

CALL mondhaus.DropUserIfExists('mondhausadmin','localhost');
-- DROP PROCEDURE IF EXISTS databaseName.DropUserIfExists;

CREATE USER 'mondhausadmin'@'localhost' IDENTIFIED BY 'mond€pass';
GRANT ALL PRIVILEGES  ON mondhaus.* TO 'mondhausadmin'@'localhost'
 WITH GRANT OPTION;
-- grants access to metadata of the parameters of a PROCEDURE 
-- by a call from java
GRANT SELECT ON `mysql`.`proc` TO 'mondhausadmin'@'localhost';

SELECT count(*) AS Anzahl FROM mysql.user WHERE User='mondhausadmin';

SELECT * FROM mysql.user;
