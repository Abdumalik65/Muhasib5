USE `technochain`;
DROP function IF EXISTS `Narh`;

DELIMITER $$
USE `technochain`$$
CREATE FUNCTION `Narh` (tovarId int, hisobId int)
RETURNS double
deterministic
BEGIN
declare narhDouble double default 0;
select sum(if(hisob2=hisobId,dona*narh/kurs,-1*dona*narh/kurs)) into narhDouble from hisobkitob where tovar=tovarId and (hisob1=hisobid or hisob2=hisobid);
RETURN narhDouble;
END$$

DELIMITER ;
select Narh(326, 9) as hisobot;