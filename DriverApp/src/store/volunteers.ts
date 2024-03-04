import axios from "axios";
import { defineStore } from "pinia";

interface State {
  volunteers: null | Object;
  loading: boolean;
  error: null | unknown;
  selectedVolunteers: null | Object;
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
        //https://climbdev.smartcommunitylab.it/v3/api/volunteer/TEST/fe21135b-991d-4f1e-ae0f-3d15e15b4ee7/418184aa-26df-4a25-a026-c8511db469b2
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
