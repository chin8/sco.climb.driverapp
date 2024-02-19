<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
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

// checked true per elementi spuntati nel componente
function handleAlertDismiss(event) {
  if (event.detail.role === "confirm") {
    alertInputs.forEach((input) => {
      input.checked = event.detail.data.values.includes(input.value);
    });
    // elementi con checked true aggiunti allo store selected
    const selectedVolunteers = alertInputs.filter((input) => input.checked).map((input) => input.value);
    selected(selectedVolunteers);
  }
}

let alertInputs = [];

onMounted(async () => {
  await fetchVolunteers(selectedInstitute.value.objectId, selectedSchool.value.objectId);
  // ion-alert viene popolata
  if (all_volunteers.value) {
    const volunteers = all_volunteers.value;
    if (volunteers) {
      alertInputs = Object.keys(volunteers).map((key) => {
        const volunteer = volunteers[key];
        return {
          type: "checkbox",
          label: volunteer.name,
          value: volunteer,
          checked: false,
        };
      });
    }
  }
});
  

const alertButtons = [
  {
    text: "Annulla",
    role: "cancel",
  },
  {
    text: "Conferma",
    role: "confirm",
  },
];
</script>

<template>
  <base-layout
    page-title="Configurazione Percorso"
    page-default-back-link="/routes"
  >
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <ion-list>
        <ion-list-header>
          <ion-label>Volontari</ion-label>
          <ion-button id="present-alert" :inputs="alertInputs"
            >Aggiungi</ion-button
          >
        </ion-list-header>

        <ion-alert
          trigger="present-alert"
          header="Volontari"
          :buttons="alertButtons"
          :inputs="alertInputs"
          @didDismiss="handleAlertDismiss($event)"
        >
        </ion-alert>

        <ion-item
          v-for="volunteer in selectedVolunteers"
          :key="volunteer.objectId"
        >
          <ion-label>{{ volunteer.name }}</ion-label></ion-item
        >
      </ion-list>

      <div class="ion-padding">
        <ion-button
          expand="full"
          class="ion-margin-top"
          fill="solid"
          v-if="selectedVolunteers && selectedVolunteers.length > 0"
          @click="router.push('/recap')"
          >Conferma</ion-button
        >
      </div>
    </div>
  </base-layout>
</template>

<style>
div.select-text {
  display: none !important;
}
</style>
