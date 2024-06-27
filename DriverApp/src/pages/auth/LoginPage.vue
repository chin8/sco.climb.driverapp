<script setup>
import { Auth } from "@/services/AuthService";
import {
  IonHeader,
  IonPage,
  IonTitle,
  IonToolbar,
  IonButton,
  IonImg
} from "@ionic/vue";
import { AuthActions } from "ionic-appauth";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { geolocalize } from "../../services/GeoService";
import logo from "../../img/logo_climb.png"

const router = useRouter();
let event = ref("");
Auth.Instance.events$.subscribe((action) => {
  event = JSON.stringify(action);
  if (action.action === AuthActions.SignInSuccess) {
    geolocalize();
    router.push("/Institutes");
  }
});

const signIn = () => {
  Auth.Instance.signIn();
};
</script>
<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Login</ion-title>
      </ion-toolbar>
    </ion-header>
    <div class="ion-padding">
      <ion-img :src="logo" class="ion-padding"></ion-img>
    </div>
    <ion-button class="ion-padding" @click="signIn()">Accedi</ion-button>
  </ion-page>
</template>
