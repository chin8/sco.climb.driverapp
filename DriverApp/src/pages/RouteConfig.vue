<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from 'vue';
import { storeToRefs } from "pinia";
import { useVolunteerStore } from "../store";
import { IonContent, IonPage, IonItem } from '@ionic/vue';
import { useRouter } from "vue-router";
import { useConfigStore } from '../store/config'

const { all_institutes, all_schools, all_routes, all_volunteers, loading, error, selectedInstitute, selectedSchool, selectedRoute, selectedVolunteers } = storeToRefs(useConfigStore());
const { fetchData, selected } = useConfigStore();


const router = useRouter();

const fetching = async () => {
    if (!selectedInstitute.value) {
      await fetchData("institute");
    } else if (!selectedSchool.value && selectedInstitute.value) {
      await fetchData("schools");
    } else if (!selectedRoute.value && selectedSchool.value) {
      await fetchData("route");
    } else if (!selectedVolunteers.value) {
      await fetchData("volunteers");
    }
};

onMounted(fetching);



</script>

<template>
    <base-layout page-title="Configurazione Percorso" page-default-back-link="/RouteConfig">
  <div>
    <p v-if="loading">Loading...</p>
    <p v-if="error">{{ error.message }}</p>
    <h5>{{ selectedInstitute }} / {{ selectedSchool }} / {{ selectedRoute }} / {{ selectedVolunteers }}</h5>
    <div v-if="all_institutes && !all_schools">
      <p v-for="institute in all_institutes" :key="institute.objectId" @click="selected('institutes', institute.name); fetching()">{{ institute.name }}</p>
    </div>
    <div v-if="all_schools && !all_routes">
      <p v-for="school in all_schools" :key="school.objectId" @click="selected('schools', school.name); fetching()">{{ school.name }}</p>
    </div>
    <div v-if="all_routes && !all_volunteers">
      <p v-for="route in all_routes" :key="route.objectId" @click="selected('routes', route.name); fetching()">{{ route.name }}</p>
    </div>
    <div v-if="all_volunteers">
      <p v-for="volunteer in all_volunteers" :key="volunteer.objectId" @click="selected('volunteers', volunteer.name)">{{ volunteer.name }}</p>
    </div>
  </div>
  </base-layout>
</template>
