<script setup>
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
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
  IonNote,
  IonButton,
  
} from "@ionic/vue";
import { useRouter } from "vue-router";
import { useInstituteStore } from "../../store/institute";
import { useSchoolStore } from "../../store/school";
import { useRouteStore } from "../../store/route";
import { useVolunteersStore } from "../../store/volunteers";
import { useProfileStore } from "../../store/profile";
import { useEventsStore } from "../../store/events";
import { useChildStore } from "../../store/child";
import { useStopsStore } from "../../store/stops";

const { selectedInstitute, all_institutes } = storeToRefs(useInstituteStore());
const { selectedSchool, all_schools } = storeToRefs(useSchoolStore());
const { selectedRoute, all_routes } = storeToRefs(useRouteStore());
const { selectedVolunteers } = storeToRefs(useVolunteersStore());
const { profile } = storeToRefs(useProfileStore());
const { changeLayout } = useStopsStore();
const { fetchChild } = useChildStore();

const { fetchProfile } = useProfileStore();
const { setDriver, setHelper } = useEventsStore();


const router = useRouter();

onMounted(() => {
  fetchProfile();
  fetchChild(selectedInstitute.value.objectId, selectedSchool.value.objectId);
});

const helpers = () => {
  for (let index = 0; index < selectedVolunteers.value.length; index++) {
    setHelper(selectedVolunteers.value[index].objectId, selectedRoute.value.objectId)
  }
}

const handleStart = async () => {
  changeLayout();
  await setDriver(profile.value.objectId, selectedRoute.value.objectId);
  helpers();
  await router.push({ path: '/stops', replace: true, transition: false });
}
</script>

<template>
  <ion-page>
    <div>
      <!-- back button -->
      <ion-header>
        <ion-toolbar>
          <ion-buttons slot="start">
            <ion-back-button :default-href="'/volunteers'"></ion-back-button>
          </ion-buttons>
          <ion-title>Riepilogo</ion-title>
          <ion-buttons slot="end">
            <slot name="actions-end"></slot>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>
      <ion-item class="ion-padding-top">
        <ion-label>
          <p>Institute</p>
          <h2>{{ selectedInstitute.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push({ path: '/institutes', replace: true, transition: false });"
          v-if="all_institutes && all_institutes.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>School</p>
          <h2>{{ selectedSchool.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push({ path: '/schools', replace: true, transition: false });"
          v-if="all_schools && all_schools.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>Routes</p>
          <h2>{{ selectedRoute.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push({ path: '/routes', replace: true, transition: false });"
          v-if="all_routes && all_routes.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-list>
        <ion-item>
          <ion-label>
            <p>Volunteers</p>
          </ion-label>
          <ion-note slot="end" class="ion-margin-bottom" @click="router.push({ path: '/volunteers', replace: true, transition: false });">Modifica</ion-note>
        </ion-item>
        <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
        </ion-item>
      </ion-list>

      <div class="ion-padding">
        <ion-button expand="full" class="ion-margin-top" fill="solid" @click="handleStart()">Inizia</ion-button>
      </div>
    </div>
  </ion-page>
</template>


<style>
div.select-text {
  display: none !important;
}
</style>
