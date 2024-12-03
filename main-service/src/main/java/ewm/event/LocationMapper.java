package ewm.event;

import ewm.event.dto.LocationDto;
import ewm.event.model.Location;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {

    public LocationDto toLocationDtoFromLocation(Location location) {

        LocationDto locationDto = new LocationDto();

        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }



}
