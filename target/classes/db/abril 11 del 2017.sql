--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.11
-- Dumped by pg_dump version 9.4.11
-- Started on 2017-04-11 07:10:43

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
-- TOC entry 2038 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 191 (class 1255 OID 32768)
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
-- TOC entry 173 (class 1259 OID 16394)
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
-- TOC entry 174 (class 1259 OID 16397)
-- Name: contadores; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE contadores (
    id integer NOT NULL,
    "cantidad_de_billetes_de_$1" integer,
    "cantidad_de_billetes_de_$2" integer,
    "cantidad_de_billetes_de_$5" integer,
    "cantidad_de_billetes_de_$10" integer,
    "cantidad_de_billetes_de_$20" integer,
    "cantidad_de_billetes_de_$50" integer,
    "cantidad_de_billetes_de_$100" integer,
    "cantidad_de_billetes_de_$200" integer,
    "cantidad_de_billetes_de_$500" integer,
    cantidad_de_juegos_jugados integer,
    cantidad_de_juegos_con_victorias integer,
    cantidad_de_juegos_sin_victorias integer,
    cantidad_de_juegos_desde_el_encendido integer,
    cantidad_de_juegos_desde_apertura_o_cierre_de_puerta_principal integer
);


ALTER TABLE contadores OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 16400)
-- Name: denominacion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE denominacion (
    nombre character varying(32) NOT NULL,
    valor double precision
);


ALTER TABLE denominacion OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16403)
-- Name: estadisticas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE estadisticas (
    id integer NOT NULL,
    apostado integer,
    pagado integer
);


ALTER TABLE estadisticas OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 16406)
-- Name: idiomas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE idiomas (
    numero integer NOT NULL,
    nombre character varying(64)
);


ALTER TABLE idiomas OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16409)
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
    ganado_carton4 integer
);


ALTER TABLE juego OWNER TO postgres;

--
-- TOC entry 2025 (class 0 OID 16394)
-- Dependencies: 173
-- Data for Name: configuracion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO configuracion VALUES (0, 'ESSENTIT-X2017-NS', 1, true, 'ENGLISH', 1, false, false, false);


--
-- TOC entry 2026 (class 0 OID 16397)
-- Dependencies: 174
-- Data for Name: contadores; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2027 (class 0 OID 16400)
-- Dependencies: 175
-- Data for Name: denominacion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO denominacion VALUES ('UN_CENTAVO', 0.01);
INSERT INTO denominacion VALUES ('CINCO_CENTAVOS', 0.050000000000000003);
INSERT INTO denominacion VALUES ('DIEZ_CENTAVOS', 0.10000000000000001);
INSERT INTO denominacion VALUES ('CINCUENTA_CENTAVOS', 0.5);
INSERT INTO denominacion VALUES ('UN_PESO', 1);
INSERT INTO denominacion VALUES ('VEINTICINCO_CENTAVOS', 0.25);


--
-- TOC entry 2028 (class 0 OID 16403)
-- Dependencies: 176
-- Data for Name: estadisticas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2029 (class 0 OID 16406)
-- Dependencies: 177
-- Data for Name: idiomas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO idiomas VALUES (0, 'SPANISH');
INSERT INTO idiomas VALUES (1, 'ENGLISH');
INSERT INTO idiomas VALUES (2, 'PORTUGUESE');


--
-- TOC entry 2030 (class 0 OID 16409)
-- Dependencies: 178
-- Data for Name: juego; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO juego VALUES (0, 300, 24, 0, true, true, true, true, 0, 30, 'DIEZ_CENTAVOS', 10, '2017-04-10 13:43:42.174', '2017-04-10 13:43:42.364', 0, NULL, NULL, NULL, NULL);


--
-- TOC entry 1904 (class 2606 OID 16413)
-- Name: configuracion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configuracion
    ADD CONSTRAINT configuracion_pk PRIMARY KEY (id);


--
-- TOC entry 1906 (class 2606 OID 16415)
-- Name: contadores_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contadores
    ADD CONSTRAINT contadores_pk PRIMARY KEY (id);


--
-- TOC entry 1908 (class 2606 OID 16417)
-- Name: denominacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY denominacion
    ADD CONSTRAINT denominacion_pk PRIMARY KEY (nombre);


--
-- TOC entry 1910 (class 2606 OID 16419)
-- Name: estadisticas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estadisticas
    ADD CONSTRAINT estadisticas_pk PRIMARY KEY (id);


--
-- TOC entry 1912 (class 2606 OID 16421)
-- Name: idiomas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY idiomas
    ADD CONSTRAINT idiomas_pk PRIMARY KEY (numero);


--
-- TOC entry 1914 (class 2606 OID 16423)
-- Name: juego_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY juego
    ADD CONSTRAINT juego_pk PRIMARY KEY (id);


--
-- TOC entry 1915 (class 2620 OID 32769)
-- Name: before_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER before_update BEFORE UPDATE ON juego FOR EACH ROW EXECUTE PROCEDURE before_update_juego();


--
-- TOC entry 2037 (class 0 OID 0)
-- Dependencies: 7
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-04-11 07:10:43

--
-- PostgreSQL database dump complete
--

