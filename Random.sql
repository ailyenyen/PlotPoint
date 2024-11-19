CREATE DATABASE book_tracker;

USE book_tracker;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    tag_id INT,
    PRIMARY KEY (book_id, tag_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
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

INSERT INTO books (title, author, publication_date, page_count, synopsis)
VALUES
('The Midnight Library', 'Matt Haig', '2020-09-29', 288, 'Between life and death, there is a library filled with books of infinite possibilities. Nora Seed explores the lives she could have lived, uncovering truths about regret, hope, and the meaning of happiness. Every book represents a choice, and every choice shapes a story.'),
('The Silent Patient', 'Alex Michaelides', '2019-02-05', 336, 'A famous painter is accused of murdering her husband but refuses to speak a word. A determined psychotherapist takes on the challenge of unraveling her silence. As he delves deeper, he uncovers shocking revelations that turn the case on its head.'),
('The Night Circus', 'Erin Morgenstern', '2011-09-13', 512, 'In a mysterious circus that appears only at night, two magicians are locked in a deadly competition. As their love for one another grows, the fate of the circus and everyone in it hangs in the balance. The night circus is more than it seems—a world of enchantment and peril.'),
('The Gilded Wolves', 'Roshani Chokshi', '2019-01-15', 400, 'Set in 1889 Paris, a group of unlikely allies embarks on a dangerous heist to uncover ancient secrets. The gilded wolves guard treasures of unimaginable power. Together, they must navigate betrayal, ambition, and love to succeed.'),
('Piranesi', 'Susanna Clarke', '2020-09-15', 245, 'In an endless labyrinth of halls and statues, a man named Piranesi catalogs the wonders of his world. But as he pieces together the truth of his existence, his quiet life begins to unravel. The labyrinth holds secrets he was never meant to discover.'),
('The City We Became', 'N.K. Jemisin', '2020-03-24', 437, 'In a world where cities come alive, New York City is on the brink of awakening. Its avatars, five individuals representing the city’s boroughs, must band together to fight an ancient evil. The city we became is as vibrant and diverse as its champions.'),
('Project Hail Mary', 'Andy Weir', '2021-05-04', 496, 'Ryland Grace wakes up alone on a spaceship, with no memory of how he got there. As he pieces together his mission, he realizes he’s humanity’s last hope to save Earth. Project Hail Mary is a tale of ingenuity, survival, and unlikely friendship.'),
('Mexican Gothic', 'Silvia Moreno-Garcia', '2020-06-30', 320, 'In 1950s Mexico, a young woman is sent to rescue her cousin from a remote and eerie mansion. As she uncovers the sinister truth behind its walls, she faces horrors beyond imagination. Mexican Gothic is a haunting tale of family and madness.'),
('The House in the Cerulean Sea', 'TJ Klune', '2020-03-17', 393, 'Linus Baker is a by-the-book caseworker for magical children. When he’s sent to evaluate a special orphanage, he finds himself questioning everything he believes. The house in the cerulean sea is a heartwarming story of found family and acceptance.'),
('Circe', 'Madeline Miller', '2018-04-10', 393, 'Circe, a daughter of the sun god Helios, is banished to a remote island for defying the gods. There, she hones her powers and confronts both mortals and immortals alike. Her tale is one of love, loss, and defiance in the face of destiny.'),
('The Invisible Life of Addie LaRue', 'V.E. Schwab', '2020-10-06', 448, 'Addie LaRue makes a Faustian bargain to live forever, but she’s cursed to be forgotten by everyone she meets. Her life changes when she meets someone who remembers her. The invisible life of Addie LaRue is a poignant tale of love and legacy.'),
('An Absolutely Remarkable Thing', 'Hank Green', '2018-09-25', 352, 'A young woman becomes an overnight sensation after discovering a mysterious statue. As the world reacts to the statues, she grapples with fame, humanity, and her place in it all. An absolutely remarkable thing explores how we connect in an age of social media.'),
('The 5th Wave', 'Rick Yancey', '2013-05-07', 457, 'In a world devastated by alien invasions, Cassie fights to survive and save her brother. The fifth wave threatens to annihilate humanity, but Cassie refuses to give up hope. Together with unlikely allies, she faces impossible odds.'),
('Daisy Jones & The Six', 'Taylor Jenkins Reid', '2019-03-05', 368, 'In the 1970s, a rock band rises to fame and falls apart in spectacular fashion. Through interviews and flashbacks, the story of Daisy Jones & The Six unfolds. It’s a tale of love, ambition, and the cost of chasing your dreams.'),
('Red Queen', 'Victoria Aveyard', '2015-02-10', 400, 'In a world divided by blood, a common girl discovers she has extraordinary powers. She becomes a pawn in a dangerous game of rebellion and betrayal. The red queen must navigate a world of power, lies, and revolution.'),
('A Court of Thorns and Roses', 'Sarah J. Maas', '2015-05-05', 416, 'When a young huntress kills a wolf, she’s taken to a magical kingdom as punishment. There, she discovers love and danger in equal measure. A court of thorns and roses is a retelling of Beauty and the Beast with a dark twist.'),
('We Were Liars', 'E. Lockhart', '2014-05-13', 242, 'On a private island, a wealthy family hides dark secrets. A young girl’s search for the truth uncovers betrayal and heartbreak. We were liars is a gripping tale of privilege, memory, and the lies we tell ourselves.'),
('The Book Thief', 'Markus Zusak', '2005-03-14', 592, 'During World War II, a young girl finds solace in stealing books and sharing them. Narrated by Death, her story reveals the resilience of the human spirit. The book thief is a testament to the power of words and hope in dark times.'),
('Eleanor Oliphant Is Completely Fine', 'Gail Honeyman', '2017-05-09', 390, 'Eleanor Oliphant lives a carefully ordered life, avoiding human interaction. When she unexpectedly befriends a coworker, her life begins to change. Eleanor Oliphant is completely fine is a quirky and heartwarming story of growth and healing.'),
('It Ends With Us', 'Colleen Hoover', '2016-08-02', 384, 'A young woman falls for a charming neurosurgeon but finds herself caught in a cycle of abuse. She must make difficult choices to break free. It ends with us is an emotional and empowering tale of love and strength.'),
('All the Light We Cannot See', 'Anthony Doerr', '2014-05-06', 544, 'During World War II, a blind French girl and a German boy are drawn together by fate. Their paths cross in occupied France, revealing the resilience of the human spirit. All the light we cannot see is a story of love, war, and redemption.'),
('To Sleep in a Sea of Stars', 'Christopher Paolini', '2020-09-15', 878, 'In the distant future, a scientist discovers an ancient alien relic. Her discovery thrusts her into an interstellar war that could decide the fate of humanity. To sleep in a sea of stars is an epic tale of exploration and survival.'),
('A Man Called Ove', 'Fredrik Backman', '2012-08-27', 337, 'Ove, a grumpy old man, has his life turned upside down when a lively young family moves next door. Their unlikely friendship transforms his world. A man called Ove is a heartwarming story about love, loss, and second chances.'),
('The Paris Library', 'Janet Skeslien Charles', '2021-02-09', 352, 'Based on the true story of the American Library in Paris during World War II, librarians risk everything to protect books and the people who love them. The Paris library is a tale of courage, friendship, and resilience.'),
('Beach Read', 'Emily Henry', '2020-05-19', 384, 'Two writers with opposing styles agree to swap genres for the summer. As they work through writer’s block, their professional rivalry turns into something more. Beach read is a fun and heartfelt story of love and second chances.'),
('People We Meet on Vacation', 'Emily Henry', '2021-05-11', 384, 'Two best friends with opposite personalities take yearly vacations together. A falling out threatens their friendship, but one last trip could change everything. People we meet on vacation is a charming and romantic tale of friendship and love.'),
('The Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '2017-06-13', 400, 'A reclusive Hollywood icon tells the story of her glamorous life and seven marriages. The seven husbands of Evelyn Hugo is a tale of love, ambition, and the cost of fame.'),
('Malibu Rising', 'Taylor Jenkins Reid', '2021-06-01', 384, 'Four famous siblings throw a legendary party that spirals out of control. Malibu rising is a story of family, secrets, and the ties that bind us.'),
('The Way of Kings', 'Brandon Sanderson', '2010-08-31', 1007, 'In a world wracked by storms, a soldier turned slave discovers an ancient power. A young woman seeks forbidden knowledge to save her family. Their paths converge in a world on the brink of war.'),
('The Shadow of the Wind', 'Carlos Ruiz Zafón', '2001-01-01', 487, 'A boy discovers a mysterious book in the Cemetery of Forgotten Books. As he investigates the book’s author, he uncovers a web of intrigue and tragedy. The story reveals the power of literature and the human spirit.'),
('The Martian', 'Andy Weir', '2014-02-11', 369, 'An astronaut is stranded on Mars after a failed mission. Using his wits and limited resources, he fights to survive while trying to signal Earth. It is a gripping tale of ingenuity, survival, and hope.'),
('Pachinko', 'Min Jin Lee', '2017-02-07', 496, 'A multi-generational tale of a Korean family in Japan. It explores themes of resilience, cultural identity, and sacrifice. The family’s story unfolds against the backdrop of history and discrimination.'),
('The Sword of Kaigen', 'M.L. Wang', '2019-02-19', 651, 'A warrior mother and her son fight to defend their home. As war looms, they grapple with hidden truths and family bonds. This is a tale of sacrifice, courage, and the cost of conflict.'),
('The Little Prince', 'Antoine de Saint-Exupéry', '1943-04-06', 96, 'A pilot stranded in the desert meets a young prince from another world. Through their conversations, they explore themes of love, loss, and human connection. The story is a timeless allegory of innocence and discovery.'),
('The Iliad', 'Homer', '-750-01-01', 704, 'An epic poem recounting the events of the Trojan War. It focuses on the wrath of Achilles and the fates of men and gods. The Iliad is a cornerstone of ancient Greek literature and mythology.'),
('Ella Enchanted', 'Gail Carson Levine', '1997-04-25', 240, 'A young girl is cursed with obedience, unable to disobey commands. She embarks on a journey to break the spell while discovering her inner strength. It is a charming retelling of the Cinderella story with a twist.'),
('The Perks of Being a Wallflower', 'Stephen Chbosky', '1999-02-01', 213, 'A shy teenager navigates high school, friendships, and first love. Told through letters, it explores themes of mental health and self-discovery. The story is a poignant and heartfelt coming-of-age tale.'),
('Good Omens', 'Neil Gaiman & Terry Pratchett', '1990-05-01', 412, 'An angel and a demon team up to prevent the apocalypse. As they try to locate the misplaced Antichrist, chaos ensues. This is a hilarious and satirical tale of good, evil, and everything in between.'),
('The Color of Magic', 'Terry Pratchett', '1983-11-24', 285, 'A hapless wizard embarks on a dangerous journey across the Discworld. Alongside a naive tourist, he encounters humor, danger, and absurdity. This is the first novel in the beloved Discworld series.'),
('Elantris', 'Brandon Sanderson', '2005-05-21', 638, 'A city of gods falls into decay after its inhabitants are struck by a curse. A young prince investigates the mystery while fighting political intrigue. Elantris is a tale of hope, sacrifice, and redemption.'),
('Warbreaker', 'Brandon Sanderson', '2009-06-09', 688, 'Two sisters are thrust into a dangerous political conflict. In a world where magic is fueled by color, they must navigate secrets and alliances. Warbreaker is a story of love, betrayal, and unexpected heroism.');

-- Inserting sample tags for genres
INSERT INTO tags (tag_name, tag_type)
VALUES
('Fantasy', 'genre'),
('Classic', 'genre'),
('Science Fiction', 'genre'),
('Romance', 'genre'),
('Dystopian', 'genre'),
('Adventure', 'genre'),
('Drama', 'genre'),
('Mystery', 'genre');
('Thriller', 'genre'),
('Literary Fiction', 'genre'),
('Non-Fiction', 'genre'),
('Contemporary', 'genre'),
('Historical Fiction', 'genre'),
('Horror', 'genre'),
('Magical Realism', 'genre'),

('Emotional', 'mood'),
('Dark', 'mood'),
('Tense', 'mood'),
('Hopeful', 'mood'),
('Inspiring', 'mood'),
('Lighthearted', 'mood'),
('Relaxing', 'mood'),
('Funny', 'mood'),
('Exciting', 'mood'),
('Thought-provoking', 'mood'),
('Heartwarming', 'mood');

INSERT INTO book_tags (book_id, tag_name)
VALUES 
    (1, 'Fantasy'),
    (1, 'Magical Realism'),
    (1, 'Emotional'),
    (1, 'Thought-provoking'),

    (2, 'Mystery'),
    (2, 'Thriller'),
    (2, 'Dark'),
    (2, 'Tense'),

