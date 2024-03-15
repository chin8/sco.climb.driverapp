<script setup>
import { RouterLink } from "vue-router";
import { storeToRefs } from "pinia";
import { ref, onMounted, computed } from "vue";
import {
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonIcon,
  IonHeader,
  IonButtons,
  IonButton,
  IonNote
} from "@ionic/vue";
import { useRouter } from "vue-router";
import { useInstituteStore } from "../../store/institute";
import { useSchoolStore } from "../../store/school";
import { useRouteStore } from "../../store/route";
import { useVolunteersStore } from "../../store/volunteers";
import { useProfileStore } from "../../store/profile";
import { useEventsStore } from "../../store/events";


const { selectedInstitute, all_institutes } = storeToRefs(useInstituteStore());
const { selectedSchool, all_schools } = storeToRefs(useSchoolStore());
const { selectedRoute, all_routes } = storeToRefs(useRouteStore());
const { selectedVolunteers } = storeToRefs(useVolunteersStore());
const { profile } = storeToRefs(useProfileStore());

const { fetchProfile } = useProfileStore();
const { setDriver, setHelper } = useEventsStore();


const router = useRouter();

onMounted(() => {
  fetchProfile();
});

const helpers = () => {
  for (let index = 0; index < selectedVolunteers.value.length; index++) {
    setHelper(selectedVolunteers.value[index].objectId, selectedRoute.value.objectId)
  }
}

const handleStart = async () => {
  await router.push({ path: '/stops' });
  await setDriver(profile.value.objectId, selectedRoute.value.objectId);
  helpers();
}
</script>

<template>
  <base-layout page-title="Riepilogo" page-default-back-link="/volunteers">
    <div class="ion-padding-top">
      <ion-item>
        <ion-label>
          <p>Institute</p>
          <h2>{{ selectedInstitute.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.go(-4)" v-if="all_institutes && all_institutes.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>School</p>
          <h2>{{ selectedSchool.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.go(-3)" v-if="all_schools && all_schools.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>Routes</p>
          <h2>{{ selectedRoute.name }}</h2>
        </ion-label>
        <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.go(-2)" v-if="all_routes && all_routes.length > 1">Modifica</ion-note>
      </ion-item>
      <ion-list>
        <ion-item>
          <ion-label>
            <p>Volunteers</p>
          </ion-label>
           <ion-note slot="end" class="ion-margin-bottom" @click="router.go(-1)">Modifica</ion-note>
        </ion-item>
        <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
        </ion-item>
      </ion-list>

      <div class="ion-padding">
        <ion-button expand="full" class="ion-margin-top" fill="solid" @click="handleStart()">Inizia</ion-button>
      </div>
    </div>
  </base-layout>
</template>


<style>
div.select-text {
  display: none !important;
}
</style>
