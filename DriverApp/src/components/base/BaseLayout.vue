<script setup>
import { storeToRefs } from "pinia";
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButtons,
  IonItem,
  IonIcon,
  IonLabel,
  IonList,
  IonMenu,
  IonMenuButton
} from "@ionic/vue";
import {
  bluetooth,
  people,
  logOut,
} from "ionicons/icons";
defineProps(['pageTitle', 'pageDefaultBackLink']);

import { useStopsStore } from "../../store/stops";
const { menu_layout } = storeToRefs(useStopsStore());
const { toggleModal } = useStopsStore();

const toggle_modal = () => {
  toggleModal()
}
</script>

<template>
    <!-- menu -->
    <ion-menu v-if="menu_layout" @ionWillClose="toggle_modal()" @ionWillOpen="toggle_modal()" menu-id="main-menu"
      content-id="main-content">
      <ion-header>
        <ion-toolbar>
          <ion-title>Nome Cognome</ion-title>
        </ion-toolbar>
      </ion-header>
      <ion-content>
        <ion-list>
          <ion-item>
            <ion-icon :icon="people" slot="start"></ion-icon>
            <ion-label>Volontari</ion-label>
          </ion-item>
          <ion-item>
            <ion-icon :icon="bluetooth" slot="start"></ion-icon>
            <ion-label>Bluetooth</ion-label>
          </ion-item>
          <ion-item>
            <ion-icon :icon="logOut" slot="start"></ion-icon>
            <ion-label>Logout</ion-label>
          </ion-item>
        </ion-list>
      </ion-content>
    </ion-menu>

    <ion-page>
      <ion-header v-if="menu_layout">
        <ion-toolbar>
          <ion-buttons slot="start">
            <ion-menu-button menu="main-menu"></ion-menu-button>
          </ion-buttons>
          <ion-title>Menu</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-content>
        <slot />
      </ion-content>
    </ion-page>
</template>