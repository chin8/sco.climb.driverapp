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
import { useChildStore } from "../store/child";
import { OverlayEventDetail } from "@ionic/core/components";
import { ref } from "vue";
import { startWatchingPosition, stopWatchingPosition } from "../services/GeoService";


const modal = ref();
const input = ref();

const { selectedRoute } = storeToRefs(useRouteStore());
const { fetchStops, addOnBoard, removeOnBoard } = useStopsStore();
const { nodeCheckin, nodeCheckout, stopLeave, startRoute, driverPosition, nodeAtDestination, endRoute, events } = useEventsStore();
const { all_child } = storeToRefs(useChildStore());

const { all_stops, loading, error } = storeToRefs(useStopsStore());

const router = useRouter();

const viewIndex = ref(0);
const stopIndex = ref(0);

const routeId = selectedRoute.value.objectId;

startWatchingPosition(function (position:any) {
            if (!!position && !!position.coords) {
              var lat = position.coords.latitude
              var lon = position.coords.longitude
              var acc = position.coords.accuracy
              driverPosition(null,null, lat, lon, acc)
            }
          }, null, 4000)

const goForward = () => {
  if (stopIndex.value < all_stops.value.length - 1) {
    if(stopIndex.value == 0) {
      startRoute(routeId, all_stops.value[viewIndex.value].objectId);

    };
    if (stopIndex.value === viewIndex.value) {
      stopIndex.value++;
    }
    if(all_stops.value[stopIndex.value + 1]) {
      stopLeave(routeId, all_stops.value[viewIndex.value].objectId);
    }
  }
  if (viewIndex.value < all_stops.value.length - 1) {
    viewIndex.value++;
  }
};

const goBack = () => {
  if (viewIndex.value > 0) {
    viewIndex.value--;
  }
};

const handleAdd = (passenger: string) => {
  addOnBoard(passenger);
  nodeCheckin(passenger, routeId);
}

const handleRemove = (passenger: string) => {
  removeOnBoard(passenger);
  nodeCheckout(passenger, routeId)
}

const getChild = (childId: any) => {
  var foundChild = null
  for (var i = 0; i < all_child.value.length; i++) {
    var child = all_child.value[i];
    if (child.objectId === childId) {
      foundChild = child;
    }
  }
  return foundChild
}

const send = () => {
  // get onBoard
  const onBoard = [];
  all_stops.value.forEach(stop => {
    stop.passengerList.forEach(passenger => {
      if (passenger.onBoard) {
        onBoard.push(passenger.passenger);
      }
    });
  });
  // get Child from Id
  onBoard.forEach(child => {
    var child = getChild(child);
    nodeCheckout(child.objectId, routeId);
    nodeAtDestination(child.objectId, routeId);
  })
  endRoute(all_stops.value[stopIndex.value].objectId, routeId);
  stopWatchingPosition();
  console.log(events);
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
          <ion-button @click="goBack()" v-if="viewIndex !== 0">
            <ion-icon :icon="chevronBack" slot="icon-only"></ion-icon>
          </ion-button>
        </ion-buttons>
        <ion-title v-if="all_stops">{{
            all_stops[viewIndex]?.name
          }}</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="goForward()" v-if="all_stops && viewIndex !== all_stops.length - 1 && all_stops[viewIndex + 1]">
            <p v-if="viewIndex === stopIndex">{{ all_stops[viewIndex+1].name }}</p>
            <ion-icon v-if="viewIndex < stopIndex" :icon="chevronForward" slot="icon-only"></ion-icon>
          </ion-button>
          <ion-button v-if="viewIndex === all_stops?.length - 1" @click="send()">arriva</ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding" v-if="all_stops">
      <ion-list>
        <div v-for="passenger in all_stops[viewIndex]?.passengerList" :key="passenger">
          <ion-item v-if="passenger.onBoard === false">
            <ion-label>{{ passenger.passenger }}</ion-label>
            <button class="addButton" @click="handleAdd(passenger.passenger)">
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
                  <button class="addButton" @click="handleRemove(passenger.passenger)">
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
