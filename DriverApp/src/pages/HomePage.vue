<script setup>
import { IonButton, IonContent, IonPage } from "@ionic/vue";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

// export default defineComponent({
//   name: "HomePage",
//   components: {
//     IonButton,
//     IonContent,
//     IonPage,
//   },
//   setup() {
//     const router = useRouter();
//     return { router };
//   },
// });
</script>
<template>
  <ion-page>
    <ion-content>
      <div>Welcome</div>
      <ion-button @click="() => router.push('/volunteers')">Volontari</ion-button>
    </ion-content>
  </ion-page>
</template>
