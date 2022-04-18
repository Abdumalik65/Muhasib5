USE `technochain`;
DROP function IF EXISTS `tovarDona`;

DELIMITER $$
USE `technochain`$$
CREATE FUNCTION `tovarDona` (tovarId int, hisobId int)
RETURNS double
deterministic
BEGIN
declare countTovar double default 0;
select sum(if(hisob2=hisobId,dona,-dona)) into countTovar from hisobkitob where tovar=tovarId and (hisob1=hisobid or hisob2=hisobid);
RETURN countTovar;
END$$

DELIMITER ;
select tovarDona(326, 9) as hisobot;