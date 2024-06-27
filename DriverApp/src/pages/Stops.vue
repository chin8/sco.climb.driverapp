<script lang="ts" setup>
import {
  IonPage,
  IonButtons,
  IonButton,
  IonModal,
  IonHeader,
  IonContent,
  IonToolbar,
  IonTitle,
  IonItem,
  IonIcon,
  IonLabel,
  IonList,
} from "@ionic/vue";
import { storeToRefs } from "pinia";
import {
  chevronForward,
  chevronBack,
  addOutline,
  removeOutline
} from "ionicons/icons";
import { useStopsStore } from "../store/stops";
import { useRouteStore } from "../store/route";
import { useEventsStore } from "../store/events";
import { useChildStore } from "../store/child";
import { useProfileStore } from "../store/profile";
import { ref } from "vue";
import { addEvents } from "../services/APIService";
import { useRouter } from "vue-router";
import {
  startWatchingPosition,
  stopWatchingPosition,
} from "../services/GeoService";
import PassengersModal from "../pages/PassengersModal.vue";
import { useBackButton } from '@ionic/vue';


const router = useRouter();

const { selected_route } = storeToRefs(useRouteStore());
const { profile } = useProfileStore();
const { fetchStops, addOnBoard, removeOnBoard, changeLayout } = useStopsStore();
const {
  nodeCheckin,
  nodeCheckout,
  stopLeave,
  startRoute,
  driverPosition,
  nodeAtDestination,
  endRoute,
  events,
} = useEventsStore();
const { all_child } = storeToRefs(useChildStore());
const { all_stops, is_open } = storeToRefs(useStopsStore());

const viewIndex = ref(0);
const stopIndex = ref(0);

const routeId: string = (selected_route?.value as any)?.objectId || "";

if (routeId && all_stops.value.length == 0) {
  fetchStops(routeId);
}

startWatchingPosition(
  function (position: any) {
    if (!!position && !!position.coords) {
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;
      const acc = position.coords.accuracy;
      driverPosition(routeId, profile.objectId, lat, lon, acc);
    }
  },
  null,
  4000
);

const goForward = () => {
  if (stopIndex.value < all_stops.value.length - 1) {
    if (stopIndex.value == 0) {
      startRoute(routeId, all_stops.value[viewIndex.value].objectId);
    }
    if (all_stops.value[stopIndex.value + 1]) {
      stopLeave(routeId, all_stops.value[viewIndex.value].objectId);
    }
    if (stopIndex.value === viewIndex.value) {
      stopIndex.value++;
    }
  }
  if (viewIndex.value < all_stops.value?.length - 1) {
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
};

const handleRemove = (passenger: string) => {
  removeOnBoard(passenger);
  nodeCheckout(passenger, routeId);
};

const getChild = (childId: any) => {
  let foundChild = null;
  for (let i = 0; i < all_child.value.length; i++) {
    const child = all_child.value[i];
    if (child.objectId === childId) {
      foundChild = child;
    }
  }
  return foundChild;
};

const getChildName = (childId: any) => {
  let foundChild = null;
  for (let i = 0; i < all_child.value.length; i++) {
    const child = all_child.value[i];
    if (child.objectId === childId) {
      foundChild = child;
    }
  }
  return foundChild.name + " " + foundChild.surname;
};

const send = () => {
  // get onBoard
  const onBoard: any[] = [];
  all_stops.value?.forEach((stop) => {
    stop.passengerList.forEach((passenger: any) => {
      if (passenger.onBoard) {
        onBoard.push(passenger.passenger);
      }
    });
  });
  // get Child from Id
  onBoard.forEach((passenger) => {
    const child = getChild(passenger);
    nodeCheckout(child.objectId, routeId);
    nodeAtDestination(child.objectId, routeId);
  });
  endRoute(all_stops.value[stopIndex.value].objectId, routeId);
  stopWatchingPosition();
  addEvents(routeId, events).then((result) => {
          if (result) {
            console.error('ok', result);
            changeLayout();
            router.push({ path: '/finish', replace: true })
          } else {
            console.error('not ok', result);
          }
        })
        .catch((err) => {
          console.error('Error adding event:', err);
        });
  
};

useBackButton(1, () => {
  console.log('Another handler was called!');
});
</script>

<template>
  <ion-page>
    <ion-header>
      <ion-item @click="router.push('/editconfig')" color="medium">
        <ion-label>
          Configurazione percorso
        </ion-label>
        <ion-icon :icon="chevronForward"></ion-icon>
      </ion-item>
    </ion-header>
    <ion-header class="header">
      <ion-toolbar color="light">
        <ion-buttons slot="start">
          <ion-button @click="goBack()" v-if="viewIndex !== 0">
            <ion-icon :icon="chevronBack" slot="icon-only"></ion-icon>
          </ion-button>
        </ion-buttons>
        <ion-title v-if="all_stops">{{ all_stops[viewIndex]?.name }}</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="goForward()" v-if="all_stops &&
            viewIndex !== all_stops?.length - 1 &&
            all_stops[viewIndex + 1]
          ">
            <p v-if="viewIndex === stopIndex">
              {{ all_stops[viewIndex + 1]?.name }}
            </p>
            <ion-icon v-if="viewIndex < stopIndex" :icon="chevronForward" slot="icon-only"></ion-icon>
          </ion-button>
          <ion-button v-if="all_stops && viewIndex === all_stops?.length - 1" @click="send()">arriva</ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding" v-if="all_stops">
      <ion-list>
        <div v-for="passenger in all_stops[viewIndex]?.passengerList" :key="passenger">
          <ion-item v-if="passenger.onBoard === false">
            <ion-label>{{ getChildName(passenger.passenger) }}</ion-label>
            <button class="childButton" @click="handleAdd(passenger.passenger)">
              <ion-icon :icon="addOutline" slot="end"></ion-icon>
            </button>
          </ion-item>
        </div>
      </ion-list>
      <div class="ion-padding-top">
      </div>
      <ion-modal v-if="router.currentRoute.value.path === '/stops' && is_open" :is-open="true"
        :initial-breakpoint="0.25" :breakpoints="[0.25, 0.5, 0.75]" :backdrop-dismiss="false"
        :backdrop-breakpoint="0.5">
        <ion-header>
          <ion-toolbar>
            <ion-title>Bambini a bordo</ion-title>
            <ion-buttons slot="end">
              <PassengersModal />
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <ion-list>
            <div v-for="stop in all_stops" :key="stop">
              <div v-for="passenger in stop.passengerList" :key="passenger">
                <ion-item v-if="passenger.onBoard === true">
                  <ion-label>{{ getChildName(passenger.passenger) }}</ion-label>
                  <button class="childButton" @click="handleRemove(passenger.passenger)">
                    <ion-icon :icon="removeOutline" slot="end"></ion-icon>
                  </button>
                </ion-item>
              </div>
            </div>
          </ion-list>
        </ion-content>
      </ion-modal>
    </ion-content>
  </ion-page>
</template>

<style>
.childButton {
  padding: 5px;
  border-radius: 5px;
  background: #FF8232;
}
</style>
