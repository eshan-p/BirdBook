import { Point } from "geojson";

export interface Coordinates {
    latitude: number;
    longitude: number;
}

//Format fields
export const pointToCoords = (point: Point): Coordinates => {
    const [longitude, latitude] = point.coordinates;
    return {latitude, longitude};
};

//Convert coords to GeoJSON point
export const coordsToPoint = (latitude: number, longitude: number): Point => {
    return {
        type: 'Point',
        coordinates: [longitude, latitude]
    }
}

//Format coords for display
export const formatCoords = (coords: Coordinates, decimals = 4): string => {
    return `${coords.latitude.toFixed(decimals)}, ${coords.longitude.toFixed(decimals)}`;
}

export const reverseCoordsToCityState = async (coords: Coordinates): Promise<string> => {
    const {latitude, longitude} = coords;
    try{
        const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`)
        const data = await response.json();
        const city = data.address.city || data.address.town || data.address.village;
        const state = data.address.state;
        if(city && state){
            return `${city}, ${state}`;
        }else{
            return formatCoords(coords);
        }
    }catch(err){
        console.error("Error reversing Geo Code: " + err);
        return formatCoords(coords)
    }
}