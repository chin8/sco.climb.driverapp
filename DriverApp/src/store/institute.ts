import { defineStore } from "pinia";
import { useRouter } from 'vue-router';

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
      const router = useRouter();
      try {
        //https://climbdev.smartcommunitylab.it/v3/api/institute/TEST
        const fetchedData = await fetch(`src/tmp-data/institute.json`).then(
          (response) => response.json()
        );
        if (fetchedData.length === 1) {
          this.selectedInstitute = fetchedData[0];
          router.push('/schools');
        } else {
          this.institutes = fetchedData;
        }        
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
