package com.example.parcel;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Level1Test extends BaseParcelTest {

    @Test
    void setAndGet() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        assertEquals(Optional.of(4), system.getAttribute(2, "PKG-001", "priority"));
    }

    @Test
    void getMissingParcelOrAttribute() {
        assertEquals(Optional.empty(), system.getAttribute(1, "NOPE", "priority"));
        system.setAttribute(2, "PKG-001", "priority", 4);
        assertEquals(Optional.empty(), system.getAttribute(3, "PKG-001", "weight"));
    }

    @Test
    void setOverwritesCurrentValue() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.setAttribute(2, "PKG-001", "zone_code", 6);
        system.setAttribute(3, "PKG-001", "priority", 9);
        assertEquals(Optional.of(9), system.getAttribute(4, "PKG-001", "priority"));
        assertEquals(Optional.of(6), system.getAttribute(5, "PKG-001", "zone_code"));
    }

    @Test
    void parcelsAreIsolated() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        system.setAttribute(2, "PKG-002", "priority", 7);
        assertEquals(Optional.of(4), system.getAttribute(3, "PKG-001", "priority"));
        assertEquals(Optional.of(7), system.getAttribute(4, "PKG-002", "priority"));
    }

    @Test
    void updateIfMatch() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        assertTrue(system.updateIfMatch(2, "PKG-001", "priority", 4, 9));
        assertEquals(Optional.of(9), system.getAttribute(3, "PKG-001", "priority"));
        assertFalse(system.updateIfMatch(4, "PKG-001", "priority", 4, 12));
        assertEquals(Optional.of(9), system.getAttribute(5, "PKG-001", "priority"));
    }

    @Test
    void updateIfMatchOnMissing() {
        assertFalse(system.updateIfMatch(1, "NOPE", "priority", 4, 9));
        system.setAttribute(2, "PKG-001", "priority", 4);
        assertFalse(system.updateIfMatch(3, "PKG-001", "weight", 4, 9));
    }

    @Test
    void removeIfMatch() {
        system.setAttribute(1, "PKG-001", "zone_code", 6);
        assertTrue(system.removeIfMatch(2, "PKG-001", "zone_code", 6));
        assertEquals(Optional.empty(), system.getAttribute(3, "PKG-001", "zone_code"));
    }

    @Test
    void removeIfMatchWrongValueKeepsAttribute() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        assertFalse(system.removeIfMatch(2, "PKG-001", "priority", 9));
        assertEquals(Optional.of(4), system.getAttribute(3, "PKG-001", "priority"));
    }

    @Test
    void removeMissing() {
        assertFalse(system.removeIfMatch(1, "NOPE", "priority", 4));
        system.setAttribute(2, "PKG-001", "priority", 4);
        assertFalse(system.removeIfMatch(3, "PKG-001", "weight", 4));
    }

    @Test
    void canSetAgainAfterRemove() {
        system.setAttribute(1, "PKG-001", "priority", 4);
        assertTrue(system.removeIfMatch(2, "PKG-001", "priority", 4));
        system.setAttribute(3, "PKG-001", "priority", 8);
        assertEquals(Optional.of(8), system.getAttribute(4, "PKG-001", "priority"));
    }
}
