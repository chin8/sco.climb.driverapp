import axios from "axios";
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
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/school/TEST/'+instituteId);
        this.schools = fetchedData.data;
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
