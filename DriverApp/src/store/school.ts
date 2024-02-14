import { defineStore } from "pinia";
import { useRouter } from 'vue-router';


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
      const router = useRouter();
      try {
        //https://climbdev.smartcommunitylab.it/v3/api/school/TEST/fe21135b-991d-4f1e-ae0f-3d15e15b4ee7
        const fetchedData = await fetch(`src/tmp-data/schools.json`).then(
          (response) => response.json()
        );
        if (fetchedData.length === 1) {
          this.selectedSchool = fetchedData[0];
          router.push('/routes');
        } else {
          this.schools = fetchedData;
        } 
        this.schools = fetchedData;
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
