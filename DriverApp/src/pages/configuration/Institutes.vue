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
    selected(all_institutes.value[0]);
    router.push("/schools");
  }
});

</script>

<template>
  <base-layout page-title="Configurazione Percorso" page-default-back-link="">
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_institutes">
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
