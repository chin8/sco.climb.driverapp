<script lang="ts" setup>
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import {
  IonButtons,
  IonListHeader,
  IonButton,
  IonModal,
  IonContent,
  IonTitle,
  IonItem,
  IonList,
  IonLabel,
  IonCheckbox,
  IonIcon
} from "@ionic/vue";
import {
  addOutline
} from "ionicons/icons";

import { ref } from "vue";
import { useVolunteersStore } from "../../store/volunteers";
import { useSchoolStore } from "../../store/school";
import { useInstituteStore } from "../../store/institute";

const { fetchVolunteers, selected } = useVolunteersStore();
const { all_volunteers, selectedVolunteers } = storeToRefs(
  useVolunteersStore()
);
const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedInstitute } = storeToRefs(useInstituteStore());

let modalInputs: any[] = [];

const modal = ref();

const cancel = () => {
  modal.value.$el.dismiss(null, "cancel");
  if (selectedVolunteers && selectedVolunteers.value) {
    modalInputs.forEach((input) => {
      input.checked = selectedVolunteers.value?.some(
        (selected: { objectId: any; }) => selected.objectId === input.value.objectId
      );
    });
  }
};

const confirm = () => {
  modal.value.$el.dismiss(null, "cancel");
  const selectedVolunteers = modalInputs
    .filter((input) => input.checked)
    .map((input) => input.value);
  selected(selectedVolunteers);
};

defineProps(['pageTitle']);

onMounted(async () => {
  await fetchVolunteers(
    selectedInstitute.value?.objectId,
    selectedSchool.value?.objectId
  );

  if (all_volunteers.value) {
    const volunteers = all_volunteers.value;
    if (volunteers) {
      modalInputs = Object.keys(volunteers).map((key) => {
        const volunteer = volunteers[key];
        return {
          label: volunteer.name,
          value: volunteer,
          checked: false,
        };
      });
    }
    cancel();
  }
});
</script>

<template>
  <div>
    <ion-list-header v-if="pageTitle === 'volunteers'">
      <ion-label>Volontari selezionati</ion-label>
      <ion-button id="open-modal-volunteers"><ion-icon slot="start" :icon="addOutline"></ion-icon>Aggiungi</ion-button>
    </ion-list-header>

    <ion-button v-if="pageTitle === 'stops'" id="open-modal-stops"><ion-icon slot="start" :icon="addOutline"></ion-icon>Volontari</ion-button>

    <ion-modal ref="modal" :trigger="pageTitle === 'volunteers' ? 'open-modal-volunteers' : 'open-modal-stops'">
      <div class="ion-padding-left ion-padding-right ion-padding-top">
        <ion-title>Volontari</ion-title>
      </div>
      <ion-content>
        <ion-list>
          <ion-item v-for="vol in modalInputs" :key="vol.label">
            <ion-checkbox v-model="vol.checked">{{ vol.label }}</ion-checkbox>
          </ion-item>
        </ion-list>
      </ion-content>
      <div>
        <ion-buttons class="ion-padding ion-justify-content-end">
          <ion-button @click="cancel()">Cancel</ion-button>
          <ion-button @click="confirm()">Confirm</ion-button>
        </ion-buttons>
      </div>
    </ion-modal>
  </div>
</template>

<style>
ion-modal {
  --height: 50%;
  --width: 90%;
  --border-radius: 16px;
  --box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1),
    0 4px 6px -4px rgb(0 0 0 / 0.1);
}

ion-modal::part(backdrop) {
  background: rgba(209, 213, 219);
  opacity: 1;
}
</style>