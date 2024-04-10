<script setup>
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonBackButton,
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
import { useRouter } from 'vue-router';
import VolunteersModal from "../../pages/configuration/VolunteersModal.vue";


const router = useRouter();
const currentPath = router.currentRoute.value.path;
</script>

<template>
  <ion-page id="main-content">
    <!-- menu -->
    <ion-menu class="menu" content-id="main-content" v-if="currentPath === '/stops'">
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

    <ion-header v-if="currentPath === '/stops'">
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-menu-button></ion-menu-button>
        </ion-buttons>
        <ion-title>Menu</ion-title>
        <ion-buttons slot="end">
          <VolunteersModal page-title="stops" />
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <!-- back button -->
    <ion-header v-if="currentPath !== '/stops'">
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button :default-href="pageDefaultBackLink"></ion-back-button>
        </ion-buttons>
        <ion-title>{{ pageTitle }}</ion-title>
        <ion-buttons slot="end">
          <slot name="actions-end"></slot>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <slot />
    </ion-content>
  </ion-page>
</template>