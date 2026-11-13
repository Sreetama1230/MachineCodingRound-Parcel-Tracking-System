package com.example.parcel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Level2Test extends BaseParcelTest {

    private void seed() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.setAttribute(2, "PKG-001", "zone_code", 30);
        system.setAttribute(3, "PKG-001", "product_code", 20);
        system.setAttribute(4, "PKG-001", "cost_estimate", 100);
    }

    @Test
    void singleMatch() {
        seed();
        assertEquals(List.of("cost_estimate(100)"), system.listAttributesByPrefix(5, "PKG-001", "co"));
        assertEquals(List.of("zone_code(30)"), system.listAttributesByPrefix(6, "PKG-001", "z"));
    }

    @Test
    void multipleMatchesSortedLexicographically() {
        seed();
        assertEquals(List.of("priority(4)", "product_code(20)"),
                system.listAttributesByPrefix(7, "PKG-001", "p"));
    }

    @Test
    void emptyPrefixReturnsEverythingSorted() {
        seed();
        assertEquals(List.of("cost_estimate(100)", "priority(4)", "product_code(20)", "zone_code(30)"),
                system.listAttributesByPrefix(8, "PKG-001", ""));
    }

    @Test
    void noMatchOrMissingParcel() {
        seed();
        assertEquals(List.of(), system.listAttributesByPrefix(9, "PKG-001", "xyz"));
        assertEquals(List.of(), system.listAttributesByPrefix(10, "NOPE", ""));
    }

    @Test
    void removedAttributesAreNotListed() {
        seed();
        system.removeIfMatch(5, "PKG-001", "priority", 4);
        assertEquals(List.of("product_code(20)"), system.listAttributesByPrefix(6, "PKG-001", "p"));
    }

    @Test
    void listReflectsLatestValue() {
        seed();
        system.setAttribute(5, "PKG-001", "priority", 9);
        assertEquals(List.of("priority(9)", "product_code(20)"),
                system.listAttributesByPrefix(6, "PKG-001", "p"));
    }
}
