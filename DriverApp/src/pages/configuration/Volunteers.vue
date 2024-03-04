<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import VolunteersModal from "./VolunteersModal.vue";
import {
  IonTitle,
  IonContent,
  IonToolbar,
  IonBreadcrumbs,
  IonBreadcrumb,
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonIcon,
  IonHeader,
  IonButtons,
  IonButton,
  IonAlert,
} from "@ionic/vue";
import { useRouter } from "vue-router";
import { useVolunteersStore } from "../../store/volunteers";
import { useSchoolStore } from "../../store/school";
import { useInstituteStore } from "../../store/institute";

const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedInstitute } = storeToRefs(useInstituteStore());

const { fetchVolunteers, selected, delete_selected } = useVolunteersStore();
delete_selected();

const { all_volunteers, loading, error, selectedVolunteers } = storeToRefs(
  useVolunteersStore()
);

const router = useRouter();

</script>

<template>
  <base-layout page-title="Configurazione Percorso" page-default-back-link="/routes">
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <VolunteersModal />

      <ion-list v-if="selectedVolunteers">
          <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
        </ion-item>
      </ion-list>
      <div class="ion-padding">
        <ion-button expand="full" class="ion-margin-top" fill="solid"
          v-if="selectedVolunteers && selectedVolunteers.length > 0" @click="router.push('/recap')">Conferma</ion-button>
      </div>
    </div>
  </base-layout>
</template>

<style>
div.select-text {
  display: none !important;
}
</style>
