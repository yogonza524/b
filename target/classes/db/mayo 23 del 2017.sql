--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.11
-- Dumped by pg_dump version 9.4.11
-- Started on 2017-05-23 06:37:26

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
-- TOC entry 2084 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 194 (class 1255 OID 65611)
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
-- TOC entry 196 (class 1255 OID 65612)
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

--
-- TOC entry 195 (class 1255 OID 65691)
-- Name: bytea_import(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION bytea_import(p_path text, OUT p_result bytea) RETURNS bytea
    LANGUAGE plpgsql
    AS $$
declare
  l_oid oid;
  r record;
begin
  p_result := '';
  select lo_import(p_path) into l_oid;
  for r in ( select data 
             from pg_largeobject 
             where loid = l_oid 
             order by pageno ) loop
    p_result = p_result || r.data;
  end loop;
  perform lo_unlink(l_oid);
end;$$;


ALTER FUNCTION public.bytea_import(p_path text, OUT p_result bytea) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 173 (class 1259 OID 65613)
-- Name: billetero; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE billetero (
    id integer DEFAULT 0 NOT NULL,
    dataset character varying(128),
    firmware integer
);


ALTER TABLE billetero OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 65617)
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
    bloqueada boolean,
    servidor boolean DEFAULT false NOT NULL,
    factor_para_jackpot real DEFAULT 0.01 NOT NULL,
    acumulado double precision,
    ip_servidor character varying(20),
    puerto_jackpot integer,
    game bytea
);


ALTER TABLE configuracion OWNER TO postgres;

--
-- TOC entry 2086 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN configuracion.acumulado; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN configuracion.acumulado IS 'Representa la cantidad de dinero destinado al pozo acumulado (Jackpot), el mismo se otorga al jugador en determinadas condiciones

La logica de negocios es: cada vez que el jugador oprime el boton "Jugar" entonces la maquina debe descontar un X% del dinero que acumulado en el campo "acumulado"';


--
-- TOC entry 175 (class 1259 OID 65622)
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
-- TOC entry 176 (class 1259 OID 65640)
-- Name: denominacion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE denominacion (
    nombre character varying(32) NOT NULL,
    valor double precision
);


ALTER TABLE denominacion OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 65643)
-- Name: estadisticas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE estadisticas (
    id integer NOT NULL,
    apostado integer,
    pagado integer
);


ALTER TABLE estadisticas OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 65646)
-- Name: idiomas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE idiomas (
    numero integer NOT NULL,
    nombre character varying(64)
);


ALTER TABLE idiomas OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 65649)
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
    liberar_bolas_extra boolean DEFAULT false,
    recaudado integer DEFAULT 0 NOT NULL
);


ALTER TABLE juego OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 65661)
-- Name: logs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE logs (
    id integer NOT NULL,
    creditos_backend integer NOT NULL,
    creditos_frontend integer NOT NULL,
    descripcion character varying(512),
    apuesta_total_backend integer NOT NULL,
    apuesta_total_frontend integer NOT NULL
);


ALTER TABLE logs OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 65667)
-- Name: logs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE logs_id_seq OWNER TO postgres;

--
-- TOC entry 2094 (class 0 OID 0)
-- Dependencies: 181
-- Name: logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE logs_id_seq OWNED BY logs.id;


--
-- TOC entry 1940 (class 2604 OID 65669)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY logs ALTER COLUMN id SET DEFAULT nextval('logs_id_seq'::regclass);


--
-- TOC entry 2068 (class 0 OID 65613)
-- Dependencies: 173
-- Data for Name: billetero; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO billetero VALUES (0, NULL, NULL);


--
-- TOC entry 2069 (class 0 OID 65617)
-- Dependencies: 174
-- Data for Name: configuracion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO configuracion VALUES (0, 'ESSENTIT-X2017-NS', 1, true, 'ENGLISH', 1, false, true, false, true, 0.00999999978, 3.6980000010877854, 'localhost', 8895, NULL);


--
-- TOC entry 2070 (class 0 OID 65622)
-- Dependencies: 175
-- Data for Name: contadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO contadores VALUES (0, 0, 2, 7, 17, 0, 2, 44, 0, 0, 0, 0, 0, 0, 0, 0);


--
-- TOC entry 2071 (class 0 OID 65640)
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
-- TOC entry 2072 (class 0 OID 65643)
-- Dependencies: 177
-- Data for Name: estadisticas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2073 (class 0 OID 65646)
-- Dependencies: 178
-- Data for Name: idiomas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO idiomas VALUES (0, 'SPANISH');
INSERT INTO idiomas VALUES (1, 'ENGLISH');
INSERT INTO idiomas VALUES (2, 'PORTUGUESE');


--
-- TOC entry 2074 (class 0 OID 65649)
-- Dependencies: 179
-- Data for Name: juego; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO juego VALUES (0, 395, 4, 0, true, true, true, true, 1, 19.75, 'CINCO_CENTAVOS', 20, '2017-05-23 05:19:48.698', '2017-05-23 05:19:50.691', 0, 0, 1, 0, 0, 0, '{41,30,35,32,38,28,43,48,37,25,34,7,59,52,50,46,42,2,51,5,13,49,26,19,6,4,36,53,3,11}', '{58,40,15,24,16,60,55,56,33,22}', false, 0);


--
-- TOC entry 2075 (class 0 OID 65661)
-- Dependencies: 180
-- Data for Name: logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO logs VALUES (18, 2000, 1910, NULL, 120, 120);
INSERT INTO logs VALUES (19, 1880, 1880, NULL, 120, 120);
INSERT INTO logs VALUES (20, 1940, 1760, NULL, 120, 120);
INSERT INTO logs VALUES (21, 1910, 1820, NULL, 120, 120);
INSERT INTO logs VALUES (22, 3590, 3500, NULL, 120, 120);
INSERT INTO logs VALUES (23, 470, 470, NULL, 120, 120);
INSERT INTO logs VALUES (24, 350, 350, NULL, 120, 120);
INSERT INTO logs VALUES (25, 380, 230, NULL, 120, 120);
INSERT INTO logs VALUES (26, 530, 530, NULL, 120, 120);
INSERT INTO logs VALUES (27, 470, 410, NULL, 120, 120);
INSERT INTO logs VALUES (28, 440, 350, NULL, 120, 120);
INSERT INTO logs VALUES (29, 320, 320, NULL, 120, 120);
INSERT INTO logs VALUES (30, 200, 200, NULL, 120, 120);
INSERT INTO logs VALUES (31, 80, 80, NULL, 60, 120);
INSERT INTO logs VALUES (32, 2050, 1960, NULL, 120, 120);
INSERT INTO logs VALUES (33, 1960, 1930, NULL, 120, 120);
INSERT INTO logs VALUES (34, 1840, 1840, NULL, 120, 120);
INSERT INTO logs VALUES (35, 2020, 1720, NULL, 120, 120);
INSERT INTO logs VALUES (36, 7900, 7000, NULL, 120, 120);
INSERT INTO logs VALUES (37, 7780, 7780, NULL, 120, 120);
INSERT INTO logs VALUES (38, 7660, 7660, NULL, 120, 120);
INSERT INTO logs VALUES (39, 7630, 7540, NULL, 120, 120);
INSERT INTO logs VALUES (40, 10570, 10480, NULL, 120, 120);
INSERT INTO logs VALUES (41, 10450, 10450, NULL, 120, 120);
INSERT INTO logs VALUES (42, 13510, 13510, NULL, 120, 120);
INSERT INTO logs VALUES (43, 13450, 13390, NULL, 120, 120);
INSERT INTO logs VALUES (44, 13450, 13330, NULL, 120, 120);
INSERT INTO logs VALUES (45, 12730, 12730, NULL, 120, 120);
INSERT INTO logs VALUES (46, 12610, 12610, NULL, 120, 120);
INSERT INTO logs VALUES (47, 12490, 12490, NULL, 120, 120);
INSERT INTO logs VALUES (48, 12370, 12370, NULL, 120, 120);
INSERT INTO logs VALUES (49, 1940, 1880, NULL, 120, 120);
INSERT INTO logs VALUES (50, 2360, 2360, NULL, 120, 120);
INSERT INTO logs VALUES (51, 2240, 2240, NULL, 120, 120);
INSERT INTO logs VALUES (52, 2270, 2120, NULL, 120, 120);
INSERT INTO logs VALUES (53, 80, 50, NULL, 60, 120);
INSERT INTO logs VALUES (54, 1580, 1580, NULL, 120, 120);
INSERT INTO logs VALUES (55, 1460, 1460, NULL, 120, 120);
INSERT INTO logs VALUES (56, 1370, 1340, NULL, 120, 120);
INSERT INTO logs VALUES (57, 1340, 1250, NULL, 120, 120);
INSERT INTO logs VALUES (58, 110, 110, NULL, 90, 120);
INSERT INTO logs VALUES (59, 20, 20, NULL, 20, 90);
INSERT INTO logs VALUES (60, 1900, 1940, NULL, 120, 80);
INSERT INTO logs VALUES (61, 1910, 1860, NULL, 120, 80);
INSERT INTO logs VALUES (62, 5030, 5070, NULL, 120, 80);
INSERT INTO logs VALUES (63, 4950, 4990, NULL, 120, 80);
INSERT INTO logs VALUES (64, 4870, 4910, NULL, 120, 80);
INSERT INTO logs VALUES (65, 4820, 4790, NULL, 120, 120);
INSERT INTO logs VALUES (66, 2810, 2810, NULL, 120, 120);
INSERT INTO logs VALUES (67, 2690, 2690, NULL, 120, 120);
INSERT INTO logs VALUES (68, 2630, 2570, NULL, 120, 120);
INSERT INTO logs VALUES (69, 2570, 2510, NULL, 120, 120);
INSERT INTO logs VALUES (70, 2450, 2450, NULL, 120, 120);
INSERT INTO logs VALUES (71, 2360, 2330, NULL, 120, 120);
INSERT INTO logs VALUES (72, 2540, 2240, NULL, 120, 120);
INSERT INTO logs VALUES (73, 6860, 6800, NULL, 120, 120);
INSERT INTO logs VALUES (74, 9980, 9890, NULL, 120, 120);
INSERT INTO logs VALUES (75, 9860, 9860, NULL, 120, 120);
INSERT INTO logs VALUES (76, 9830, 9740, NULL, 120, 120);
INSERT INTO logs VALUES (77, 9770, 9710, NULL, 120, 120);
INSERT INTO logs VALUES (78, 10550, 9650, NULL, 120, 120);
INSERT INTO logs VALUES (79, 17870, 17810, NULL, 120, 120);
INSERT INTO logs VALUES (80, 13610, 13610, NULL, 120, 120);
INSERT INTO logs VALUES (81, 17720, 17630, NULL, 120, 120);
INSERT INTO logs VALUES (82, 13460, 13460, NULL, 120, 120);
INSERT INTO logs VALUES (83, 17480, 17480, NULL, 120, 120);
INSERT INTO logs VALUES (84, 13220, 13220, NULL, 120, 120);
INSERT INTO logs VALUES (85, 17240, 17240, NULL, 120, 120);
INSERT INTO logs VALUES (86, 13010, 12980, NULL, 120, 120);
INSERT INTO logs VALUES (87, 17030, 17030, NULL, 120, 120);
INSERT INTO logs VALUES (88, 12890, 12770, NULL, 120, 120);
INSERT INTO logs VALUES (89, 16940, 16910, NULL, 120, 120);
INSERT INTO logs VALUES (90, 12770, 12680, NULL, 120, 120);
INSERT INTO logs VALUES (91, 16880, 16790, NULL, 120, 120);
INSERT INTO logs VALUES (92, 15050, 15050, NULL, 120, 120);
INSERT INTO logs VALUES (93, 14990, 14930, NULL, 120, 120);
INSERT INTO logs VALUES (94, 14960, 14870, NULL, 120, 120);
INSERT INTO logs VALUES (95, 8900, 8840, NULL, 120, 120);
INSERT INTO logs VALUES (96, 8780, 8780, NULL, 120, 120);
INSERT INTO logs VALUES (97, 8660, 8660, NULL, 120, 120);
INSERT INTO logs VALUES (98, 8540, 8540, NULL, 120, 120);
INSERT INTO logs VALUES (99, 8420, 8420, NULL, 120, 120);
INSERT INTO logs VALUES (100, 8300, 8300, NULL, 120, 120);
INSERT INTO logs VALUES (101, 8180, 8180, NULL, 120, 120);
INSERT INTO logs VALUES (102, 8150, 8060, NULL, 120, 120);
INSERT INTO logs VALUES (103, 8030, 8030, NULL, 120, 120);
INSERT INTO logs VALUES (104, 7910, 7910, NULL, 120, 120);
INSERT INTO logs VALUES (105, 7850, 7790, NULL, 120, 120);
INSERT INTO logs VALUES (106, 5750, 5750, NULL, 120, 120);
INSERT INTO logs VALUES (107, 5930, 5630, NULL, 120, 120);


--
-- TOC entry 2096 (class 0 OID 0)
-- Dependencies: 181
-- Name: logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('logs_id_seq', 107, true);


--
-- TOC entry 1942 (class 2606 OID 65671)
-- Name: billetero_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY billetero
    ADD CONSTRAINT billetero_pk PRIMARY KEY (id);


--
-- TOC entry 1944 (class 2606 OID 65673)
-- Name: configuracion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configuracion
    ADD CONSTRAINT configuracion_pk PRIMARY KEY (id);


--
-- TOC entry 1946 (class 2606 OID 65675)
-- Name: contadores_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contadores
    ADD CONSTRAINT contadores_pk PRIMARY KEY (id);


--
-- TOC entry 1948 (class 2606 OID 65677)
-- Name: denominacion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY denominacion
    ADD CONSTRAINT denominacion_pk PRIMARY KEY (nombre);


--
-- TOC entry 1950 (class 2606 OID 65679)
-- Name: estadisticas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY estadisticas
    ADD CONSTRAINT estadisticas_pk PRIMARY KEY (id);


--
-- TOC entry 1952 (class 2606 OID 65681)
-- Name: idiomas_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY idiomas
    ADD CONSTRAINT idiomas_pk PRIMARY KEY (numero);


--
-- TOC entry 1954 (class 2606 OID 65683)
-- Name: juego_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY juego
    ADD CONSTRAINT juego_pk PRIMARY KEY (id);


--
-- TOC entry 1956 (class 2606 OID 65685)
-- Name: logs_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY logs
    ADD CONSTRAINT logs_pk PRIMARY KEY (id);


--
-- TOC entry 1958 (class 2620 OID 65686)
-- Name: before_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER before_update BEFORE UPDATE ON juego FOR EACH ROW EXECUTE PROCEDURE before_update_juego();


--
-- TOC entry 1957 (class 2620 OID 65687)
-- Name: contadores_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER contadores_trigger BEFORE INSERT ON contadores FOR EACH ROW EXECUTE PROCEDURE before_insert_contador();


--
-- TOC entry 2083 (class 0 OID 0)
-- Dependencies: 7
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
GRANT USAGE ON SCHEMA public TO readonly;


--
-- TOC entry 2085 (class 0 OID 0)
-- Dependencies: 173
-- Name: billetero; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE billetero FROM PUBLIC;
REVOKE ALL ON TABLE billetero FROM postgres;
GRANT ALL ON TABLE billetero TO postgres;
GRANT SELECT ON TABLE billetero TO readonly;


--
-- TOC entry 2087 (class 0 OID 0)
-- Dependencies: 174
-- Name: configuracion; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE configuracion FROM PUBLIC;
REVOKE ALL ON TABLE configuracion FROM postgres;
GRANT ALL ON TABLE configuracion TO postgres;
GRANT SELECT,INSERT ON TABLE configuracion TO PUBLIC;
GRANT SELECT ON TABLE configuracion TO readonly;


--
-- TOC entry 2088 (class 0 OID 0)
-- Dependencies: 175
-- Name: contadores; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE contadores FROM PUBLIC;
REVOKE ALL ON TABLE contadores FROM postgres;
GRANT ALL ON TABLE contadores TO postgres;
GRANT SELECT ON TABLE contadores TO readonly;


--
-- TOC entry 2089 (class 0 OID 0)
-- Dependencies: 176
-- Name: denominacion; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE denominacion FROM PUBLIC;
REVOKE ALL ON TABLE denominacion FROM postgres;
GRANT ALL ON TABLE denominacion TO postgres;
GRANT SELECT ON TABLE denominacion TO readonly;


--
-- TOC entry 2090 (class 0 OID 0)
-- Dependencies: 177
-- Name: estadisticas; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE estadisticas FROM PUBLIC;
REVOKE ALL ON TABLE estadisticas FROM postgres;
GRANT ALL ON TABLE estadisticas TO postgres;
GRANT SELECT ON TABLE estadisticas TO readonly;


--
-- TOC entry 2091 (class 0 OID 0)
-- Dependencies: 178
-- Name: idiomas; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE idiomas FROM PUBLIC;
REVOKE ALL ON TABLE idiomas FROM postgres;
GRANT ALL ON TABLE idiomas TO postgres;
GRANT SELECT ON TABLE idiomas TO readonly;


--
-- TOC entry 2092 (class 0 OID 0)
-- Dependencies: 179
-- Name: juego; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE juego FROM PUBLIC;
REVOKE ALL ON TABLE juego FROM postgres;
GRANT ALL ON TABLE juego TO postgres;
GRANT SELECT ON TABLE juego TO readonly;


--
-- TOC entry 2093 (class 0 OID 0)
-- Dependencies: 180
-- Name: logs; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE logs FROM PUBLIC;
REVOKE ALL ON TABLE logs FROM postgres;
GRANT ALL ON TABLE logs TO postgres;
GRANT SELECT ON TABLE logs TO readonly;


--
-- TOC entry 2095 (class 0 OID 0)
-- Dependencies: 181
-- Name: logs_id_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE logs_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE logs_id_seq FROM postgres;
GRANT ALL ON SEQUENCE logs_id_seq TO postgres;
GRANT SELECT ON SEQUENCE logs_id_seq TO readonly;


--
-- TOC entry 1582 (class 826 OID 65693)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES  TO readonly;


-- Completed on 2017-05-23 06:37:26

--
-- PostgreSQL database dump complete
--

