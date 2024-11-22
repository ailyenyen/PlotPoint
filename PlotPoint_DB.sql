CREATE DATABASE plot_point;

USE plot_point;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publication_date DATE,
    page_count INT,
    synopsis TEXT,
    average_rating DECIMAL(3, 2) DEFAULT 0
);

CREATE TABLE tags (
    tag_name VARCHAR(30) PRIMARY KEY,
    tag_type ENUM('genre', 'mood') NOT NULL
);

CREATE TABLE book_tags (
    book_id INT,
    tag_name VARCHAR(30),
    PRIMARY KEY (book_id, tag_name),
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_name) REFERENCES tags(tag_name) ON DELETE CASCADE
);

CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    user_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT unique_user_book_review UNIQUE (user_id, book_id)
);


CREATE TABLE shelves (
    shelf_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    shelf_name VARCHAR(50) NOT NULL,
    UNIQUE(user_id, shelf_name),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE shelf_books (
    shelf_book_id INT AUTO_INCREMENT PRIMARY KEY,
    shelf_id INT,
    book_id INT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shelf_id) REFERENCES shelves(shelf_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);


DELIMITER //

CREATE TRIGGER update_average_rating_after_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    DECLARE new_avg_rating DECIMAL(3, 2);

    SELECT AVG(rating) INTO new_avg_rating
    FROM reviews
    WHERE book_id = NEW.book_id;

    UPDATE books
    SET average_rating = new_avg_rating
    WHERE book_id = NEW.book_id;
END;
//

CREATE TRIGGER update_average_rating_after_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    DECLARE new_avg_rating DECIMAL(3, 2);

    SELECT AVG(rating) INTO new_avg_rating
    FROM reviews
    WHERE book_id = NEW.book_id;

    UPDATE books
    SET average_rating = new_avg_rating
    WHERE book_id = NEW.book_id;
END;
//

CREATE TRIGGER create_default_shelves
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO shelves (user_id, shelf_name)
    VALUES (NEW.user_id, 'Read'),
           (NEW.user_id, 'Reading'),
           (NEW.user_id, 'Want to Read');
END //

DELIMITER ;

INSERT INTO users (username, password, is_admin)
VALUES ('admin', 'admin', TRUE);

INSERT INTO books (title, author, publication_date, page_count, synopsis)
VALUES
('The Night Circus', 'Erin Morgenstern', '2011-09-13', 512, 'In a mysterious circus that appears only at night, two magicians are locked in a deadly competition. As their love for one another grows, the fate of the circus and everyone in it hangs in the balance. The night circus is more than it seems—a world of enchantment and peril.'),
('Piranesi', 'Susanna Clarke', '2020-09-15', 245, 'In an endless labyrinth of halls and statues, a man named Piranesi catalogs the wonders of his world. But as he pieces together the truth of his existence, his quiet life begins to unravel. The labyrinth holds secrets he was never meant to discover.'),
('The House in the Cerulean Sea', 'TJ Klune', '2020-03-17', 393, 'Linus Baker is a by-the-book caseworker for magical children. When he’s sent to evaluate a special orphanage, he finds himself questioning everything he believes. The house in the cerulean sea is a heartwarming story of found family and acceptance.'),
('Circe', 'Madeline Miller', '2018-04-10', 393, 'Circe, a daughter of the sun god Helios, is banished to a remote island for defying the gods. There, she hones her powers and confronts both mortals and immortals alike. Her tale is one of love, loss, and defiance in the face of destiny.'),
('The Invisible Life of Addie LaRue', 'V.E. Schwab', '2020-10-06', 448, 'Addie LaRue makes a Faustian bargain to live forever, but she’s cursed to be forgotten by everyone she meets. Her life changes when she meets someone who remembers her. The invisible life of Addie LaRue is a poignant tale of love and legacy.'),
('The 5th Wave', 'Rick Yancey', '2013-05-07', 457, 'In a world devastated by alien invasions, Cassie fights to survive and save her brother. The fifth wave threatens to annihilate humanity, but Cassie refuses to give up hope. Together with unlikely allies, she faces impossible odds.'),
('Daisy Jones & The Six', 'Taylor Jenkins Reid', '2019-03-05', 368, 'In the 1970s, a rock band rises to fame and falls apart in spectacular fashion. Through interviews and flashbacks, the story of Daisy Jones & The Six unfolds. It’s a tale of love, ambition, and the cost of chasing your dreams.'),
('Red Queen', 'Victoria Aveyard', '2015-02-10', 400, 'In a world divided by blood, a common girl discovers she has extraordinary powers. She becomes a pawn in a dangerous game of rebellion and betrayal. The red queen must navigate a world of power, lies, and revolution.'),
('A Court of Thorns and Roses', 'Sarah J. Maas', '2015-05-05', 416, 'When a young huntress kills a wolf, she’s taken to a magical kingdom as punishment. There, she discovers love and danger in equal measure. A court of thorns and roses is a retelling of Beauty and the Beast with a dark twist.'),
('We Were Liars', 'E. Lockhart', '2014-05-13', 242, 'On a private island, a wealthy family hides dark secrets. A young girl’s search for the truth uncovers betrayal and heartbreak. We were liars is a gripping tale of privilege, memory, and the lies we tell ourselves.');

INSERT INTO tags (tag_name, tag_type)
VALUES
('Fantasy', 'genre'),
('Classic', 'genre'),
('Science Fiction', 'genre'),
('Romance', 'genre'),
('Dystopian', 'genre'),
('Adventure', 'genre'),
('Mystery', 'genre'),
('Non-Fiction', 'genre'),
('Contemporary', 'genre'),
('Horror', 'genre'),

('Emotional', 'mood'),
('Dark', 'mood'),
('Tense', 'mood'),
('Hopeful', 'mood'),
('Inspiring', 'mood'),
('Lighthearted', 'mood'),
('Funny', 'mood'),
('Thought-provoking', 'mood'),
('Myterious', 'mood'),
('Heartwarming', 'mood');

INSERT INTO book_tags (book_id, tag_name)
VALUES
    (1, 'Fantasy'),
    (1, 'Romance'),
    (1, 'Mystery'),
    (1, 'Thought-provoking'),
    (1, 'Myterious'),

    (2, 'Fantasy'),
    (2, 'Thought-provoking'),
    (2, 'Myterious'),

    (3, 'Fantasy'),
    (3, 'Heartwarming'),
    (3, 'Emotional'),
    (3, 'Hopeful'),

    (4, 'Fantasy'),
    (4, 'Dark'),
    (4, 'Tense'),

    (5, 'Fantasy'),
    (5, 'Emotional'),
    (5, 'Thought-provoking'),

    (6, 'Dystopian'),
    (6, 'Adventure'),
    (6, 'Emotional'),
    (6, 'Tense'),

    (7, 'Contemporary'),
    (7, 'Emotional'),
    (7, 'Tense'),

    (8, 'Fantasy'),
    (8, 'Dark'),
    (8, 'Tense'),

    (9, 'Fantasy'),
    (9, 'Dark'),
    (9, 'Tense'),

    (10, 'Mystery'),
    (10, 'Horror'),
    (10, 'Dark'),
    (10, 'Tense');

