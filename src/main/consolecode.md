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

## articles (02.26)
```mariadb
CREATE TABLE `kkirikkiri`.`articles`
(
    `id`                INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    `board_id`          VARCHAR(10)    NOT NULL,
    `user_id`           VARCHAR(50)    NOT NULL,
    `title`             VARCHAR(100)   NOT NULL,
    `menu`              VARCHAR(20)    NULL,
    `menu_name`         VARCHAR(50)    NULL,
    `min_order_price`   INT UNSIGNED   NULL,
    `order_price`       INT UNSIGNED   NULL,
    `delivery_price`    INT UNSIGNED   NULL,
    `order_time`        DATETIME       NULL,
    `pickup_time`       DATETIME       NULL,
    `restaurant`        VARCHAR(50)    NULL,
    `point_lat`         VARCHAR(20)    NULL,
    `point_lng`         VARCHAR(20)    NULL,
    `address_primary`   VARCHAR(100)   NULL     DEFAULT NULL,
    `address_secondary` VARCHAR(100)   NULL     DEFAULT NULL,
    `content`           VARCHAR(10000) NULL     DEFAULT NULL,
    `isShareChecked`    BOOLEAN        NOT NULL,
    `isEntryChecked`    BOOLEAN        NOT NULL,
    `created_at`        DATETIME       NOT NULL DEFAULT NOW(),
    `updated_at`        DATETIME       Null     DEFAULT Null,
    `view`              INT UNSIGNED   NOT NULL DEFAULT 0,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`board_id`) REFERENCES `kkirikkiri`.`boards` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (`user_id`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (`menu`) REFERENCES `kkirikkiri`.`menus` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```

## participants (02.11)
```mariadb
CREATE TABLE `kkirikkiri`.`participants`
(
    `id`                    INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `article_id`            INT UNSIGNED NOT NULL,
    `leader`                VARCHAR(20)  NOT NULL,
    `participants`          VARCHAR(210) NULL     DEFAULT NULL,
    `count`                 INT UNSIGNED NOT NULL DEFAULT 1,
    `participants_nickname` VARCHAR(200) NOT NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`article_id`) REFERENCES `kkirikkiri`.`articles` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`leader`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

## files (02.26)
````mariadb
CREATE TABLE `kkirikkiri`.`files`
(
    `id`                INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`           VARCHAR(20)  NOT NULL,
    `article_id`        INT UNSIGNED NOT NULL,
    `original_filename` VARCHAR(50)  NOT NULL,
    `saved_filename`    VARCHAR(50)  NOT NULL,
    `saved_filepath`    VARCHAR(100) NOT NULL,
    `size`              INT UNSIGNED NOT NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`user_id`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (`article_id`) REFERENCES `kkirikkiri`.`articles` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE

);
````

## messages (02.11)
```mariadb
CREATE TABLE `kkirikkiri`.`messages`
(
    `id`        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `sender`    VARCHAR(20)  NOT NULL,
    `receiver`  VARCHAR(20)  NOT NULL,
    `content`   VARCHAR(300) NOT NULL,
    `timestamp` DATETIME     NOT NULL DEFAULT NOW(),
    `isChecked` BOOLEAN      NOT NULL DEFAULT FALSE,
    `article_id` INT UNSIGNED NULL,
    `usage`     VARCHAR(10)  NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    FOREIGN KEY (`sender`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (`receiver`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (`article_id`) REFERENCES `kkirikkiri`.`articles` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
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

## user_wallet (02.23)
```mariadb
CREATE TABLE `kkirikkiri`.`user_wallet`
(
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_email`   VARCHAR(50)  NOT NULL,
    `cash`         INT UNSIGNED NOT NULL DEFAULT 0,
    `last_charge`  DATETIME     NULL     DEFAULT NULL,
    `last_pay`     DATETIME     NULL     DEFAULT NULL,
    `customer_key` VARCHAR(36)  NOT NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT UNIQUE (`customer_key`),
    CONSTRAINT FOREIGN KEY (`user_email`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```

## group_wallet
```mariadb
CREATE TABLE `kkirikkiri`.`group_wallet`
(
    `id`                 INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `article_id`         INT UNSIGNED NOT NULL,
    `wallet`             INT UNSIGNED NOT NULL DEFAULT 0,
    `payed_participants` varchar(200) NOT NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`article_id`) REFERENCES `kkirikkiri`.`articles` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```

## payment
```mariadb
CREATE TABLE `kkirikkiri`.`payment`
(
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id`     VARCHAR(36)  NOT NULL,
    `customer_key` VARCHAR(36)  NOT NULL,
    `user_email`   VARCHAR(50)  NOT NULL,
    `user_name`    VARCHAR(20)  NOT NULL,
    `amount`       INT UNSIGNED NOT NULL,
    `status`       VARCHAR(10)  NOT NULL DEFAULT 'READY',
    `payment_key`  VARCHAR(36)  NULL,
    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT UNIQUE (`order_id`),
    CONSTRAINT FOREIGN KEY (`customer_key`) REFERENCES `kkirikkiri`.`user_wallet` (`customer_key`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`user_email`) REFERENCES `kkirikkiri`.`users` (`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
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

## User 관련 테이블 변경 > 이걸로 쓰세용 (02.05)
```mariadb
CREATE SCHEMA `kkirikkiri`;

CREATE TABLE `kkirikkiri`.`social_types`
(
    `code` VARCHAR(10) NOT NULL,
    `text` VARCHAR(50) NOT NULL,
    CONSTRAINT PRIMARY KEY (`code`)
);

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

CREATE TABLE `kkirikkiri`.`admins`
(
    `email`     VARCHAR(50) NOT NULL,
    `access_ip` VARCHAR(45),
    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE
);


ALTER TABLE `kkirikkiri`.`stores`
    RENAME COLUMN address_primary TO `address_primary`;

ALTER TABLE `kkirikkiri`.`stores`
    ADD COLUMN `address_secondary` VARCHAR(100) NOT NULL AFTER `address_primary`;

CREATE TABLE `kkirikkiri`.`email_tokens`
(
    `email`       VARCHAR(50)  NOT NULL,
    `code`        VARCHAR(6)   NOT NULL,
    `salt`        VARCHAR(255) NOT NULL,
    `is_verified` BOOLEAN      NOT NULL DEFAULT FALSE,
    `is_used`     BOOLEAN      NOT NULL DEFAULT FALSE,
    `created_at`  DATETIME     NOT NULL DEFAULT NOW(),
    `expires_at`  DATETIME     NOT NULL,
    CONSTRAINT PRIMARY KEY (`email`, `code`, `salt`)
);

ALTER TABLE `kkirikkiri`.`users`
  ADD COLUMN `term_policy_at` DATETIME NOT NULL,
  ADD COLUMN `term_privacy_at` DATETIME NOT NULL,
  ADD COLUMN `term_location_at` DATETIME NOT NULL,
  ADD COLUMN `term_marketing_at` DATETIME NULL;

ALTER TABLE `kkirikkiri`.`stores`
ADD COLUMN `term_policy_at` DATETIME NOT NULL AFTER `report_card_url`,
    ADD COLUMN `term_privacy_at` DATETIME NOT NULL AFTER `term_policy_at`,
    ADD COLUMN `term_third_party_at` DATETIME NOT NULL AFTER `term_privacy_at`,
    ADD COLUMN `term_business_verify_at` DATETIME NOT NULL AFTER `term_third_party_at`,
    ADD COLUMN `term_doc_submission_at` DATETIME NOT NULL AFTER `term_business_verify_at`,
    ADD COLUMN `term_marketing_at` DATETIME NULL AFTER `term_doc_submission_at`;

ALTER TABLE `kkirikkiri`.`users`
ADD COLUMN `telecom` VARCHAR(10) NOT NULL AFTER `birth`;

ALTER TABLE `kkirikkiri`.`users`
    MODIFY COLUMN `status` VARCHAR(20) NOT NULL DEFAULT 'GENERAL';
```


- articles table
  - participant_id, wallet_id columns 지우기 
- participants table
  - participants_nickname 추가하기