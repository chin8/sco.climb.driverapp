<script setup>
import { storeToRefs } from "pinia";
import {
  IonList,
  IonItem,
  IonLabel,
  IonPage,
  IonIcon,
  IonAlert
} from "@ionic/vue";
import {
  call
} from "ionicons/icons";
import { useVolunteersStore } from "../store/volunteers";

const { loading, error, volunteers } = storeToRefs(
  useVolunteersStore()
);
</script>

<template>
  <ion-page>
    <div>
      <!-- back button -->
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>

      <ion-list v-if="volunteers.length">
        <ion-item v-for="volunteer in volunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
          <div v-if="volunteer.phone" :id="`trigger-${volunteer.objectId}`">
            <ion-icon :icon="call" slot="end"></ion-icon>
          </div>
          <ion-alert v-if="volunteer.phone" :trigger="`trigger-${volunteer.objectId}`" :sub-header="volunteer.name" :message="volunteer.phone"></ion-alert>
        </ion-item>
      </ion-list>
    </div>
  </ion-page>
</template>
