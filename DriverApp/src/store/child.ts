import axios from "axios";
import { defineStore } from "pinia";

interface State {
  child: any[];
  loading: boolean;
  error: null | unknown;
}

export const useChildStore = defineStore("childStore", {
  state: (): State => ({
    child: [],
    loading: false,
    error: null,
  }),
  getters: {
    all_child: (state) => state.child,
    the_error: (state) => state.error,
  },
  actions: {
    async fetchChild(instituteId: string, schoolId: string) {
      this.loading = true;
      try {
        const fetchedData = await axios.get(import.meta.env.VITE_SERVER_URL+'/child/TEST/'+instituteId+'/'+schoolId);
        this.child = fetchedData.data;
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    }
  },
});
