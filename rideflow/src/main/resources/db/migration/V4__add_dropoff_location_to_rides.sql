ALTER TABLE rides
ADD COLUMN dropoff_location geography(Point, 4326);

CREATE INDEX idx_rides_dropoff_location
ON rides USING GIST (dropoff_location);