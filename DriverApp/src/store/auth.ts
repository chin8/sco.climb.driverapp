import { defineStore } from "pinia";

export interface User {
  user: any;
  userError: any;
}

interface State {
  user: any | null;
  loading:boolean;
  error: null|unknown;
}


export const useAuthStore = defineStore("authStore", {
  // convert to a function
  state: (): State => ({
    user: null,
    loading : true,
    error: null,
  }),
  getters: {
    isLoggedIn: (state) => state.user !== null,
    userError: (state) => state.error,
  },
  actions: {

    initializeAuthListener() {
    },
   
    async logInUser() {
    },

    async logoutUser() {

    },
     
  },
});