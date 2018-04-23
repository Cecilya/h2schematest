CREATE TABLE modela (
  id bigserial,
  field varchar,

  CONSTRAINT PK_modela PRIMARY KEY (id)
);

CREATE TABLE b.modelb (
  id bigserial,
  attribute varchar,

  CONSTRAINT PK_modelb PRIMARY KEY (id)
);