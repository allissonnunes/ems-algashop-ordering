package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LoyaltyPointsTest {

    @Test
    void shouldCreateLoyaltyPoints() {
        final LoyaltyPoints loyaltyPoints = new LoyaltyPoints(100);
        assertThat(loyaltyPoints.value()).isEqualTo(100);
    }

    @Test
    void shouldThrowExceptionIfTryToCreateLoyaltyPointsWithNegativeValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new LoyaltyPoints(-1))
                .withMessage("loyaltyPoints cannot be negative");
    }

    @Test
    void shouldAddLoyaltyPoints() {
        final LoyaltyPoints loyaltyPoints = new LoyaltyPoints(100);
        final LoyaltyPoints pointsAfterAddition = loyaltyPoints.add(new LoyaltyPoints(50));
        assertThat(pointsAfterAddition.value()).isEqualTo(150);
    }

    @Test
    void shouldThrowExceptionIfTryToAddNullLoyaltyPoints() {
        final LoyaltyPoints loyaltyPoints = new LoyaltyPoints(100);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> loyaltyPoints.add(null))
                .withMessage("loyaltyPointsToAdd cannot be null");
    }

    @Test
    void shouldThrowExceptionIfTryToAddZeroLoyaltyPoints() {
        final LoyaltyPoints loyaltyPoints = new LoyaltyPoints(100);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(new LoyaltyPoints(0)))
                .withMessage("loyaltyPointsToAdd cannot be negative or zero");
    }

}