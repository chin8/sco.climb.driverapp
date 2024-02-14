<script setup>
import { Auth } from "@/services/AuthService";
import {
  IonContent,
  IonHeader,
  IonPage,
  IonTitle,
  IonToolbar,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonButton,
} from "@ionic/vue";
import { AuthActions } from "ionic-appauth";
import { defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
let event = ref("");
Auth.Instance.events$.subscribe((action) => {
  event = JSON.stringify(action);
  if (action.action === AuthActions.SignInSuccess) {
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
        <ion-title>Logged Out</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content>
      <ion-button @click="signIn()">Sign In</ion-button>
      <ion-card v-if="event !== ''">
        <ion-card-header> Action Data </ion-card-header>
        <ion-card-content>
          {{ event }}
        </ion-card-content>
      </ion-card>
    </ion-content>
  </ion-page>
</template>
