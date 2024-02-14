
<script setup>
import { ref, onMounted, computed } from "vue";
import { useProfileStore } from "../store/profile";
const store = useProfileStore();

const profile = computed(() => {
  return store.profile;
});
onMounted(() => {
  store.fetchProfile();
});
</script>
<template>
    <base-layout page-title="Profilo" page-default-back-link="/home">
      <div v-if="profile">
        {{ profile.name }} {{ profile.surname }} <br> {{ profile.email }}
      </div>
</base-layout>
  </template>
  
