import { defineStore } from "pinia";

interface State {
  schools: null;
  loading: boolean;
  error: null | unknown;
  selectedSchool: null | string;
}

export const useSchoolStore = defineStore("schoolStore", {
  state: (): State => ({
    schools: null,
    loading: false,
    error: null,
    selectedSchool: null,
  }),
  getters: {
    all_schools: (state) => state.schools,
    the_error: (state) => state.error,
    selected_school: (state) => state.selectedSchool,
  },
  actions: {
    async fetchSchools(instituteId: string) {
      this.loading = true;
      try {
        //https://climbdev.smartcommunitylab.it/v3/api/school/TEST/fe21135b-991d-4f1e-ae0f-3d15e15b4ee7
        const fetchedData = await fetch(`src/tmp-data/schools.json`).then(
          (response) => response.json()
        );
        this.schools = fetchedData;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(selected: string){
      this.selectedSchool = selected;
    }
  },
});
