package com.example.parcel;

import com.example.parcel.service.ParcelTrackingSystem;
import com.example.parcel.service.ParcelTrackingSystemImpl;
import org.junit.jupiter.api.BeforeEach;

abstract class BaseParcelTest {

    protected ParcelTrackingSystem system;

    @BeforeEach
    void setUp() {
        system = new ParcelTrackingSystemImpl();
    }
}
