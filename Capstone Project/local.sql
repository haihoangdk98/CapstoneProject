--
-- Name: mpaa_rating; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.mpaa_rating AS ENUM (
    'G',
    'PG',
    'PG-13',
    'R',
    'NC-17'
);


ALTER TYPE public.mpaa_rating OWNER TO postgres;

--
-- Name: year; Type: DOMAIN; Schema: public; Owner: postgres
--

CREATE DOMAIN public.year AS integer
	CONSTRAINT year_check CHECK (((VALUE >= 1901) AND (VALUE <= 2155)));


ALTER DOMAIN public.year OWNER TO postgres;

--
-- Name: last_day(timestamp without time zone); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.last_day(timestamp without time zone) RETURNS date
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
  SELECT CASE
    WHEN EXTRACT(MONTH FROM $1) = 12 THEN
      (((EXTRACT(YEAR FROM $1) + 1) operator(pg_catalog.||) '-01-01')::date - INTERVAL '1 day')::date
    ELSE
      ((EXTRACT(YEAR FROM $1) operator(pg_catalog.||) '-' operator(pg_catalog.||) (EXTRACT(MONTH FROM $1) + 1) operator(pg_catalog.||) '-01')::date - INTERVAL '1 day')::date
    END
$_$;


ALTER FUNCTION public.last_day(timestamp without time zone) OWNER TO postgres;

--
-- Name: last_updated(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.last_updated() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.last_update = CURRENT_TIMESTAMP;
    RETURN NEW;
END $$;


ALTER FUNCTION public.last_updated() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    account_id integer NOT NULL,
    user_name character varying(255) NOT NULL unique,
    password character varying(255) NOT NULL,
	email character varying(255) NOT NULL,
    role character varying(32) NOT NULL,
	email_token character varying,
	email_verified boolean default false
);


ALTER TABLE public.account OWNER TO postgres;

--
-- Name: account_account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.account_account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.account_account_id_seq OWNER TO postgres;

--
-- Name: account_account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.account_account_id_seq OWNED BY public.account.account_id;



--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_category_id_seq OWNER TO postgres;

--
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    category_id integer DEFAULT nextval('public.category_category_id_seq'::regclass) NOT NULL,
    name character varying(50) NOT NULL,
	image character varying not null
);


ALTER TABLE public.category OWNER TO postgres;

--
-- Name: message_message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.message_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	
	
ALTER TABLE public.message_message_id_seq OWNER TO postgres;

CREATE SEQUENCE public.notification_notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	
	
ALTER TABLE public.notification_notification_id_seq OWNER TO postgres;

--
-- Name: chatmessage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chatmessage (
    message_id integer DEFAULT nextval('public.message_message_id_seq'::regclass) NOT NULL,
    sender_id integer NOT NULL,
    receiver_id integer NOT NULL,
    sent_at timestamp without time zone DEFAULT now() NOT NULL,
    content character varying(255) NOT NULL,
	seen boolean
);


ALTER TABLE public.chatmessage OWNER TO postgres;

CREATE TABLE public.notification (
    notification_id integer DEFAULT nextval('public.notification_notification_id_seq'::regclass) NOT NULL,
    sender_id integer NOT NULL,
    receiver_id integer NOT NULL,
    sent_at timestamp without time zone DEFAULT now() NOT NULL,
    content character varying(255) NOT NULL,
	seen boolean
);

ALTER TABLE public.notification OWNER TO postgres;

--
-- Name: guider; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.guider (
    guider_id integer NOT NULL unique,
    first_name character varying(45) NOT NULL,
    last_name character varying(45) NOT NULL,
    date_of_birth timestamp without time zone DEFAULT now() NOT NULL,
	phone character varying(50),
    about_me character varying NOT NULL,
    contribution integer,
    city character varying(50) NOT NULL,
    languages text[],
    active boolean NOT NULL,
    rated numeric(5,1),
    avatar text,
    passion text,
	profile_video character varying
);


ALTER TABLE public.guider OWNER TO postgres;

--
-- Name: locations_locations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.locations_locations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.locations_locations_id_seq OWNER TO postgres;

--
-- Name: locations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locations (
    location_id integer DEFAULT nextval('public.locations_locations_id_seq'::regclass) NOT NULL,
    country character varying(50) NOT NULL,
	city character varying(50) NOT NULL,
    place character varying(50) NOT NULL
);


ALTER TABLE public.locations OWNER TO postgres;

--
-- Name: trip_trip_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trip_trip_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trip_trip_id_seq OWNER TO postgres;

--
-- Name: trip; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trip (
    trip_id integer DEFAULT nextval('public.trip_trip_id_seq'::regclass) NOT NULL,
    traveler_id integer NOT NULL,
    post_id integer NOT NULL,
    begin_date timestamp without time zone DEFAULT now() NOT NULL,
    finish_date timestamp without time zone DEFAULT now() NOT NULL,
    adult_quantity integer NOT NULL,
    children_quantity integer NOT NULL,
    fee_paid numeric(13,2) NOT NULL,
    transaction_id character varying(255),
	status character varying(255) not null,
	book_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.trip OWNER TO postgres;

--
-- Name: plan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.plan (
    plan_id integer NOT NULL,
    meeting_point character varying(255) NOT NULL,
    detail text,
    post_id integer
);


ALTER TABLE public.plan OWNER TO postgres;

--
-- Name: plan_plan_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.plan_plan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.plan_plan_id_seq OWNER TO postgres;

--
-- Name: plan_plan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.plan_plan_id_seq OWNED BY public.plan.plan_id;


--
-- Name: post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.post_post_id_seq OWNER TO postgres;

--
-- Name: post; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.post (
    post_id integer DEFAULT nextval('public.post_post_id_seq'::regclass) NOT NULL,
    guider_id integer NOT NULL,
    location_id integer NOT NULL,
    category_id integer NOT NULL,
    title character varying(256) NOT NULL,
    video_link character varying(512) NOT NULL,
    picture_link text[],
    total_hour integer NOT NULL,
    description text NOT NULL,
    including_service text[],
    active boolean default true NOT NULL,
    price numeric(10,2),
    rated numeric(5,1) default 0,
    reasons text,
	authorized boolean default true not null
);


ALTER TABLE public.post OWNER TO postgres;

--
-- Name: review; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.review (
    trip_id integer unique,
    rated numeric(5,1),
    post_date timestamp without time zone DEFAULT now() NOT NULL,
    review character varying(512),
	visible boolean default true
);


ALTER TABLE public.review OWNER TO postgres;

--
-- Name: account account_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account ALTER COLUMN account_id SET DEFAULT nextval('public.account_account_id_seq'::regclass);


--
-- Name: plan plan_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plan ALTER COLUMN plan_id SET DEFAULT nextval('public.plan_plan_id_seq'::regclass);

create table public.traveler(
	traveler_id integer NOT NULL unique,
	first_name character varying(255) not null,
	last_name character varying(255) not null,
	phone character varying(50) not null,
	gender integer not null,
	date_of_birth timestamp without time zone not null,
	street character varying(255),
	house_number character varying(255),
	postal_code character varying(255),
	slogan character varying(255),
	about_me character varying,
	language TEXT [],
	country character varying(255),
	city character varying(255) not null,
	avatar_link character varying(255)
);

ALTER TABLE public.traveler OWNER TO postgres;

create table public.transaction(
	transaction_id character varying(255) primary key not null,
	payment_id character varying(255) not null,
	payer_id character varying(255) not null,
	description character varying(255) not null,
	date_of_transaction timestamp without time zone DEFAULT now() NOT NULL,
	success boolean not null
);

ALTER TABLE public.transaction OWNER TO postgres;

create table public.refund(
	refund_id serial primary key,
	transaction_id character varying(255) not null,
	date_of_refund timestamp without time zone DEFAULT now() NOT NULL,
	message character varying(255) not null
);

ALTER TABLE public.refund OWNER TO postgres;

create table public.feedback(
	feedback_id serial primary key,
	account_id integer not null,
	time_sent timestamp without time zone DEFAULT now() NOT NULL,
	message character varying(255) not null
);

ALTER TABLE public.feedback OWNER TO postgres;

create table public.contract_detail(
	contract_id serial primary key,
	guider_id integer not null,
	name character varying(255) not null,
	nationality character varying(255) not null,
	date_of_birth timestamp without time zone not null,
	gender integer not null,
	hometown character varying(255) not null,
	address character varying(255) not null,
	identity_card_number character varying(255) not null,
	card_issued_date timestamp without time zone not null,
	card_issued_province character varying(255) not null,
	account_active_date timestamp without time zone,
	account_deactive_date timestamp without time zone,
	file_link character varying(255)
);

ALTER TABLE public.contract_detail OWNER TO postgres;

create table public.contract(
	guider_id integer not null unique,
	contract_id integer not null
);

ALTER TABLE public.contract OWNER TO postgres;

create table public.favoritepost(
	traveler_id integer not null,
	post_id integer not null
);

ALTER TABLE public.favoritepost OWNER TO postgres;

create table public.travelerreviews(
	review_id serial primary key,
	traveler_id integer not null,
	guider_id integer not null,
	post_date timestamp without time zone DEFAULT now() NOT NULL,
	review character varying(255) not null,
	visible boolean default true
);

ALTER TABLE public.travelerreviews OWNER TO postgres;

--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (account_id);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: chatmessage chatmessage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chatmessage
    ADD CONSTRAINT chatmessage_pkey PRIMARY KEY (message_id);
	
ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (notification_id);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);


--
-- Name: trip trip_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT trip_pkey PRIMARY KEY (trip_id);


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY (plan_id);


--
-- Name: post post_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pkey PRIMARY KEY (post_id);
	
--
-- Foreign keys
--

ALTER TABLE ONLY public.guider
    ADD CONSTRAINT fk_guider FOREIGN KEY (guider_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.contract
    ADD CONSTRAINT fk_contract_guider FOREIGN KEY (guider_id) REFERENCES public.guider(guider_id);
	
ALTER TABLE ONLY public.contract
    ADD CONSTRAINT fk_contract_contractdetail FOREIGN KEY (contract_id) REFERENCES public.contract_detail(contract_id);
	
ALTER TABLE ONLY public.contract_detail
    ADD CONSTRAINT fk_contractdetail_guider FOREIGN KEY (guider_id) REFERENCES public.guider(guider_id);

ALTER TABLE ONLY public.traveler
    ADD CONSTRAINT fk_traveler FOREIGN KEY (traveler_id) REFERENCES public.account(account_id);

ALTER TABLE ONLY public.plan
    ADD CONSTRAINT fk_plan_post FOREIGN KEY (post_id) REFERENCES public.post(post_id);

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT fk_feedback FOREIGN KEY (account_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.post
    ADD CONSTRAINT fk_post_guider FOREIGN KEY (guider_id) REFERENCES public.guider(guider_id);
	
ALTER TABLE ONLY public.post
    ADD CONSTRAINT fk_post_location FOREIGN KEY (location_id) REFERENCES public.locations(location_id);
	
ALTER TABLE ONLY public.post
    ADD CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES public.category(category_id);
	
ALTER TABLE ONLY public.favoritepost
    ADD CONSTRAINT fk_fav_traveler FOREIGN KEY (traveler_id) REFERENCES public.traveler(traveler_id);
	
ALTER TABLE ONLY public.favoritepost
    ADD CONSTRAINT fk_fav_post FOREIGN KEY (post_id) REFERENCES public.post(post_id);
	
ALTER TABLE public.favoritepost
  ADD CONSTRAINT uq_fav UNIQUE(traveler_id, post_id);

ALTER TABLE ONLY public.review
    ADD CONSTRAINT fk_review FOREIGN KEY (trip_id) REFERENCES public.trip(trip_id);
	
ALTER TABLE ONLY public.trip
    ADD CONSTRAINT fk_order_post FOREIGN KEY (post_id) REFERENCES public.post(post_id);
	
ALTER TABLE ONLY public.trip
    ADD CONSTRAINT fk_order_traveler FOREIGN KEY (traveler_id) REFERENCES public.traveler(traveler_id);
	
ALTER TABLE ONLY public.trip
    ADD CONSTRAINT fk_order_transaction FOREIGN KEY (transaction_id) REFERENCES public.transaction(transaction_id);
	
ALTER TABLE ONLY public.refund
    ADD CONSTRAINT fk_refund FOREIGN KEY (transaction_id) REFERENCES public.transaction(transaction_id);
	
ALTER TABLE ONLY public.chatmessage
    ADD CONSTRAINT fk_chatmessage_sender FOREIGN KEY (sender_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.chatmessage
    ADD CONSTRAINT fk_chatmessage_receiver FOREIGN KEY (receiver_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.notification
    ADD CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.notification
    ADD CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES public.account(account_id);
	
ALTER TABLE ONLY public.travelerreviews
    ADD CONSTRAINT fk_travelerreviews_traveler FOREIGN KEY (traveler_id) REFERENCES public.traveler(traveler_id);
	
ALTER TABLE ONLY public.travelerreviews
    ADD CONSTRAINT fk_travelerreviews_guider FOREIGN KEY (guider_id) REFERENCES public.guider(guider_id);
	
--
-- Dummy data
--

insert into account values (1,'dungnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (2,'dungbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (3,'dunganh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (4,'dungdang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (5,'dungjustin','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (6,'dungdao','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (7,'dungtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (8,'dungdinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (9,'dunghoang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (10,'hainguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (11,'haibui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (12,'haihoang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (13,'haijustin','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (14,'haidinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (15,'haidao','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (16,'haitran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (17,'haianh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (18,'haiha','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (19,'haidung','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (20,'haingo','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (21,'longnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (22,'longdang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (23,'longtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (24,'longhoang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (25,'longdinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (26,'lonhtrong','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (27,'lonhngo','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (28,'longhai','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (29,'longbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (30,'longkhang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (31,'duongnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (32,'duongbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (33,'duonghoang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (34,'duongdinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (35,'duongngo','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (36,'duongdang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (37,'duongtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (38,'duongkha','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (39,'dangnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (40,'dangbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (41,'danghoang','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (42,'dangtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (43,'ngocanh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (44,'ngocnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (45,'ngocdinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (46,'ngocbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (47,'trangha','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (48,'trangnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (49,'trangbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (50,'trangtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (51,'uyennguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (52,'uyentran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (53,'uyenlinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (54,'uyenbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (55,'uyendinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (56,'hanguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (57,'halan','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (58,'habui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (59,'hatran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (60,'hadinh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (61,'bichngoc','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (62,'bichtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (63,'bichngo','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (64,'bichha','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (65,'bichbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (66,'linhnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (67,'linhtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (68,'linhanh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (69,'linhbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (70,'linhkhanh','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (71,'minhngoc','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (72,'minhnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (73,'minhtran','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (74,'khanhle','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (75,'anhngoc','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (76,'anhduc','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (77,'anhphan','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (78,'oanhnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (79,'oanhbui','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (80,'jeniferpham','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (81,'jenifernguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (82,'khanhnguyen','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','findguider@gmail.com','GUIDER');
insert into account values (100,'Jill','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','dungndse05558@fpt.edu.vn','TRAVELER');
insert into account values (101,'Kevin','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','dungndse05558@fpt.edu.vn','TRAVELER');
insert into account values (102,'Steve','$2a$10$Zf8lbBizad.a8jIzWzzER.mbb34BnBKBEBTaYHnsrbxBQBWx85gUW','dungndse05558@fpt.edu.vn','TRAVELER');

insert into guider values (1,'Dung','Nguyen',now(),'012345678901','As a local one who loves food, cultures, and meeting people, I see that many tourists are now stuck in touristy area, which doesnt make them "really travel" to Vietnam. So I decided to bring up a team with Vietnamese students, who are passionate in bringing values to tourists, to solve your insight travelling in Vietnam.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-2.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=hVLyXJCIiEU');
insert into guider values (2,'Dung','Bui',now(),'975645646201','I am a history lover. I study history to revise the old in order to know the new. I am also a nature lover, I can live in a forest or near the sea for a month without wifi if I have a dozen of books in order to read or play some sports like walking, running, swimming, trekking and so on. I am happy to chat with you all everything because I always wish to open my mind.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-3.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=EUTiRnN1Afk');
insert into guider values (3,'Dung','Anh',now(),'436985677436','After working abroad for a while, I am back in HCMC. I amm looking to meet new people from other nations, make new friends and talk things over. This gives me a little time to get away from computer, email and other bright flashy things.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-4.png','Peaceful city life experience','https://www.youtube.com/watch?v=-sHQgym_T-Y');
insert into guider values (4,'Dung','Dang',now(),'976545677651','I am international sales guy who loves spending time with my family and playing with my kids. My passions are traveling and martial arts. I love Ho Chi Minh City and I am ready to share it with you! Drop me a message and lets meet!',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-5.png','An experience you will never forget','https://www.youtube.com/watch?v=eWFGqpuA4Bw');
insert into guider values (5,'Dung','Justin',now(),'976545677651','Having many things in your life make you so tired. Cant eat , can not sleep well. Although delicious food with beautiful perform. Meal and taste is very important to you. From simple things, which cook good food for you and moreover, food will be better with funny or interesting story, making laugh comfortable. your stressful out and nothing for your sleep well for new power day. Cooking by heart and soul is made really good food.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-6.png','An experience you will never forget','https://www.youtube.com/watch?v=j5pgwc7X3MY');
insert into guider values (6,'Dung','Dao',now(),'976545677651','I always like new and strange things. I enjoy new experiences and meeting new people. I want to go to as many places as possible. I love making new friends, trying food that I have never tried before and taking pictures of beautiful landscapes.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-7.png','An experience you will never forget','https://www.youtube.com/watch?v=k-VkGeNovXo');
insert into guider values (7,'Dung','Tran',now(),'976545677651','My name is Anh and I was born and raised in Hanoi, Vietnam. I have been involved in travel and tourism in Vietnam for over 15 years and have worked with clients from over 50 countries from around the world. Throughout my career in the tourism industry, I have worked as an English-speaking tour guide for individual clients as well as large-sized groups of 25 – 30 people.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-8.png','An experience you will never forget','https://www.youtube.com/watch?v=hkZCiNfwFno');
insert into guider values (8,'Dung','Dinh',now(),'976545677651','I graduated from Hue University, College of Foreign Languages in 2011, I have been working as English speaking guide for years. I am a friendly guide, passionate and resourceful guide who always try my best to ensure the tour and services provided as per customers requirement. Moreover, I define myself as a traveler who has strong aspiration to discover the beauty of the places and cultures and share them all with my guests. I feel like I am not just a guide but my guest companion, my happiness after each trip is to leave my guests with great smiles and sweet memories about Vietnam country and people',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-9.png','An experience you will never forget','https://www.youtube.com/watch?v=2nuUGwpcQZs');
insert into guider values (9,'Dung','Hoang',now(),'976545677651','I was born and raised in Hanoi and have been hanging around with foreign friends, visiting scholars and tourists for 10 years. Working as a Consultant in a Fitness and Yoga Center sparks me ideas about mindfulness and how you can improve your life through stress relief by choosing the right places to relax and enjoy life in Hanoi and over the country.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-10.png','An experience you will never forget','https://www.youtube.com/watch?v=aTQm2ddSrlc');
insert into guider values (10,'Hai','Nguyen',now(),'012345678901','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-11.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=FU6bNONU4e8');
insert into guider values (11,'Hai','Bui',now(),'975645646201','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-12.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=FU6bNONU4e8');
insert into guider values (12,'Hai','Hoang',now(),'436985677436','I am a culture driven boy who value traditional values yet treasure integration to live in the opening spirit of the Vietnamese economic scene.  I am outgoing, sociable and communicative with a good sense of humor and clarifications.  I am an office boy in advertising with a highly dynamic working schedule, yet would love to spend time with new people to reward myself and to learn ways of thinking and new communications.  I am a food geek who spends time not just to eat, but also to build the experience. And I am a travel lover who by myself good trips of my own just to explore all above mentioned.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-13.png','Peaceful city life experience','https://www.youtube.com/watch?v=FU6bNONU4e8');
insert into guider values (13,'Hai','Justin',now(),'976545677651','My name is Shelby Mai, I am 23 year olds. I am really love travelling and making friends with people around the world. With 2 years working as a motobike tour leader and 6 years living in Ho Chi Minh city, I am excited to build my own motobike tour aiming to alleviate your concerns regarding Vietnam and enjoy in the best way.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-14.png','An experience you will never forget','https://www.youtube.com/watch?v=FU6bNONU4e8');
insert into guider values (14,'Hai','Dinh',now(),'976545677651','Anyone who loves healthy food, who wants to open a Vietnamese restaurant in his homeland, or wants to stand out in the professional Asian Kitchen should come to me to discover over 1000 recipes, created by myself and immerse themselves in the Vietnamese culture, agriculture and history. I would love to share my passion for cooking and my Vietnamese gardening skills with people who travel to teach them how to combine food and medicine - eating for a healthy life.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-15.png','An experience you will never forget','https://www.youtube.com/watch?v=FU6bNONU4e8');
insert into guider values (15,'Hai','Dao',now(),'976545677651','Frankly, I am not cut out for 9-5 office work. I prefer being outside, talking to people and socializing (as a typical pisces). Joining hospitality and tourism has made my wish come true. I have been in this industry in Hanoi for 4+ years and (to some extent) understood its potentials and shortcomings. The locals, who have maintained and run invaluable social quintessence, are not receiving what they deserve. I appreciate ''Slow food'' culture and sustainable tourism, in which people can travel with awareness and responsiblity to promote local values. Thats the reason I team up with my friends to create ''Hanoi Nom Nom'' team ,with view of bringing all the positive insights of Hanoi to travellers. We have assisted more than 1000 guests from at least 50 countries and territories.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-16.png','An experience you will never forget','https://www.youtube.com/watch?v=cGxkQi7-N58');
insert into guider values (16,'Hai','Tran',now(),'976545677651','Hi, I am Hung, but you can call me Charlie. Having lived in Berlin Germany for many years, so I love speaking German. I have worked as an freelancer German - speaking tour guide for individual clients as well as large-sized groups.  I love Hanoi City because it is full of history, food to try and people. Would like to be your companion on the Hanoi trip.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-17.png','An experience you will never forget','https://www.youtube.com/watch?v=cGxkQi7-N58');
insert into guider values (17,'Hai','Anh',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-18.png','An experience you will never forget','https://www.youtube.com/watch?v=cGxkQi7-N58');
insert into guider values (18,'Hai','Ha',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-19.png','An experience you will never forget','https://www.youtube.com/watch?v=d89Y-b0PABs');
insert into guider values (19,'Hai','Dung',now(),'012345678901','As a local one who loves food, cultures, and meeting people, I see that many tourists are now stuck in touristy area, which doesnt make them "really travel" to Vietnam. So I decided to bring up a team with Vietnamese students, who are passionate in bringing values to tourists, to solve your insight travelling in Vietnam.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-20.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=d89Y-b0PABs');
insert into guider values (20,'Hai','Ngo',now(),'975645646201','I am a history lover. I study history to revise the old in order to know the new. I am also a nature lover, I can live in a forest or near the sea for a month without wifi if I have a dozen of books in order to read or play some sports like walking, running, swimming, trekking and so on. I am happy to chat with you all everything because I always wish to open my mind.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-21.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=d89Y-b0PABs');
insert into guider values (21,'Long','Nguyen',now(),'436985677436','After working abroad for a while, I am back in HCMC. I amm looking to meet new people from other nations, make new friends and talk things over. This gives me a little time to get away from computer, email and other bright flashy things.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-22.png','Peaceful city life experience','https://www.youtube.com/watch?v=d89Y-b0PABs');
insert into guider values (22,'Long','Dang',now(),'976545677651','I am international sales guy who loves spending time with my family and playing with my kids. My passions are traveling and martial arts. I love Ho Chi Minh City and I am ready to share it with you! Drop me a message and lets meet!',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-23.png','An experience you will never forget','https://www.youtube.com/watch?v=SaAGuMuqkj8');
insert into guider values (23,'Long','Tran',now(),'976545677651','Having many things in your life make you so tired. Cant eat , can not sleep well. Although delicious food with beautiful perform. Meal and taste is very important to you. From simple things, which cook good food for you and moreover, food will be better with funny or interesting story, making laugh comfortable. your stressful out and nothing for your sleep well for new power day. Cooking by heart and soul is made really good food.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-24.png','An experience you will never forget','https://www.youtube.com/watch?v=SaAGuMuqkj8');
insert into guider values (24,'Long','Hoang',now(),'976545677651','I always like new and strange things. I enjoy new experiences and meeting new people. I want to go to as many places as possible. I love making new friends, trying food that I have never tried before and taking pictures of beautiful landscapes.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-25.png','An experience you will never forget','https://www.youtube.com/watch?v=SaAGuMuqkj8');
insert into guider values (25,'Long','Dinh',now(),'976545677651','My name is Anh and I was born and raised in Hanoi, Vietnam. I have been involved in travel and tourism in Vietnam for over 15 years and have worked with clients from over 50 countries from around the world. Throughout my career in the tourism industry, I have worked as an English-speaking tour guide for individual clients as well as large-sized groups of 25 – 30 people.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-26.png','An experience you will never forget','https://www.youtube.com/watch?v=SaAGuMuqkj8');
insert into guider values (26,'Long','Trong',now(),'976545677651','I graduated from Hue University, College of Foreign Languages in 2011, I have been working as English speaking guide for years. I am a friendly guide, passionate and resourceful guide who always try my best to ensure the tour and services provided as per customers requirement. Moreover, I define myself as a traveler who has strong aspiration to discover the beauty of the places and cultures and share them all with my guests. I feel like I am not just a guide but my guest companion, my happiness after each trip is to leave my guests with great smiles and sweet memories about Vietnam country and people',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-27.png','An experience you will never forget','https://www.youtube.com/watch?v=SaAGuMuqkj8');
insert into guider values (27,'Long','Ngo',now(),'976545677651','I was born and raised in Hanoi and have been hanging around with foreign friends, visiting scholars and tourists for 10 years. Working as a Consultant in a Fitness and Yoga Center sparks me ideas about mindfulness and how you can improve your life through stress relief by choosing the right places to relax and enjoy life in Hanoi and over the country.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-28.png','An experience you will never forget','https://www.youtube.com/watch?v=2iw_K-1AmVk');
insert into guider values (28,'Long','Hai',now(),'012345678901','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-29.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=2iw_K-1AmVk');
insert into guider values (29,'Long','Bui',now(),'975645646201','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-30.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=2iw_K-1AmVk');
insert into guider values (30,'Long','Khang',now(),'436985677436','I am a culture driven boy who value traditional values yet treasure integration to live in the opening spirit of the Vietnamese economic scene.  I am outgoing, sociable and communicative with a good sense of humor and clarifications.  I am an office boy in advertising with a highly dynamic working schedule, yet would love to spend time with new people to reward myself and to learn ways of thinking and new communications.  I am a food geek who spends time not just to eat, but also to build the experience. And I am a travel lover who by myself good trips of my own just to explore all above mentioned.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-31.png','Peaceful city life experience','https://www.youtube.com/watch?v=2iw_K-1AmVk');
insert into guider values (31,'Duong','Nguyen',now(),'976545677651','My name is Shelby Mai, I am 23 year olds. I am really love travelling and making friends with people around the world. With 2 years working as a motobike tour leader and 6 years living in Ho Chi Minh city, I am excited to build my own motobike tour aiming to alleviate your concerns regarding Vietnam and enjoy in the best way.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-32.png','An experience you will never forget','https://www.youtube.com/watch?v=2iw_K-1AmVk');
insert into guider values (32,'Duong','Bui',now(),'976545677651','Anyone who loves healthy food, who wants to open a Vietnamese restaurant in his homeland, or wants to stand out in the professional Asian Kitchen should come to me to discover over 1000 recipes, created by myself and immerse themselves in the Vietnamese culture, agriculture and history. I would love to share my passion for cooking and my Vietnamese gardening skills with people who travel to teach them how to combine food and medicine - eating for a healthy life.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-33.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (33,'Duong','Hoang',now(),'976545677651','Frankly, I am not cut out for 9-5 office work. I prefer being outside, talking to people and socializing (as a typical pisces). Joining hospitality and tourism has made my wish come true. I have been in this industry in Hanoi for 4+ years and (to some extent) understood its potentials and shortcomings. The locals, who have maintained and run invaluable social quintessence, are not receiving what they deserve. I appreciate ''Slow food'' culture and sustainable tourism, in which people can travel with awareness and responsiblity to promote local values. Thats the reason I team up with my friends to create ''Hanoi Nom Nom'' team ,with view of bringing all the positive insights of Hanoi to travellers. We have assisted more than 1000 guests from at least 50 countries and territories.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-34.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (34,'Duong','Dinh',now(),'976545677651','Hi, I am Hung, but you can call me Charlie. Having lived in Berlin Germany for many years, so I love speaking German. I have worked as an freelancer German - speaking tour guide for individual clients as well as large-sized groups.  I love Hanoi City because it is full of history, food to try and people. Would like to be your companion on the Hanoi trip.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-35.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (35,'Duong','Ngo',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-36.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (36,'Duong','Dang',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-37.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (37,'Duong','Tran',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-38.png','An experience you will never forget','https://www.youtube.com/watch?v=avf_e48uKns');
insert into guider values (38,'Duong','Kha',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-39.png','An experience you will never forget','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (39,'Dang','Nguyen',now(),'012345678901','As a local one who loves food, cultures, and meeting people, I see that many tourists are now stuck in touristy area, which doesnt make them "really travel" to Vietnam. So I decided to bring up a team with Vietnamese students, who are passionate in bringing values to tourists, to solve your insight travelling in Vietnam.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-40.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (40,'Dang','Bui',now(),'975645646201','I am a history lover. I study history to revise the old in order to know the new. I am also a nature lover, I can live in a forest or near the sea for a month without wifi if I have a dozen of books in order to read or play some sports like walking, running, swimming, trekking and so on. I am happy to chat with you all everything because I always wish to open my mind.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-41.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (41,'Dang','Hoang',now(),'436985677436','After working abroad for a while, I am back in HCMC. I amm looking to meet new people from other nations, make new friends and talk things over. This gives me a little time to get away from computer, email and other bright flashy things.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-42.png','Peaceful city life experience','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (42,'Dang','Tran',now(),'976545677651','I am international sales guy who loves spending time with my family and playing with my kids. My passions are traveling and martial arts. I love Ho Chi Minh City and I am ready to share it with you! Drop me a message and lets meet!',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-43.png','An experience you will never forget','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (43,'Ngoc','Anh',now(),'976545677651','Having many things in your life make you so tired. Cant eat , can not sleep well. Although delicious food with beautiful perform. Meal and taste is very important to you. From simple things, which cook good food for you and moreover, food will be better with funny or interesting story, making laugh comfortable. your stressful out and nothing for your sleep well for new power day. Cooking by heart and soul is made really good food.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-.png','An experience you will never forget','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (44,'Ngoc','Huyen',now(),'976545677651','I always like new and strange things. I enjoy new experiences and meeting new people. I want to go to as many places as possible. I love making new friends, trying food that I have never tried before and taking pictures of beautiful landscapes.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--2.png','An experience you will never forget','https://www.youtube.com/watch?v=4bFnn3p3Yys');
insert into guider values (45,'Ngoc','Dinh',now(),'976545677651','My name is Anh and I was born and raised in Hanoi, Vietnam. I have been involved in travel and tourism in Vietnam for over 15 years and have worked with clients from over 50 countries from around the world. Throughout my career in the tourism industry, I have worked as an English-speaking tour guide for individual clients as well as large-sized groups of 25 – 30 people.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--3.png','An experience you will never forget','https://www.youtube.com/watch?v=KFWDxtcVXMI');
insert into guider values (46,'Ngoc','Bui',now(),'976545677651','I graduated from Hue University, College of Foreign Languages in 2011, I have been working as English speaking guide for years. I am a friendly guide, passionate and resourceful guide who always try my best to ensure the tour and services provided as per customers requirement. Moreover, I define myself as a traveler who has strong aspiration to discover the beauty of the places and cultures and share them all with my guests. I feel like I am not just a guide but my guest companion, my happiness after each trip is to leave my guests with great smiles and sweet memories about Vietnam country and people',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--4.png','An experience you will never forget','https://www.youtube.com/watch?v=KFWDxtcVXMI');
insert into guider values (47,'Trang','Ha',now(),'976545677651','I was born and raised in Hanoi and have been hanging around with foreign friends, visiting scholars and tourists for 10 years. Working as a Consultant in a Fitness and Yoga Center sparks me ideas about mindfulness and how you can improve your life through stress relief by choosing the right places to relax and enjoy life in Hanoi and over the country.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--5.png','An experience you will never forget','https://www.youtube.com/watch?v=KFWDxtcVXMI');
insert into guider values (48,'Trang','Nguyen',now(),'012345678901','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--6.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=KFWDxtcVXMI');
insert into guider values (49,'Trang','Bui',now(),'975645646201','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--7.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=KFWDxtcVXMI');
insert into guider values (50,'Trang','Tran',now(),'436985677436','I am a culture driven boy who value traditional values yet treasure integration to live in the opening spirit of the Vietnamese economic scene.  I am outgoing, sociable and communicative with a good sense of humor and clarifications.  I am an office boy in advertising with a highly dynamic working schedule, yet would love to spend time with new people to reward myself and to learn ways of thinking and new communications.  I am a food geek who spends time not just to eat, but also to build the experience. And I am a travel lover who by myself good trips of my own just to explore all above mentioned.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--8.png','Peaceful city life experience','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (51,'Uyen','Nguyen',now(),'976545677651','My name is Shelby Mai, I am 23 year olds. I am really love travelling and making friends with people around the world. With 2 years working as a motobike tour leader and 6 years living in Ho Chi Minh city, I am excited to build my own motobike tour aiming to alleviate your concerns regarding Vietnam and enjoy in the best way.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--9.png','An experience you will never forget','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (52,'Uyen','Tran',now(),'976545677651','Anyone who loves healthy food, who wants to open a Vietnamese restaurant in his homeland, or wants to stand out in the professional Asian Kitchen should come to me to discover over 1000 recipes, created by myself and immerse themselves in the Vietnamese culture, agriculture and history. I would love to share my passion for cooking and my Vietnamese gardening skills with people who travel to teach them how to combine food and medicine - eating for a healthy life.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--10.png','An experience you will never forget','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (53,'Uyen','Linh',now(),'976545677651','Frankly, I am not cut out for 9-5 office work. I prefer being outside, talking to people and socializing (as a typical pisces). Joining hospitality and tourism has made my wish come true. I have been in this industry in Hanoi for 4+ years and (to some extent) understood its potentials and shortcomings. The locals, who have maintained and run invaluable social quintessence, are not receiving what they deserve. I appreciate ''Slow food'' culture and sustainable tourism, in which people can travel with awareness and responsiblity to promote local values. Thats the reason I team up with my friends to create ''Hanoi Nom Nom'' team ,with view of bringing all the positive insights of Hanoi to travellers. We have assisted more than 1000 guests from at least 50 countries and territories.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--11.png','An experience you will never forget','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (54,'Uyen','Bui',now(),'976545677651','Hi, I am Hung, but you can call me Charlie. Having lived in Berlin Germany for many years, so I love speaking German. I have worked as an freelancer German - speaking tour guide for individual clients as well as large-sized groups.  I love Hanoi City because it is full of history, food to try and people. Would like to be your companion on the Hanoi trip.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--12.png','An experience you will never forget','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (55,'Uyen','Dinh',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--13.png','An experience you will never forget','https://www.youtube.com/watch?v=nMABH0IBdTw');
insert into guider values (56,'Ha','Nguyen',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--14.png','An experience you will never forget','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (57,'Ha','Lan',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--15.png','An experience you will never forget','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (58,'Ha','Bui',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--16.png','An experience you will never forget','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (59,'Ha','Tran',now(),'012345678901','As a local one who loves food, cultures, and meeting people, I see that many tourists are now stuck in touristy area, which doesnt make them "really travel" to Vietnam. So I decided to bring up a team with Vietnamese students, who are passionate in bringing values to tourists, to solve your insight travelling in Vietnam.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--17.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (60,'Ha','Dinh',now(),'975645646201','I am a history lover. I study history to revise the old in order to know the new. I am also a nature lover, I can live in a forest or near the sea for a month without wifi if I have a dozen of books in order to read or play some sports like walking, running, swimming, trekking and so on. I am happy to chat with you all everything because I always wish to open my mind.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--18.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (61,'Bich','Ngoc',now(),'436985677436','After working abroad for a while, I am back in HCMC. I amm looking to meet new people from other nations, make new friends and talk things over. This gives me a little time to get away from computer, email and other bright flashy things.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--19.png','Peaceful city life experience','https://www.youtube.com/watch?v=urGfdfH3h4w');
insert into guider values (62,'Bich','Tran',now(),'976545677651','I am international sales guy who loves spending time with my family and playing with my kids. My passions are traveling and martial arts. I love Ho Chi Minh City and I am ready to share it with you! Drop me a message and lets meet!',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--20.png','An experience you will never forget','https://www.youtube.com/watch?v=BaX2f1yjLjY');
insert into guider values (63,'Bich','Ngo',now(),'976545677651','Having many things in your life make you so tired. Cant eat , can not sleep well. Although delicious food with beautiful perform. Meal and taste is very important to you. From simple things, which cook good food for you and moreover, food will be better with funny or interesting story, making laugh comfortable. your stressful out and nothing for your sleep well for new power day. Cooking by heart and soul is made really good food.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--21.png','An experience you will never forget','https://www.youtube.com/watch?v=BaX2f1yjLjY');
insert into guider values (64,'Bich','Ha',now(),'976545677651','I always like new and strange things. I enjoy new experiences and meeting new people. I want to go to as many places as possible. I love making new friends, trying food that I have never tried before and taking pictures of beautiful landscapes.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--22.png','An experience you will never forget','https://www.youtube.com/watch?v=BaX2f1yjLjY');
insert into guider values (65,'Bich','Bui',now(),'976545677651','My name is Anh and I was born and raised in Hanoi, Vietnam. I have been involved in travel and tourism in Vietnam for over 15 years and have worked with clients from over 50 countries from around the world. Throughout my career in the tourism industry, I have worked as an English-speaking tour guide for individual clients as well as large-sized groups of 25 – 30 people.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--23.png','An experience you will never forget','https://www.youtube.com/watch?v=BaX2f1yjLjY');
insert into guider values (66,'Linh','Nguyen',now(),'976545677651','I graduated from Hue University, College of Foreign Languages in 2011, I have been working as English speaking guide for years. I am a friendly guide, passionate and resourceful guide who always try my best to ensure the tour and services provided as per customers requirement. Moreover, I define myself as a traveler who has strong aspiration to discover the beauty of the places and cultures and share them all with my guests. I feel like I am not just a guide but my guest companion, my happiness after each trip is to leave my guests with great smiles and sweet memories about Vietnam country and people',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--24.png','An experience you will never forget','https://www.youtube.com/watch?v=BaX2f1yjLjY');
insert into guider values (67,'Linh','Tran',now(),'976545677651','I was born and raised in Hanoi and have been hanging around with foreign friends, visiting scholars and tourists for 10 years. Working as a Consultant in a Fitness and Yoga Center sparks me ideas about mindfulness and how you can improve your life through stress relief by choosing the right places to relax and enjoy life in Hanoi and over the country.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--25.png','An experience you will never forget','https://www.youtube.com/watch?v=jGjuY5bJsZM');
insert into guider values (68,'Linh','Anh',now(),'012345678901','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',1000,'Hanoi','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--26.png','Enjoy other people company and having fun','https://www.youtube.com/watch?v=jGjuY5bJsZM');
insert into guider values (69,'Linh','Bui',now(),'975645646201','Hello, my name is Vi. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I am born and raised in Vietnam and I love to show people my country. I am just starting my tour company but I have already given many successful tours in Ho Chi Minh and the greater countryside. I would be happy to provide you with references of people that have used my service. I am very honest, hard-working and will do my best to provide you a high value service. I am very honest, hard-working and will do my best to provide you a high value service.',100,'Da Nang','{vi,en}',true,3,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--27.png','Eating til your belly cant anymore','https://www.youtube.com/watch?v=jGjuY5bJsZM');
insert into guider values (70,'Linh','Khanh',now(),'436985677436','I am a culture driven boy who value traditional values yet treasure integration to live in the opening spirit of the Vietnamese economic scene.  I am outgoing, sociable and communicative with a good sense of humor and clarifications.  I am an office boy in advertising with a highly dynamic working schedule, yet would love to spend time with new people to reward myself and to learn ways of thinking and new communications.  I am a food geek who spends time not just to eat, but also to build the experience. And I am a travel lover who by myself good trips of my own just to explore all above mentioned.',2500,'Vinh','{vi,en}',true,5,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--28.png','Peaceful city life experience','https://www.youtube.com/watch?v=jGjuY5bJsZM');
insert into guider values (71,'Minh','Ngoc',now(),'976545677651','My name is Shelby Mai, I am 23 year olds. I am really love travelling and making friends with people around the world. With 2 years working as a motobike tour leader and 6 years living in Ho Chi Minh city, I am excited to build my own motobike tour aiming to alleviate your concerns regarding Vietnam and enjoy in the best way.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--29.png','An experience you will never forget','https://www.youtube.com/watch?v=jGjuY5bJsZM');
insert into guider values (72,'Minh','Nguyen',now(),'976545677651','Anyone who loves healthy food, who wants to open a Vietnamese restaurant in his homeland, or wants to stand out in the professional Asian Kitchen should come to me to discover over 1000 recipes, created by myself and immerse themselves in the Vietnamese culture, agriculture and history. I would love to share my passion for cooking and my Vietnamese gardening skills with people who travel to teach them how to combine food and medicine - eating for a healthy life.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--30.png','An experience you will never forget','https://www.youtube.com/watch?v=wJwehMRq634');
insert into guider values (73,'Minh','Tran',now(),'976545677651','Frankly, I am not cut out for 9-5 office work. I prefer being outside, talking to people and socializing (as a typical pisces). Joining hospitality and tourism has made my wish come true. I have been in this industry in Hanoi for 4+ years and (to some extent) understood its potentials and shortcomings. The locals, who have maintained and run invaluable social quintessence, are not receiving what they deserve. I appreciate ''Slow food'' culture and sustainable tourism, in which people can travel with awareness and responsiblity to promote local values. Thats the reason I team up with my friends to create ''Hanoi Nom Nom'' team ,with view of bringing all the positive insights of Hanoi to travellers. We have assisted more than 1000 guests from at least 50 countries and territories.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--31.png','An experience you will never forget','https://www.youtube.com/watch?v=wJwehMRq634');
insert into guider values (74,'Khanh','Le',now(),'976545677651','Hi, I am Hung, but you can call me Charlie. Having lived in Berlin Germany for many years, so I love speaking German. I have worked as an freelancer German - speaking tour guide for individual clients as well as large-sized groups.  I love Hanoi City because it is full of history, food to try and people. Would like to be your companion on the Hanoi trip.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--32.png','An experience you will never forget','https://www.youtube.com/watch?v=wJwehMRq634');
insert into guider values (75,'Anh','Ngoc',now(),'976545677651','My life begins at the end of my comfort zone. When I was no longer under my familys protection, I started to learn how to do everything myself. The first steps was really hard. But I went through it and now looking back to the past, it really liked a miracle. If you are interested in hearing about it, I would love to tell you when we have a chance to meet.',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--33.png','An experience you will never forget','https://www.youtube.com/watch?v=wJwehMRq634');
insert into guider values (76,'Anh','Duc',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-45.png','An experience you will never forget','https://www.youtube.com/watch?v=wJwehMRq634');
insert into guider values (77,'Anh','Phan',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-43.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');
insert into guider values (78,'Oanh','Nguyen',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--34.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');
insert into guider values (79,'Oanh','Bui',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--35.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');
insert into guider values (80,'Jenifer','Pham',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--36.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');
insert into guider values (81,'Jenifer','Nguyen',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic--37.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');
insert into guider values (82,'Khanh','Nguyen',now(),'976545677651','Me ? cute, kind, super friendly and always willing to hepl you guys. Iam a foodaholic, kind of wanderlust always looking for fun. i promise ( with all my heart ) when you guys be with me, will never feel boring. Life is too short to bored, right ? Iam trying to improve my English skill so if i have any mistake please forgive me. I really really wanna be your tour guide, your friend in Vietnam so feel free when you contact me, ok ?',1300,'Hanoi','{vi,en}',true,4,'https://res.cloudinary.com/findguider/image/upload/profile_picture/profilepic-44.png','An experience you will never forget','https://www.youtube.com/watch?v=kHJJ8EPosa4');

insert into contract_detail values (1,1,'Dung Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (2,2,'Dung Bui','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (3,3,'Dung Anh','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (4,4,'Dung Dang','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (5,5,'Dung Justin','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (6,6,'Dung Dao','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (7,7,'Dung Tran','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (8,8,'Dung Dinh','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (9,9,'Dung Hoang','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (10,10,'Hai Nguyen','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (11,11,'Hai Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (12,12,'Hai Hoang','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (13,13,'Hai Justin','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (14,14,'Hai Dinh','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (15,15,'Hai Dao','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (16,16,'Hai Tran','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (17,17,'Hai Anh','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (18,18,'Hai Ha','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (19,19,'Hai Dung','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (20,20,'Hai Ngo','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (21,21,'Long Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (22,22,'Long Dang','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (23,23,'Long Tran','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (24,24,'Long Hoang','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (25,25,'Long Dinh','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (26,26,'Long Trong','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (27,27,'Long Ngo','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (28,28,'Long Hai','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (29,29,'Long Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (30,30,'Long Khang','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (31,31,'Duong Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (32,32,'Duong Bui','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (33,33,'Duong Hoang','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (34,34,'Duong Dinh','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (35,35,'Duong Ngo','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (36,36,'Duong Dang','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (37,37,'Duong Tran','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (38,38,'Duong Kha','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (39,39,'Dang Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (40,40,'Dang Bui','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (41,41,'Dang Hoang','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (42,42,'Dang Tran','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (43,43,'Ngoc Anh','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (44,44,'Ngoc Huyen','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (45,45,'Ngoc Dinh','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (46,46,'Ngoc Bui','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (47,47,'Trang Ha','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (48,48,'Trang Nguyen','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (49,49,'Trang Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (50,50,'Trang Tran','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (51,51,'Uyen Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (52,52,'Uyen Tran','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (53,53,'Uyen Linh','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (54,54,'Uyen Bui','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (55,55,'Uyen Dinh','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (56,56,'Ha Nguyen','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (57,57,'Ha Lan','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (58,58,'Ha Bui','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (59,59,'Ha Tran','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (60,60,'Ha Dinh','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (61,61,'Bich Ngoc','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (62,62,'Bich Tran','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (63,63,'Bich Ngo','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (64,64,'Bich Ha','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (65,65,'Bich Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (66,66,'Linh Nguyen','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (67,67,'Linh Tran','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (68,68,'Linh Anh','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (69,69,'Linh Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (70,70,'Linh Khanh','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (71,71,'Minh Ngoc','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (72,72,'Minh Nguyen','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (73,73,'Minh Tran','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (74,74,'Khanh Le','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (75,75,'Anh Ngoc','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (76,76,'Anh Duc','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (77,77,'Anh Phan','Vietnamese','1992-12-13',2,'Ha Noi','156 Ly Nam De Street Vinh','C2C36479875319','2010-09-13','Hanoi','2014-11-22',null);
insert into contract_detail values (78,78,'Oanh Nguyen','Vietnamese','1985-10-01',0,'Hanoi','65 Lang Ha Street Ha Noi','1B5EB479847632','2018-11-06','Hanoi','2017-09-30',null);
insert into contract_detail values (79,79,'Oanh Bui','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (80,80,'Jenifer Pham','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
insert into contract_detail values (81,81,'Jenifer Nguyen','Vietnamese','1995-05-18',1,'Ha Noi','12 Tran Hung Dao Street Ha Noi','A1936479889461','2001-04-23','Hanoi','2010-11-02',null);
insert into contract_detail values (82,82,'Khanh Nguyen','Vietnamese','1993-04-11',1,'Ha Noi','161 Ba Trung Street Da Nang','BB93746389982','2002-01-15','Hanoi','2016-05-13',null);
update contract_detail set account_deactive_date = null;
update contract_detail set file_link = 'Certificate.pdf';

insert into contract values (1,1);
insert into contract values (2,2);
insert into contract values (3,3);
insert into contract values (4,4);
insert into contract values (5,5);
insert into contract values (6,6);
insert into contract values (7,7);
insert into contract values (8,8);
insert into contract values (9,9);
insert into contract values (10,10);
insert into contract values (11,11);
insert into contract values (12,12);
insert into contract values (13,13);
insert into contract values (14,14);
insert into contract values (15,15);
insert into contract values (16,16);
insert into contract values (17,17);
insert into contract values (18,18);
insert into contract values (19,19);
insert into contract values (20,20);
insert into contract values (21,21);
insert into contract values (22,22);
insert into contract values (23,23);
insert into contract values (24,24);
insert into contract values (25,25);
insert into contract values (26,26);
insert into contract values (27,27);
insert into contract values (28,28);
insert into contract values (29,29);
insert into contract values (30,30);
insert into contract values (31,31);
insert into contract values (32,32);
insert into contract values (33,33);
insert into contract values (34,34);
insert into contract values (35,35);
insert into contract values (36,36);
insert into contract values (37,37);
insert into contract values (38,38);
insert into contract values (39,39);
insert into contract values (40,40);
insert into contract values (41,41);
insert into contract values (42,42);
insert into contract values (43,43);
insert into contract values (44,44);
insert into contract values (45,45);
insert into contract values (46,46);
insert into contract values (47,47);
insert into contract values (48,48);
insert into contract values (49,49);
insert into contract values (50,50);
insert into contract values (51,51);
insert into contract values (52,52);
insert into contract values (53,53);
insert into contract values (54,54);
insert into contract values (55,55);
insert into contract values (56,56);
insert into contract values (57,57);
insert into contract values (58,58);
insert into contract values (59,59);
insert into contract values (60,60);
insert into contract values (61,61);
insert into contract values (62,62);
insert into contract values (63,63);
insert into contract values (64,64);
insert into contract values (65,65);
insert into contract values (66,66);
insert into contract values (67,67);
insert into contract values (68,68);
insert into contract values (69,69);
insert into contract values (70,70);
insert into contract values (71,71);
insert into contract values (72,72);
insert into contract values (73,73);
insert into contract values (74,74);
insert into contract values (75,75);
insert into contract values (76,76);
insert into contract values (77,77);
insert into contract values (78,78);
insert into contract values (79,79);
insert into contract values (80,80);
insert into contract values (81,81);
insert into contract values (82,82);

insert into traveler values (100,'Jill','Langley','651651616561',2,'1996-04-23','St Peter','45','12000','Travel is life','I like to travel around the world','{en,vi}','England','London','https://res.cloudinary.com/findguider/image/upload/Jill.jpg');
insert into traveler values (101,'Kevin','Gorine','476351616693',1,'1993-06-19','William','143','20000','Eat while you can','Im love eating delicious food','{en}','Vietnam','Ho Chi Minh','https://res.cloudinary.com/findguider/image/upload/Kevin.jpg');
insert into traveler values (102,'Steve','Mark','158961614365',1,'1994-10-06','Wall','03','30000','Me around the world','What more a man need beside peaceful relaxation','{en}','China','Shanghai','https://res.cloudinary.com/findguider/image/upload/Steve.jpg');

insert into travelerreviews values (1,100,1,'2018-10-25T13:50:06','She is a very funny person',true);
insert into travelerreviews values (2,100,2,'2018-06-24T06:13:20','Very chill and relax',true);
insert into travelerreviews values (3,100,2,'2018-06-24T06:13:20','Greatest company to go around with',true);
insert into travelerreviews values (4,101,3,'2018-10-25T13:50:06','Great guy',true);
insert into travelerreviews values (5,101,4,'2018-06-24T06:13:20','Quite a gluton',true);
insert into travelerreviews values (6,101,2,'2018-06-24T06:13:20','5 out of 5',true);
insert into travelerreviews values (7,102,3,'2018-10-25T13:50:06','Looking forward to travel with him again',true);
insert into travelerreviews values (8,102,1,'2018-06-24T06:13:20','He is quite knowledgeable, recommend taking him to rich culture places',true);
insert into travelerreviews values (9,102,4,'2018-06-24T06:13:20','Fun to travel with',true);

insert into category values (1,'Art','https://res.cloudinary.com/findguider/image/upload/Art.jpg');
insert into category values (2,'History','https://res.cloudinary.com/findguider/image/upload/History.jpg');
insert into category values (3,'Culture','https://res.cloudinary.com/findguider/image/upload/Culture.jpg');
insert into category values (4,'Food','https://res.cloudinary.com/findguider/image/upload/Food.jpg');
insert into category values (5,'Lake','https://res.cloudinary.com/findguider/image/upload/Lake.jpg');
insert into category values (6,'Bike','https://res.cloudinary.com/findguider/image/upload/Bike.jpg');

insert into locations values (1,'Vietnam','Hanoi','Art Museum');
insert into locations values (2,'Vietnam','Hanoi','Long Bien Bridge');
insert into locations values (3,'Vietnam','Hanoi','Vietnamese Museum of Ethnology');
insert into locations values (4,'Vietnam','Hanoi','Dong Xuan Market');
insert into locations values (5,'Vietnam','Hanoi','Ho Chi Minh Mausolium');
insert into locations values (6,'Vietnam','Hanoi','Trang Tien Plaza');
insert into locations values (7,'Vietnam','Hanoi','Hoan Kiem Lake');
insert into locations values (8,'Vietnam','Hanoi','West Lake');
insert into locations values (9,'Vietnam','Hanoi','Ha Noi');
insert into locations values (10,'Vietnam','Hanoi','Hanoi Old Quarters');
insert into locations values (11,'Vietnam','Hochiminh','Ho Chi Minh');
insert into locations values (12,'Vietnam','Hochiminh','District 1');
insert into locations values (13,'Vietnam','Hochiminh','Bui Vien street');
insert into locations values (14,'Vietnam','Danang','Da Nang');
insert into locations values (15,'Vietnam','Danang','Hoi An');
insert into locations values (16,'Vietnam','Danang','My Khe Beach');
insert into locations values (17,'Vietnam','Danang','Ba Na Hills');
insert into locations values (18,'Vietnam','Halong','Ha Long');
insert into locations values (19,'Vietnam','Hue','Hue');
insert into locations values (20,'Vietnam','Sapa','Sapa');
insert into locations values (21,'Vietnam','Quangbinh','Quang Binh');
insert into locations values (22,'Vietnam','Thanhhoa','Sam Son Beach');
insert into locations values (23,'Vietnam','Thanhhoa','Thanh Hoa');
insert into locations values (24,'Vietnam','Ninhbinh','Ninh Binh');
insert into locations values (25,'Vietnam','Ninhbinh','Mua cave');
insert into locations values (26,'Vietnam','Ninhbinh','Trang An');
insert into locations values (27,'Vietnam','Nhatrang','Nha Trang');
insert into locations values (28,'Vietnam','Dalat','Da lat');
insert into locations values (29,'Vietnam','Phuquoc','Phu Quoc');
insert into locations values (30,'Vietnam','Quangninh','Quang Ninh');

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (1, 1, 9, 3, 'Hanoi Half Day Tour - 4 Hours Exloring Hanoi', 'https://www.youtube.com/watch?v=z0Z4pC3fJgI', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-14.jpg}', 4,'Hanoi, former name is Thang Long, a thousand years old charming and fascinating royal capital. A city blends both Western and Eastern architecture styles, and mixture of ancient and modern air.',
			'{Air-conditioned vehicle,All Fees and Taxes,Bottled water}', 
			40, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Mausoleum</h4><p>The first attraction is Ho Chi Minh Mausoleum (viewed from outside) where honors the Greatest President of Vietnam, Mr Ho Chi Minh and his former residence (closed on Monday and Friday).</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>One Pillar Pagoda</h4><p>Next, we continue to visit One-Pillar Pagoda and listen a special story related to Ly Thai Tong King hidden inside this attraction.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Temple of Literature & National University</h4><p>Then, moving Temple of Literature, known as the oldest university of Vietnam</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Old Quarter</h4><p>The tour also offers you 1-hour traveling around Hanoi Old Quarter and Hoan Kiem Lake by traditional cyclo/ rickshaw or Electrical car. It will give you the best chance to get acquainted with the old streets and the life of Hanoi people, discovering Old Quarter will satisfy your love of shopping with range of authentic products at fair prices.</p></div></div>', 1);


INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (2, 1, 9, 1, 'Hanoi Photo tour - Hanoi Motorbike Tours', 'https://www.youtube.com/watch?v=-Co0SeVyHg0', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-5.jpg}', 5,'Get the best possible photos of Hanois must-see landmarks on this photography-focused tour. With a guide driving you around on a motorbike, you will feel like a local zipping through the streets. Learn plenty of tips and tricks to make your photos look the best; you will be sure to take home many frame-worthy shots.',
			'{Local guide,Tour escort/host,Hotel pickup,Hotel drop-off,Fuel surcharge,Landing and facility fees}', 
			57, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Convenient hotel pickup and drop-off.</p></li><li><i class="fa fa-check"></i><p>Snap the best possible photos of Hanoi.</p></li><li><i class="fa fa-check"></i><p>Photographer guide takes you to the most scenic places.</p></li><li><i class="fa fa-check"></i><p>Comprehensive tour of Hanoi via motorbike.</p></li></ul></div>  ');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Mausoleum</h4><p>The first attraction is Ho Chi Minh Mausoleum (viewed from outside) where honors the Greatest President of Vietnam, Mr Ho Chi Minh and his former residence (closed on Monday and Friday).</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>HIGHLIGHTS OF HANOI PHOTO TOUR</h4><p>Hanoi Photo Tour’s program will depend on the weather of the day you book so we can organize the perfect places. When you book this tour we will send you the detailed program. We will make sure that you can take the best photos of not only the best landscapes in Hanoi but also the daily life of local people here.</p></div></div>', 2);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (3, 1, 9, 4, 'Hanoi Foodie Tour', 'https://www.youtube.com/watch?v=FEf_sKH6jl4', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-10.jpg}', 3,'In 3 hour walking tour, our guest will get to know culture and people of Hanoi, try the most delicious local food, visit famous attractions such as Dong Xuan market, st Joseph Church, Hoan Kiem Lake,..... We also visit local market and do some shopping for fruits and gifts. During the tour, our guest will learn how to make Vietnamese food and meet a lot of local Vietnamese people and have so much fun.',
			'{A free bottle of water,All food and drinks,Free pick-up at your hotel in Hoan Kiem,English speaking tour guide,Lunch,Dinner,Bottled water,Coffee and/or Tea}', 
			25, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Hoan Kiem Lake Walking Street</h4><p>learn stories about Vietnamese history under Le Dynasty and Ngoc Son temple	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Dong Xuan Market</h4><p>Learn about history of Dong Xuan Market	</p></div></div>', 3);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (4, 1, 9, 6, 'Hanoi Greatest Hits', 'https://www.youtube.com/watch?v=rwznk7W4G84', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-18.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-12.jpg}', 2,'Hanoi, former name is Thang Long, a thousand years old charming and fascinating royal capital. A city blends both Western and Eastern architecture styles, and mixture of ancient and modern air.',
			'{Air-conditioned vehicle,All Fees and Taxes,Bottled water}', 
			64, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Driver through Hanoi Old Quarter to St Joseph’s Cathedralm</h4><p>Lovely charming old church in the middle of a bustling city. People said it is a mini replica of Notre Dame de Paris.	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you around the Hoan Kiem Lake</h4><p>The heart of Hanoi, there are always something going on around the lake such as exercising, dancing people, artists or students working on assignment, you can feel the peaceful of the city by being at the lake.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you to Long Bien bridge</h4><p>The first steel bridge ever made in Vietnam, built in the late 1890s - early 1900s, the bridge is a big part of Hanoi history. If you love art and take photos here is the ideal place.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Old Quarter</h4><p>The tour also offers you 1-hour traveling around Hanoi Old Quarter and Hoan Kiem Lake by traditional cyclo/ rickshaw or Electrical car. It will give you the best chance to get acquainted with the old streets and the life of Hanoi people, discovering Old Quarter will satisfy your love of shopping with range of authentic products at fair prices.</p></div></div>', 4);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (5, 2, 9, 6, 'Hanoi Hideaway', 'https://www.youtube.com/watch?v=KfsV2GDlGV8', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-17.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-11.jpg}', 4,'The ideal way to really enjoy the charm of the capital. Discover the knowledge and experience of our driver and guide during this tour. We provide 3 different plans to discover deeply Hanoi.',
			'{Experienced Certified English speaking tour Guide (other languages upon request),Sidecar Certified Driver, Standard Vintage Sidecar Ural, Helmet(s) and Rain-poncho, Entrance Fees, Free pick-up and drop-off within Hanoi Old Quarter}', 
			69, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Driver through Hanoi Old Quarter to St Joseph’s Cathedralm</h4><p>Lovely charming old church in the middle of a bustling city. People said it is a mini replica of Notre Dame de Paris.	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you around the Hoan Kiem Lake</h4><p>The heart of Hanoi, there are always something going on around the lake such as exercising, dancing people, artists or students working on assignment, you can feel the peaceful of the city by being at the lake.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you to Long Bien bridge</h4><p>The first steel bridge ever made in Vietnam, built in the late 1890s - early 1900s, the bridge is a big part of Hanoi history. If you love art and take photos here is the ideal place.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Old Quarter</h4><p>The tour also offers you 1-hour traveling around Hanoi Old Quarter and Hoan Kiem Lake by traditional cyclo/ rickshaw or Electrical car. It will give you the best chance to get acquainted with the old streets and the life of Hanoi people, discovering Old Quarter will satisfy your love of shopping with range of authentic products at fair prices.</p></div></div>', 5);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (6, 2, 9, 6, 'Cycling in Hanoi', 'https://www.youtube.com/watch?v=GHeK6GUin2s', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-15.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-16.jpg}', 10,'Embark on a full-day bike tour of Hanoi, following an informative guide through labyrinthine streets in the Vietnamese capital. Get a glimpse of its contemporary culture and ancient history with a brief walk through the Old Quarter and across Long Bien Bridge. Then pedal across the Red River and along the shoreline of West Lake, where you will enjoy lunch at an open-air restaurant. Visit Tran Quoc Pagoda and the Temple of Literature, and view the Ho Chi Minh Mausoleum among other notable landmarks along the way.',
			'{Meals as specified in the program and coffee breaks through out the day,Mountain bike and safety gear,The services of an experienced English-speaking guide}', 
			90, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Dong Xuan Market</h4><p>The oldest marlet in Hanoi.	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Mausoleum</h4><p>The first attraction is Ho Chi Minh Mausoleum (viewed from outside) where honors the Greatest President of Vietnam, Mr Ho Chi Minh and his former residence (closed on Monday and Friday).</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Temple of Literature & National University</h4><p>Then, moving Temple of Literature, known as the oldest university of Vietnam</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Old Quarter</h4><p>The tour also offers you 1-hour traveling around Hanoi Old Quarter and Hoan Kiem Lake by traditional cyclo/ rickshaw or Electrical car. It will give you the best chance to get acquainted with the old streets and the life of Hanoi people, discovering Old Quarter will satisfy your love of shopping with range of authentic products at fair prices.</p></div></div>', 6);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (7, 2, 30, 5, 'Full Day Ha Long bay with kayaking', 'https://www.youtube.com/watch?v=wYOfS5fuVts', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-2.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-8.jpg}', 4,'If you don’t have time for one of the popular and prominent overnight cruises in Ha Long Bay, don’t fret – you can still enjoy the sights and sounds of this stunning coastal area on this full day tour from Hanoi. Spend your afternoon cruising around this UNESCO World Heritage Site on a spacious junk with your tour group. Visit the otherworldly multicoloured caverns of Thien Cung Cave, and enjoy free time to kayak or sunbathe while anchored at a floating fishing village.',
			'{Lunch,Bottled water,All Fees and Taxes,English speaking guide,Hotel pickup and drop-off (if option selected),Air-conditioned vehicle}', 
			40, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Halong Bay</h4><p>Visit Halong bay - the natural world heritage. The most beautiful place of Vietnam Visit The majestic cave</p></div></div>', 7);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (8, 2, 30, 3, 'Ha Long Bay Discovery One Day Cruise', 'https://www.youtube.com/watch?v=SpOWgms2uto', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-13.jpg}', 12,'Explore the highlights of HaLong Bay in a single day on this tour from Hanoi. With a guide in the lead, you’ll follow a carefully curated itinerary designed to ensure you don’t miss a thing. After convenient pickup in Hanoi, transfer to HaLong Bay where you’ll board a boat bound for the area’s top sights such as Duck,Dog, and Thumb islets as well as the Hoa Cuong fishing village. Lunch is served on board the boat and you can opt for a bamboo boat ride or kayak excursion along the limestone cliffs.',
			'{Fast Expressway 2 way/ A/C tourist car ,& 0.5l bottle water per person/day.,Boat trip in Halong Bay English speaking guide during the trip,Bamboo boat or Kayaking (optional),Free pick up and see off in Hanoi Old Quarter area and Hoan Kiem district,All entrance fee,Set menu seafood lunch,English speaking guide.}', 
			35, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Vietnam Open Tour</h4><p>Transported by model AC bus Boat trip in Halong Bay Hotel pickup (pick up and drop off at hotels in Hanoi Old Quarter area, Hoan Kiem district only. otherwise kindly go to our office at No 9 Hang Huong at 7:45am or contact our hotline after making reservation for best arrangement) 0.5L bottled water on bus per person per day Bamboo boat or kayaking (you can choose 1 of 2 activities) Driver/guide Local, English-speaking guide Seafood lunch (Vegetarian food available on requested) Entry/Admission - Halong Bay</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4> Halong Bay</h4><p>Halong Bay	</p></div></div>', 8);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (9, 3, 30, 3, 'Private Ha Long bay day trip', 'https://www.youtube.com/watch?v=W11uEiVNjOA', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-7.jpg}', 4,'If you do not have long time in Ha Noi, and you want to have a glint of breathtaking Halong Bay...Our private day tour would be the best choice for you. For this trip, you will have a chance to know about the floating village, discover their daily life and visit a beautiful cave on Halong Bay. If you want to do kayaking, it is also available on this tour. We depart daily from Hanoi or Halong Bay, Tuan Chau tourism Harbor.',
			'{1 bottle of Mineral water,Entrance and Sightseeing Fees.,Cruise to visiting Ha Long bay}', 
			40, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Halong Bay</h4><p>Boat cruising on the romantic bay. - Seafood lunch on the cruise. - Visiting one of the most beautiful cave in Halong named Heaven palace grotto. - Doing sampan boat at the floating village and visiting water caves. - Round trip with the deluxe Van/ Car.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Dong Thien Cung</h4><p>In the morning tourist guide and driver pick you up from your hotel at about 8.00 – 8.30 Bus drive 170 km to the Northeast of Viet Nam ( 3,5 hrs ). Along the way you can enjoy the stunning scenery of the Red River delta with peaceful villages, farmers working in their green fields, herding their water buffalos, flocking their ducks, trading garden herbs by the side of the road.</p></div></div>', 9);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (10, 3, 30, 3, 'Discover Ha Long Bay with Kayaking and Caving', 'https://www.youtube.com/watch?v=wYOfS5fuVts', '{https://res.cloudinary.com/findguider/image/upload/Dung1/dung-6.jpg,https://res.cloudinary.com/findguider/image/upload/Dung1/dung-8.jpg}', 8,'If you do not have long time in Ha Noi, and you want to have a glint of breathtaking Halong Bay...Our private day tour would be the best choice for you. For this trip, you will have a chance to know about the floating village, discover their daily life and visit a beautiful cave on Halong Bay. If you want to do kayaking, it is also available on this tour. We depart daily from Hanoi or Halong Bay, Tuan Chau tourism Harbor.',
			'{Lunch,Air-conditioned vehicle,Kayaking/bamboo boat ride,Entrance fees,English-speaking guide,Bottled water}', 
			37, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Hanoi Opera House</h4><p>Meet up at Hanoi Opera House at 8:20 am</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Tuần Châu Harbor</h4><p>12:00 pm, after 3 1/2 hours of drive, arrive at Tuan Chau Harbor and get aboard</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Halong Bay</h4><p>Have lunch while cruising further to the bay, through many islands and islets</p></div></div>', 10);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (11, 3, 29, 3, 'Phu Quoc Southern Island day tour', 'https://www.youtube.com/watch?v=5uQ2b6p8ZL8', '{https://res.cloudinary.com/findguider/image/upload/PhuQuoc/PhuQuoc-3.jpg,https://res.cloudinary.com/findguider/image/upload/v1577037512/PhuQuoc/PhuQuoc.jpg}', 8,'Welcome to Phu Quoc Southern Island! With our Phu Quoc Southern Island day tour, you will have a wonderful day on Phu Quoc island by an interesting itinerary Sao beach, Ham Ninh Fishing Village, Tranh Stream, and have chance to enjoy sim wine - specialty of Phu Quoc.',
			'{Entrance fees,Mineral water,English speaking guide,Local lunch,A/C Roundtrip transfer}', 
			40, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Phu Quoc Prison</h4><p>Our guide and driver will pick you from your hotel lobby in Duong Dong town (Surcharge for far pick up) Visit local Sim Winery and sample some wines, it known to ferment by natural ingredient. On way a stopover a guided tour of the island pearl harvesting and process and the fine design jewelry.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Sao Beach</h4><p>Lunch time We will relax and enjoy lunch at local restaurant on Bai Sao Beach, which is well know and beloved beach with white sandy and inviting turquoise water a quick swim is a must. We will drive along the scene coastline to visit one of the one of largest Pagoda built above between the sea and mountain, and it is a peaceful surroundings</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ham Ninh Pier</h4><p>Continue on to visit Ham Ninh Fishing Village famous for the fresh seafood, shellfish and shallow water and observe the daily of the fishermen</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Tranh Streamr</h4><p>On our way back we will visit Tranh Stream where we can enjoy a walking tour through the waterfall (NOTES:Tranh Stream is only available during the month of May to Oct) 15:30 pm Transfer back to your hotel.End of Tour</p></div></div>', 11);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (12,3, 9, 6, 'Tour de Ha Noi', 'https://www.youtube.com/watch?v=GHeK6GUin2s', '{https://res.cloudinary.com/findguider/image/upload/Cycling/cycling-3.jpg,https://res.cloudinary.com/findguider/image/upload/Cycling/cycling.jpg}', 2,'Hanoi, former name is Thang Long, a thousand years old charming and fascinating royal capital. A city blends both Western and Eastern architecture styles, and mixture of ancient and modern air.',
			'{Air-conditioned vehicle,All Fees and Taxes,Bottled water}', 
			64, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Driver through Hanoi Old Quarter to St Joseph’s Cathedralm</h4><p>Lovely charming old church in the middle of a bustling city. People said it is a mini replica of Notre Dame de Paris.	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you around the Hoan Kiem Lake</h4><p>The heart of Hanoi, there are always something going on around the lake such as exercising, dancing people, artists or students working on assignment, you can feel the peaceful of the city by being at the lake.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Drive you to Long Bien bridge</h4><p>The first steel bridge ever made in Vietnam, built in the late 1890s - early 1900s, the bridge is a big part of Hanoi history. If you love art and take photos here is the ideal place.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Old Quarter</h4><p>The tour also offers you 1-hour traveling around Hanoi Old Quarter and Hoan Kiem Lake by traditional cyclo/ rickshaw or Electrical car. It will give you the best chance to get acquainted with the old streets and the life of Hanoi people, discovering Old Quarter will satisfy your love of shopping with range of authentic products at fair prices.</p></div></div>', 12);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (13,4,9,1, 'Art Exibition', 'https://www.youtube.com/watch?v=vCcrKZS5Vq4', '{https://res.cloudinary.com/findguider/image/upload/ART/art.jpg,https://res.cloudinary.com/findguider/image/upload/bao tang my thuat.jpg}', 3,'Vietnamese art is visual art that, whether ancient or modern, originated in or is practiced in Vietnam or by Vietnamese artists.Vietnamese art has a long and rich history, the earliest examples of which date back as far as the Stone Age around 8,000 BCE. With the millennium of Chinese domination starting in the 2nd century BC, Vietnamese art undoubtedly absorbed many Chinese influences, which would continue even following independence from China in the 10th century AD. However, Vietnamese art has always retained many distinctively Vietnamese characteristics.',
			'{Air-conditioned vehicle,All Fees and Taxes,Bottled water}', 
			25, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');
INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Mausoleum</h4><p>The first attraction is Ho Chi Minh Mausoleum (viewed from outside) where honors the Greatest President of Vietnam, Mr Ho Chi Minh and his former residence (closed on Monday and Friday).</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Vincom contemporary art</h4><p>Next, we continue to visit Vincom contemporary art and introduce to you some collection of art here</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ha Noi museum</h4><p>Finally, we land up at Hanoi museum-the most largest museum in the city</p></div></div>', 13);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (14, 4, 9, 1, 'Ninh Binh, Hoa Lu,Tam Coc, Mua Cave Day Tour', 'https://www.youtube.com/watch?v=Klw8NNulZMk', '{https://res.cloudinary.com/findguider/image/upload/NinhBinh/ninhbinh-4.jpg,https://res.cloudinary.com/findguider/image/upload/NinhBinh/ninhbinh-3.jpg}', 4,'See some of the natural wonders of Vietnam’s scenic Ninh Binh Province without blowing out your tight travel schedule with this day-trip from Hanoi. Drive from Hanoi to Ninh Binh in a luxurious limousine bus – no cramped seats and stuffy conditions for this long drive. Discover the ancient capital of Hoa Lu, cycle around local villages and sail down the Tam Coc valley, taking in vibrant rice terraces and towering mountains along the way.',
			'{Limousine Bus with experienced driver,English Speaking Guide,Free Wifi & Water in the bus,Lunch with Vietnamese cuisine,Cycling activity, Entrance fee,Free usage of hat & umbrellar}', 
			40, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Hanoi Opera House', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Hoa Lu temples of the Dinh & Le Dynasties</h4><p>Visit Hoa Lu ancient capital of Vietnam between 968 and 1010. You have a chance to learn about Vietnamese history in the feudal system under Dinh, Le and Ly Dynasty.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Tam Coc</h4><p>Have buffet lunch in the restaurant with a lot of local foods goat meat, fried rice…. Vegetarian foods is always available in the buffet lunch.</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Mua Caves</h4><p>Get back to Limousine Bus then leave for Mua Cave (Dancing Cave). Walk up almost 500 steps, you can reach to the top of Lying Dragon Mountain & have an amazing panoramic view of Tam Coc from here 16h30: Get on the bus to return to Hanoi. 18h30 – 19h00: Get dropped off at Hotel in Hanoi</p></div></div>', 14);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (15,4,9,1, 'Vietnam minors arts', 'https://www.youtube.com/watch?v=Ry3ho8k3yKg', '{https://res.cloudinary.com/findguider/image/upload/v1577043407/ART/art-16.jpg,https://res.cloudinary.com/findguider/image/upload/tranh dan toc.jpg}', 4,'Vietnamese art is visual art that, whether ancient or modern, originated in or is practiced in Vietnam or by Vietnamese artists.Vietnamese art has a long and rich history, the earliest examples of which date back as far as the Stone Age around 8,000 BCE. With the millennium of Chinese domination starting in the 2nd century BC, Vietnamese art undoubtedly absorbed many Chinese influences, which would continue even following independence from China in the 10th century AD. However, Vietnamese art has always retained many distinctively Vietnamese characteristics.',
			'{Air-conditioned vehicle,All Fees and Taxes,Bottled water}', 
			40, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Mausoleum</h4><p>The first attraction is Ho Chi Minh Mausoleum (viewed from outside) where honors the Greatest President of Vietnam, Mr Ho Chi Minh and his former residence (closed on Monday and Friday).</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Vincom contemporary art</h4><p>Next, we continue to visit Vincom contemporary art and introduce to you some collection of art here</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Ha Noi museum</h4><p>Finally, we land up at Hanoi museum-the most largest museum in the city</p></div></div>', 15);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (16,4,9,4, 'Street foods hanoi', 'https://www.youtube.com/watch?v=e8Ge4MtsLXA', '{https://res.cloudinary.com/findguider/image/upload/food/food-9.jpg,https://res.cloudinary.com/findguider/image/upload/food/food-2.jpg}', 3,'In 3 hour walking tour, our guest will get to know culture and people of Hanoi, try the most delicious local food, visit famous attractions such as Dong Xuan market, st Joseph Church, Hoan Kiem Lake,..... We also visit local market and do some shopping for fruits and gifts. During the tour, our guest will learn how to make Vietnamese food and meet a lot of local Vietnamese people and have so much fun.',
			'{A free bottle of water,All food and drinks,Free pick-up at your hotel in Hoan Kiem,English speaking tour guide,Lunch,Dinner,Bottled water,Coffee and/or Tea}', 
			25, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Hoan Kiem Lake Walking Street</h4><p>learn stories about Vietnamese history under Le Dynasty and Ngoc Son temple	</p></div></div><div class="detail" data-reactroot=""><i class="fa fa-circle"></i><div class="detailPlan"><h4>Dong Xuan Market</h4><p>Learn about history of Dong Xuan Market	</p></div></div>', 16);
	
INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (17, 13, 11, 3, 'Private Ho Chi Minh City Shore Excursion from Phu My Port', 'https://www.youtube.com/watch?v=BRN1agowpb4', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/8c.jpg}', 8,'Experience Ho Chi Minh City’s major landmarks and attractions on an all-day tour. After a convenient pickup from Phu My Port, travel in a private air-conditioned minivan with a personal guide to the War Remnants Museum, Reunification Palace (formerly the Presidential Palace), and the French colonial Notre Dame Cathedral and General Post Office. Enjoy a cafe sua da (coffee with condensed-milk) and lunch at a local restaurant included.',
	'{Port pickup and drop-off,Professional Tour Guide and Driver,All Entrance Fees,Vietnamese Traditional Lunch,Private, customized and flexible tour,Air-conditioned vehicle}',
	119, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Guider fluent in English</p></li></ul></div>');
	
INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Phu My Port, Đường số 3, TT. Phú Mỹ, Tân Thành', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Saigon Notre Dame Cathedral</h4><p>Saigon Notre Dame Cathedral, built in the late 1880s by French colonists, is one of the few remaining strongholds of Catholicism in the largely Buddhist Vietnam. Located in Paris Square, the name Notre Dame was given after the installation of the statue ‘Peaceful Notre Dame’ in 1959. In 1962, the Vatican conferred the Cathedral status as a basilica and gave it the official name of Saigon Notre-Dame Cathedral Basilica. Measuring almost 60 metres in height, the cathedral’s distinctive neo-Romanesque features include the all-red brick façade (which were imported from Marseille), stained glass windows, two bell towers containing six bronze bells that still ring to this day, and a peaceful garden setting in the middle of downtown Ho Chi Minh City District 1.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Central Post Office</h4><p>The Central Post Office in Ho Chi Minh is a beautifully preserved remnant of French colonial times and perhaps the grandest post office in all of Southeast Asia. Located next door to Notre Dame Cathedral, the two cultural sites can be visited together and offers visitors a chance to imagine life in Vietnam during the times of the Indochinese Empire. The building was designed by Alfred Foulhoux and features arched windows and wooden shutters, just as it would have in its heyday in the late 19th Century.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Independence Palace</h4><p>Independence Palace was the base of Vietnamese General Ngo Dinh Diem until his death in 1963. It made its name in global history in 1975. A tank belonging to the North Vietnamese Army crashed through its main gate, ending the Vietnam War. Today, its a must-visit for tourists in Ho Chi Minh City. The palace is like a time capsule frozen in 1975. You can see two of the original tanks used in the capture of the palace parked in the grounds. Independence Palace was the home and workplace of the French Governor of Cochin-China. It has lush gardens, secret rooms, antique furniture, and a command bunker. Its still in use to host important occasions in Ho Chi Minh, including APEC summits.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>War Remnants Museum</h4><p>The War Remnants Museum in Ho Chi Minh City first opened to the public in 1975. Once known as the ‘Museum of American War Crimes’, it a shocking reminder of the long and brutal Vietnam War. Graphic photographs and American military equipment are on display. There ís a helicopter with rocket launchers, a tank, a fighter plane, a single-seater attack aircraft. You can also see a conventional bomb that weighs at 6,800kg. American troops had used these weapons against the Vietnamese between 1945 and 1975.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Peoples Committee Building</h4><p>The People’s Committee Building Saigon in central Ho Chi Minh City features well-preserved French colonial architecture in a spacious garden landscape. Originally constructed as a hotel in 1898 by French architect Gardes, it now serves as a city hall and one of the city’s most iconic landmarks. Occupying the end of Nguyen Hue walking promenade, it has three buildings with embossed statues of animals and people, intricate bas-reliefs on the walls, as well as a statue of Ho Chi Minh in front of the main building. The best time to visit is in the evening, as these features are beautifully illuminated with LED lights.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ba Thien Hau Temple</h4><p>Ba Thien Hau Temple in Saigon is a Buddhist temple dedicated to the Chinese sea goddess, Mazu. It’s believed that she protects and rescues ships and people on the sea by flying around on a mat or cloud. Mazuism is connected with traditions and beliefs from both Taoism and Buddhism. Mazuism is therefore an incorporation of different aspects and traditions which have merged to form a new belief. You will find this temple in ‘Cholon’ (Chinatown) in District 5, which is roughly a twenty minute drive from the city centre.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ben Thanh Market</h4><p>Ben Thanh Market in Ho Chi Minh City District 1 is a great place to buy local handicrafts, branded goods, Vietnamese art and other souvenirs. Here, you’ll find eating stalls inside the market where you can get a taste of hawker-style Vietnamese cuisine or simply cool off with a cold drink when the bargaining becomes too much. The market is big, difficult to navigate at times and certainly best avoided during the hottest part of the day but all the same its well worth a look. When night falls, restaurants around the perimeter of the market open their doors creating a vibrant street side scene filling the air with the scents of wok-fried noodles, barbecued fish and meats. One of Saigon’s oldest landmarks, Ben Thanh offers a great atmosphere that is absolutely authentically Vietnamese.</p></div></div>', 17);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (18, 24, 11, 3, 'Mekong Delta Guided Tour from Ho Chi Minh city with Vinh Trang Pagoda & Lunch', 'https://www.youtube.com/watch?v=sg2Uuvadizo', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/fa.jpg}', 9,'Experience the best of the Mekong Delta on a day trip from Ho Chi Minh City, and explore the distinctive delta landscape by motorized river boat, row boat, and horse cart. You’ll visit a traditional temple, canal island, and local home along the way, then stop in a restaurant for a delta-style lunch. Your tour includes all transport, activities, entrance fees, lunch, and snacks, as well as hotel pickup and drop-off in central Ho Chi Minh City.',
			'{Transfer by Air-conditioned bus,Motor Boat Trip,Small Rowing Boat Trip,Lunch of Vietnamese cuisine (Vegan food available),Experienced English speaking Tour Guide}', 
			16, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('123 Lý Tu Trong st, Dist 1, HCMC', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Mekong Delta</h4><p>Departing from your Ho Chi Minh City. During the 1.5 hour drive, pass by green rice fields before arriving at the beautiful rural My Tho.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Vinh Trang pagoda</h4><p>It is the biggest pagoda in the Mekong Delta region.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Independence Palace</h4><p>Independence Palace was the base of Vietnamese General Ngo Dinh Diem until his death in 1963. It made its name in global history in 1975. A tank belonging to the North Vietnamese Army crashed through its main gate, ending the Vietnam War. Today, its a must-visit for tourists in Ho Chi Minh City. The palace is like a time capsule frozen in 1975. You can see two of the original tanks used in the capture of the palace parked in the grounds. Independence Palace was the home and workplace of the French Governor of Cochin-China. It has lush gardens, secret rooms, antique furniture, and a command bunker. Its still in use to host important occasions in Ho Chi Minh, including APEC summits.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ben Tre province — "the land of coconuts."</h4><p>Here you will see a typical agricultural model called "Garden - Pond – Cage,” before visiting the coconut candy-making shop.</p></div></div>', 18);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (19, 36, 11, 2, 'Black Virgin Mountain, Cao Dai Temple & Cu Chi Tunnels', 'https://www.youtube.com/watch?v=kGtbTlqOVoM', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/fd.jpg}', 11,'Discover the popular attractions in southern Vietnam’s Tay Ninh province on this all-day private tour. First, catch expansive views after climbing up Black Virgin Mountain and learn the religious significance of the Cao Dai temple. Then, explore the historic Cu Chi tunnels, an underground network used by resistance fighters. Plus, visit the Ba Den pagoda via cable car.',
			'{Lunch,First drink at restaurant (beer/soft drink/mineral water),Professional guide,Private transportation}', 
			120, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('07 Lam Sơn, Phường 6, Quận 1', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cao Dai Temple</h4><p>Admission Ticket Free</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cu Chi Tunnels</h4><p>Admission Ticket Included</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Tiger Tours</h4><p>Admission Ticket Free</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Black Virgin Mountain</h4><p>Admission Ticket Included</p></div></div>', 19);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (20, 51, 12, 4, 'Ho Chi Minh Evening Food Tour', 'https://www.youtube.com/watch?v=AtfHt32rR9s', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/ec.jpg}', 4,'Make the most of your time in Ho Chi Minh City on this private evening food tour, which traverses various districts of the city on a scooter. Taste the flavors of Vietnam and get to know the city, from gorgeous antiquated apartments to the blooms of an enormous flower market. This nighttime tour has all the ingredients for an adventure.',
			'{Every single thing in the tour.,6 authentic dishes,English speaking guides with good driving skills,Travel over 7 districts}', 
			49, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Bun thit nuong The Vietnamese noodles</h4><p>One of the best noodles in Vietnam – which many tourists have not known about. If you have try “Pho”, this one is even better.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Vietnamese pancake “Banh xeo” and "Banh Khot"</h4><p>The Vietnamese pancake “banh xeo” is one of the must-try in Ho Chi Minh. Called as the heaven of taste, “Banh xeo” is offered by many restaurants, however, the one we’re going to try is a mix of Saigon taste and Mekong Delta taste, which is unique. And we also try a "Banh Khot" at the restaurant.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Dessert at 43 year-shop with Coconut ice-cream</h4><p>After having some food and strolling around some places, We will get a special treats from local area. This is Coconut ice-cream with many toppings such as sticky rice, corn, coconut flake…. Make sure it’s a great choice for a hot night.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Mini cooking class</h4><p>Try 2 unique food in HCM city District 7, the place that no tourists know about, is hiding a secret food from mountainous area of Vietnam. With the recipe that no restaurants in Vietnam have, these food are definitely satisfying every food lover. You will get a chance to cook a traditional food of Vietnam and sample 2 dishes in here</p></div></div>', 20);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (21, 46, 11, 6, 'Mekong Delta Bike Tour', 'https://www.youtube.com/watch?v=5r8D3MmAY2c', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/1b.jpg}', 9,'Most visitors choose to explore the Mekong Delta by boat, but the scenic canals and lush countryside is also an idyllic backdrop for a bike tour. On this full-day cycling tour from Ho Chi Minh City, pedal to rural villages, rice paddies, and temples; enjoy a typical Vietnamese lunch at a local home; and visit traditional produce markets, wet markets, and artisan workshops.',
			'{Top-quality Merida, Giant and GT bikes for all size and ages, including kids,Helmets meeting international standards,Homemade lunch served in a private home,Professional, English-speaking Vietnamese guides,Support van for transfers during your ride}', 
			93, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Grasshopper Adventures</h4><p>The van will leave from our office to your hotel.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Mekong Delta</h4><p>On arrival in the Mekong Delta as the bikes are set-up you may enjoy coffee or tea at a small village cafe. Soon we are off riding through the serene countryside on quiet, paved rural roads, past countless rice paddies, vegetable farms and orchards full of locally grown fruit. Our tour weaves along canal paths and orchard trails, across ferries, through the charming villages and towns full of friendly faces. Along the way, we’ll stop at a small shop to see traditional woven mats and visit a nearby cocoa plantation. We will ride through two very traditional local markets with fresh fruits and vegetables from the region, as well as the recent catch of fish from local rivers and streams.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Vĩnh Kim</h4><p>As we head further south, rural roads give way to concrete canal paths and the vegetation grows denser and many of the streams start to look more like the large tributaries of the Mekong. We will visit the largest wholesale fresh produce market in the region, see indoor fresh-water shrimp cultivation, and stop at a beautiful traditional temple of the Cao Dai religion.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cai Be</h4><p>Lunch is prepared in a private home with a beautiful garden patio for dining. After our meal and short rest, you will board a small canal boat for a relaxing journey through quiet canals, before our private van takes us back to hectic central Saigon. Our Mekong Delta in A Day bike tour is the perfect opportunity to savor the flavors and diversity of southern Vietnam. Come taste the freshness, marvel at the beauty, listen to the river songs and capture unforgettable moments of the Mekong Delta on this full-day bike adventure.</p></div></div>', 21);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (22, 63, 11, 6, 'Experience Mekong By Bike,boat & Kayak', 'https://www.youtube.com/watch?v=i7a4jSaDmrg', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/7e.jpg}', 9,'Visiting the Mekong Delta on an independent day trip from Ho Chi Minh may mean you spend more time planning than actually experiencing the region. This full-day private trip lets you get the best from your day: by combining cycling and kayaking with opportunities to encounter local life. Paddle a kayak, cycle around orchards and farms to see and meet locals, and learn all about the delta from a guide.',
			'{Lunch,Local guide,Transport by air-conditioned minivan}', 
			95, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('07 Công Trường Lam Sơn, Bến Nghé, Quận 1', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ho Chi Minh Squares</h4><p>Departure: Morning, Pick up at your hotel located in District 1,3,4 Transfer to Mekong countryside. 02 hours Enjoy coffee at Countryside Coffee house before our Adventures. Take our Bikes and enjoy riding through villages</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cai Be Floating Market</h4><p>Enjoy riding along river bank,villages will give you a real Mekong daily life. Visit to some local spots along the way. Visit to local market where you can understand their life. Enjoy fruit at local market. Guarantee: No touristic stop. No shopping places You will: Experience the daily life of the locals. Tasting local food. Meeting with local people.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Mekong Delta</h4><p>Keep riding into the heart of Mekong Delta. Lunch break at supper local place. Afternoon, riding to a boat where you will enjoy a boat trip to see Floating market/Villages ( This activities can be convert to the morning. We Guarantee: No Touristic stopping. No Shopping Places You will: Experience the daily life of the locals. Tasting local food. Meeting with local people.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Mekong River</h4><p>Enjoy an hour kayak into canal/river would give you the real River life. It would be a great chance to see Mekong river life. Back to the pier and transfer back to Ho Chi Minh in the afternoon. Ends of wonderful Mekong Bike and kayak. We Guarantee: No Touristic stopping. No Shopping places You will : Experience the daily life of the locals. Tasting local food. Meeting with local people.</p></div></div>', 22);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (23, 7, 12, 2, 'Cu Chi Ben Duoc Tunnel - A realistic tunnel system', 'https://www.youtube.com/watch?v=tbeknopdAnw', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/b5.jpg}', 7,'Visit Ben Duoc Tunnel, you will be able to get the realest experience of Vietnam War in the southern part, see how tough a life in the cramped tunnels can be, and challenge yourself to overcome the odds.',
			'{Air-conditioned vehicle,All Fees and Taxes}', 
			21, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('171 Pham Ngu Lao street, district 1', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cu Chi Tunnels</h4><p>Upon arriving at the Ben Duoc tunnel, you will view a short documentary film which explains the tunnels’ history and the war from the Vietnamese point of view. After that, your guide will take you on a tour through the actual tunnels where you’ll learn more about the living conditions and the hardships the tunnel’s residents faced, and the ingenuity utilized to maintain life in the tunnels, which included weapon workshops and booby traps. You will have an opportunity to navigate an authentic Viet Cong tunnel, visit bunkers designed as kitchens, meeting rooms, weapon areas, give your best shot at a firing range, and much more. For a snack, you can try the local specialty – cassava, a starchy root – that sustained Viet Cong fighters for years.</p></div></div>', 23);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (24, 29, 11, 4, 'Vietnamese Street Food And Sightseeing Tour In Saigon', 'https://www.youtube.com/watch?v=mFp9XIUPf8w', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/cb.jpg}', 5,'Explore a medieval complex of Hindu temples famous for its erotic sculptures. After a convenient airport or rail station or airport pickup, head to Khajuraho and check out a newly opened museum and a sound and light show at the complex’s western temples. Enjoy a delicious complimentary dinner and breakfast the next morning before spending the day marveling at temples dedicated to Shiva, Devi Jagdamba, Parsvanath, and others.',
			'{Dinner/Lunch/ Breakfast,Food tasting,Local guide}', 
			39, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Please tell us your hotel name and address where our guides will pick up you.', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Vietnamese Street Food Tour</h4><p>Admission Ticket Free</p></div></div>', 24);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (25, 48, 11, 3, 'Cai Be Floating Market Private Tour', 'https://www.youtube.com/watch?v=BXsV5wb16MM', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/e7.jpg}', 4,'Witness the daily activities of local vendors at Cai Be Floating Market, a popular attraction of the Mekong Delta. Travel in the comfort of your private air-conditioned vehicle on the 2-hour scenic drive from Ho Chi Minh City and take a sampan ride along the network of canals. See local industries, stroll through a fruit orchard and enjoy lunch at a riverside restaurant.',
			'{Transportations: New air-condition vehicle transfer,Private boat trip in Mekong Delta,English or French speaking guide,Sightseeing and entrances fees at local guide.}', 
			44, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English and French</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Pick up at hotel in Ho Chi Minh City Center', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Cai Be Floating Market</h4><p>Admission Ticket Included</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Roadstour Vietnam</h4><p>Admission Ticket Free</p></div></div>', 25);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (26, 69, 12, 2, 'Saigon Historical City Tour', 'https://www.youtube.com/watch?v=l7r3AdBdG80', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/cf.jpg}', 4,'Tick off more of Ho Chi Minh City’s historical sights in less time on a sightseeing tour that’s designed with first-time visitors in mind. Rather than navigating HCMC’s manic streets alone, you’ll explore alongside a local guide and get the inside scoop on both well-known attractions and hidden gems.',
			'{Every single thing in the tour.,Transportation by motorbikes, including fuel and a high-quality, open-face helmet}', 
			45, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Saigon Notre Dame Cathedral</h4><p>Visit the most famous cathedral in Ho Chi Minh City, which was build of 100% material from France. When visiting the next post office, we will meet a 90-years-old man, who is the last handwriting letter-writer in Vietnam</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Venerable Thich Quang Duc Monument</h4><p>As a famous Vietnamese monk, Thich Quang Duc burnt himself to protest against the Southern government in 1963. Why? Our guides will tell you the stories, with the highlight is the legend of indestructible heart</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ho Thi Ky Flower Market</h4><p>Started in 1980, the old and biggest flower market in Ho Chi Minh is a sleepless one, opened nearly 24/7. Flowers here are transferred from Da Lat, the Paris of Vietnam every morning. We will take a walk here to see thousands of flowers.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ba Thien Hau Temple</h4><p>With 1,5 million Chinese-Vietnamese living for generations, the China Town will show the the culture via motorbike market, bird market, and most special – “Thien Hau” the 300 years old temple</p></div></div>', 26);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (27, 37, 15, 3, 'Da Nang and Hoi An city Private Tour', 'https://www.youtube.com/watch?v=avk5UTNEjXE', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/f8.jpg}', 9,'Our Tourguidepick you up at Hotel in Da Nang or Hoi An city to visit the first stop in our trip – Marble Moutain. It is a cluster of five marble and limestone hills in the south of Da Nang, the marble mountains have several tunnels and caves inside it with Buddhist sanctuaries located inside. The second stop is Han market – one of the best central markets in Vietnam. Coffee at local coffee shop sounds like great way understand more about an important part of Vietnamese life. Stop at the Dragon Bridge to take the beautiful photos of the whole long steel dragon and enjoy the nice view of Da Nang city. Travel to the Giant Lady Buddha Statue, the tallest in Viet Nam. Lady Buddha is located at Linh Ung Pagoda on Son Tra Peninsula After that we go to have lunch in Hoi an city, Then,go to coconut village to row basket boat, Visit ancient town. Chinese Assembly halls, Japanese bridge and Vietnamese heritage houses we visit Night Market with colourful lanterns',
			'{Private Car or Minivan with safe Driver,English Speaking Tourguide,Entrance tickets in Da Nang and Hoi An city,Basket Boat Rowing fee}', 
			125, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Lady Buddha</h4><p>Lady Buddha is located at Linh Ung Pagoda on Son Tra Peninsula in Da Nang. The statue is situated on the mountains facing the sea. There are 17 floors in the statue, each floor has an altar with 21 Buddha statues which have different shapes, facial expressions and postures.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Han Market</h4><p>Local Market</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Dragon Bridge</h4><p>Dragon Bridge to take the beautiful photos of the whole long steel dragon and enjoy the nice view of Da Nang modern city. According to local beliefs, which date back to the Ly Dynasty, the Dragon is a significant symbol of power, nobility and good fortune.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Marble Mountains</h4><p>Visit the holy pagodas and explore the natural caves where used to be a hospital to treat Vietnamese wounded soldiers and Viet Cong hiding during America war – Enjoy the stunning views from summit of mountain and admire a cluster of 5 small mountains following 5 important elements of universe : water fire, wood, earth and metal. – Visit the family Traditional Marble Handicraft carving at marble village to experience how local people are so skillful.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Hoi An Ancient Town</h4><p>Walk around the ancient old town of Hoi An, which UNESCO World Heritage site, learning about the history of the buildings and the town with your knowledgeable tour guide Explore the back alleys and see the real Hoi An</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Japanese Covered Bridge</h4><p>Symbol of Hoi an city</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Hoi An Night Market</h4><p>Colourful lanterns</p></div></div>', 27);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (28, 74, 17, 3, 'Golden Bridge & Ba Na Hills', 'https://www.youtube.com/watch?v=QnY74vH-iNE', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/7d.jpg}', 9,'Combine scenic sightseeing with family adventure on this full-day tour to the action-packed Ba Na Hills. Avoid crowded tour buses and footpaths on this small-group tour of some of the highlights of the hills. Take in panoramic views of Da Nang from the impressive Golden Bridge and take a cable car – one of the longest in the world – over the lush Ba Na forest. Afterward, spend some time exploring the rest of the Hills, including the games and experiences at the action-packed theme park Fantasy Park.',
			'{English Professional local Guide,Air-conditioned vehicle,All Fees and Taxes}', 
			75, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Golden Bridge</h4><p>Our first amazing views will start right away, to reach Ba Na Hills we will take a ride via Cable Cars. The most modern Cable Car in Southeast Asia, visit the Dream Stream Cable car Station and see the panoramic views of Quang Nam - Danang City from high above. After finishing the first cable car, you will visit the Golden Bridge that has recently gone viral, a France wine cellar, Le Jardin D’amour(consists of 9 gardens) and Linh Ung pagoda. Continue the 2nd Cable Car to visit the French Village Campanile, Nine Floor Goddess Shrine, Tombstone Temple, Linh Phong Monastery, Linh Chua Linh Tu Temple & Tru Vu Tea Shop. Watch Carnival Performance Show, Square Du Dome ...Challenge the most popular adventure ride - Alpine Coaster (free ride), Lunch at the Restaurant is included (Buffet) Join in Fantasy Park with a walk in Fairy Forest, discover Dinosaur Park, play 5D wild west, enjoy 4D death race ride, watch 3D mega 360 degree, ride on Journey into the underground, enter Jurassic Park, challenge Freefall Tower and watch out for the Horror House and over 90 free games.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ba Na Hills</h4><p>Explore Ba Na hill, sightseeing the panoramic views of Quang nam - Da Nang city Discover the famous bridge is Golden bridge Sightseeing some famous place in here such as: French Village Campanile, Nine Floor Goddess Shrine, Tombstone Temple, Linh Phong Monastery, Linh Chua Linh Tu Temple & Tru Vu Tea Shop... Discover Fantasy park with some adventure game</p></div></div>', 28);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (29, 48, 14, 3, 'My Son Sanctuary & Marble Mountain', 'https://www.youtube.com/watch?v=LxkwqxW0yTU', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/88.jpg}', 8,'The UNESCO-listed My Son Sanctuary and the natural wonders of the Marble Mountains are two of the most popular day trips from Da Nang. This full-day tour visits both—start by exploring the ancient city of the Champa Kings, then enjoy spectacular views from the five peaks of the Marble Mountains.',
			'{Transfer and transportation as per itinerary,One way elevator at Marble Mountain,English speaking tour guide}', 
			58, 2, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We will pick you up from your accommodation', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>My Son Sanctuary</h4><p>Follow your guide on a walking tour of the well-preserved ruins and hear how the UNESCO World Heritage site was once the capital and religious center of the ancient Champa Kingdom. Enjoy plenty of time to admire the beautiful temples and red brick towers, set against an idyllic backdrop of lush green hills.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Marble Mountains.</h4><p>Enjoy views of the five marble and limestone peaks, and explore the caves, temples, and viewpoints dotted around the mountains. At the foot of the mountains, pay a visit to the Non Nuoc Stone Carving Village, where you’ll have chance to admire the traditional crafts and perhaps purchase some unique souvenirs.</p></div></div>', 29);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (30, 19, 19, 3, 'Hue Imperial City - UNESCO World Heritage Site', 'https://www.youtube.com/watch?v=HvaGdxA0Pc8', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/c3.jpg}', 11,'Easy transfers, private transport and a professional, friendly guide add to the charm of historic Hue on this day-long tour that includes a cruise along the fabled Perfume River. Visit some of the temples, royal tombs, palaces, pavilions and other architectural marvels that define this ancient city - a World Heritage site once home to Vietnam’s Imperial Capital.',
			'{Transport by private vehicle,Professional guide}', 
			125, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Chan May Port, Loc Vinh Commune, - Phu Loc District, Lộc Vĩnh, Phú Lộc', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Minh Mang King’s Tomb</h4><p>Most majestic of all Nguyen Dynasty royal tombs with a complex of 40 constructions: palaces, temples, pavilions, etc. Many said that it’s a perfect combination of the manmade and natural beauty of Hue, where architecture fits harmony into the surrounding landscape.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Khai Dinh King’s Tomb</h4><p>A blend of Western and Eastern architecture</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Martial Art Van An</h4><p>Originated in the Nguyen Dynasty</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Imperial Citadel</h4><p>Known as the last royal dynasty of Vietnam. Then visit Thien Mu pagoda which is the oldest pagoda in Hue city.</p></div></div>', 30);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (31, 3, 14, 3, 'Explore Danang City Tour', 'https://www.youtube.com/watch?v=KEEg28cfwKw', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/92.jpg}', 4,'Da Nang city is located in the middle of Vietnam Map. It includes mountains, river, beaches combined with famous sightseeing. A place "must to visit" in your travel to Vietnam. During this amazing full day tour you will see all the most popular sights and experience the best parts of the city with a local guide.',
			'{Transportation,English speaking tour guide,Entrance fees}', 
			93, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Pick up and drop off available for hotel in Danang city area', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Da Nang Museum of Cham Sculpture</h4><p>The Museum of Cham Sculpture is a museum located in Hải Châu District, Đà Nẵng, central Vietnam, near the Han River. The establishment of a Cham sculpture museum in Da Nang was first proposed in 1902 by the Department of Archaeology of EFEO.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Dragon Bridge</h4><p>Watch the coolest bridge in Danang city with the shape of the dragon crossesis a bridge with a dragon over the Han River at Danang, Vietnam.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Han Market</h4><p>Buzzing, multi-level indoor market with vendors selling food, clothing, fabrics & housewares.</p></div></div>', 31);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (32, 61, 14, 4, 'Da Nang Local Food Tour', 'https://www.youtube.com/watch?v=Td55SEUw-WY', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/86.jpg}', 4,'Private tour. Transportaion: WALKING or Motorbike tour. Depends on request from customers. If you want to have walking tour and only you on tour, we will charge more. We dont take you guys to the tourist area, you may be the only one in place where we take you but safe and delicious food.',
			'{Alcoholic Beverages,Dinner,Lunch,Breakfast}', 
			39, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Pick up and drop off available for hotel in Danang city area', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>"Banh Canh cua"</h4><p>Vietnamese noodles with Crab, specialty in Da Nang</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>"Banh Xeo"</h4><p>Vietnamese pancake with a lot of herbs and yummy sauce</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Vietnamese pizza</h4><p>We will offer Vietnamese pizza, that is not like West Pizza and sure that you love it</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Bloating Fern Shaped Cake</h4><p>Is gonna be amazing for you guys</p></div></div>', 32);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (33, 45, 19, 3, 'Bach Ma National Park', 'https://www.youtube.com/watch?v=8eRWh45prTM', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/71.jpg}', 9,'Escape the bustle of Hue for true wilderness on this guided day trip to Bach Ma National Park. Savor bird’s-eye views of mountains, lakes, lagoons, villages, and beaches from the summit of Bach Ma. Explore the scenic Five Lakes, view the towering Do Quyen Waterfall, and keep an eye out for the park’s many rare animals. Tour includes lunch and door-to-door round-trip transfers.',
			'{Professional guide,Entrance fees}', 
			34, 5, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Vĩnh Ninh, Huế, Thua Thien Hue, Vietnam', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Hai Van Pass</h4><p>Admission Ticket Free</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Bach Ma National Park</h4><p>Admission Ticket Included</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Wati Travel</h4><p>Admission Ticket Free</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Lang Co Beach</h4><p>Admission Ticket Free</p></div></div>', 33);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (34, 20, 14, 6, 'Scooter Adventure on Monkey Mountain', 'https://www.youtube.com/watch?v=yzFYT5WYgLc', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/87.jpg}', 5,'Order of the destinations on the trip: Ban Co Peak - Heliport - Linh Ung Pagoda - Man Thai Fishing Village and Paragliding zone. One of the things people love about living in Da Nang is the fact that there are so many undiscovered gems around the city and Son Tra Peninsula provides many of them. It is not only famous for its amazing views, but it’s also full of things to do, things to experience and things to see.',
			'{Meal: Hamburger (Chicken/Beef/Vegetarian).,Parking fee,Small Scooter(Drive by yourself- only for experienced drivers) or ride on the back of private driver}',
			58, 2, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('Nhà hàng Phước Mỹ 2, Võ Nguyên Giáp, Phước Mỹ, Sơn Trà, Đà Nẵng', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Son Tra Mountain (Monkey Mountain)</h4><p>Fishing Village is our first stop. The village is rested right next to Monkey Mountain, most of the residents in this area are living on the ocean. They get up early to drop fishing net to the sea and collect very fresh fishes and squids for seafood market nearby. The popular boat most of the fishermen use is basketboat. It is made of bamboo which is available in Viet Nam and durable as well as friendly to the environment. We will learn about how they make one and might have a conversation with one of a fisherman there.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Linh Ung Pagoda</h4><p>The pagoda was built in 2009 as one of the symbols of Da Nang. There is 67m high lady buddha stands on the edge of the mountain, she faces the ocean and local people often visit her to pray for her protection toward Da Nang city. In here, we will have chance to learn about Vietnamese religion and visit the most spectacular pagoda in the central of Viet Nam</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Ban Co Peak</h4><p>Before the Summit of Monkey Mt. we will spend some time at a magnificent edge nearby Intercontinental Resort Da Nang for burger pinic lunch. While enjoying cool breeze from the sea, we will have some drink and burger which made to your own preference. Following is a narrow path inside the rainforest which leads us to the summit of Son Tra. From there we will have many opportunities to capture epic panoramic photos of Da Nang and Hai Van Mountain.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Bai Da</h4><p>Going down the hill, we will switch to a hidden road under shades of big trees and jungle, the voices of birds singing, monkeys and the wildlife all blended together makes the trip an adventurous one. The experienced guide will show you where the monkeys playing and how they hide from the human. If we have good luck, getting to see Red Shanked Langurs is just so easy. They are endangered animals and highly protected since the population of them is just less than 2000 worldwide. End of the trip, we will come to a secluded beach on the Mountain where only locals know about. Feel free to collect some oyster shells as a gift to friends back home and we will all head back to city.</p></div></div>', 34);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (35, 50, 14, 4, 'Danang Backstreets', 'https://www.youtube.com/watch?v=BnUddNhVfVc', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/84.jpg}', 3,'Forget Pho, Banh Xeo- Vietnamese Crispy Pancake, our cuisine has much more delicious dishes than that. Let me take you to discover the other side of Danang cuisine as the truly insider. With 3 hours tour, you will be a local diner with the local eateries and our local culture stories behind each dish. What is the better way to explore new place, new culture through the cuisine- way. You will have a big and fun dinner as Vietnamese style As a foodie-traveler, I always want to go and explore new places, try new foods and learn more about another culture.',
			'{Food tasting,Local guide,Air-conditioned vehicle,All Fees and Taxes}', 
			40, 3, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We pick up at hotel in the city center and nearby.', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Dragon Bridge</h4><p>Visit Dragon Bridge for the fire- watering dragon(at weekend only)</p></div></div>', 35);

INSERT INTO public.post(post_id, guider_id, location_id, category_id, title, video_link, picture_link, total_hour, description, including_service, price, rated, reasons)
	VALUES (36, 39, 14, 3, 'Linh Ung Pagoda - Marble Mountains - Hoi An Ancient Town', 'https://www.youtube.com/watch?v=EHrn7myTBBw', '{https://res.cloudinary.com/findguider/image/upload/v1576945565/hoanghai/18.jpg}', 8,'Taking the trip with Dacotours, you will see how enthusiastic our tour guides are as we always try our best not only to bring the happiness and to deliver great stories about culture to customers but also to devote to sustainable tourism development. Head to the Marble Mountains and the Son Tra Peninsula on this tour from Da Nang, you’ll visit mysterious caves hidden within Marble Mountains and see the 220-foot-high marble Budha statue that resides at Ling Ung Pagoda. Enjoy sweeping views and visit Non Nuoc, a centuries-old stone carving village and get amazed by the beautiful Feng Shui lucky marble bracelets made by local artists. After discovering Marble Mountain, we continue our journey to Hoi An Ancient Town - Unesco World Heritage. You will be surrounded by thousand of colorful laterns everywhere. Besides, you also improve your knowledge about the culture and history of Hoi An from the past, immers your mind with fresh air by nearby peaceful Hoai river',
			'{Air-conditioned vehicle,Travel Insurance up to 10000USD/case}', 
			36, 4, '<div class="activities reason" data-reactroot=""><h2><!-- --> Reasons to book this tour</h2><ul><li><i class="fa fa-check"></i><p>Hotel pickup offered</p></li><li><i class="fa fa-check"></i><p>Mobile ticket</p></li><li><i class="fa fa-check"></i><p>Offered in: English</p></li></ul></div>');

INSERT INTO public.plan(meeting_point, detail, post_id)
	VALUES ('We pick up at hotel in the city center and nearby.', '<div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Linh Ung Pagoda</h4><p>Visiting Son Tra Peninsula and Linh Ung Pagoda where has the highest Lady Budda statue in Vietnam with 67 meters height looking down and protect the city below throughout stormy seasons.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>The Marble Mountains</h4><p>Arriving at Marble Mountain which have so much meaning relating to Vietnamese Feng Shui - visiting mysterious Pagoda, Xa Loi tower, discovering the stunning natural caves like Tang Chon cave and Non Nuoc Stone Carving Village where artists work everyday to produce the Feng Shui marble bracelet to pray for luck.</p></div></div><div class="detail" data-reactroot=""><i class="fas fa-circle"></i><div class="detailPlan"><h4>Hoi An Ancient Town</h4><p>Visiting Fukian Assembly Hall, Japanese Covered Bridge, Old House. - 18h30: Free and easy time to take a stroll for shopping at Nguyen Hoang night market and enjoy the beautiful light from thousands of lanterns.</p></div></div>', 36);

INSERT INTO transaction(transaction_id, payment_id, payer_id, description, success)
	VALUES ('ABC', 'abc', 'abc', 'abc', true);
	
INSERT INTO public.refund(transaction_id, date_of_refund, message)
	VALUES ('ABC', now(), 'success');
	
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (1, 100, 1, '2019-01-05T05:00', '2019-01-05T09:00', 1, 2, 250, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (1, 5, '2019-01-06T09:00', 'We had a bit of a delay connecting with tour guide but once we did it was fantastic. Our tour guide was excellent and we did have a good time. We got to see and experience so much in a very short period of time.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (2, 101, 1, '2019-01-10T09:30', '2019-01-10T12:00', 2, 1, 370, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (2, 5, '2019-01-11T09:00', 'This tour was excellent. We enjoyed the day going around at our pace and had one if the best tour guides!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (3, 102, 1, '2019-01-08T07:00', '2019-01-08T10:00', 3, 2, 320, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (3, 5, '2019-01-09T09:00', 'The trip was very good. An eye opener for the kids.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (4, 100, 2, '2019-01-18T08:00', '2019-01-18T13:00', 2, 1, 36, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (4, 4, '2019-01-19T09:00', 'The tour guide was nice and knowledgeable. We did everything as outlined. The transportation could have been more comfortable.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (5, 101, 2, '2019-01-19T10:30', '2019-01-19T12:00', 3, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (5, 5, '2019-01-20T09:00', 'The day was run very well with a nice lunch. The guide was very informative and made the journey a peasure.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (6, 102, 2, '2019-01-07T13:00', '2019-01-07T15:00', 4, 2, 80, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (6, 5, '2019-01-08T09:00', 'My family and I thoroughly enjoyed this experience');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (7, 100, 3, '2019-01-25T17:00', '2019-01-25T19:00', 4, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (7, 5, '2019-01-26T09:00', 'Excellent Tour on a very well air-conditioned bus.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (8, 101, 3, '2019-01-19T20:30', '2019-01-19T22:00', 1, 1, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (8, 5, '2019-01-20T09:00', 'This tour was breath taking!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (9, 102, 3, '2019-01-21T01:00', '2019-01-21T05:00', 2, 2, 180, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (9, 5, '2019-01-22T09:00', 'An excellent guide led tour and a very comfortable bus.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (10, 100, 4, '2019-02-06T05:00', '2019-02-05T09:00', 3, 1, 200, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (10, 5, '2019-02-06T09:00', 'Great day trip. Lots of driving but the van was very comfortable and as pictured.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (11, 101, 4, '2019-03-10T19:30', '2019-03-10T22:30', 2, 1, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (11, 5, '2019-03-11T09:00', 'What a great experience. The day was very enjoyable and educational. The vehicle was comfortable and exactly as described and as per the photos.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (12, 102, 4, '2019-03-08T07:00', '2019-03-08T10:00', 3, 1, 250, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (12, 5, '2019-03-09T09:00', 'Our guide was superb, very informative and funny but without being too intense. There is a fair bit of ‘van time’ being driven to each location but that was fine to have a rest in the heat.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (13, 100, 5, '2019-04-05T05:00', '2019-04-05T09:00', 1, 1, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (13, 5, '2019-04-06T09:00', 'We had a family emergency come up that didnt allow us to take part in this trip but after contacting them, they rebooked us for a half day tour that same day which was so unexpected. The tour guide was very friendly and knowledgeable as well. I would recommend them in a heart beat.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (14, 101, 5, '2019-04-10T09:30', '2019-04-10T12:00', 1, 1, 50, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (14, 5, '2019-04-11T09:00', 'Very good day trip. Van was extremely comfortable.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (15, 102, 5, '2019-05-08T07:00', '2019-05-08T10:00', 2, 2, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (15, 5, '2019-05-09T09:00', 'Thoroughly enjoyable tour, would highly recommend.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (16, 100, 6, '2019-05-05T05:00', '2019-05-05T09:00', 3, 2, 130, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (16, 4, '2019-05-06T09:00', 'Great tour, only drawback is significant unavoidable transit times. Guide and driver were super! Tour locations were well worth it!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (17, 101, 6, '2019-06-10T09:30', '2019-06-10T12:00', 2, 1, 180, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (17, 4, '2019-06-11T09:00', 'Be prepared for a very long day. Alot of time spent in the car driving. However it is worth the effort.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (18, 102, 6, '2019-06-08T07:00', '2019-06-08T10:00', 1, 1, 160, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (18, 5, '2019-06-09T09:00', 'The tour was a lot more informative than expected. Our tour guide was witty and super funny.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (19, 100, 7, '2019-05-05T05:00', '2019-05-05T09:00', 1, 1, 70, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (19, 4, '2019-05-06T09:00', 'The transport was very comfortable although only 1 beer per person was provided. The lunch was really good. The only downside was our tour guide was very difficult to understand.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (20, 101, 7, '2019-05-10T09:30', '2019-05-10T12:00', 3, 1, 160, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (20, 5, '2019-05-11T09:00', 'This tour is perfect if you want to get a lot done in a short time.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (21, 102, 7, '2019-06-08T07:00', '2019-06-08T10:00', 3, 2, 250, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (21, 5, '2019-06-09T09:00', 'great day trip for all ages, great tour guide, fantastic ,prompt ,informative ,clean bus ,small group in tour');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (22, 100, 8, '2019-07-05T05:00', '2019-07-05T09:00', 2, 1, 230, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (22, 5, '2019-07-06T09:00', 'Our guide was excellent, the trip was wonderful.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (23, 101, 8, '2019-08-10T09:30', '2019-08-10T12:00', 1, 3, 100, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (23, 5, '2019-08-11T09:00', 'Long day but our guide was perfection. Lunch was great too! Did not mind tipping local people for services.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (24, 102, 8, '2019-08-08T07:00', '2019-08-08T10:00', 1, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (24, 5, '2019-08-09T09:00', 'Best tour in the area. Our guide was quite knowledgeable and spoke English well.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (25, 100, 9, '2019-08-05T05:00', '2019-08-05T09:00', 1, 1, 55, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (25, 5, '2019-08-06T09:00', 'The whole tour is one awesome experience. Fun and knowledgeable tour guide. Very comfortable van and great lunch.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (26, 101, 9, '2019-08-10T09:30', '2019-08-10T12:00', 2, 0, 90, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (26, 5, '2019-08-11T09:00', 'Great day interesting and fun.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (27, 102, 9, '2019-08-08T07:00', '2019-08-08T10:00', 3, 1, 260, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (27, 4, '2019-08-09T09:00', 'Tour guide was great. Prepare small change for various tips along the way.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (28, 100, 10, '2019-09-05T05:00', '2019-09-09T09:00', 2, 2, 240, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (28, 5, '2019-09-10T09:00', 'Normally. I would opt for the cheaper option-a bigger van with more people. But it was our last day in Vietnam, so we decided to splurge a bit. It turned out to be the absolute best tour we took.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (29, 101, 10, '2019-10-10T09:30', '2019-10-10T12:00', 2, 1, 90, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (29, 5, '2019-10-11T09:00', 'Our guide was 5. He was absolutely incredible, knowledgeable, fun, accommodating and made this experience such a wonderful and worth while day. I would recommend this trip to anyone.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (30, 102, 10, '2019-10-08T07:00', '2019-10-08T10:00', 1, 0, 15, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (30, 5, '2019-10-09T09:00', 'This is how I want to tour every city now! Super comfortable and ice cold!! Tour guide was very knowledgeable.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (31, 100, 11, '2019-01-05T05:00', '2019-01-09T09:00', 1, 1, 30, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (31, 5, '2019-01-10T09:00', 'fantastic tour?? highly recommended. will go again');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (32, 101, 11, '2019-05-10T09:30', '2019-05-10T12:00', 3, 0, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (32, 5, '2019-05-11T09:00', 'Our tour guide told about very interesting Vietnam history. It was the best trip. Me and my over 80years old father enjoyed alot.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (33, 102, 11, '2019-07-08T07:00', '2019-07-08T10:00', 3, 1, 170, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (33, 5, '2019-07-09T09:00', 'Long day and well worth it! So much packed into it! Great as a small tour as it gives you plenty of opportunities to raise questions you have got. A must if you are on tight time schedule.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (34, 100, 12, '2019-10-05T05:00', '2019-10-09T09:00', 1, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (34, 5, '2019-10-10T09:00', 'Had a amazing, enjoyed, wonderful trip with our friendly tour guide');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (35, 101, 12, '2019-10-10T09:30', '2019-10-10T12:00', 1, 0, 30, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (35, 5, '2019-10-11T09:00', 'This tour was amazing. The transportation is just as pictured- There is wifi and there are usb charging porst on board for charging your devices.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (36, 102, 12, '2019-10-08T07:00', '2019-10-08T10:00', 1, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (36, 5, '2019-10-09T09:00', 'Very well organised tour with a very good English speaking guide. Enjoyed it thoroughly with lunch at a great Vietnamese restaurant. Would definitely advise it.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (37, 100, 13, '2019-11-05T05:00', '2019-11-09T09:00', 1, 2, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (37, 5, '2019-11-10T09:00', 'Had a wonderful day LONG DAY at that.The car was wonderful and our driver spoke great English and explained lots of different places as we went along. Wasnt the best start to our holiday as I was about to Cancel this trip due to having upset stomach but good old Gastro stop help me through and glad I went. I did have a few hours sleep in car which probably saved me.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (38, 101, 13, '2019-11-10T09:30', '2019-11-10T12:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (38, 5, '2019-11-11T09:00', 'The guide explained very well and were nice, had humor and talked good English. It is a long drive but we didnt have much time so we had to do it on the same day and this was perfect. They picked us up right on time and droved safely in the traffic.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (39, 102, 13, '2019-11-08T07:00', '2019-11-08T10:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (39, 4, '2019-11-09T09:00', 'The guide was lovely and the van is very nice and comfortable.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (40, 100, 14, '2019-11-05T05:00', '2019-11-09T09:00', 3, 1, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (40, 5, '2019-11-10T09:00', 'Had an amazing day trip. Our guide did an excellent job informing us on route and at the sites. It is the one tour you need to do.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (41, 101, 14, '2019-10-10T09:30', '2019-10-10T12:00', 2, 0, 80, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (41, 5, '2019-10-11T09:00', 'This tour is something that I have wanted to do for a very long time. Having finally managed to get to Vietnam, I looked for a tour that felt right and this was it. The luxury vehicle very comfortable arrived on time and the guide, who spoke pretty good English was very amenable.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (42, 102, 14, '2019-05-08T07:00', '2019-05-08T10:00', 1, 1, 25, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (42, 5, '2019-05-09T09:00', 'Great trip very knowledgeable guide we had a great experience.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (43, 100, 15, '2019-07-05T05:00', '2019-07-09T09:00', 4, 2, 300, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (43, 5, '2019-07-10T09:00', 'A great trip and a good way to see both in one day. It was a long day but worth it. The transport was comfortable and equivalent to business class on aeroplane (well almost) . The flinch provided was good quality and there was plenty of it.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (44, 101, 15, '2019-04-10T09:30', '2019-04-10T12:00', 1, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (44, 5, '2019-04-11T09:00', 'The luxury van drove us looked exactly the same as the tour profile picture and it was very comfortable. Most destinations were interesting to visit.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (45, 102, 15, '2019-06-08T07:00', '2019-06-08T10:00', 2, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (45, 5, '2019-06-09T09:00', 'The tour was well organised and easy to book.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (46, 100, 16, '2019-03-05T05:00', '2019-03-09T09:00', 1, 1, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (46, 5, '2019-03-10T09:00', 'We had an excellent trip, supported by a very good guide');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (47, 101, 16, '2019-03-10T09:30', '2019-03-10T12:00', 1, 3, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (47, 5, '2019-03-11T09:00', 'Good pace. Comfortable vehicle and accommodating guide.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (48, 102, 16, '2019-11-08T07:00', '2019-11-08T10:00', 1, 1, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (48, 5, '2019-11-09T09:00', 'Highly recommended. Small group is a big bonus as well as the comfortable coach');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (49, 100, 17, '2019-05-09T05:00', '2019-05-09T09:00', 1, 1, 40, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (49, 5, '2019-05-10T09:00', 'This is a good tour and the splurge is worth it for comfort.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (50, 101, 17, '2019-04-12T09:30', '2019-04-12T12:00', 2, 3, 150, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (50, 5, '2019-04-13T09:00', 'Tour guide was fantastic great knowledge of all the tour sites , very friendly and made for a great day - very nice and comfortable ford transit van');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (51, 102, 17, '2019-07-18T07:00', '2019-07-18T10:00', 1, 0, 20, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (51, 5, '2019-07-19T09:00', 'Totally amazing, Excellent in every way.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (52, 100, 18, '2019-12-05T05:00', '2019-12-09T09:00', 3, 1, 90, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (52, 5, '2019-12-10T09:00', 'Really enjoyed this tour! Our tour guide was very friendly, helpful and knowledgeable! The transportation was very comfortable and on board wifi was a great bonus!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (53, 101, 18, '2019-12-10T09:30', '2019-12-10T12:00', 1, 1, 30, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (53, 5, '2019-12-11T09:00', 'Highly recommended tour. It s an amazing experience by seeing beautiful places');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (54, 102, 18, '2019-12-08T07:00', '2019-12-08T10:00', 1, 0, 10, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (54, 5, '2019-12-09T09:00', 'My girlfriend and I joined this tour and had a great experience.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (55, 100, 19, '2019-06-15T05:00', '2019-06-15T09:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (55, 5, '2019-06-16T09:00', 'Excellent trip and made more enjoyable by our guide');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (56, 101, 19, '2019-10-30T09:30', '2019-10-30T12:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (56, 5, '2019-11-01T09:00', 'The transfers are comfortable and the guide friendly and funny. The attractions are informative');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (57, 102, 19, '2019-01-01T07:00', '2019-01-01T10:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (57, 5, '2019-01-02T09:00', 'Tour guide was very nice and spoke well English.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (58, 100, 20, '2019-02-15T05:00', '2019-02-15T09:00', 1, 1, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (58, 5, '2019-02-16T09:00', 'Hotel pick up was on-time and tour guide spoke good English. Transport was excellent in terms of comfort levels and number of tourists.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (59, 101, 20, '2019-03-17T09:30', '2019-03-17T12:00', 1, 0, 30, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (59, 5, '2019-03-18T09:00', 'Brilliant tour guide and friendly bus driver.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (60, 102, 20, '2019-05-21T07:00', '2019-05-21T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (60, 5, '2019-05-22T09:00', 'I was visiting for work and only had one weekend day free for sightseeing outside of the city. This tour is a great way to see a lot of some nearby parts of Vietnam.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (61, 100, 21, '2019-02-15T05:00', '2019-02-15T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (61, 5, '2019-02-15T09:00', 'Our family had an excellent trip thanks to our friendly and informative guide');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (62, 101, 21, '2019-07-01T09:30', '2019-07-01T12:00', 2, 1, 65, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (62, 5, '2019-07-02T09:00', 'The tour allowed us to see and experience "real life" Vietnam. We were taken to places that were not tourist traps yet gave us great insights into the lives of the people');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (63, 102, 21, '2019-08-03T07:00', '2019-08-03T10:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (63, 4, '2019-08-04T09:00', 'great tour guide. All ok, the people were super friendly and the service well done. Sadly the price was to much for this tour');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (64, 100, 22, '2019-11-11T05:00', '2019-11-11T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (64, 5, '2019-11-12T09:00', 'Having a small group was great and we went to places that were not crowded with trillions of other tourists. Way better than expected and would highly recommend this tour.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (65, 101, 22, '2019-07-19T09:30', '2019-07-19T12:00', 3, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (65, 5, '2019-07-20T09:00', 'This tour was very well organised, very informative and very enjoyable. I found it difficult to know exactly where we were going before the trip but the experience was interesting nonetheless. It would be good to have a map available to know exactly the route the tour takes. I highly recommend this experience. Great guide.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (66, 102, 22, '2019-03-03T07:00', '2019-03-03T10:00', 1, 1, 65, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (66, 5, '2019-03-04T09:00', 'A genuine experience which I will always remember! Highly recommended.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (67, 100, 23, '2019-05-23T05:00', '2019-05-23T09:00', 1, 0, 30, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (67, 5, '2019-05-24T09:00', 'Loved this trip. Not sure what to expect but the service from the tour operator was fantastic. Drinks and fruit provided through-out the day and all the guides were very pleasant and friendly.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (68, 101, 23, '2019-10-11T09:30', '2019-10-11T12:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (68, 5, '2019-10-12T09:00', 'this tour was amazing. Cant rate it highly enough.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (69, 102, 23, '2019-01-30T07:00', '2019-01-30T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (69, 5, '2019-01-31T09:00', 'Absolutely loved this tour.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (70, 100, 24, '2019-02-03T05:00', '2019-02-03T09:00', 1, 0, 10, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (70, 5, '2019-02-04T09:00', 'This tour was awesome! Great tour guide and assistants. More snacks, food and drinks offered than we could even consume.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (71, 101, 24, '2019-06-13T09:30', '2019-06-13T12:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (71, 5, '2019-06-14T09:00', 'Very enjoyable tour, great tour guide, interesting stop offs, highly recommended');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (72, 102, 24, '2019-12-07T07:00', '2019-12-07T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (72, 5, '2019-12-08T09:00', 'Excellent tour - well worth doing. Very interesting.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (73, 100, 25, '2019-12-13T05:00', '2019-12-13T09:00', 1, 2, 50, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (73, 4, '2019-12-14T09:00', 'Staff were excellent and the included meals and drinks were great as well. Itinerary was ok');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (74, 101, 25, '2019-05-21T09:30', '2019-05-21T12:00', 3, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (74, 5, '2019-05-22T09:00', 'This was an excellent tour. They took very good care of us, providing drinks, snacks and one Delicious Lunch!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (75, 102, 25, '2019-01-28T07:00', '2019-01-28T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (75, 5, '2019-01-29T09:00', 'A really great day tour, well organised from the moment we were picked up at our hotel.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (76, 100, 26, '2019-09-19T05:00', '2019-09-19T09:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (76, 5, '2019-09-20T09:00', 'We are so glad we invested in this trip. it was outstanding in all aspects');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (77, 101, 26, '2019-04-13T09:30', '2019-04-13T12:00', 1, 1, 50, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (77, 5, '2019-04-14T09:00', 'Was an outstanding day even our kids 16/12 loved it. The tour guides where great full of knowledge and could answer any question the group had.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (78, 102, 26, '2019-06-28T07:00', '2019-06-28T10:00', 2, 3, 200, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (78, 5, '2019-06-29T09:00', 'Very interesting places and varied experiences worth doing');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (79, 100, 27, '2019-11-16T05:00', '2019-11-16T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (79, 5, '2019-11-17T09:00', 'Fantastic tour, endless supply of fruit, drinks and towels. Great staff');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (80, 101, 27, '2019-07-20T09:30', '2019-07-20T12:00', 1, 1, 50, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (80, 5, '2019-07-21T09:00', 'I thoroughly enjoyed my trip. The guide was informative and the lunch was excellent.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (81, 102, 27, '2019-04-09T07:00', '2019-04-09T10:00', 1, 0, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (81, 5, '2019-04-10T09:00', 'We really enjoyed this tour! The staff was amazing and made sure we were comfortable. Tour guide was awesome. The "light" meal they offered was pretty plentiful!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (82, 100, 28, '2019-05-10T05:00', '2019-05-10T09:00', 2, 1, 70, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (82, 3, '2019-05-11T09:00', 'Guide and staff were good. Overall just an ok experience.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (83, 101, 28, '2019-07-22T09:30', '2019-07-22T12:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (83, 5, '2019-07-23T09:00', 'What a fabulous day! From pick up till drop off we got to see so many things and it really was authentic');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (84, 102, 28, '2019-10-02T07:00', '2019-10-02T10:00', 1, 2, 55, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (84, 5, '2019-10-03T09:00', 'This tour is one of the best i ever been on, highly recommended.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (85, 100, 29, '2019-09-23T05:00', '2019-09-23T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (85, 5, '2019-09-24T09:00', 'Awesome. Loved all of it and they took good care of us with constant fluids, fruits and shade.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (86, 101, 29, '2019-12-21T09:30', '2019-12-21T12:00', 1, 0, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (86, 5, '2019-12-22T09:00', 'Good trip, guide really good plenty information');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (87, 102, 29, '2019-04-20T07:00', '2019-04-20T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (87, 5, '2019-04-21T09:00', 'Great trip with very knowledgeable and charismatic guide.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (88, 100, 30, '2019-01-11T05:00', '2019-01-11T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (88, 5, '2019-01-12T09:00', 'The tour was fantastic, the sights great and the staff amazing. Our tour guide spoke such good English and hit us with facts about both Vietnam but also our own country, Australia, that we were unaware of. Highly recommend.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (89, 101, 30, '2019-07-13T09:30', '2019-07-13T12:00', 1, 0, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (89, 3, '2019-07-14T09:00', 'It was not as expected');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (90, 102, 30, '2019-06-20T07:00', '2019-06-20T10:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (90, 5, '2019-06-21T09:00', 'Great trip. The tour guide was knowledgeable and also hilarious.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (91, 100, 31, '2019-09-21T05:00', '2019-09-21T09:00', 2, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (91, 5, '2019-09-22T09:00', 'Awesome guide. Awesome tour, I will gladly recommend.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (92, 101, 31, '2019-07-07T09:30', '2019-07-07T12:00', 1, 2, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (92, 5, '2019-07-08T09:00', 'Great day tour with very likable guide. Absolutely recommendable if you one have one day.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (93, 102, 31, '2019-12-09T07:00', '2019-12-09T10:00', 3, 2, 250, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (93, 5, '2019-12-10T09:00', 'Cold drinks and fresh fruit were on hand the whole day. Lunch was superb.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (94, 100, 32, '2019-02-07T05:00', '2019-02-07T09:00', 1, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (94, 5, '2019-02-08T09:00', 'This was a great tour. Very professional in its organisation, and the locations were great.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (95, 101, 32, '2019-08-19T09:30', '2019-08-19T12:00', 2, 1, 65, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (95, 5, '2019-08-20T09:00', 'Very interesting and informative and the staff were excellent.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (96, 102, 32, '2019-10-08T07:00', '2019-10-08T10:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (96, 5, '2019-10-09T09:00', 'Very interesting and well organised with a guide that provided intimate insight to the culture and sites.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (97, 100, 33, '2019-11-27T05:00', '2019-11-27T09:00', 1, 0, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (97, 5, '2019-11-28T09:00', 'The best tour we did, well worth the money and the time');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (98, 101, 33, '2019-03-17T09:30', '2019-03-17T12:00', 2, 1, 80, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (98, 5, '2019-03-18T09:00', 'Wow, what a day... I will never Forget this trip. How could I better compress Vietnam in one day. Our tour guide was perfcet.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (99, 102, 33, '2019-11-24T07:00', '2019-11-24T10:00', 2, 1, 100, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (99, 5, '2019-11-25T09:00', 'This tour was the best i ever get! Perfect from begin to end! Best guide!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (100, 100, 34, '2019-04-26T05:00', '2019-04-26T09:00', 1, 1, 75, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (100, 5, '2019-04-27T09:00', 'This was amazing, a long day but would not have swapped it for anything, the guides we had were absolutely brilliant. We were taken places that not ll tourists go to and the things we saw were brilliant.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (101, 101, 34, '2019-07-11T09:30', '2019-07-11T12:00', 1, 0, 5, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (101, 5, '2019-07-12T09:00', 'Awesome experience! Travel guide very, very good - friendly, informative and just lovely all-round.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (102, 102, 34, '2019-03-13T07:00', '2019-03-13T10:00', 3, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (102, 5, '2019-03-14T09:00', 'This was an excellent tour! Our guide was very friendly, funny and knowledgeable. The tour was as described. Lunch was at a local home and it was good!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (103, 100, 35, '2019-01-03T05:00', '2019-01-03T09:00', 1, 0, 35, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (103, 5, '2019-01-04T09:00', 'Excellent tour. My daughter and I had a great day and I would highly recommend it. The tour guide was very knowledgable and friendly. They looked after us really well. A memorable trip.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (104, 101, 35, '2019-08-25T09:30', '2019-08-25T12:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (104, 5, '2019-08-26T09:00', 'This was a fun tour, our guide was great and spoke English very well.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (105, 102, 35, '2019-06-02T07:00', '2019-06-02T10:00', 1, 0, 40, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (105, 5, '2019-06-03T09:00', 'This is an amazing tour that if you can you should do it!');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (106, 100, 36, '2019-11-25T05:00', '2019-11-25T09:00', 1, 1, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (106, 5, '2019-11-26T09:00', 'A brilliantly organised and informative trip. We packed lots of things into a very short time frame. Our private guide was fascinating and so full of information.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (107, 101, 36, '2019-02-07T09:30', '2019-02-07T12:00', 2, 1, 60, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (107, 5, '2019-02-08T09:00', 'This was a wonderful tour. It was packed full of activities and included a knowledgeable and attentive tour guide. Highly recommend this tour.');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (108, 102, 36, '2019-09-17T07:00', '2019-09-17T10:00', 1, 0, 45, 'ABC', 'FINISHED');
INSERT INTO public.review(trip_id, rated, post_date, review)
	VALUES (108, 5, '2019-09-18T09:00', 'Great trip lots of interestong and informative. Guide was good. Good excellent great day really felt I had learned but also really enjoyed it');

INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (109, 101, 1, '2019-01-17T07:00', '2019-01-17T10:00', 1, 1, 45, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (110, 102, 3, '2019-05-11T15:00', '2019-05-11T17:00', 2, 0, 50, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (111, 100, 5, '2019-06-06T20:30', '2019-06-16T22:30', 1, 2, 75, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (112, 101, 7, '2019-07-18T13:00', '2019-07-18T15:00', 2, 0, 60, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (113, 102, 9, '2019-02-24T14:00', '2019-02-24T16:00', 1, 1, 45, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (114, 100, 15, '2019-10-30T09:30', '2019-10-30T12:00', 3, 0, 100, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (115, 101, 17, '2019-04-14T10:00', '2019-04-14T13:30', 3, 2, 170, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (116, 102, 25, '2019-06-05T12:00', '2019-06-05T15:00', 1, 0, 10, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (117, 100, 25, '2019-11-01T03:00', '2019-11-01T08:30', 2, 1, 75, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (118, 101, 1, '2019-12-13T07:30', '2019-12-13T10:00', 1, 0, 15, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (119, 102, 30, '2019-01-17T07:00', '2019-01-17T12:00', 3, 1, 90, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (120, 100, 24, '2019-03-18T05:30', '2019-03-18T10:00', 1, 0, 30, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (121, 101, 13, '2019-05-17T07:00', '2019-05-17T11:00', 2, 1, 60, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (122, 102, 26, '2019-09-17T09:30', '2019-09-17T13:00', 1, 0, 20, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (123, 100, 8, '2019-08-19T07:00', '2019-08-19T12:30', 2, 2, 80, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (124, 101, 9, '2019-10-10T15:00', '2019-10-10T19:00', 1, 0, 15, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (125, 102, 8, '2019-09-01T05:30', '2019-09-01T08:30', 2, 1, 40, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (126, 100, 18, '2019-02-07T19:00', '2019-02-07T21:00', 1, 0, 30, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (127, 101, 21, '2019-07-16T07:00', '2019-07-16T10:30', 2, 1, 55, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (128, 102, 31, '2019-09-29T21:00', '2019-09-29T23:00', 1, 0, 35, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (129, 100, 27, '2019-03-24T07:00', '2019-03-24T13:00', 3, 2, 120, 'ABC', 'CANCELLED');
INSERT INTO public.trip(trip_id, traveler_id, post_id, begin_date, finish_date, adult_quantity, children_quantity, fee_paid, transaction_id, status)
	VALUES (130, 102, 19, '2019-09-15T13:00', '2019-09-15T18:30', 1, 0, 30, 'ABC', 'CANCELLED');

SELECT pg_catalog.setval('public.account_account_id_seq', 102, true);
SELECT pg_catalog.setval('public.contract_detail_contract_id_seq', 82, true);
SELECT pg_catalog.setval('public.category_category_id_seq', 6, true);
SELECT pg_catalog.setval('public.locations_locations_id_seq', 30, true);
SELECT pg_catalog.setval('public.post_post_id_seq', 36, true);
SELECT pg_catalog.setval('public.travelerreviews_review_id_seq', 9, true);
SELECT pg_catalog.setval('public.trip_trip_id_seq', 130, true);