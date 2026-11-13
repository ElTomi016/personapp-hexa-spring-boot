INSERT INTO 
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`) 
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);
--
INSERT INTO
	`persona_db`.`profesion`(`id`,`nom`,`des`)
VALUES
	(1,'Ingeniero de Sistemas','Diseno y construccion de software'),
	(2,'Medico','Atencion y diagnostico de pacientes'),
	(3,'Abogado','Asesoria y defensa juridica'),
	(4,'Arquitecto',null);
--
INSERT INTO
	`persona_db`.`telefono`(`num`,`oper`,`duenio`)
VALUES
	('3001112233','Claro',123456789),
	('3109998877','Movistar',123456789),
	('3205554433','Tigo',321654987),
	('3157776655','Claro',147258369),
	('3012223344','WOM',963852741);
--
INSERT INTO
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`)
VALUES
	(1,123456789,'2015-06-15','Pontificia Universidad Javeriana'),
	(2,123456789,'2019-12-10','Universidad Nacional'),
	(3,321654987,'2018-11-30','Universidad de los Andes'),
	(1,963852741,null,'Pontificia Universidad Javeriana');
COMMIT;
