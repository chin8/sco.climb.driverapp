<script setup>
import { storeToRefs } from "pinia";
import { useVolunteerStore } from "../store";
import {  IonItem } from '@ionic/vue';
import { useRouter } from "vue-router";
const { volunteers, loading, error } = storeToRefs(useVolunteerStore());
const { fetchVolunteers } = useVolunteerStore();
const router = useRouter() 

fetchVolunteers();
</script>

<template>
    <base-layout page-title="Volontari" page-default-back-link="/home">
  <div>
    <p v-if="loading">Loading volunteers...</p>
    <p v-if="error">{{ error.message }}</p>
    <div v-if="volunteers">
      <div v-for="volunteer in volunteers" :key="volunteer.id">
        <ion-item @click="() => router.push(`/volunteer/${volunteer.id}`)"><p>{{ volunteer.name }}</p>
        </ion-item>
      </div>
    </div>
  </div>
  </base-layout>
</template>
