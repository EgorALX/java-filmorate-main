# java-filmorate

Приложение отслеживает популярность фильмов.
Добавляли пользователям лайки созданные ими фильмы,
тем самым поднимая их в рейтинге.
Приложение было написано с использованием Spring Boot, включая протокол REST.
Данные хранятся в базе данных.
Подробно описанная спроектированная схема базы данных указана ниже.

## Схема дазы данных

![Схема базы данных](./DATA-BASE.png)

## Таблица films:

Хранит информацию о фильмах и содержит в себе:

* film_id - идентификатор фильма;

* name - название фильма;

* description - описание фильма;

* release_date - дата выхода фильма;

* duration - продолжительность фильма.

## Таблица mpa_ids:

Хранит рейтинг Ассоциации кинокомпаний, которому соответствует фильм, и содержит в себе:

* film_id - идентификатор фильма (дает отсылку к таблице film);

* MPA_id - идентификатор возрастного ограничения (дает отсылку к таблице motion_picture_association).

## Таблица motion_picture_association

Хранит наименования рейтингов Ассоциации кинокомпаний с соответствующим индентификатором и содержит в себе:

* MPA_id - идентификатор возрастного ограничения;

* MPA_name - наименование рейтинга.

### Наименования рейтингов имеют фиксированные значение, а именно:

- G — у фильма нет возрастных ограничений;
- PG — детям рекомендуется смотреть фильм с родителями;
- PG-13 — детям до 13 лет просмотр не желателен;
- R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого;
- NC-17 — лицам до 18 лет просмотр запрещён.

## Таблица film_genres:

Хранит информацию о жанрах, к которым принадлежит фильм, и содержит в себе:

* film_id - идентификатор фильма (дает отсылку к таблице film);

* genre_id - идентификатор наименования заголовка жанра (дает отсылку к таблице genres).

## Таблица genres:

Хранит наименования жанров с соответствующим индентификатором и содержит в себе:

* genre_id - идентификатор жанра;

* genre_name - наименование жанра.

### Наименования жанров имеют фиксированные значение, а именно:

- Комедия
- Драма;
- Мультфильм;
- Триллер;
- Документальный;
- Боевик.

## Таблица films_directors:

Хранит информацию о режиссерах, которые работали над фильмом, и содержит в себе:

* film_id - идентификатор фильма (дает отсылку к таблице film);

* director_id - идентификатор режиссера (дает отсылку к таблице directors).

## Таблица directors:

Хранит имена режиссеров с соответствующим индентификатором и содержит в себе:

* director_id - идентификатор режиссера;

* director_name - имя режиссера;

## Таблица users:

Хранит информацию пользователей и содержит в себе:

* user_id - идентификатор пользователя;

* name - имя пользователя;

* email - почта пользователя;

* login - логин пользователя;

* birthday - дата рождения пользователя.

## Таблица friends:

Хранит статус дружбы пользователя с другими пользователями и содержит в себе:

* user_id - идентификатор пользователя (дает отсылку к таблице user);

* friends_id - идентификаторы друзей пользователя;

* status - статус друга по связке user_id-friends_id.

## Таблица likes:

Хранит "лайки" пользователей для конкретных фильмов.
В данной таблице стоит расценивать идентификаторы пользователей как "лайки".
Таблица содержит в себе:

* film_id - идентификатор фильма (дает отсылку к таблице film);

* User_id - идентификатор пользователя поставивший лайк фильму (дает отсылку к таблице user).

## Таблица events:

Хранит информацию о событиях, сделанными пользователями и содержит в себе:

* event_id - идентификатор события;

* entity_id - идентификатор сущности, с которой произошло событие;

* user_id - идентификатор пользователя (дает отсылку к таблице user);

* event_type - наименование события;

* operation - наименования операции в событии.

### Наименования событий имеют фиксированные значение, а именно:

- LIKE;
- REVIEW;
- FRIEND.

### Наименования событий имеют фиксированные значение, а именно:

- REMOVE;
- ADD;
- UPDATE.

## Таблица reviews:

Хранит отзывы пользователей на определенные фильмы и содержит в себе:

* review_id - идентификатор отзыва (дает отсылку к таблице film_reviews);

* is_positive - тип отзыва(может иметь тип положительного или негативного);

* user_id - идентификатор пользователя (дает отсылку к таблице user);

* film_id - идентификатор фильма (дает отсылку к таблице film);

* useful - стутс отзыва по полезности (может иметь статус полезного или бесполезного).

## Таблица film_reviews:

Хранит информацию по конкретному отзыву и содержит в себе:

* review_id - идентификатор отзыва ;

* user_id - идентификатор пользователя (дает отсылку к таблице user);

* is_positive - тип отзыва(может иметь тип положительного или негативного).
