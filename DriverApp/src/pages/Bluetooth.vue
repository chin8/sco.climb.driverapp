<script setup>
import { ref } from 'vue';

import {
    IonPage,
    IonList,
    IonItem,
    IonToggle,
    IonRange,
    IonGrid,
    IonRow,
    IonCol,
    IonButton
} from "@ionic/vue";

const myToggle = ref(false);
const defaultRange = ref(3);
const range = ref(defaultRange.value);

const saveRange = () => {
    defaultRange.value = range.value;
};
</script>

<template>
    <ion-page>
        <ion-list>
            <ion-item>
                <ion-toggle v-model="myToggle" justify="space-between">Modifica distanza segnale</ion-toggle>
            </ion-item>
            <div v-if="myToggle" class="ion-padding-horizontal">
                <ion-grid>
                    <ion-row>
                        <ion-col class="ion-text-center" size="2">
                            <p>min</p>
                        </ion-col>
                        <ion-col size="8">
                            <ion-range class="ion-padding-horizontal ion-padding-top" aria-label="Range with ticks"
                                :ticks="true" :snaps="true" :min="0" :max="6" :pin="true" v-model="range">
                            </ion-range>
                        </ion-col>
                        <ion-col class="ion-text-center" size="2">
                            <p>max</p>
                        </ion-col>
                    </ion-row>
                </ion-grid>
            </div>
            <div v-if="myToggle" class="ion-padding">
                <ion-button expand="full" class="ion-margin-top" fill="solid" :disabled="range === defaultRange" @click="saveRange">Salva</ion-button>
            </div>
        </ion-list>
    </ion-page>
</template>
