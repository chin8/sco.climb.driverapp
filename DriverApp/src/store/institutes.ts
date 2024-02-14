import { defineStore } from "pinia";

interface State {
  institutes: any[] | null;
  selectedInstitute:null;
  loading: boolean;
  error: null|unknown;
}

export const useInstituteStore = defineStore("instituteStore", {
  // convert to a function
  state: (): State => ({
    institutes: [],
    selectedInstitute:null,
    loading: false,
    error: null
  }),
  getters: {
    allInstitutes: (state) => state.institutes,
    instituteError: (state) => state.error,
  },
  actions: {
    async fetchInstitutes() {
      this.institutes = []
      this.loading = true
      try {
        this.institutes = await fetch('https://jsonplaceholder.typicode.com/users')
        .then((response) => response.json()) 
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
    async fetchInstitute(id: any) {
      this.selectedInstitute = null
      this.loading = true
      try {
        this.selectedInstitute = await fetch(`https://jsonplaceholder.typicode.com/users/${id}`)
        .then((response) => response.json())
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    }
  }
});