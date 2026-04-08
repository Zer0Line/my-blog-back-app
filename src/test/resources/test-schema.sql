-- Таблица с постами
CREATE TABLE IF NOT EXISTS posts (
 id bigserial PRIMARY KEY,
 title varchar(256) NOT NULL,
 text text NOT NULL,
 likes_count bigint DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_posts_title ON posts(title);

-- Таблица с тегами
CREATE TABLE IF NOT EXISTS tags (
 id bigserial PRIMARY KEY,
 name varchar(128) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);

-- Таблица связи many-to-many между posts и tags
CREATE TABLE IF NOT EXISTS post_tags (
 post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
 tag_id bigint NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
 PRIMARY KEY (post_id, tag_id)
);

-- Таблица с комментариями
CREATE TABLE IF NOT EXISTS comments (
 id bigserial PRIMARY KEY,
 post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
 text text NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id);

