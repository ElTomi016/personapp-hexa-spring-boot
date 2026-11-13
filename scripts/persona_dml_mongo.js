db = db.getSiblingDB("persona_db")

db.persona.insertMany([
	{
		"_id": NumberInt(123456789),
		"nombre": "Pepe",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(987654321),
		"nombre": "Pepito",
		"apellido": "Perez",
		"genero": "M",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(321654987),
		"nombre": "Pepa",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(147258369),
		"nombre": "Pepita",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(10),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(963852741),
		"nombre": "Fede",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(18),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	}
], { ordered: false })

db.profesion.insertMany([
	{
		"_id": NumberInt(1),
		"nom": "Ingeniero de Sistemas",
		"des": "Diseno y construccion de software",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(2),
		"nom": "Medico",
		"des": "Atencion y diagnostico de pacientes",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(3),
		"nom": "Abogado",
		"des": "Asesoria y defensa juridica",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(4),
		"nom": "Arquitecto",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	}
], {ordered:false})

db.telefono.insertMany([
	{ "_id": "3001112233", "oper": "Claro", "primaryDuenio": NumberInt(123456789), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3109998877", "oper": "Movistar", "primaryDuenio": NumberInt(123456789), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3205554433", "oper": "Tigo", "primaryDuenio": NumberInt(321654987), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3157776655", "oper": "Claro", "primaryDuenio": NumberInt(147258369), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3012223344", "oper": "WOM", "primaryDuenio": NumberInt(963852741), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" }
], {ordered:false})

db.estudios.insertMany([
	{
		"_id": "123456789-1",
		"primaryPersona": NumberInt(123456789),
		"primaryProfesion": NumberInt(1),
		"fecha": new Date("2015-06-15T12:00:00Z"),
		"univer": "Pontificia Universidad Javeriana",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "123456789-2",
		"primaryPersona": NumberInt(123456789),
		"primaryProfesion": NumberInt(2),
		"fecha": new Date("2019-12-10T12:00:00Z"),
		"univer": "Universidad Nacional",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "321654987-3",
		"primaryPersona": NumberInt(321654987),
		"primaryProfesion": NumberInt(3),
		"fecha": new Date("2018-11-30T12:00:00Z"),
		"univer": "Universidad de los Andes",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "963852741-1",
		"primaryPersona": NumberInt(963852741),
		"primaryProfesion": NumberInt(1),
		"univer": "Pontificia Universidad Javeriana",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	}
], {ordered:false})
