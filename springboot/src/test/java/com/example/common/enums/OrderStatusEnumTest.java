package com.example.common.enums;

import org.junit.jupiter.api.Test;

import static com.example.common.enums.OrderStatusEnum.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusEnumTest {

    @Test
    void businessCanOnlyShipPendingOrders() {
        assertTrue(TO_SHIP.canTransitTo(SHIPPED, RoleEnum.BUSINESS));
        assertFalse(TO_SHIP.canTransitTo(DONE, RoleEnum.BUSINESS));
        assertFalse(SHIPPED.canTransitTo(SHIPPED, RoleEnum.BUSINESS));
        assertFalse(TO_SHIP.canTransitTo(CANCELLED, RoleEnum.BUSINESS));
    }

    @Test
    void userCanCancelPendingOrConfirmShipped() {
        assertTrue(TO_SHIP.canTransitTo(CANCELLED, RoleEnum.USER));
        assertTrue(SHIPPED.canTransitTo(DONE, RoleEnum.USER));
        assertFalse(TO_SHIP.canTransitTo(SHIPPED, RoleEnum.USER));
        assertFalse(TO_SHIP.canTransitTo(DONE, RoleEnum.USER));
        assertFalse(SHIPPED.canTransitTo(CANCELLED, RoleEnum.USER));
        assertFalse(DONE.canTransitTo(CANCELLED, RoleEnum.USER));
    }

    @Test
    void unknownTargetIsRejected() {
        assertNull(OrderStatusEnum.of("随便写"));
        assertFalse(TO_SHIP.canTransitTo(null, RoleEnum.ADMIN));
    }

    @Test
    void onlyDoneOrCancelledIsFinished() {
        assertTrue(DONE.isFinished());
        assertTrue(CANCELLED.isFinished());
        assertFalse(TO_SHIP.isFinished());
        assertFalse(SHIPPED.isFinished());
    }
}
