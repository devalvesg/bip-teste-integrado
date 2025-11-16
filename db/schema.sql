DROP TABLE IF EXISTS beneficios CASCADE;

CREATE SEQUENCE IF NOT EXISTS beneficios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE beneficios (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            balance NUMERIC(19, 2) NOT NULL,
                            version BIGINT DEFAULT 0,
                            CONSTRAINT beneficios_name_not_empty CHECK (CHAR_LENGTH(TRIM(name)) > 0),
                            CONSTRAINT beneficios_balance_positive CHECK (balance >= 0)
);

CREATE INDEX idx_beneficios_name ON beneficios(name);
CREATE INDEX idx_beneficios_balance ON beneficios(balance);

COMMENT ON TABLE beneficios IS 'Table storing beneficiary information with balance tracking';
COMMENT ON COLUMN beneficios.id IS 'Primary key - auto-generated';
COMMENT ON COLUMN beneficios.name IS 'Beneficiary name - required';
COMMENT ON COLUMN beneficios.balance IS 'Current balance with 2 decimal precision';
COMMENT ON COLUMN beneficios.version IS 'Optimistic locking version control';
