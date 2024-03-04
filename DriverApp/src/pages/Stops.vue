<script setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import {
  IonTitle,
  IonContent,
  IonToolbar,
  IonBreadcrumbs,
  IonBreadcrumb,
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonIcon,
  IonHeader,
  IonButtons,
  IonButton,
  IonAlert,
  IonModal,
} from "@ionic/vue";
import { chevronForward, chevronBack } from "ionicons/icons";
import { useRouter } from "vue-router";
import { useStopsStore } from "../store/stops";
import { useRouteStore } from "../store/route";

const { selectedRoute } = storeToRefs(useRouteStore());
const { fetchStops } = useStopsStore();

const { all_stops, loading, error } = storeToRefs(useStopsStore());

const router = useRouter();

const currentIndex = ref(0);

const isOpen = false;

const goBack = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};

const goForward = () => {
  if (currentIndex.value < all_stops.value.length - 1) {
    currentIndex.value++;
  }
};

fetchStops(selectedRoute.value.objectId);
console.log(all_stops);
</script>

<template>

  <base-layout v-if="all_stops">
    <ion-header class="header">
      <ion-toolbar color="secodary">
        <ion-buttons slot="start">
          <ion-button @click="goBack()" v-if="currentIndex !== 0">
            <ion-icon :icon="chevronBack" slot="icon-only"></ion-icon>
          </ion-button>
        </ion-buttons>

        <ion-title>{{ all_stops[currentIndex].name }}</ion-title>

        <ion-buttons slot="end">
          <ion-button
            @click="goForward()"
            v-if="currentIndex !== all_stops.length - 1"
          >
            <ion-icon :icon="chevronForward" slot="icon-only"></ion-icon>
          </ion-button>
          <ion-button v-if="currentIndex === all_stops.length - 1"
            >arriva</ion-button
          >
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-list>
      <ion-item
        v-for="passenger in all_stops[currentIndex].passengerList"
        :key="passenger"
      >
        <ion-label>{{ passenger }}</ion-label>
      </ion-item>
    </ion-list>


    <div class="ion-padding-top">
      <ion-button @click="isOpen = true">A bordo</ion-button>
    </div>
    <ion-modal :initial-breakpoint="0.25" :breakpoints="[0, 0.25, 0.5, 0.75]" :is-open="isOpen">
        <ion-title>Volontari</ion-title>
        <ion-content>
          <ion-list>
            <ion-item>Test</ion-item>
          </ion-list>
        </ion-content>
      </ion-modal>
      
  </base-layout>
</template>

<style></style>
