<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useSchoolStore } from "../../store/school";
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

const { all_schools, loading, error, selectedSchool } = storeToRefs(
  useSchoolStore()
);
const { selectedInstitute } = storeToRefs(
  useInstituteStore()
);
const { fetchSchools, selected } = useSchoolStore();

const router = useRouter();

fetchSchools(selectedInstitute.value);
</script>

<template>
  <base-layout
    page-title="Configurazione Percorso"
    page-default-back-link="/institutes"
  >
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_schools">
        <ion-list>
          <ion-item
            v-for="school in all_schools"
            :key="school.objectId"
            @click="selected(school), router.push('/routes')"
          >
            <ion-label>{{ school.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </base-layout>
</template>
