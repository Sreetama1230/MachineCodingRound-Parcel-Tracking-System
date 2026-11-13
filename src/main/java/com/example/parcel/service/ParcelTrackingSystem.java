package com.example.parcel.service;

import java.util.List;
import java.util.Optional;

public interface ParcelTrackingSystem {

    // ---------- Level 1 ----------
    void setAttribute(int timestamp, String parcelId, String attribute, int value);

    Optional<Integer> getAttribute(int timestamp, String parcelId, String attribute);

    boolean updateIfMatch(int timestamp, String parcelId, String attribute, int expectedValue, int newValue);

    boolean removeIfMatch(int timestamp, String parcelId, String attribute, int expectedValue);

    // ---------- Level 2 ----------
    /** Returns "attribute(value)" strings, lexicographically sorted by attribute name. */
    List<String> listAttributesByPrefix(int timestamp, String parcelId, String prefix);

    // ---------- Level 3 ----------
    void setAttributeWithTtl(int timestamp, String parcelId, String attribute, int value, int ttl);

    // ---------- Level 4 ----------
    Optional<Integer> getAttributeAt(int timestamp, String parcelId, String attribute, int atTimestamp);
}
