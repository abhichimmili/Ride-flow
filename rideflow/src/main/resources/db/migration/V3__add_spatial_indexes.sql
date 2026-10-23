CREATE INDEX IF NOT EXISTS idx_driver_profiles_location
    ON driver_profiles
    USING GIST (location);

CREATE INDEX IF NOT EXISTS idx_rides_pickup_location
    ON rides
    USING GIST (pickup_location);