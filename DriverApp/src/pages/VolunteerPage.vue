<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useVolunteerStore } from "../store/volunteer";
import Volunteer from "../components/volunteers/Volunteer.vue";

const route = useRoute();
const { volunteers } = storeToRefs(useVolunteerStore());

const getVolunteerByUserId = computed(() => {
  return volunteers.value.find((volunteer) => volunteer.id == route.params.id);
});
</script>

<template>
  <base-layout page-title="Dettaglio" page-default-back-link="/volunteers">
      <Volunteer :volunteer="getVolunteerByUserId"></Volunteer>
  </base-layout>
</template>
