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
  IonSelect,
  IonSelectOption,
} from "@ionic/vue";
import { useRouter } from "vue-router";
import { useVolunteersStore } from "../../store/configuration/volunteers";
import { useSchoolStore } from "../../store/configuration/school";
import { useInstituteStore } from "../../store/configuration/institute";

const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedInstitute } = storeToRefs(useInstituteStore());


const { fetchVolunteers, selected, delete_selected } = useVolunteersStore();
delete_selected()

const { all_volunteers, loading, error, selectedVolunteers } = storeToRefs(
  useVolunteersStore()
);

const router = useRouter();

fetchVolunteers(selectedSchool.value, selectedInstitute.value);

function handleChange(ev) {
  selected(ev.detail.value);
}
</script>

<template>
    <base-layout page-title="Configurazione Percorso" page-default-back-link="/routes">
      <div>
        <p v-if="loading">Loading...</p>
        <p v-if="error">{{ error.message }}</p>
        <ion-list>
          <ion-list-header>
            <ion-label>Volontari</ion-label>
            <ion-button>
              <ion-select placeholder="Aggiungi" @ionChange="handleChange($event)" :multiple="true">
                Aggiungi
                <ion-select-option v-for="volunteer in all_volunteers" :key="volunteer.objectId" :value="volunteer">{{
                  volunteer.name }}</ion-select-option>
              </ion-select>
            </ion-button>
          </ion-list-header>

          <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
            <ion-label>{{ volunteer.name }}</ion-label></ion-item>
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
