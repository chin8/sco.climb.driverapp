<script setup>
import { RouterLink } from "vue-router";
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useRouteStore } from "../../store/configuration/route";
import { useSchoolStore } from "../../store/configuration/school";
import { useInstituteStore } from "../../store/configuration/institute";
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


const { selectedSchool } = storeToRefs(
  useSchoolStore()
);
const { selectedInstitute } = storeToRefs(
  useInstituteStore()
);
const { all_routes, loading, error, selectedRoutes } = storeToRefs(
  useRouteStore()
);
const { fetchRoutes, selected } = useRouteStore();

const router = useRouter();

fetchRoutes(selectedSchool.value, selectedInstitute.value);

</script>

<template>
  <base-layout
    page-title="Configurazione Percorso"
    page-default-back-link="/schools"
  >
    <div>
      <p v-if="loading">Loading...</p>
      <p v-if="error">{{ error.message }}</p>
      <div v-if="all_routes">
        <ion-list>
          <ion-item
            v-for="route in all_routes"
            :key="route.objectId"
            @click="selected(route), router.push('/volunteers')"
          >
            <ion-label>{{ route.name }}</ion-label>
            <ion-icon :icon="chevronForward" slot="end"></ion-icon>
          </ion-item>
        </ion-list>
      </div>
    </div>
  </base-layout>
</template>
