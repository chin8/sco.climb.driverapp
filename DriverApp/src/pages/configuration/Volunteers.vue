<script setup>
import { storeToRefs } from "pinia";
import VolunteersModal from "./VolunteersModal.vue";
import {
  IonList,
  IonItem,
  IonLabel,
  IonPage,
  IonButtons,
  IonTitle,
  IonToolbar,
  IonHeader,
  IonBackButton,
  IonButton
} from "@ionic/vue";
import { useRouter } from "vue-router";
import { useVolunteersStore } from "../../store/volunteers";

const { delete_selected } = useVolunteersStore();
delete_selected();

const { loading, error, selectedVolunteers } = storeToRefs(
  useVolunteersStore()
);

const router = useRouter();

</script>

<template>
  <ion-page>
    <div>
      <!-- back button -->
      <ion-header>
        <ion-toolbar>
          <ion-buttons slot="start">
            <ion-back-button :default-href="'/routes'"></ion-back-button>
          </ion-buttons>
          <ion-title>Volontari</ion-title>
          <ion-buttons slot="end">
            <slot name="actions-end"></slot>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <VolunteersModal page-title="volunteers" class="ion-padding-top" />

      <ion-list v-if="selectedVolunteers">
        <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
        </ion-item>
      </ion-list>
      <div class="ion-padding">
        <ion-button expand="full" class="ion-margin-top" fill="solid"
          v-if="selectedVolunteers && selectedVolunteers.length > 0"
          @click="router.push('/recap')">Conferma</ion-button>
      </div>
    </div>
  </ion-page>
</template>

<style>
div.select-text {
  display: none !important;
}
</style>
