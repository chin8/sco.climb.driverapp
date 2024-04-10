import axios from "axios";
import { defineStore } from "pinia";

interface State {
  volunteers: null | any;
  loading: boolean;
  error: null | unknown;
  selectedVolunteers: null | any;
}

export const useVolunteersStore = defineStore("volunteersStore", {
  state: (): State => ({
    volunteers: null,
    loading: false,
    error: null,
    selectedVolunteers: [],
  }),
  getters: {
    all_volunteers: (state) => state.volunteers,
    the_error: (state) => state.error,
    selected_volunteers: (state) => state.selectedVolunteers,
  },
  actions: {
    async fetchVolunteers(instituteId: string, schoolId: string) {
      this.loading = true;
      try {
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/volunteer/TEST/' + instituteId + '/' + schoolId);
        this.volunteers = fetchedData.data;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },
    selected(value: object){
        this.selectedVolunteers = value;
    },
    delete_selected(){
      this.selectedVolunteers = null;
  },
  },
});
