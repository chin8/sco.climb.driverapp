<script setup>
import { Auth } from "../../services/AuthService";
import { IonPage } from "@ionic/vue";
import { AuthActions } from "ionic-appauth";
import { Subscription } from "rxjs";
import { defineComponent, ref,onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";

const route = useRoute();
const router = useRouter();
onBeforeUnmount(() => {
  sub.unsubscribe();
});
let sub = ref(Subscription.EMPTY);
sub = Auth.Instance.events$.subscribe((action) => {
  if (action.action === AuthActions.SignInSuccess) {
  setTimeout(() => router.push("/Institutes"), 2500);
  }

  if (action.action === AuthActions.SignInFailed) {
    setTimeout(() => router.push("/login"), 2500);
  }
});

const url = route.fullPath;
Auth.Instance.authorizationCallback(url);

</script>
<template>
  <ion-page>
    <p>Signing in...</p>
  </ion-page>
</template>
