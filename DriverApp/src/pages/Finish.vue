<script setup>
import {
    IonPage,
    IonButton
} from "@ionic/vue";
import {  } from 'ionicons/icons';
import { useRouter } from "vue-router";
import { useStopsStore } from "../store/stops";

const { reset } = useStopsStore();
const router = useRouter()

</script>

<template>
    <ion-page>
        <div class="ion-padding">
            <h1>Dati inviati con successo</h1>
            <ion-button @click="reset(); router.push('/')">Nuovo percorso</ion-button>
        </div>
    </ion-page>
</template>