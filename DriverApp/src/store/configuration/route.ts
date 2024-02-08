import { defineStore } from "pinia";

interface State {
  routes: null;
  loading: boolean;
  error: null | unknown;
  selectedRoute: null | string;
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
        //https://climbdev.smartcommunitylab.it/v3/api/route/TEST/fe21135b-991d-4f1e-ae0f-3d15e15b4ee7/418184aa-26df-4a25-a026-c8511db469b2
        const fetchedData = await fetch(`src/tmp-data/route.json`).then(
          (response) => response.json()
        );
        this.routes = fetchedData;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(selected: string){
      this.selectedRoute = selected;
    }
  },
});
