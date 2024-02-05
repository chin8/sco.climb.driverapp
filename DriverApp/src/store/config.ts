import { defineStore } from "pinia";

interface State {
  institutes: null;
  schools: null;
  routes: null;
  volunteers: null;
  loading: boolean;
  error: null | unknown;
  selectedInstitute: null;
  selectedSchool: null;
  selectedRoute: null;
  selectedVolunteers: null;
}

export const useConfigStore = defineStore("configStore", {
  state: (): State => ({
    institutes: null,
    schools: null,
    routes: null,
    volunteers: null,
    loading: false,
    error: null,
    selectedInstitute: null,
    selectedSchool: null,
    selectedRoute: null,
    selectedVolunteers: null,
  }),
  getters: {
    all_institutes: (state) => state.institutes,
    all_schools: (state) => state.schools,
    all_routes: (state) => state.routes,
    all_volunteers: (state) => state.volunteers,
    the_error: (state) => state.error,
    selected_institute: (state) => state.selectedInstitute,
    selected_school: (state) => state.selectedSchool,
    selected_route: (state) => state.selectedRoute,
    selected_volunteers: (state) => state.selectedVolunteers,
  },
  actions: {
    async fetchData(dataKey: string) {
      this.loading = true;
      try {
        const fetchedData = await fetch(`src/tmp-data/${dataKey}.json`).then(
          (response) => response.json()
        );
        switch (dataKey) {
          case "institute":
            this.institutes = fetchedData;
            console.log(this.institutes);
            break;
          case "schools":
            this.schools = fetchedData;
            break;
          case "route":
            this.routes = fetchedData;
            break;
          case "volunteers":
            this.volunteers = fetchedData;
            break;
          default:
            break;
        }
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(dataKey: string, selectedId: any) {
      switch (dataKey) {
        case "institutes":
          this.selectedInstitute = selectedId;
          break;
        case "schools":
          this.selectedSchool = selectedId;
          break;
        case "routes":
          this.selectedRoute = selectedId;
          break;
        case "volunteers":
          this.selectedVolunteers = selectedId;
          break;
        default:
          break;
      }
    }
  },
});
