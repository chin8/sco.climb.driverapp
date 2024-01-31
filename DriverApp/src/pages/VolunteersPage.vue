<script setup>
import { RouterLink } from "vue-router";
import { storeToRefs } from "pinia";
import { useVolunteerStore } from "../store";
import {  IonContent, IonPage, IonItem } from '@ionic/vue';
import { useRouter } from "vue-router";
const { volunteers, loading, error } = storeToRefs(useVolunteerStore());
const { fetchVolunteers } = useVolunteerStore();
const router = useRouter() 

fetchVolunteers();
</script>

<template>
  <ion-page>
    <ion-content>
  <main>
    <p v-if="loading">Loading volunteers...</p>
    <p v-if="error">{{ error.message }}</p>
    <div v-if="volunteers">
      <div v-for="volunteer in volunteers" :key="volunteer.id">
        <ion-item @click="() => router.push(`/volunteer/${volunteer.id}`)"><p>{{ volunteer.name }}</p>
        </ion-item>
      </div>
    </div>
  </main>
</ion-content>
  </ion-page>
</template>
