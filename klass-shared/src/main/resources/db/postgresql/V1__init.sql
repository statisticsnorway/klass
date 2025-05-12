--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: klass; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA klass;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: changelog; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.changelog (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    change_occured timestamp(0) without time zone NOT NULL,
    changed_by character varying(255) NOT NULL,
    description character varying(4096) NOT NULL
);


--
-- Name: changelog_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.changelog_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: changelog_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.changelog_id_seq OWNED BY klass.changelog.id;


--
-- Name: classification_access_counter; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.classification_access_counter (
    id bigint NOT NULL,
    classification_series_id bigint,
    correspondence_table_id bigint,
    classification_version_id bigint,
    time_stamp timestamp(0) without time zone NOT NULL
);


--
-- Name: classification_access_counter_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.classification_access_counter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: classification_access_counter_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.classification_access_counter_id_seq OWNED BY klass.classification_access_counter.id;


--
-- Name: classification_family; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.classification_family (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    icon_name character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: classification_family_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.classification_family_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: classification_family_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.classification_family_id_seq OWNED BY klass.classification_family.id;


--
-- Name: classification_item; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.classification_item (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    code character varying(255) DEFAULT NULL::character varying,
    notes character varying(6000) DEFAULT NULL::character varying,
    official_name character varying(2048) DEFAULT NULL::character varying,
    short_name character varying(1024) DEFAULT NULL::character varying,
    level_id bigint NOT NULL,
    parent_id bigint,
    reference_id bigint,
    valid_from character varying(16) DEFAULT NULL::character varying,
    valid_to character varying(16) DEFAULT NULL::character varying
);


--
-- Name: classification_item_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.classification_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: classification_item_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.classification_item_id_seq OWNED BY klass.classification_item.id;


--
-- Name: classification_series; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.classification_series (
    id bigint NOT NULL,
    deleted boolean NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    classification_type character varying(255) NOT NULL,
    copyrighted boolean NOT NULL,
    description text NOT NULL,
    include_notes boolean NOT NULL,
    include_short_name boolean NOT NULL,
    name_en character varying(255) DEFAULT NULL::character varying,
    name_nn character varying(255) DEFAULT NULL::character varying,
    name_no character varying(255) DEFAULT NULL::character varying,
    primary_language bigint NOT NULL,
    classification_family_id bigint NOT NULL,
    contact_person_id bigint NOT NULL,
    migrated_from character varying(255) DEFAULT NULL::character varying,
    migrated_from_id bigint,
    include_validity boolean NOT NULL
);


--
-- Name: classification_series_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.classification_series_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: classification_series_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.classification_series_id_seq OWNED BY klass.classification_series.id;


--
-- Name: classification_series_statistical_units; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.classification_series_statistical_units (
    classification_series_id bigint NOT NULL,
    statistical_units_id bigint NOT NULL
);


--
-- Name: correspondence_map; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.correspondence_map (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    source_id bigint,
    target_id bigint,
    correspondence_table_id bigint NOT NULL
);


--
-- Name: correspondence_map_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.correspondence_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: correspondence_map_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.correspondence_map_id_seq OWNED BY klass.correspondence_map.id;


--
-- Name: correspondence_table; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.correspondence_table (
    id bigint NOT NULL,
    deleted boolean NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    description text NOT NULL,
    published_en boolean NOT NULL,
    published_nn boolean NOT NULL,
    published_no boolean NOT NULL,
    source_id bigint NOT NULL,
    source_level_number bigint NOT NULL,
    target_id bigint NOT NULL,
    target_level_number bigint NOT NULL,
    draft boolean NOT NULL
);


--
-- Name: correspondence_table_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.correspondence_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: correspondence_table_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.correspondence_table_id_seq OWNED BY klass.correspondence_table.id;


--
-- Name: correspondencetable_changelog; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.correspondencetable_changelog (
    correspondencetable_id bigint NOT NULL,
    changelog_id bigint NOT NULL
);


--
-- Name: level; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.level (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    level_number bigint NOT NULL,
    name character varying(1000) NOT NULL,
    statistical_classification_id bigint NOT NULL
);


--
-- Name: level_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.level_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: level_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.level_id_seq OWNED BY klass.level.id;


--
-- Name: search_words; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.search_words (
    id bigint NOT NULL,
    time_stamp timestamp(0) without time zone NOT NULL,
    hit boolean NOT NULL,
    search_string character varying(255) NOT NULL
);


--
-- Name: search_words_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.search_words_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: search_words_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.search_words_id_seq OWNED BY klass.search_words.id;


--
-- Name: statistical_classification; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.statistical_classification (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    deleted boolean NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(64) NOT NULL,
    version bigint NOT NULL,
    introduction text NOT NULL,
    published_en boolean NOT NULL,
    published_nn boolean NOT NULL,
    published_no boolean NOT NULL,
    name character varying(1000) DEFAULT NULL::character varying,
    derived_from character varying(4000) DEFAULT NULL::character varying,
    legal_base character varying(4000) DEFAULT NULL::character varying,
    publications character varying(4000) DEFAULT NULL::character varying,
    valid_from character varying(16) DEFAULT NULL::character varying,
    valid_to character varying(16) DEFAULT NULL::character varying,
    classification_version_id bigint,
    contact_person_id bigint,
    classification_id bigint,
    predecessor_id bigint,
    successor_id bigint,
    alias character varying(1024) DEFAULT NULL::character varying
);


--
-- Name: statistical_classification_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.statistical_classification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: statistical_classification_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.statistical_classification_id_seq OWNED BY klass.statistical_classification.id;


--
-- Name: statistical_unit; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.statistical_unit (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: statistical_unit_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.statistical_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: statistical_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.statistical_unit_id_seq OWNED BY klass.statistical_unit.id;


--
-- Name: statisticalclassification_changelog; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.statisticalclassification_changelog (
    statisticalclassification_id bigint NOT NULL,
    changelog_id bigint NOT NULL
);


--
-- Name: subscriber; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.subscriber (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    email character varying(255) NOT NULL
);


--
-- Name: subscriber_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.subscriber_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: subscriber_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.subscriber_id_seq OWNED BY klass.subscriber.id;


--
-- Name: subscription; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.subscription (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    expiry_date timestamp(0) without time zone NOT NULL,
    token character varying(255) NOT NULL,
    verification character varying(255) NOT NULL,
    classification_id bigint,
    subscriber bigint,
    end_subscription_url character varying(255) NOT NULL
);


--
-- Name: subscription_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.subscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: subscription_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.subscription_id_seq OWNED BY klass.subscription.id;


--
-- Name: user; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass."user" (
    id bigint NOT NULL,
    last_modified timestamp(0) without time zone NOT NULL,
    uuid character varying(255) NOT NULL,
    version bigint NOT NULL,
    fullname character varying(255) NOT NULL,
    role bigint NOT NULL,
    section character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    email character varying(255) DEFAULT NULL::character varying,
    phone character varying(255) DEFAULT NULL::character varying
);


--
-- Name: user_favorites; Type: TABLE; Schema: klass; Owner: -
--

CREATE TABLE klass.user_favorites (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    favorites_id bigint NOT NULL
);


--
-- Name: user_favorites_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.user_favorites_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_favorites_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.user_favorites_id_seq OWNED BY klass.user_favorites.id;


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: klass; Owner: -
--

CREATE SEQUENCE klass.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: klass; Owner: -
--

ALTER SEQUENCE klass.user_id_seq OWNED BY klass."user".id;


--
-- Name: changelog id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.changelog ALTER COLUMN id SET DEFAULT nextval('klass.changelog_id_seq'::regclass);


--
-- Name: classification_access_counter id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_access_counter ALTER COLUMN id SET DEFAULT nextval('klass.classification_access_counter_id_seq'::regclass);


--
-- Name: classification_family id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_family ALTER COLUMN id SET DEFAULT nextval('klass.classification_family_id_seq'::regclass);


--
-- Name: classification_item id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_item ALTER COLUMN id SET DEFAULT nextval('klass.classification_item_id_seq'::regclass);


--
-- Name: classification_series id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_series ALTER COLUMN id SET DEFAULT nextval('klass.classification_series_id_seq'::regclass);


--
-- Name: correspondence_map id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondence_map ALTER COLUMN id SET DEFAULT nextval('klass.correspondence_map_id_seq'::regclass);


--
-- Name: correspondence_table id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondence_table ALTER COLUMN id SET DEFAULT nextval('klass.correspondence_table_id_seq'::regclass);


--
-- Name: level id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.level ALTER COLUMN id SET DEFAULT nextval('klass.level_id_seq'::regclass);


--
-- Name: search_words id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.search_words ALTER COLUMN id SET DEFAULT nextval('klass.search_words_id_seq'::regclass);


--
-- Name: statistical_classification id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_classification ALTER COLUMN id SET DEFAULT nextval('klass.statistical_classification_id_seq'::regclass);


--
-- Name: statistical_unit id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_unit ALTER COLUMN id SET DEFAULT nextval('klass.statistical_unit_id_seq'::regclass);


--
-- Name: subscriber id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.subscriber ALTER COLUMN id SET DEFAULT nextval('klass.subscriber_id_seq'::regclass);


--
-- Name: subscription id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.subscription ALTER COLUMN id SET DEFAULT nextval('klass.subscription_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass."user" ALTER COLUMN id SET DEFAULT nextval('klass.user_id_seq'::regclass);


--
-- Name: user_favorites id; Type: DEFAULT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.user_favorites ALTER COLUMN id SET DEFAULT nextval('klass.user_favorites_id_seq'::regclass);


--
-- Name: classification_family idx_16387_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_family
    ADD CONSTRAINT idx_16387_primary PRIMARY KEY (id);


--
-- Name: classification_item idx_16394_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_item
    ADD CONSTRAINT idx_16394_primary PRIMARY KEY (id);


--
-- Name: classification_series idx_16407_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_series
    ADD CONSTRAINT idx_16407_primary PRIMARY KEY (id);


--
-- Name: correspondence_map idx_16505_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondence_map
    ADD CONSTRAINT idx_16505_primary PRIMARY KEY (id);


--
-- Name: correspondence_table idx_16510_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondence_table
    ADD CONSTRAINT idx_16510_primary PRIMARY KEY (id);


--
-- Name: statistical_classification idx_16551_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_classification
    ADD CONSTRAINT idx_16551_primary PRIMARY KEY (id);


--
-- Name: statistical_unit idx_16565_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_unit
    ADD CONSTRAINT idx_16565_primary PRIMARY KEY (id);


--
-- Name: subscriber idx_16653_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.subscriber
    ADD CONSTRAINT idx_16653_primary PRIMARY KEY (id);


--
-- Name: subscription idx_16660_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.subscription
    ADD CONSTRAINT idx_16660_primary PRIMARY KEY (id);


--
-- Name: changelog idx_16684_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.changelog
    ADD CONSTRAINT idx_16684_primary PRIMARY KEY (id);


--
-- Name: classification_access_counter idx_16691_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_access_counter
    ADD CONSTRAINT idx_16691_primary PRIMARY KEY (id);


--
-- Name: level idx_16696_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.level
    ADD CONSTRAINT idx_16696_primary PRIMARY KEY (id);


--
-- Name: search_words idx_16703_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.search_words
    ADD CONSTRAINT idx_16703_primary PRIMARY KEY (id);


--
-- Name: user idx_16722_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass."user"
    ADD CONSTRAINT idx_16722_primary PRIMARY KEY (id);


--
-- Name: user_favorites idx_16731_primary; Type: CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.user_favorites
    ADD CONSTRAINT idx_16731_primary PRIMARY KEY (id);


--
-- Name: idx_16387_cf_name_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16387_cf_name_idx ON klass.classification_family USING btree (name);


--
-- Name: idx_16394_fk_g9g8ko4hiflhcdvt7431oofli; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16394_fk_g9g8ko4hiflhcdvt7431oofli ON klass.classification_item USING btree (level_id);


--
-- Name: idx_16394_fk_ge0nq2ndexf8l2cmvppl66qh3; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16394_fk_ge0nq2ndexf8l2cmvppl66qh3 ON klass.classification_item USING btree (reference_id);


--
-- Name: idx_16394_fk_r370q2nlapshi9qrvswx98vrm; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16394_fk_r370q2nlapshi9qrvswx98vrm ON klass.classification_item USING btree (parent_id);


--
-- Name: idx_16407_cs_name_en_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16407_cs_name_en_idx ON klass.classification_series USING btree (name_en);


--
-- Name: idx_16407_cs_name_nn_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16407_cs_name_nn_idx ON klass.classification_series USING btree (name_nn);


--
-- Name: idx_16407_cs_name_no_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16407_cs_name_no_idx ON klass.classification_series USING btree (name_no);


--
-- Name: idx_16407_fk_666t6jamu95kshjhawy2co5ej; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16407_fk_666t6jamu95kshjhawy2co5ej ON klass.classification_series USING btree (classification_family_id);


--
-- Name: idx_16407_fk_jefvdo01kn4kq98m64ajli6y5; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16407_fk_jefvdo01kn4kq98m64ajli6y5 ON klass.classification_series USING btree (contact_person_id);


--
-- Name: idx_16501_fk_9obbpbvq6quql391al79il4l0; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16501_fk_9obbpbvq6quql391al79il4l0 ON klass.correspondencetable_changelog USING btree (correspondencetable_id);


--
-- Name: idx_16501_uk_8gl7hjwhs4c6iiih7myp4dtud; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16501_uk_8gl7hjwhs4c6iiih7myp4dtud ON klass.correspondencetable_changelog USING btree (changelog_id);


--
-- Name: idx_16505_fk_74gwtjl4rjw3gw4x9idj4ux98; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16505_fk_74gwtjl4rjw3gw4x9idj4ux98 ON klass.correspondence_map USING btree (correspondence_table_id);


--
-- Name: idx_16505_fk_polg03jsn36cq2pujlee5k13y; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16505_fk_polg03jsn36cq2pujlee5k13y ON klass.correspondence_map USING btree (target_id);


--
-- Name: idx_16505_uk_kac4sjyxvjamubg5vrhvbvqe; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16505_uk_kac4sjyxvjamubg5vrhvbvqe ON klass.correspondence_map USING btree (source_id, target_id, correspondence_table_id);


--
-- Name: idx_16510_ct_source_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16510_ct_source_idx ON klass.correspondence_table USING btree (source_id);


--
-- Name: idx_16510_ct_target_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16510_ct_target_idx ON klass.correspondence_table USING btree (target_id);


--
-- Name: idx_16510_fk_8bn11tm1gsou84v8kmoug6qyt; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16510_fk_8bn11tm1gsou84v8kmoug6qyt ON klass.correspondence_table USING btree (source_level_number);


--
-- Name: idx_16510_fk_cqdqwek3akelq2qqnhuydrkqr; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16510_fk_cqdqwek3akelq2qqnhuydrkqr ON klass.correspondence_table USING btree (target_level_number);


--
-- Name: idx_16544_fk_6nl7ywwll2hvo9ucndhg1b1xy; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16544_fk_6nl7ywwll2hvo9ucndhg1b1xy ON klass.classification_series_statistical_units USING btree (classification_series_id);


--
-- Name: idx_16544_fk_rfx4t7rjg3hykfr39xi8dvrdu; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16544_fk_rfx4t7rjg3hykfr39xi8dvrdu ON klass.classification_series_statistical_units USING btree (statistical_units_id);


--
-- Name: idx_16547_fk_fhfff2m0ltcoqt9hxbnsw3u6j; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16547_fk_fhfff2m0ltcoqt9hxbnsw3u6j ON klass.statisticalclassification_changelog USING btree (statisticalclassification_id);


--
-- Name: idx_16547_uk_k1e2lk17i4v7t76hequ2cegri; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16547_uk_k1e2lk17i4v7t76hequ2cegri ON klass.statisticalclassification_changelog USING btree (changelog_id);


--
-- Name: idx_16551_fk_12hsnu91tsf1c8b4697tv1bts; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16551_fk_12hsnu91tsf1c8b4697tv1bts ON klass.statistical_classification USING btree (contact_person_id);


--
-- Name: idx_16551_fk_e2pju4h6p0pdlbgievunfqrtg; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16551_fk_e2pju4h6p0pdlbgievunfqrtg ON klass.statistical_classification USING btree (classification_version_id);


--
-- Name: idx_16551_fk_ie31cb245vkf1vhswws053s29; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16551_fk_ie31cb245vkf1vhswws053s29 ON klass.statistical_classification USING btree (classification_id);


--
-- Name: idx_16551_fk_ije1678rnf2dso2pm83xchu9d; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16551_fk_ije1678rnf2dso2pm83xchu9d ON klass.statistical_classification USING btree (predecessor_id);


--
-- Name: idx_16551_fk_tdivea13yro7vtbon5l0elqtg; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16551_fk_tdivea13yro7vtbon5l0elqtg ON klass.statistical_classification USING btree (successor_id);


--
-- Name: idx_16653_subscriber_email_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16653_subscriber_email_idx ON klass.subscriber USING btree (email);


--
-- Name: idx_16660_fk_d9uwbiv0kt50iw4fcfydc7te7; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16660_fk_d9uwbiv0kt50iw4fcfydc7te7 ON klass.subscription USING btree (subscriber);


--
-- Name: idx_16660_fk_in6dab29f2fdp035fja02e2k3; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16660_fk_in6dab29f2fdp035fja02e2k3 ON klass.subscription USING btree (classification_id);


--
-- Name: idx_16696_fk_sxgu9ljubog3jnujn8fvohvn4; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16696_fk_sxgu9ljubog3jnujn8fvohvn4 ON klass.level USING btree (statistical_classification_id);


--
-- Name: idx_16722_user_fullname_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16722_user_fullname_idx ON klass."user" USING btree (fullname);


--
-- Name: idx_16722_user_username_idx; Type: INDEX; Schema: klass; Owner: -
--

CREATE UNIQUE INDEX idx_16722_user_username_idx ON klass."user" USING btree (username);


--
-- Name: idx_16731_fk_favorite_id; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16731_fk_favorite_id ON klass.user_favorites USING btree (favorites_id);


--
-- Name: idx_16731_fk_user_id; Type: INDEX; Schema: klass; Owner: -
--

CREATE INDEX idx_16731_fk_user_id ON klass.user_favorites USING btree (user_id);


--
-- Name: classification_series fk_666t6jamu95kshjhawy2co5ej; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_series
    ADD CONSTRAINT fk_666t6jamu95kshjhawy2co5ej FOREIGN KEY (classification_family_id) REFERENCES klass.classification_family(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: correspondence_map fk_74gwtjl4rjw3gw4x9idj4ux98; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondence_map
    ADD CONSTRAINT fk_74gwtjl4rjw3gw4x9idj4ux98 FOREIGN KEY (correspondence_table_id) REFERENCES klass.correspondence_table(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: correspondencetable_changelog fk_9obbpbvq6quql391al79il4l0; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.correspondencetable_changelog
    ADD CONSTRAINT fk_9obbpbvq6quql391al79il4l0 FOREIGN KEY (correspondencetable_id) REFERENCES klass.correspondence_table(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: subscription fk_d9uwbiv0kt50iw4fcfydc7te7; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.subscription
    ADD CONSTRAINT fk_d9uwbiv0kt50iw4fcfydc7te7 FOREIGN KEY (subscriber) REFERENCES klass.subscriber(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: statistical_classification fk_e2pju4h6p0pdlbgievunfqrtg; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_classification
    ADD CONSTRAINT fk_e2pju4h6p0pdlbgievunfqrtg FOREIGN KEY (classification_version_id) REFERENCES klass.statistical_classification(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: statisticalclassification_changelog fk_fhfff2m0ltcoqt9hxbnsw3u6j; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statisticalclassification_changelog
    ADD CONSTRAINT fk_fhfff2m0ltcoqt9hxbnsw3u6j FOREIGN KEY (statisticalclassification_id) REFERENCES klass.statistical_classification(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: classification_item fk_ge0nq2ndexf8l2cmvppl66qh3; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_item
    ADD CONSTRAINT fk_ge0nq2ndexf8l2cmvppl66qh3 FOREIGN KEY (reference_id) REFERENCES klass.classification_item(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: statistical_classification fk_ije1678rnf2dso2pm83xchu9d; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_classification
    ADD CONSTRAINT fk_ije1678rnf2dso2pm83xchu9d FOREIGN KEY (predecessor_id) REFERENCES klass.statistical_classification(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: classification_item fk_r370q2nlapshi9qrvswx98vrm; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_item
    ADD CONSTRAINT fk_r370q2nlapshi9qrvswx98vrm FOREIGN KEY (parent_id) REFERENCES klass.classification_item(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: classification_series_statistical_units fk_rfx4t7rjg3hykfr39xi8dvrdu; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.classification_series_statistical_units
    ADD CONSTRAINT fk_rfx4t7rjg3hykfr39xi8dvrdu FOREIGN KEY (statistical_units_id) REFERENCES klass.statistical_unit(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: statistical_classification fk_tdivea13yro7vtbon5l0elqtg; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.statistical_classification
    ADD CONSTRAINT fk_tdivea13yro7vtbon5l0elqtg FOREIGN KEY (successor_id) REFERENCES klass.statistical_classification(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: user_favorites fk_user_id; Type: FK CONSTRAINT; Schema: klass; Owner: -
--

ALTER TABLE ONLY klass.user_favorites
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES klass."user"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

