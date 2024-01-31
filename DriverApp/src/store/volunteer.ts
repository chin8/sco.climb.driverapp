import { defineStore } from "pinia";

interface State {
  volunteers: any[] | null;
  volunteer:null;
  loading: boolean;
  error: null|unknown;
}

export const useVolunteerStore = defineStore("volunteerStore", {
  // convert to a function
  state: (): State => ({
    volunteers: [],
    volunteer:null,
    loading: false,
    error: null
  }),
  getters: {
    allVolunteers: (state) => state.volunteers,
    volunterError: (state) => state.error,
  },
  actions: {
    async fetchVolunteers() {
      this.volunteers = []
      this.loading = true
      try {
        this.volunteers = await fetch('https://jsonplaceholder.typicode.com/users')
        .then((response) => response.json()) 
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
    async fetchVolunteer(id: any) {
      this.volunteer = null
      this.loading = true
      try {
        this.volunteer = await fetch(`https://jsonplaceholder.typicode.com/users/${id}`)
        .then((response) => response.json())
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    }
  }
});