import axios from "axios";
import { defineStore } from "pinia";


interface State {
  stops: null;
  loading: boolean;
  error: null | unknown;
}

export const useStopsStore = defineStore("useStopsStore", {
  state: (): State => ({
    stops: null,
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
        this.stops = fetchedData.data;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    }
  },
});
