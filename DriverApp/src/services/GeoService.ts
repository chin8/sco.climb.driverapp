import { Geolocation } from '@capacitor/geolocation';

var watchId = null;
var position = null;

const geolocalize = () => {
    const options = {
        enableHighAccuracy: true
    };
    watchId = Geolocation.watchPosition(options, (pos, err) => {
        if (err) {
            console.error('Error getting current position:', err);
            return;
        }
        position = pos;
    });
}

var geoInterval = null;

const startWatchingPosition = (successCb, errorCb, delay) => {
    if (!watchId) {
        geolocalize();
    }

    if (geoInterval === null && typeof successCb === 'function') {
        if (typeof errorCb !== 'function') {
            errorCb = function () {};
        }

        geoInterval = setInterval(function () {
            if (position) {
                successCb(position);
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

    if (!!geoInterval) {
        clearInterval(geoInterval);
        geoInterval = null;
    }
}


const geolocalizeEvent = async (event) => {
    try {
      var position = await Geolocation.getCurrentPosition();
    } catch (error) {
      console.error("Error getting current position:", error);
      throw error;
    }
    console.log(position);
    if (position) {
      event.payload['latitude'] = position.coords.latitude;
      event.payload['longitude'] = position.coords.longitude;
      event.payload['accuracy'] = position.coords.accuracy;
    }
    return event;
  };

export { startWatchingPosition, stopWatchingPosition, geolocalizeEvent };