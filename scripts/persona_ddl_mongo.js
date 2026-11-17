// [Integrante A] DDL MongoDB: usuario persona_db y colecciones (1_ddl.js en Docker).
// BUG ORIGINAL: usaba la base "prueba_db" y `use`, que mongosh no acepta en scripts.
db = db.getSiblingDB("admin")

db.createUser({
  user: "persona_db",
  pwd: "persona_db",
  roles: [
    { role: "read", db: "persona_db" },
    { role: "readWrite", db: "persona_db" },
    { role: "dbAdmin", db: "persona_db" }
  ],
  mechanisms: ["SCRAM-SHA-1","SCRAM-SHA-256"]
})

// Colecciones de la base persona_db (mismo modelo que las tablas de MariaDB)
db = db.getSiblingDB("persona_db")
db.createCollection("persona")
db.createCollection("profesion")
db.createCollection("telefono")
db.createCollection("estudios")
