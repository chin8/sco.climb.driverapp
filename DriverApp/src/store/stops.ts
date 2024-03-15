import axios from "axios";
import { defineStore } from "pinia";


interface State {
  stops: null;
  onBoard: null;
  loading: boolean;
  error: null | unknown;
}

export const useStopsStore = defineStore("useStopsStore", {
  state: (): State => ({
    stops: null,
    onBoard: null,
    loading: false,
    error: null,
  }),
  getters: {
    all_stops: (state) => state.stops,
    the_error: (state) => state.error,
  },
  actions: {
    async fetchStops(routeId: string) {
      this.loading = true;
      try {
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/stop/TEST/'+routeId);
        const stopsWithModifiedPassengerList = fetchedData.data.map(stop => ({
          ...stop,
          passengerList: stop.passengerList.map(passenger => ({
            passenger: passenger,
            onBoard: false
          }))
        }));
        this.stops = stopsWithModifiedPassengerList;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },    
    addOnBoard(passenger: string) {
      this.stops.forEach(stop => {
        stop.passengerList.forEach(item => {
          if (item.passenger === passenger) {
            item.onBoard = true;
          }
        });
      });
    },    
    removeOnBoard(passenger: string) {
      this.stops.forEach(stop => {
        stop.passengerList.forEach(item => {
          if (item.passenger === passenger) {
            item.onBoard = false;
          }
        });
      });
    }
  },
});
