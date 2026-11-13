package com.example.parcel;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Level4Test extends BaseParcelTest {

    @Test
    void historyAcrossOverwrites() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.setAttribute(5, "PKG-001", "priority", 8);
        system.setAttribute(10, "PKG-001", "priority", 12);

        assertEquals(Optional.empty(), system.getAttributeAt(15, "PKG-001", "priority", 0));
        assertEquals(Optional.of(4), system.getAttributeAt(15, "PKG-001", "priority", 1));
        assertEquals(Optional.of(4), system.getAttributeAt(15, "PKG-001", "priority", 3));
        assertEquals(Optional.of(8), system.getAttributeAt(15, "PKG-001", "priority", 5));
        assertEquals(Optional.of(8), system.getAttributeAt(15, "PKG-001", "priority", 7));
        assertEquals(Optional.of(12), system.getAttributeAt(15, "PKG-001", "priority", 10));
        assertEquals(Optional.of(12), system.getAttributeAt(15, "PKG-001", "priority", 12));
    }

    @Test
    void historySurvivesRemoval() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.removeIfMatch(5, "PKG-001", "priority", 4);
        assertEquals(Optional.of(4), system.getAttributeAt(10, "PKG-001", "priority", 3));
        assertEquals(Optional.empty(), system.getAttributeAt(10, "PKG-001", "priority", 5));
        assertEquals(Optional.empty(), system.getAttributeAt(10, "PKG-001", "priority", 7));
    }

    @Test
    void historyRespectsTtl() {
        system.setAttributeWithTtl(10, "PKG-001", "priority", 5, 20);
        assertEquals(Optional.empty(), system.getAttributeAt(40, "PKG-001", "priority", 9));
        assertEquals(Optional.of(5), system.getAttributeAt(40, "PKG-001", "priority", 10));
        assertEquals(Optional.of(5), system.getAttributeAt(40, "PKG-001", "priority", 20));
        assertEquals(Optional.of(5), system.getAttributeAt(40, "PKG-001", "priority", 29));
        assertEquals(Optional.empty(), system.getAttributeAt(40, "PKG-001", "priority", 30));
        assertEquals(Optional.empty(), system.getAttributeAt(40, "PKG-001", "priority", 35));
    }

    @Test
    void historyThroughUpdateIfMatch() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.updateIfMatch(5, "PKG-001", "priority", 4, 9);
        assertEquals(Optional.of(4), system.getAttributeAt(10, "PKG-001", "priority", 4));
        assertEquals(Optional.of(9), system.getAttributeAt(10, "PKG-001", "priority", 5));
    }

    @Test
    void historyAfterRemoveThenSetAgain() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.removeIfMatch(5, "PKG-001", "priority", 4);
        system.setAttribute(8, "PKG-001", "priority", 6);
        assertEquals(Optional.of(4), system.getAttributeAt(20, "PKG-001", "priority", 2));
        assertEquals(Optional.empty(), system.getAttributeAt(20, "PKG-001", "priority", 6));
        assertEquals(Optional.of(6), system.getAttributeAt(20, "PKG-001", "priority", 9));
    }

    @Test
    void overwriteBeforeExpiryClosesOldVersion() {
        system.setAttributeWithTtl(1, "PKG-001", "priority", 5, 100); // would last until 101
        system.setAttribute(10, "PKG-001", "priority", 7);
        assertEquals(Optional.of(5), system.getAttributeAt(20, "PKG-001", "priority", 9));
        assertEquals(Optional.of(7), system.getAttributeAt(20, "PKG-001", "priority", 50));
    }

    @Test
    void missingParcelOrAttribute() {
        assertEquals(Optional.empty(), system.getAttributeAt(1, "NOPE", "priority", 0));
        system.setAttribute(2, "PKG-001", "priority", 1);
        assertEquals(Optional.empty(), system.getAttributeAt(3, "PKG-001", "weight", 2));
    }
}
