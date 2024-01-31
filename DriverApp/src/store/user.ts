import { defineStore } from "pinia";

export interface User {
  user: any;
  profile: any;
  userError: any;
}

interface State {
  user: any | null;
  profile: any;
  error: null;
}


export const useAuthStore = defineStore("authStore", {
  // convert to a function
  state: (): State => ({
    user: null,
    profile: null,
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