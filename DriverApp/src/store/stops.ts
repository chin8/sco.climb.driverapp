import axios from "axios";
import { defineStore } from "pinia";


interface State {
  stops: any[];
  onBoard: any[];
  isOpen: boolean;
  menu_layout: boolean;
  loading: boolean;
  error: null | unknown;
}

export const useStopsStore = defineStore("useStopsStore", {
  state: (): State => ({
    stops: [],
    onBoard: [],
    isOpen: true,
    menu_layout: false,
    loading: false,
    error: null,
  }),
  getters: {
    all_stops: (state) => state.stops,
    the_error: (state) => state.error,
    is_open: (state) => state.isOpen
    },
  actions: {
    async fetchStops(routeId: string) {
      this.loading = true;
      try {
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/stop/TEST/'+routeId);
        const stopsWithModifiedPassengerList = fetchedData.data.map((stop: any) => ({
          ...stop,
          passengerList: stop.passengerList.map((passenger: string) => ({
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
      this.stops?.forEach(stop => {
        stop.passengerList.forEach((item: any) => {
          if (item.passenger === passenger) {
            item.onBoard = true;
          }
        });
      });
    },
    removeOnBoard(passenger: string) {
      this.stops?.forEach(stop => {
        stop.passengerList.forEach((item: any) => {
          if (item.passenger === passenger) {
            item.onBoard = false;
          }
        });
      });
    },
    changeLayout() {
      this.menu_layout = !this.menu_layout;
    },
    toggleModal() {
      this.isOpen = !this.isOpen;
    },
    reset() {
      this.$reset();
    }
  },
});
