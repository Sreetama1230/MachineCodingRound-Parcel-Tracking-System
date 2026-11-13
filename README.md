# Parcel Tracking System (Spring Boot, Java 17)

In-memory machine-coding practice project with 4 progressive levels.

| Level | Feature | Method(s) |
|---|---|---|
| 1 | Basic attribute ops | `setAttribute`, `getAttribute`, `updateIfMatch`, `removeIfMatch` |
| 2 | Prefix listing | `listAttributesByPrefix` |
| 3 | TTL | `setAttributeWithTtl` |
| 4 | History | `getAttributeAt` |


## Run
```
mvn test            # run unit tests
mvn spring-boot:run # start REST API on :8080
```

Example calls:
```
curl -X PUT localhost:8080/api/parcels/PKG-001/attributes/priority \
     -H 'Content-Type: application/json' -d '{"timestamp":1,"value":4}'
curl -X PUT localhost:8080/api/parcels/PKG-001/attributes/zone_code \
     -H 'Content-Type: application/json' -d '{"timestamp":2,"value":6,"ttl":20}'
curl 'localhost:8080/api/parcels/PKG-001/attributes/priority?timestamp=3'
curl 'localhost:8080/api/parcels/PKG-001/attributes/priority?timestamp=3&at=1'
curl 'localhost:8080/api/parcels/PKG-001/attributes?timestamp=3&prefix=p'
```

## Practice tip
Delete `ParcelTrackingSystemImpl` (keep the interface and tests) and rebuild it level by level, running that level's test class after each one.

## Implementation

Parcel Tracking System: Quick Notes
Two maps:
- hm: Stores the latest DTO (current state).
- historicalRecordMap: Stores all historical DTOs in a list.
Level-wise implementation:
1. Level 1: Set and get attributes.
2. Level 2: Conditional update and removal.
3. Level 3: TTL-based expiration using expiryTime.
4. Level 4: Historical lookup using getAttributeAt().
Important logic:
- On update, close the previous DTO and create a new one.
- On removal, remove from hm but retain history in historicalRecordMap.
- On set, close the previous DTO and create a new one.
- Never modify an already expired DTO.
- Validity condition:

```
dto.getTimestamp() <= atTimestamp
    && atTimestamp < dto.getExpiryTime()
```

Remember: hm stores the current state; historicalRecordMap preserves the complete history.

# MachineCodingRound-Parcel-Tracking-System
