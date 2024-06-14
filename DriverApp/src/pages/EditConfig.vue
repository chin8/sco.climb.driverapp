<script setup>
import { storeToRefs } from "pinia";
import {
  IonList,
  IonItem,
  IonLabel,
  IonPage,
} from "@ionic/vue";
import { useInstituteStore } from "../store/institute";
import { useSchoolStore } from "../store/school";
import { useRouteStore } from "../store/route";
import { useVolunteersStore } from "../store/volunteers";

import VolunteersModal from "../pages/configuration/VolunteersModal.vue";

const { selectedInstitute } = storeToRefs(useInstituteStore());
const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedRoute } = storeToRefs(useRouteStore());
const { selectedVolunteers } = storeToRefs(useVolunteersStore());
</script>

<template>
  <ion-page>
    <div class="ion-padding-top">
      <ion-item>
        <ion-label>
          <p>Institute</p>
          <h2>{{ selectedInstitute.name }}</h2>
        </ion-label>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>School</p>
          <h2>{{ selectedSchool.name }}</h2>
        </ion-label>
      </ion-item>
      <ion-item>
        <ion-label>
          <p>Routes</p>
          <h2>{{ selectedRoute.name }}</h2>
        </ion-label>
      </ion-item>
      <ion-list>
        <ion-item>
          <ion-label>
            <p>Volunteers</p>
          </ion-label>
          <VolunteersModal page-title="stops" />
        </ion-item>
        <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
          <ion-label>{{ volunteer.name }}</ion-label>
        </ion-item>
      </ion-list>
    </div>
</ion-page>
</template>


<style>
div.select-text {
  display: none !important;
}
</style>
