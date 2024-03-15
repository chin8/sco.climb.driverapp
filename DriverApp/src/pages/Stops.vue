<script lang="ts" setup>
import {
  IonButtons,
  IonButton,
  IonModal,
  IonHeader,
  IonContent,
  IonToolbar,
  IonTitle,
  IonItem,
  IonInput,
  IonIcon,
  IonLabel,
  IonList,
} from "@ionic/vue";
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { chevronForward, chevronBack, addOutline, removeOutline } from "ionicons/icons";
import { useRouter } from "vue-router";
import { useStopsStore } from "../store/stops";
import { useRouteStore } from "../store/route";
import { useEventsStore } from "../store/events";
import { OverlayEventDetail } from "@ionic/core/components";
import { ref } from "vue";

const message = ref(
  "This modal example uses triggers to automatically open a modal when the button is clicked."
);

const modal = ref();
const input = ref();

const { selectedRoute } = storeToRefs(useRouteStore());
const { fetchStops, addOnBoard, removeOnBoard } = useStopsStore();
const { nodeCheckin, nodeCheckout, stopLeave, startRoute, events } = useEventsStore();

const { all_stops, loading, error } = storeToRefs(useStopsStore());

const router = useRouter();

const currentIndex = ref(0);

const routeId = selectedRoute.value.objectId;


const goForward = () => {
  if (currentIndex.value < all_stops.value.length - 1) {
    // in questo modo viene salvato l'evento tutte le volte che torno al primo stop
    // il checkin viene salvato prima di start route
    if(currentIndex.value == 0) {
      startRoute(routeId, all_stops.value[currentIndex.value].objectId)
    }
    stopLeave(routeId, all_stops.value[currentIndex.value].objectId);
    currentIndex.value++;
  }
};

const goBack = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};

const isOpen = ref(false);

const setOpen = (open: boolean) => (isOpen.value = open);

if (routeId && !all_stops.value) {
  fetchStops(routeId);
}
</script>

<template>
  <base-layout>
    <ion-header class="header">
      <ion-toolbar color="secodary">
        <ion-buttons slot="start">
          <ion-button @click="goBack()" v-if="currentIndex !== 0">
            <ion-icon :icon="chevronBack" slot="icon-only"></ion-icon>
          </ion-button>
        </ion-buttons>
        <ion-title v-if="all_stops">{{
            all_stops[currentIndex]?.name
          }}</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="goForward()" v-if="currentIndex !== all_stops?.length - 1">
            <ion-icon :icon="chevronForward" slot="icon-only"></ion-icon>
          </ion-button>
          <ion-button v-if="currentIndex === all_stops?.length - 1">arriva</ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding" v-if="all_stops">
      <ion-list>
        <div v-for="passenger in all_stops[currentIndex]?.passengerList" :key="passenger">
          <ion-item v-if="passenger.onBoard === false">
            <ion-label>{{ passenger.passenger }}</ion-label>
            <button class="addButton" @click="addOnBoard(passenger.passenger); nodeCheckin(passenger.passenger, routeId); console.log(events);">
              <ion-icon :icon="addOutline" slot="end"></ion-icon>
            </button>
          </ion-item>
        </div>
      </ion-list>
      <div class="ion-padding-top">
        <ion-button expand="block" @click="setOpen(true)">Bamibini a bordo</ion-button>
      </div>
      <ion-modal :is-open="isOpen" :initial-breakpoint="1" :breakpoints="[0, 1]" @ionModalDidDismiss="setOpen(false)">
        <ion-header>
          <ion-toolbar>
            <ion-title>A bordo</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="setOpen(false)">Close</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <ion-list>
            <div v-for="stop in all_stops" :key="stop">
              <div v-for="passenger in stop.passengerList" :key="passenger">
                <ion-item v-if="passenger.onBoard === true">
                  <ion-label>{{ passenger.passenger }}</ion-label>
                  <button class="addButton" @click="removeOnBoard(passenger.passenger); nodeCheckout(passenger.passenger, routeId)">
                    <ion-icon :icon="removeOutline" slot="end"></ion-icon>
                  </button>
                </ion-item>
              </div>
            </div>
          </ion-list>
        </ion-content>
      </ion-modal>
    </ion-content>
  </base-layout>
</template>

<style>
.addButton {
  padding: 5px;
  border-radius: 5px;
  background: #3880ff;
}
</style>
