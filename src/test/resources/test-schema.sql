-- Таблица с постами
CREATE TABLE IF NOT EXISTS posts (
 id bigserial PRIMARY KEY,
 title varchar(256) NOT NULL,
 text text NOT NULL,
 tags varchar(1024),
 likes_count bigint DEFAULT 0
);
-- Таблица с комментариями
CREATE TABLE IF NOT EXISTS comments (
 id bigserial PRIMARY KEY,
 post_id bigint NOT NULL REFERENCES posts(id) ON
DELETE
	CASCADE,
	text text NOT NULL
);
