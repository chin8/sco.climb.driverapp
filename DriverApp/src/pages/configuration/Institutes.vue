<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useInstituteStore } from "../../store/institute";
import {
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonIcon,
  IonHeader,
  IonButtons,
  IonButton,
  IonSpinner
} from "@ionic/vue";
import { arrowBack, chevronForward } from "ionicons/icons";
import { defineComponent } from "vue";
import { all } from "axios";

const { all_institutes, loading, error, selectedInstitute } = storeToRefs(
  useInstituteStore()
);
const { fetchInstitutes, selected } = useInstituteStore();

const router = useRouter();

onMounted(async () => {
  await fetchInstitutes();
  
  if (all_institutes.value && all_institutes.value.length === 1) {
    setTimeout(() => {
      selected(all_institutes.value[0]);
      router.push({ path: '/schools', replace: true, transition: false });
    }, 1500);
  }
});

</script>

<template>
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
          <ion-item
            v-for="institute in all_institutes"
            :key="institute.objectId"
            @click="selected(institute), router.push('/schools')"
          >
            <ion-label>{{ institute.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </base-layout>
</template>
