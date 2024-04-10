<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useRouteStore } from "../../store/route";
import { useSchoolStore } from "../../store/school";
import { useInstituteStore } from "../../store/institute";
import {
  IonList,
  IonItem,
  IonLabel,
  IonIcon,
  IonSpinner
} from "@ionic/vue";
import { chevronForward } from "ionicons/icons";

const { selectedSchool } = storeToRefs(
  useSchoolStore()
);
const { selectedInstitute } = storeToRefs(
  useInstituteStore()
);
const { all_routes, loading, error } = storeToRefs(
  useRouteStore()
);
const { fetchRoutes, selected } = useRouteStore();

const router = useRouter();

onMounted(async () => {
  await fetchRoutes(selectedInstitute.value.objectId, selectedSchool.value.objectId);

  if (all_routes.value && all_routes.value.length === 1) {
    setTimeout(() => {
      selected(all_routes.value[0]);
      router.push({ path: '/volunteers', replace: true, transition: false });
    }, 1000);
  }
});
</script>

<template>
  <base-layout
    page-title="Configurazione Percorso"
    page-default-back-link="/schools"
  >
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_routes && all_routes.length < 2" class="ion-padding ion-text-center">
        <ion-spinner class="ion-padding-top"></ion-spinner>
        <ion-label class="ion-padding">Autoselezione della linea...</ion-label>
      </div> 
      <div v-if="all_routes && all_routes.length > 1">
        <ion-list>
          <ion-item
            v-for="route in all_routes"
            :key="route.objectId"
            @click="selected(route), router.push('/volunteers')"
          >
            <ion-label>{{ route.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </base-layout>
</template>
