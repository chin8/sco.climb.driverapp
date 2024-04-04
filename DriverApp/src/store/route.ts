import axios from "axios";
import { defineStore } from "pinia";

interface State {
  routes: null;
  loading: boolean;
  error: null | unknown;
  selectedRoute: null | object;
}

export const useRouteStore = defineStore("routeStore", {
  state: (): State => ({
    routes: null,
    loading: false,
    error: null,
    selectedRoute: null,
  }),
  getters: {
    all_routes: (state) => state.routes,
    the_error: (state) => state.error,
    selected_route: (state) => state.selectedRoute,
  },
  actions: {
    async fetchRoutes(instituteId: string, schoolId: string) {
      this.loading = true;
      try {
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/route/TEST/'+instituteId+'/'+schoolId);
        this.routes = fetchedData.data;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(selected: object){
      this.selectedRoute = selected;
      console.log(this.selectedRoute)
    }
  },
});
