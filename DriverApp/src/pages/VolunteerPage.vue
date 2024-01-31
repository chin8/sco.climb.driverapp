<script setup>
  import { computed } from 'vue'
  import { useRoute } from "vue-router";
  import { storeToRefs } from 'pinia'
  import { useVolunteerStore } from '../store/volunteer'
  import Volunteer from '../components/Volunteer.vue'
  import {  IonContent, IonPage } from "@ionic/vue";

  const route = useRoute() 
  const { volunteers } = storeToRefs(useVolunteerStore())

  const getVolunteerByUserId = computed(() => {
    return volunteers.value.find((volunteer) => volunteer.id == route.params.id);
    })

</script>

<template>
    <ion-page>
    <ion-content>
  <div>
    <Volunteer :volunteer="getVolunteerByUserId"></Volunteer>
  </div>
</ion-content>
</ion-page>
</template>