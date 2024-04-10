<script setup>
import { ref } from "vue";
import { storeToRefs } from "pinia";
import { useStopsStore } from "../store/stops";
import { useChildStore } from "../store/child";
import { IonButton, IonModal, IonTitle, IonContent, IonList, IonItem, IonButtons, IonCheckbox, IonIcon } from "@ionic/vue";
import { addOutline } from "ionicons/icons";

const { all_stops } = useStopsStore();
const { all_child } = storeToRefs(useChildStore());

const modal = ref();

const cancel = () => {
    all_stops.forEach(stop => {
        stop.passengerList.forEach(passenger => {
            passenger.checked = false;
        });
    });

    modal.value.$el.dismiss(null, "cancel");
};

const confirm = () => {
    all_stops.forEach(stop => {
        stop.passengerList.forEach(passenger => {
            if (passenger.checked) {
                passenger.onBoard = true;
            }
            passenger.checked = false;
        });
    });
    modal.value.$el.dismiss(null, "cancel");
};

const getChildName = (childId) => {
  let foundChild = null;
  for (let i = 0; i < all_child.value.length; i++) {
    const child = all_child.value[i];
    if (child.objectId === childId) {
      foundChild = child;
    }
  }
  return foundChild.name + " " + foundChild.surname;
};
</script>

<template>
    <div>
        <ion-button id="open-modal"><ion-icon slot="start" :icon="addOutline"></ion-icon>Bambini</ion-button>
        <ion-modal ref="modal" trigger="open-modal">
            <div class="ion-padding-left ion-padding-right ion-padding-top">
                <ion-title>Bambini</ion-title>
            </div>
            <ion-content>
                <ion-list>
                    <div v-for="stop in all_stops" :key="stop.name">
                        <div v-for="passenger in stop.passengerList" :key="passenger">
                            <ion-item v-if="passenger.onBoard == false">
                                <ion-checkbox v-model="passenger.checked">{{ getChildName(passenger.passenger) }}</ion-checkbox>
                            </ion-item>
                        </div>
                    </div>
                </ion-list>
            </ion-content>
            <div>
                <ion-buttons class="ion-padding ion-justify-content-end">
                    <ion-button @click="cancel()">Cancel</ion-button>
                    <ion-button @click="confirm()">Confirm</ion-button>
                </ion-buttons>
            </div>
        </ion-modal>
    </div>
</template>