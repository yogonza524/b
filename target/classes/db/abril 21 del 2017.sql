--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.11
-- Dumped by pg_dump version 9.4.11
-- Started on 2017-04-21 07:27:33

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1 (class 3079 OID 11855)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2066 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 192 (class 1255 OID 57355)
-- Name: before_insert_contador(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION before_insert_contador() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

DECLARE
rows integer;

BEGIN
	SELECT COUNT(*) INTO rows FROM contadores;

	IF(rows = 0) THEN
		NEW.id = 0;
		RETURN NEW;
	END IF;

	RETURN OLD;
END;
$$;


ALTER FUNCTION public.before_insert_contador() OWNER TO postgres;

--
-- TOC entry 193 (class 1255 OID 57356)
-- Name: before_update_juego(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION before_update_juego() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE

BEGIN 
	NEW.id = 0;
	IF(NEW.creditos IS NULL) THEN
		NEW.creditos = 0;
	END IF;

	IF(NEW.apuesta_total IS NULL) THEN
		NEW.apesta_total = 1;
		NEW.carton1_habilitado = true;
		NEW.carton2_habilitado = true;
		NEW.carton3_habilitado = true;
		NEW.carton4_habilitado = true;
	END IF;

	IF(NEW.denominacion_actual IS NULL) THEN
		NEW.denominacion_actual = 'CINCO_CENTAVOS';
		NEW.denominacion_factor = 20;
	END IF;

	IF(NEW.denominacion_factor IS NULL) THEN
		NEW.denominacion_actual = 'CINCO_CENTAVOS';
		NEW.denominacion_factor = 20;
	END IF;

	IF (NEW.acumulado is NULL) THEN
		NEW.acumulado = 0.0;
	END IF;

	IF(NEW.carton1_habilitado IS NULL) THEN
		NEW.carton1_habilitado = true;
	END IF;

	IF(NEW.carton2_habilitado IS NULL) THEN
		NEW.carton2_habilitado = false;
	END IF;

	IF(NEW.carton3_habilitado IS NULL) THEN
		NEW.carton3_habilitado = false;
	END IF;

	IF(NEW.carton4_habilitado IS NULL) THEN
		NEW.carton4_habilitado = false;
	END IF;

	IF(NEW.denominacion_factor = 10) THEN 
		NEW.denominacion_actual = 'DIEZ_CENTAVOS';
	END IF;

	IF(NEW.denominacion_factor = 20) THEN 
		NEW.denominacion_actual = 'CINCO_CENTAVOS';
	END IF;

	NEW.dinero = NEW.creditos / NEW.denominacion_factor;

	RETURN NEW;
END;
$$;


ALTER FUNCTION public.before_update_juego() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 173 (class 1259 OID 57357)
-- Name: billetero; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE billetero (
    id integer DEFAULT 0 NOT NULL,
    dataset character varying(128),
    firmware integer
);


ALTER TABLE billetero OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 57361)
-- Name: configuracion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE configuracion (
    id integer NOT NULL,
    numero_serie_maquina character varying(64),
    numero_maquina integer,
    encendido boolean,
    idioma character varying(64),
    codigo_pais integer,
    modo_mantenimiento boolean,
    sonido boolean,
    bloqueada boolean
);


ALTER TABLE configuracion OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 57364)
-- Name: contadores; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE contadores (
    id integer NOT NULL,
    "cantidad_de_billetes_de_$1" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$2" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$5" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$10" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$20" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$50" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$100" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$200" integer DEFAULT 0 NOT NULL,
    "cantidad_de_billetes_de_$500" integer DEFAULT 0 NOT NULL,
    cantidad_de_juegos_jugados integer DEFAULT 0 NOT NULL,
    cantidad_de_juegos_con_victorias integer DEFAULT 0 NOT NULL,
    cantidad_de_juegos_sin_victorias integer DEFAULT 0 NOT NULL,
    cantidad_de_juegos_desde_el_encendido integer DEFAULT 0 NOT NULL,
    cantidad_de_juegos_desde_apertura_o_cierre_de_puerta_principal integer DEFAULT 0 NOT NULL,
    cantidad_de_veces_que_se_abrio_la_puerta_principal integer DEFAULT 0 NOT NULL
);


ALTER TABLE contadores OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 57382)
-- Name: denominacion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE denominacion (
    nombre character varying(32) NOT NULL,
    valor double precision
);


ALTER TABLE denominacion OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 57385)
-- Name: estadisticas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE estadisticas (
    id integer NOT NULL,
    apostado integer,
    pagado integer
);


ALTER TABLE estadisticas OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 57388)
-- Name: idiomas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE idiomas (
    numero integer NOT NULL,
    nombre character varying(64)
);


ALTER TABLE idiomas OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 57391)
-- Name: juego; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE juego (
    id integer NOT NULL,
    creditos integer,
    apuesta_total integer,
    apuesta_en_bolas_extra integer,
    carton1_habilitado boolean,
    carton2_habilitado boolean,
    carton3_habilitado boolean,
    carton4_habilitado boolean,
    ganado integer,
    dinero real DEFAULT 0.0,
    denominacion_actual character varying(32),
    denominacion_factor real DEFAULT 0.0,
    comenzo character varying(64),
    termino character varying(64),
    acumulado real DEFAULT 0.0,
    ganado_carton1 integer,
    ganado_carton2 integer,
    ganado_carton3 integer,
    ganado_carton4 integer,
    ganado_en_bonus integer DEFAULT 0,
    bolas_visibles character varying(256),
    bolas_extras character varying(128),
    liberar_bolas_extra boolean DEFAULT false
);


ALTER TABLE juego OWNER TO postgres;

--
-- TOC entry 2052 (class 0 OID 57357)
-- Dependencies: 173
-- Data for Name: billetero; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO billetero VALUES (0, NULL, NULL);


--
-- TOC entry 2053 (class 0 OID 57361)
-- Dependencies: 174
-- Data for Name: configuracion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO configuracion VALUES (0, 'ESSENTIT-X2017-NS', 1, true, 'ENGLISH', 1, false, true, false);


--
-- TOC entry 2054 (class 0 OID 57364)
-- Dependencies: 175
-- Data for Name: contadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO contadores VALUES (0, 0, 2, 6, 4, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0);


--
-- TOC entry 2055 (class 0 OID 57382)
-- Dependencies: 176
-- Data for Name: denominacion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO denominacion VALUES ('UN_CENTAVO', 0.01);
INSERT INTO denominacion VALUES ('CINCO_CENTAVOS', 0.050000000000000003);
INSERT INTO denominacion VALUES ('DIEZ_CENTAVOS', 0.10000000000000001);
INSERT INTO denominacion VALUES ('CINCUENTA_CENTAVOS', 0.5);
INSERT INTO denominacion VALUES ('UN_PESO', 1);
INSERT INTO denominacion VALUES ('VEINTICINCO_CENTAVOS', 0.25);


--
-- TOC entry 2056 (class 0 OID 57385)
-- Dependencies: 177
-- Data for Name: estadisticas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2057 (class 0 OID 57388)
-- Dependencies: 178
-- Data for Name: idiomas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO idiomas VALUES (0, 'SPANISH');
INSERT INTO idiomas VALUES (1, 'ENGLISH');
INSERT INTO idiomas VALUES (2, 'PORTUGUESE');


--
-- TOC entry 2058 (class 0 OID 57391)
-- Dependencies: 179
-- Data for Name: juego; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO juego VALUES (0, 2168, 4, 0, true, true, true, true, 50, 108.400002, 'CINCO_CENTAVOS', 20, '2017-04-20 09:39:43.276', '2017-04-20 09:39:43.417', 0, 0, 0, 50, 0, 0, '{4,6,56,17,16,8,53,48,21,24,9,22,15,26,46,29,7,25,36,52,32,47,11,14,37,35,38,40,31,60}', '{18,33,20,51,5,59,45,57,3,23}', true);


--
-- TOC entry 1928 (class 2606 OID 57403)
-- Name: billetero_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY billetero
    ADD CONSTRAINT billetero_pk PRIMARY KEY (id);


--
-- TOC entry 1930 (class 2606 OID 57405)
-- Name: configuracion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configuracion
    ADD CONSTRAINT configuracion_pk PRIMARY KEY (id);


--
-- TOC entry 1932 (class 2606 OID 57407)
-- Name: contadores_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contadores
    ADD CONSTRAINT contadores_pk PRIMARY KEY (id);


--
-- TOC entry 1934 (class 2606 OID 57409)
-- Name: denominacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY denominacion
    ADD CONSTRAINT denominacion_pk PRIMARY KEY (nombre);


--
-- TOC entry 1936 (class 2606 OID 57411)
-- Name: estadisticas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estadisticas
    ADD CONSTRAINT estadisticas_pk PRIMARY KEY (id);


--
-- TOC entry 1938 (class 2606 OID 57413)
-- Name: idiomas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY idiomas
    ADD CONSTRAINT idiomas_pk PRIMARY KEY (numero);


--
-- TOC entry 1940 (class 2606 OID 57415)
-- Name: juego_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY juego
    ADD CONSTRAINT juego_pk PRIMARY KEY (id);


--
-- TOC entry 1942 (class 2620 OID 57416)
-- Name: before_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER before_update BEFORE UPDATE ON juego FOR EACH ROW EXECUTE PROCEDURE before_update_juego();


--
-- TOC entry 1941 (class 2620 OID 57417)
-- Name: contadores_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER contadores_trigger BEFORE INSERT ON contadores FOR EACH ROW EXECUTE PROCEDURE before_insert_contador();


--
-- TOC entry 2065 (class 0 OID 0)
-- Dependencies: 7
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-04-21 07:27:35

--
-- PostgreSQL database dump complete
--

