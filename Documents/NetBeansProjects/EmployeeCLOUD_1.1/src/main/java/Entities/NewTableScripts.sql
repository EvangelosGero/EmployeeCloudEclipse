CREATE TABLE APP.SYSTEMTABLE (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), about_us VARCHAR(200), add1 VARCHAR(100), add3 VARCHAR(100), bottom_message VARCHAR(200), company_name VARCHAR(100), contact_us VARCHAR(200), faq VARCHAR(2000), CONSTRAINT primary_key PRIMARY KEY (id));
ALTER TABLE EMPL_ADMINS ALTER COLUMN USERNAME SET DATA TYPE VARCHAR (60);