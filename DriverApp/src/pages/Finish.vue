<script setup>
import {
    IonPage,
    IonButton,
    IonImg,
    IonIcon
} from "@ionic/vue";
import { checkmarkCircle } from 'ionicons/icons';
import { useRouter } from "vue-router";
import { useStopsStore } from "../store/stops";
import image from "../img/finish_image.png"

const { reset } = useStopsStore();
const router = useRouter()

</script>

<template>
    <ion-page>
        <div class="ion-padding ion-text-center">
            <ion-icon :icon="checkmarkCircle" color="primary" size="large" class="ion-padding-top"></ion-icon>
            <h3 class="">Dati inviati con successo!</h3>
            <ion-img :src="image" class="style-img"></ion-img>
        </div>
        <ion-button @click="reset(); router.push('/institutes')" class="ion-padding">Nuovo percorso</ion-button>
    </ion-page>
</template>

<style>
.style-img{
    margin-top: 75px;
    padding: 20px 50px;
}
</style>