package com.example.parcel;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Level3Test extends BaseParcelTest {

    @Test
    void validBeforeExpiryAndGoneAtExpiry() {
        system.setAttributeWithTtl(10, "PKG-001", "priority", 5, 20);
        assertEquals(Optional.of(5), system.getAttribute(10, "PKG-001", "priority"));
        assertEquals(Optional.of(5), system.getAttribute(15, "PKG-001", "priority"));
        assertEquals(Optional.of(5), system.getAttribute(29, "PKG-001", "priority"));
        assertEquals(Optional.empty(), system.getAttribute(30, "PKG-001", "priority"));
        assertEquals(Optional.empty(), system.getAttribute(40, "PKG-001", "priority"));
    }

    @Test
    void expiryOnlyAffectsThatAttribute() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 5);
        system.setAttribute(2, "PKG-001", "weight", 12);
        assertEquals(Optional.empty(), system.getAttribute(6, "PKG-001", "priority"));
        assertEquals(Optional.of(12), system.getAttribute(6, "PKG-001", "weight"));
    }

    @Test
    void expiredAttributesAreNotListed() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 5);
        system.setAttribute(2, "PKG-001", "product_code", 20);
        assertEquals(List.of("priority(5)", "product_code(20)"), system.listAttributesByPrefix(5, "PKG-001", "p"));
        assertEquals(List.of("product_code(20)"), system.listAttributesByPrefix(6, "PKG-001", "p"));
    }

    @Test
    void plainSetRemovesPreviousTtl() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 5);
        system.setAttribute(3, "PKG-001", "priority", 7);
        assertEquals(Optional.of(7), system.getAttribute(100, "PKG-001", "priority"));
    }

    @Test
    void ttlSetOverwritesBeforeExpiry() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 100);
        system.setAttributeWithTtl(10, "PKG-001", "priority", 6, 5);
        assertEquals(Optional.of(6), system.getAttribute(14, "PKG-001", "priority"));
        assertEquals(Optional.empty(), system.getAttribute(15, "PKG-001", "priority"));
    }

    @Test
    void conditionalOpsFailOnExpired() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 5);
        assertFalse(system.updateIfMatch(6, "PKG-001", "priority", 5, 9));
        assertFalse(system.removeIfMatch(7, "PKG-001", "priority", 5));
        assertEquals(Optional.empty(), system.getAttribute(8, "PKG-001", "priority"));
    }

    @Test
    void updateKeepsOriginalExpiry() {
        system.setAttributeWithTtl(10, "PKG-001", "priority", 5, 20); // expires at 30
        assertTrue(system.updateIfMatch(20, "PKG-001", "priority", 5, 8));
        assertEquals(Optional.of(8), system.getAttribute(29, "PKG-001", "priority"));
        assertEquals(Optional.empty(), system.getAttribute(30, "PKG-001", "priority"));
    }

    @Test
    void differentExpiryTimes() {
        system.setAttributeWithTtl(1, "PKG-001", "a", 1, 10); // until 11
        system.setAttributeWithTtl(2, "PKG-001", "b", 2, 3);  // until 5
        assertEquals(Optional.of(1), system.getAttribute(10, "PKG-001", "a"));
        assertEquals(Optional.empty(), system.getAttribute(10, "PKG-001", "b"));
    }
}
