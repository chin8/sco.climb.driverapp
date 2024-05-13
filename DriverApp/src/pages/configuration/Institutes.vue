<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useInstituteStore } from "../../store/institute";
import {
  IonList,
  IonItem,
  IonLabel,
  IonIcon,
  IonSpinner,
  IonPage
} from "@ionic/vue";
import { chevronForward } from "ionicons/icons";
import { Auth } from "@/services/AuthService";
import { AuthActions } from "ionic-appauth";

const { all_institutes, loading, error } = storeToRefs(
  useInstituteStore()
);
const { fetchInstitutes, selected } = useInstituteStore();

const router = useRouter();

onMounted(async () => {
  Auth.Instance.events$.subscribe(async (action) => {
    if (action.action === AuthActions.LoadTokenFromStorageSuccess || action.action === AuthActions.SignInSuccess) {
    await fetchInstitutes();
  if (all_institutes.value && all_institutes.value.length === 1) {
    setTimeout(() => {
      selected(all_institutes.value[0]);
      router.push({ path: '/schools', replace: true, transition: false });
    }, 1500);
  }
}
});
  })

  

</script>

<template>
  <ion-page>
    <base-layout page-title="Configurazione Percorso" page-default-back-link="">
      <div>
        <p v-if="loading">Loading...</p>
        <p v-if="error">{{ error.message }}</p>
        <div v-if="all_institutes && all_institutes.length < 2" class="ion-padding ion-text-center">
          <ion-spinner class="ion-padding-top"></ion-spinner>
          <ion-label class="ion-padding">Autoselezione dell'instituto...</ion-label>
        </div>
        <div v-if="all_institutes && all_institutes.length > 1">
          <ion-list>
            <ion-item v-for="institute in all_institutes" :key="institute.objectId"
              @click="selected(institute), router.push('/schools')">
              <ion-label>{{ institute.name }}</ion-label>
              <ion-icon :icon="chevronForward" slot="end"></ion-icon>
            </ion-item>
          </ion-list>
        </div>
      </div>
    </base-layout>
  </ion-page>
</template>
