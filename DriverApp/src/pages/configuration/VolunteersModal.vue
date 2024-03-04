<script lang="ts" setup>
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import {
  IonButtons,
  IonListHeader,
  IonButton,
  IonModal,
  IonHeader,
  IonContent,
  IonToolbar,
  IonTitle,
  IonItem,
  IonList,
  IonAvatar,
  IonImg,
  IonLabel,
  IonCheckbox,
  IonFooter,
} from "@ionic/vue";
import { ref } from "vue";
import { useVolunteersStore } from "../../store/volunteers";
import { useSchoolStore } from "../../store/school";
import { useInstituteStore } from "../../store/institute";

const { fetchVolunteers, selected, delete_selected } = useVolunteersStore();
const { all_volunteers, loading, error, selectedVolunteers } = storeToRefs(
  useVolunteersStore()
);
const { selectedSchool } = storeToRefs(useSchoolStore());
const { selectedInstitute } = storeToRefs(useInstituteStore());

let modalInputs = [];

const modal = ref();

const cancel = () => {
  modal.value.$el.dismiss(null, "cancel");
  if (selectedVolunteers && selectedVolunteers.value) {
    modalInputs.forEach((input) => {
      input.checked = selectedVolunteers.value.some(
        (selected) => selected.objectId === input.value.objectId
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

onMounted(async () => {
  await fetchVolunteers(
    selectedInstitute.value.objectId,
    selectedSchool.value.objectId
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
  }
});
</script>

<template>
  <div class="ion-padding-top">
    <ion-list-header>
      <ion-label>Volontari</ion-label>
      <ion-button id="open-modal">Seleziona</ion-button>
    </ion-list-header>
    <ion-modal ref="modal" trigger="open-modal">
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
