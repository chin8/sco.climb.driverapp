<script setup>
import { ref } from 'vue';

import {
    IonPage,
    IonList,
    IonItem,
    IonToggle,
    IonRange,
    IonIcon,
    IonButton
} from "@ionic/vue";
import { addOutline, removeOutline } from 'ionicons/icons';

const myToggle = ref(false);
</script>

<template>
    <ion-page>
        <ion-list>
            <ion-item>
                <ion-toggle v-model="myToggle" justify="space-between">Modifica la precisione</ion-toggle>
            </ion-item>
            <div v-if="myToggle" class="ion-padding-horizontal">
                <ion-range class="ion-padding-horizontal ion-padding-top" aria-label="Range with ticks" :ticks="true"
                    :snaps="true" :min="0" :max="6">
                    <ion-icon slot="start" :icon="removeOutline"></ion-icon>
                    <ion-icon slot="end" :icon="addOutline"></ion-icon>
                </ion-range>
            </div>
            <div v-if="myToggle" class="ion-padding">
                <ion-button expand="full" class="ion-margin-top" fill="solid">Salva</ion-button>
            </div>
        </ion-list>
    </ion-page>
</template>