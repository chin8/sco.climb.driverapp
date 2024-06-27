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
  IonPage,
  IonButtons,
  IonTitle,
  IonToolbar,
  IonHeader,
  IonBackButton,
  IonLoading
} from "@ionic/vue";
import { chevronForward } from "ionicons/icons";
import { ref } from "vue";


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
const isLoading = ref(true);


onMounted(async () => {
  await fetchRoutes(selectedInstitute.value.objectId, selectedSchool.value.objectId);

  if (all_routes.value && all_routes.value.length === 1) {
    setTimeout(() => {
      isLoading.value = false;
      selected(all_routes.value[0]);
      router.push({ path: '/volunteers', replace: true, transition: false });
    }, 1000);
  }
});
</script>

<template>
  <ion-page>
    <div>
      <!-- back button -->
      <ion-header>
        <ion-toolbar>
          <ion-buttons slot="start">
            <ion-back-button :default-href="'/schools'"></ion-back-button>
          </ion-buttons>
          <ion-title>Configurazione Percorso</ion-title>
          <ion-buttons slot="end">
            <slot name="actions-end"></slot>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>

      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_routes && all_routes.length < 2" class="ion-padding ion-text-center">
        <ion-loading :isOpen="isLoading" message="Autoselezione della linea..."> </ion-loading>
      </div>
      <div v-if="all_routes && all_routes.length > 1">
        <ion-list>
          <ion-item v-for="route in all_routes" :key="route.objectId"
            @click="selected(route), router.push({ path: '/volunteers', replace: true, transition: false });">
            <ion-label>{{ route.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </ion-page>
</template>
