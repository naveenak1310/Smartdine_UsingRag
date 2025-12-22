CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    location VARCHAR(255)
);

CREATE TABLE restaurants (
    id INT PRIMARY KEY,
    cuisine VARCHAR(50),
    description TEXT,
    location VARCHAR(100),
    name VARCHAR(150),
    price_range VARCHAR(20),
    rating DECIMAL(2,1),
    tags VARCHAR(255),
    image_url TEXT,
    images TEXT,
    area VARCHAR(100),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6)
);

CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    restaurant_id INT,
    persons INT,
    date VARCHAR(255),
    time VARCHAR(255),
    note VARCHAR(500),
    email VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT,
    user_id INT,
    rating INT,
    text VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE restaurant_tags (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT,
    tag VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

INSERT INTO restaurants VALUES
(1,'North Indian','Filling thalis and butter roti','Coimbatore','Naveen''s Dhaba','cheap',4.4,'spicy,comfort,thali','https://images.pexels.com/photos/9609860/pexels-photo-9609860.jpeg','https://images.pexels.com/photos/9609860/pexels-photo-9609860.jpeg,https://images.pexels.com/photos/2474661/pexels-photo-2474661.jpeg,https://images.pexels.com/photos/20446381/pexels-photo-20446381.jpeg','Peelamedu',NULL,NULL),
(2,'Italian','Thin crust and cheese burst pizzas','Coimbatore','Cheezy Bite Pizza','medium',4.6,'cheesy,pizza,treat','https://images.pexels.com/photos/825661/pexels-photo-825661.jpeg','https://images.pexels.com/photos/825661/pexels-photo-825661.jpeg,https://images.pexels.com/photos/803290/pexels-photo-803290.jpeg,https://images.pexels.com/photos/2619967/pexels-photo-2619967.jpeg','RS Puram',NULL,NULL),
(3,'Hyderabadi','Spicy Hyderabadi biryani','Coimbatore','Royal Biryani House','medium',4.4,'biryani,spicy,comfort','https://images.pexels.com/photos/6260921/pexels-photo-6260921.jpeg','https://images.pexels.com/photos/6260921/pexels-photo-6260921.jpeg,https://images.pexels.com/photos/19418741/pexels-photo-19418741.jpeg,https://images.pexels.com/photos/34217290/pexels-photo-34217290.jpeg','Gandhipuram',NULL,NULL),
(4,'Cafe','Tea, snacks and maggi for late nights','Coimbatore','Chai Stories','cheap',4.2,'snacks,comfort,chai','https://images.pexels.com/photos/2013749/pexels-photo-2013749.jpeg','https://images.pexels.com/photos/2013749/pexels-photo-2013749.jpeg,https://images.pexels.com/photos/904616/pexels-photo-904616.jpeg,https://images.pexels.com/photos/1298613/pexels-photo-1298613.jpeg','Race Course',NULL,NULL),
(5,'Mixed','Simple meals at student-friendly prices','Coimbatore','Budget Bites Canteen','cheap',4.0,'budget,comfort,meals','https://images.pexels.com/photos/14883758/pexels-photo-14883758.jpeg','https://images.pexels.com/photos/14883758/pexels-photo-14883758.jpeg,https://images.pexels.com/photos/3904035/pexels-photo-3904035.jpeg,https://images.pexels.com/photos/18488298/pexels-photo-18488298.jpeg','Saravanampatti',NULL,NULL),
(6,'Street Food','Chat, pav bhaji and bhel puri','Coimbatore','Bombay Spice Corner','cheap',4.1,'snacks,spicy,street','https://images.pexels.com/photos/34507158/pexels-photo-34507158.jpeg','https://images.pexels.com/photos/34507158/pexels-photo-34507158.jpeg,https://images.pexels.com/photos/12365247/pexels-photo-12365247.jpeg,https://images.pexels.com/photos/35008223/pexels-photo-35008223.jpeg','Town Hall',NULL,NULL),
(7,'Healthy','Salads, rice bowls and fresh juices','Coimbatore','Healthy Greens Bowl','medium',4.0,'healthy,salad,fitness','https://images.pexels.com/photos/1213710/pexels-photo-1213710.jpeg','https://images.pexels.com/photos/1213710/pexels-photo-1213710.jpeg,https://images.pexels.com/photos/1233319/pexels-photo-1233319.jpeg,https://images.pexels.com/photos/161440/smoothie-fruit-vegetables-salad-beetroot-carrots-1.jpeg','Kavundampalayam',NULL,NULL),
(8,'Desserts','Cakes, waffles and ice creams','Coimbatore','Sweet Cravings','medium',4.3,'dessert,sweet,waffle','https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg','https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg,https://images.pexels.com/photos/1055272/pexels-photo-1055272.jpeg,https://images.pexels.com/photos/131045/pexels-photo-131045.jpeg','Ukkadam',NULL,NULL),
(9,'North Indian','Tandoori starters and rich curries','Coimbatore','Tandoor Treat','expensive',4.5,'tandoori,spicy,treat','https://images.pexels.com/photos/35066816/pexels-photo-35066816.jpeg','https://images.pexels.com/photos/35066816/pexels-photo-35066816.jpeg,https://images.pexels.com/photos/5031938/pexels-photo-5031938.jpeg,https://images.pexels.com/photos/33430556/pexels-photo-33430556.jpeg','Sitra',NULL,NULL),
(10,'Asian','Noodles, fried rice and momos','Coimbatore','Sizzling Wok','medium',4.0,'spicy,asian,noodles','https://images.pexels.com/photos/2347311/pexels-photo-2347311.jpeg','https://images.pexels.com/photos/2347311/pexels-photo-2347311.jpeg,https://images.pexels.com/photos/2098135/pexels-photo-2098135.jpeg,https://images.pexels.com/photos/723198/pexels-photo-723198.jpeg','Singanallur',NULL,NULL),
(11,'Fast Food','Kathi rolls and sandwiches','Coimbatore','Roadside Roll Co.','cheap',4.5,'fastfood,snacks,spicy','https://images.pexels.com/photos/13065191/pexels-photo-13065191.jpeg','https://images.pexels.com/photos/13065191/pexels-photo-13065191.jpeg,https://images.pexels.com/photos/4491396/pexels-photo-4491396.jpeg,https://images.pexels.com/photos/1603901/pexels-photo-1603901.jpeg','Peelamedu',NULL,NULL),
(12,'South Indian','Idli, dosa and filter coffee','Coimbatore','Veggie Delight','cheap',4.7,'south indian,comfort','https://images.pexels.com/photos/4331489/pexels-photo-4331489.jpeg','https://images.pexels.com/photos/4331489/pexels-photo-4331489.jpeg,https://images.pexels.com/photos/20422138/pexels-photo-20422138.jpeg,https://images.pexels.com/photos/20422124/pexels-photo-20422124.jpeg','RS Puram',NULL,NULL),
(13,'Fast Food','Fries, burgers and late night snacks','Coimbatore','Midnight Munch','cheap',3.8,'fastfood,night,snacks','https://images.pexels.com/photos/1583884/pexels-photo-1583884.jpeg','https://images.pexels.com/photos/1583884/pexels-photo-1583884.jpeg,https://images.pexels.com/photos/133578/pexels-photo-133578.jpeg,https://images.pexels.com/photos/1639565/pexels-photo-1639565.jpeg','Gandhipuram',NULL,NULL),
(14,'Healthy','Protein bowls and smoothies','Coimbatore','Soul Bowl','medium',4.3,'healthy,fitness,salad','https://images.pexels.com/photos/6544260/pexels-photo-6544260.jpeg','https://images.pexels.com/photos/6544260/pexels-photo-6544260.jpeg,https://images.pexels.com/photos/103566/pexels-photo-103566.jpeg,https://images.pexels.com/photos/2291070/pexels-photo-2291070.jpeg','Race Course',NULL,NULL),
(15,'Cafe','Coffee, fries and a study friendly space','Coimbatore','Cafe Notebook','medium',4.2,'study,fries,coffee','https://images.pexels.com/photos/2638019/pexels-photo-2638019.jpeg','https://images.pexels.com/photos/2638019/pexels-photo-2638019.jpeg,https://images.pexels.com/photos/115740/pexels-photo-115740.jpeg,https://images.pexels.com/photos/2956954/pexels-photo-2956954.jpeg','Saravanampatti',NULL,NULL),
(16,'North Indian','A modern desi restaurant with buttery gravies and tandoori specials.','Coimbatore','La Spice Hub','medium',4.5,'spicy,comfort,biryani','https://images.pexels.com/photos/17050759/pexels-photo-17050759.jpeg','https://images.pexels.com/photos/17050759/pexels-photo-17050759.jpeg,https://images.pexels.com/photos/35079286/pexels-photo-35079286.jpeg,https://images.pexels.com/photos/9609838/pexels-photo-9609838.jpeg','Town Hall',NULL,NULL),
(17,'Italian','Authentic Italian pizza, pasta and molten cheese dishes.','Coimbatore','De Pasta Story','expensive',4.5,'cheesy,comfort,pizza','https://images.pexels.com/photos/1049620/pexels-photo-1049620.jpeg','https://images.pexels.com/photos/1049620/pexels-photo-1049620.jpeg,https://images.pexels.com/photos/1438672/pexels-photo-1438672.jpeg,https://images.pexels.com/photos/769969/pexels-photo-769969.jpeg','Kavundampalayam',NULL,NULL),
(18,'Indian','Legendary Hyderabadi biryani loved for its spice and aroma.','Coimbatore','HMR Biryani','medium',4.5,'spicy,biryani,comfort','https://images.pexels.com/photos/4439740/pexels-photo-4439740.jpeg','https://images.pexels.com/photos/4439740/pexels-photo-4439740.jpeg,https://images.pexels.com/photos/35071812/pexels-photo-35071812.jpeg,https://images.pexels.com/photos/2673353/pexels-photo-2673353.jpeg','Ukkadam',NULL,NULL),
(19,'Japanese','Premium Japanese sushi, sashimi and ramen bowls.','Coimbatore','Sushi World','expensive',4.3,'healthy,seafood','https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg','https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg,https://images.pexels.com/photos/858501/pexels-photo-858501.jpeg,https://images.pexels.com/photos/1907229/pexels-photo-1907229.jpeg','Sitra',NULL,NULL),
(20,'Ice Cream','we provide ice creams with customized scoops','Coimbatore','ibaco','expensive',4.3,'icecream,cone','https://images.pexels.com/photos/1352271/pexels-photo-1352271.jpeg','https://images.pexels.com/photos/1352271/pexels-photo-1352271.jpeg,https://images.pexels.com/photos/5060455/pexels-photo-5060455.jpeg,https://images.pexels.com/photos/2677814/pexels-photo-2677814.jpeg','saibaba colony',11.0255194,76.9369624);

INSERT INTO restaurant_tags (restaurant_id, tag) VALUES
(3,'biryani'),(3,'spicy'),(3,'comfort'),
(4,'snacks'),(4,'comfort'),(4,'chai'),
(6,'snacks'),(6,'spicy'),(6,'street'),(6,'pav bhaji'),(6,'bhel puri'),
(7,'healthy'),(7,'salad'),(7,'fitness'),
(8,'dessert'),(8,'sweet'),(8,'waffle'),(8,'cake'),(8,'ice cream'),
(9,'tandoori'),(9,'spicy'),(9,'treat'),
(10,'spicy'),(10,'asian'),(10,'noodles'),
(11,'fastfood'),(11,'snacks'),(11,'spicy'),
(12,'south indian'),(12,'comfort'),(12,'breakfast'),(12,'idli'),(12,'dosa'),(12,'coffee'),
(13,'fastfood'),(13,'night'),(13,'snacks'),(13,'fries'),(13,'burger'),
(14,'healthy'),(14,'fitness'),(14,'salad'),
(15,'study'),(15,'fries'),(15,'coffee'),
(16,'spicy'),(16,'comfort'),(16,'biryani'),
(17,'cheesy'),(17,'comfort'),(17,'pizza'),(17,'pasta'),
(18,'spicy'),(18,'biryani'),(18,'comfort'),
(19,'healthy'),(19,'seafood'),(19,'premium'),(19,'sushi'),
(20,'icecream'),(20,'cone');
