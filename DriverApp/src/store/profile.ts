import axios from "axios";
import { defineStore } from "pinia";

export interface Profile {
  profile: any;
  loading:boolean;
  userError: any;
}

interface State {
  profile: any | null;
  loading:boolean;
  error: null|unknown;
}


export const useProfileStore = defineStore("profileStore", {
  // convert to a function
  state: (): State => ({
    profile: null,
    loading : true,
    error: null,
  }),
  getters: {
    userError: (state) => state.error,
  },
  actions: {
    async fetchProfile() {
        this.profile = null
        this.loading = true
        try {
          const profile = await axios.get(import.meta.env.VITE_SERVER_URL+'/profile');
          this.profile = profile.data;
        } catch (error) {
          this.error = error
        } finally {
          this.loading = false
        }
      
    }
  },
});