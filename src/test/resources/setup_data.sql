INSERT INTO tags (name) VALUES
('tag1'), ('tag2'), ('other');

INSERT INTO posts (title, text, likes_count) VALUES
('Post 1', 'Text 1', 0),
('Post 2', 'Text 2', 0),
('Post 3', 'Text 3', 0),
('Post 4', 'Text 4', 0);


INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 2),
(4, 3);
