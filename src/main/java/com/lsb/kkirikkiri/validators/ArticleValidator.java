package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.enums.BoardId;
import com.lsb.kkirikkiri.enums.Menu;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ArticleValidator {
    private static final String TITLE_REGEX = "^[\\s\\S]{1,100}$";
    private static final String MENU_NAME_REGEX = "^[\\da-zA-Z가-힣`~!@#$%^&*()_=+\\[\\]{}\\\\|;:'\",<.>/? -]{1,50}$";
    private static final String RESTAURANT_REGEX = "^[\\da-zA-Z가-힣`~!@#$%^&*()_=+\\[\\]{}\\\\|;:'\",<.>/? -]{1,100}$";
    private static final String ADDRESS_SECONDARY_REGEX = "^[\\da-zA-Z가-힣`~!@#$%^&*()_=+\\[\\]{}\\\\|;:'\",<.>/? -]{1,100}$";
    private static final String CONTENT_REGEX = "^[\\s\\S]{1,10000}$";

    public boolean validateBoardId(String boardId) {
        if (boardId == null) {
            return false;
        }
        boolean result = false;
        for (BoardId b : BoardId.values()) {
            if (boardId.equals(b.code)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean validateBoardId(@NonNull ArticleEntity articleEntity) {
        return validateBoardId(articleEntity.getBoardId());
    }

    public boolean validateTitle(String title) {
        return title != null
                && title.matches(TITLE_REGEX)
                && ValidatorUtils.isLengthInBetween(title, 1, 100);
    }

    public boolean validateTitle(@NonNull ArticleEntity articleEntity) {
        return validateTitle(articleEntity.getTitle());
    }

    public boolean validateMenu(String menu) {
        if (menu == null) {
            return false;
        }
        boolean result = false;
        for (Menu m : Menu.values()) {
            if (menu.equals(m.code)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean validateMenu(@NonNull ArticleEntity articleEntity) {
        return validateMenu(articleEntity.getMenu());
    }

    public boolean validateMenuName(String menuName) {
        return menuName != null
                && menuName.matches(MENU_NAME_REGEX)
                && ValidatorUtils.isLengthInBetween(menuName, 1, 50);
    }

    public boolean validateMenuName(@NonNull ArticleEntity articleEntity) {
        return validateMenuName(articleEntity.getMenuName());
    }

    public boolean validateMinOrderPrice(Integer minOrderPrice) {
        return minOrderPrice != null
                && minOrderPrice >= 0
                && minOrderPrice <= 9_999_999;
    }

    public boolean validateMinOrderPrice(@NonNull ArticleEntity articleEntity) {
        return validateMinOrderPrice(articleEntity.getMinOrderPrice());
    }

    public boolean validateOrderPrice(Integer orderPrice) {
        return orderPrice != null
                && orderPrice >= 0
                && orderPrice <= 9_999_999;
    }

    public boolean validateOrderPrice(@NonNull ArticleEntity articleEntity) {
        return validateOrderPrice(articleEntity.getOrderPrice());
    }

    public boolean validateDeliveryPrice(Integer deliveryPrice) {
        return deliveryPrice != null
                && deliveryPrice >= 0
                && deliveryPrice <= 9_999_999;
    }

    public boolean validateDeliveryPrice(@NonNull ArticleEntity articleEntity) {
        return validateDeliveryPrice(articleEntity.getDeliveryPrice());
    }

    public boolean validateOrderTime(LocalDateTime orderTime) {
        return orderTime != null;
    }

    public boolean validateOrderTime(@NonNull ArticleEntity articleEntity) {
        return validateOrderTime(articleEntity.getOrderTime());
    }

    public boolean validateRestaurant(String restaurant) {
        return restaurant != null
                && restaurant.matches(RESTAURANT_REGEX)
                && ValidatorUtils.isLengthInBetween(restaurant, 1, 100);
    }

    public boolean validateRestaurant(@NonNull ArticleEntity articleEntity) {
        return validateRestaurant(articleEntity.getRestaurant());
    }

    public boolean validatePickupTime(LocalDateTime pickupTime) {
        return pickupTime != null;
    }

    public boolean validatePickupTime(@NonNull ArticleEntity articleEntity) {
        return validatePickupTime(articleEntity.getPickupTime());
    }

    public boolean validateAddressSecondary(String addressSecondary) {
        return addressSecondary != null
                && addressSecondary.matches(ADDRESS_SECONDARY_REGEX)
                && ValidatorUtils.isLengthInBetween(addressSecondary, 1, 100);
    }

    public boolean validateAddressSecondary(@NonNull ArticleEntity articleEntity) {
        return validateAddressSecondary(articleEntity.getAddressSecondary());
    }

    public boolean validateContent(String content) {
        return content != null
                && content.matches(CONTENT_REGEX)
                && ValidatorUtils.isLengthInBetween(content, 1, 10000);
    }

    public boolean validateContent(@NonNull ArticleEntity articleEntity) {
        return validateContent(articleEntity.getContent());
    }

    public boolean validateView(int view) {
        return view >= 0;
    }

    public boolean validateView(@NonNull ArticleEntity articleEntity) {
        return validateView(articleEntity.getView());
    }
}
