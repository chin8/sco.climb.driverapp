import { Geolocation, Position } from '@capacitor/geolocation';

let watchId: any | null;
let currentPosition: Position | null = null;

const geolocalize = () => {
    const options = {
        enableHighAccuracy: true
    };
    watchId = Geolocation.watchPosition(options, (pos, err) => {
        if (err) {
            console.error('Error getting current position:', err);
            return;
        }
        currentPosition = pos;
    });
}

let geoInterval: string | number | NodeJS.Timeout | null | undefined = null;

const startWatchingPosition = (successCb: any, errorCb: any, delay: any) => {
    if (!watchId) {
        geolocalize();
    }

    if (geoInterval === null && typeof successCb === 'function') {
        if (typeof errorCb !== 'function') {
            errorCb = function () {};
        }

        geoInterval = setInterval(function () {
            if (currentPosition) {
                successCb(currentPosition);
            }
        }, delay);
    }
    return geoInterval;
}

const stopWatchingPosition = () => {
    if (watchId) {
        Geolocation.clearWatch({ id: watchId });
        watchId = null;
    }

    if (geoInterval) {
        clearInterval(geoInterval);
        geoInterval = null;
    }
}


const geolocalizeEvent = async (event: any) => {
    try {
    //   var position = currentPosition;
    } catch (error) {
      console.error("Error getting current position:", error);
      throw error;
    }
    if (currentPosition) {
      event.payload['latitude'] = currentPosition.coords.latitude;
      event.payload['longitude'] = currentPosition.coords.longitude;
      event.payload['accuracy'] = currentPosition.coords.accuracy;
    }
    return event;
  };

export { startWatchingPosition, stopWatchingPosition, geolocalizeEvent, geolocalize };