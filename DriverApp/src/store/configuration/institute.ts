import { defineStore } from "pinia";

interface State {
  institutes: null;
  loading: boolean;
  error: null | unknown;
  selectedInstitute: null | string;
}

export const useInstituteStore = defineStore("InstituteStore", {
  state: (): State => ({
    institutes: null,
    loading: false,
    error: null,
    selectedInstitute: null,
  }),
  getters: {
    all_institutes: (state) => state.institutes,
    the_error: (state) => state.error,
    selected_institute_name: (state) => state.selectedInstitute,
  },
  actions: {
    async fetchInstitutes() {
      this.loading = true;
      try {
        //https://climbdev.smartcommunitylab.it/v3/api/institute/TEST
        const fetchedData = await fetch(`src/tmp-data/institute.json`).then(
          (response) => response.json()
        );
        this.institutes = fetchedData;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(selected: string){
      this.selectedInstitute = selected;
    }
  },
});
