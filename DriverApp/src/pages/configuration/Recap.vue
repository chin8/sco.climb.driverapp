<script setup>
import { RouterLink } from "vue-router";
import { storeToRefs } from "pinia";
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
import { useInstituteStore } from "../../store/configuration/institute";
import { useSchoolStore } from "../../store/configuration/school";
import { useRouteStore } from "../../store/configuration/route";
import { useVolunteersStore } from "../../store/configuration/volunteers";

const { selectedInstitute } = storeToRefs(useInstituteStore());
const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedRoute } = storeToRefs(useRouteStore());
const { selectedVolunteers } = storeToRefs(useVolunteersStore());

const router = useRouter();

console.log(selectedInstitute.value)

</script>

<template>
    <base-layout page-title="Riepilogo" page-default-back-link="/volunteers">
      <div class="ion-padding-top">
        <ion-item>
          <ion-label>
            <p>Institute</p>
            <h2>{{ selectedInstitute.name }}</h2>
          </ion-label>
          <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push('/institutes')">Modifica</ion-note>
        </ion-item>
        <ion-item>
          <ion-label>
            <p>School</p>
            <h2>{{ selectedSchool.name }}</h2>
          </ion-label>
          <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push('/schools')">Modifica</ion-note>
        </ion-item>
        <ion-item>
          <ion-label>
            <p>Routes</p>
            <h2>{{ selectedRoute.name }}</h2>
          </ion-label>
          <ion-note slot="end" class="ion-margin-top ion-margin-bottom" @click="router.push('/routes')">Modifica</ion-note>
        </ion-item>
        <ion-list>
          <ion-item>
            <ion-label>
              <p>Volunteers</p>
            </ion-label>
             <ion-note slot="end" class="ion-margin-bottom" @click="router.push('/volunteers')">Modifica</ion-note>
          </ion-item>
          <ion-item v-for="volunteer in selectedVolunteers" :key="volunteer.objectId">
            <ion-label>{{ volunteer.name }}</ion-label>
          </ion-item>
        </ion-list>

        <div class="ion-padding">
          <ion-button expand="full" class="ion-margin-top" fill="solid">Inizia</ion-button>
        </div>
      </div>
    </base-layout>
</template>

<style>
div.select-text {
  display: none !important;
}
</style>
