# Console Code
## board
```mariadb
CREATE TABLE `kkirikkiri`.`boards`
(
    `id`           VARCHAR(10) NOT NULL,
    `display_text` VARCHAR(20) NOT NULL,
    `sort_order`   INT         NOT NULL DEFAULT 0,
    `created_at`   DATETIME    NOT NULL DEFAULT NOW(),
    `is_admin_only` TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT PRIMARY KEY (`id`)
);
```
### board 레코드
```mariadb
INSERT INTO `kkirikkiri`.`boards` (`id`, `display_text`)
VALUES ('share', '공구게시판'),
       ('promote', '홍보게시판'),
       ('notice','공지게시판');
```
## menu
```mariadb
CREATE TABLE `kkirikkiri`.`menus`
(
    `id`           VARCHAR(20) NOT NULL,
    `display_text` VARCHAR(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (`id`)
);

```
### menu 레코드
```mariadb
INSERT INTO `kkirikkiri`.`menus` (id, display_text)
VALUES ('chicken', '치킨'),
       ('pizza', '피자'),
       ('hamburger', '햄버거'),
       ('stew', '찜/탕'),
       ('raw', '회'),
       ('japanese-food', '일식'),
       ('chinese-food', '중식'),
       ('western-food', '양식');

```

## articles
```mariadb
CREATE TABLE `kkirikkiri`.`articles`
(
    `id`                INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    `board_id`          VARCHAR(10)    NOT NULL,
    `title`             VARCHAR(100)   NOT NULL,
    `menu`              VARCHAR(20)    NOT NULL,
    `menu_name`         VARCHAR(50)    NOT NULL,
    `min_order_price`   INT UNSIGNED   NOT NULL,
    `order_price`       INT UNSIGNED   NOT NULL,
    `delivery_price`    INT UNSIGNED   NOT NULL,
    `order_time`        DATETIME       NOT NULL,
    `restaurant`        VARCHAR(50)    NOT NULL,
    `pickup_time`       DATETIME       NOT NULL,
    `address_postal`    VARCHAR(5)     NULL     DEFAULT NULL,
    `address_primary`   VARCHAR(100)   NULL     DEFAULT NULL,
    `address_secondary` VARCHAR(100)   NOT NULL,
    `content`           VARCHAR(10000) NULL     DEFAULT NULL,
    `nickname`          VARCHAR(30)    NOT NULL,
    `created_at`        DATETIME       NOT NULL DEFAULT NOW(),
    `updated_at`        DATETIME       Null     DEFAULT Null,
    `view`              INT UNSIGNED   NOT NULL DEFAULT 0,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`board_id`) REFERENCES `kkirikkiri`.`boards` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (`menu`) REFERENCES `kkirikkiri`.`menus` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```

## serviceFilter
```mariadb
CREATE TABLE `kkirikkiri`.`service_filter`
(
    `code`         VARCHAR(20) NOT NULL,
    `display_text` VARCHAR(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (`code`)
);
```
### serviceFilter 레코드
```mariadb
INSERT INTO `kkirikkiri`.`service_filter`
VALUES ('pay', '결제/환불'),
       ('user', '사용자 계정/프로필'),
       ('event', '이벤트/혜택'),
       ('restrict', '이용제한');

```
## service
```mariadb
CREATE TABLE `kkirikkiri`.`services`
(
    `id`         INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    `question`   VARCHAR(50)   NOT NULL,
    `answer`     VARCHAR(1000) NOT NULL,
    `filter`     VARCHAR(20)   NOT NULL,
    `created_at` DATETIME      NOT NULL DEFAULT NOW(),
    `updated_at` DATETIME      NULL DEFAULT NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`filter`) REFERENCES `kkirikkiri`.`service_filter` (`code`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```
# user
## socialTypes
```mariadb
CREATE TABLE `kkirikkiri`.`social_types`
(
    `code` VARCHAR(10) NOT NULL,
    `text` VARCHAR(50) NOT NULL,
    CONSTRAINT PRIMARY KEY (`code`)
);
```
## users
```mariadb
CREATE TABLE `kkirikkiri`.`users`
(
    `email`             VARCHAR(50)  NOT NULL,
    `password`          VARCHAR(150) NOT NULL,
    `nickname`          VARCHAR(20)  NOT NULL,
    `name`              VARCHAR(20)  NOT NULL,
    `birth`             DATE         NOT NULL,
    `contact`           VARCHAR(15)  NOT NULL,
    `address_primary`   VARCHAR(200) NOT NULL,
    `address_secondary` VARCHAR(100) NOT NULL,
    `is_admin`          BOOLEAN      NOT NULL DEFAULT 0,
    `is_boss`           BOOLEAN      NOT NULL DEFAULT 0,
    `store_email`       VARCHAR(50)  NULL,
    `status`            VARCHAR(10)  NOT NULL DEFAULT 'GENERAL',
    `my_point`          INT          NOT NULL DEFAULT 0,
    `review_total`      INT                   DEFAULT 0,
    `review_count`      INT                   DEFAULT 0,
    `review_avg`        DOUBLE                DEFAULT 0.0,
    `created_at`        DATETIME     NOT NULL DEFAULT NOW(),
    `updated_at`        DATETIME     NOT NULL,
    `social_type_code`  VARCHAR(10)  NULL     DEFAULT NULL,
    `social_id`         VARCHAR(50)  NULL     DEFAULT NULL,
    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT UNIQUE (`nickname`),
    CONSTRAINT FOREIGN KEY (`store_email`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`social_type_code`) REFERENCES `kkirikkiri`.`social_types` (`code`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);
```
## stores
```mariadb
CREATE TABLE `kkirikkiri`.`stores`
(
    `email`           VARCHAR(50)  NOT NULL,
    `business_number` VARCHAR(10)  NOT NULL,
    `store_name`      VARCHAR(50)  NOT NULL,
    `business_type`   VARCHAR(20)  NOT NULL,
    `store_address`   VARCHAR(200) NOT NULL,
    `store_contact`   VARCHAR(15)  NOT NULL,
    `operating_hours` VARCHAR(100),
    `license_url`     VARCHAR(255) NOT NULL,
    `report_card_url` VARCHAR(255) NOT NULL,
    `approval_status` VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    `reject_reason`   VARCHAR(255),
    `applied_at`      DATETIME     NOT NULL,
    `approved_at`     DATETIME,
    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE
);
```
## admins
```mariadb
CREATE TABLE `kkirikkiri`.`admins`
(
    `email`     VARCHAR(50) NOT NULL,
    `access_ip` VARCHAR(45),
    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE
);
```
