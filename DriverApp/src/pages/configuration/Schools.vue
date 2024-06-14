<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useSchoolStore } from "../../store/school";
import { useInstituteStore } from "../../store/institute";
import {
  IonList,
  IonItem,
  IonLabel,
  IonIcon,
  IonSpinner,
  IonPage,
  IonButtons,
  IonTitle,
  IonToolbar,
  IonHeader,
  IonBackButton
} from "@ionic/vue";
import { chevronForward } from "ionicons/icons";

const { all_schools, loading, error } = storeToRefs(
  useSchoolStore()
);
const { selectedInstitute } = storeToRefs(
  useInstituteStore()
);
const { fetchSchools, selected } = useSchoolStore();

const router = useRouter();


onMounted(async () => {
  await fetchSchools(selectedInstitute.value.objectId);

  if (all_schools.value && all_schools.value.length === 1) {
    setTimeout(() => {
      selected(all_schools.value[0]);
      router.push({ path: '/routes', replace: true, transition: false });
    }, 1500);
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
          <ion-back-button :default-href="'/institutes'"></ion-back-button>
        </ion-buttons>
        <ion-title>Configurazione Percorso</ion-title>
        <ion-buttons slot="end">
          <slot name="actions-end"></slot>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_schools && all_schools.length < 2" class="ion-padding ion-text-center">
        <ion-spinner class="ion-padding-top"></ion-spinner>
        <ion-label class="ion-padding">Autoselezione della scuola...</ion-label>
      </div>
      <div v-if="all_schools && all_schools.length > 1">
        <ion-list>
          <ion-item v-for="school in all_schools" :key="school.objectId"
            @click="selected(school), router.push({ path: '/routes', replace: true, transition: false });">
            <ion-label>{{ school.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </ion-page>
</template>
