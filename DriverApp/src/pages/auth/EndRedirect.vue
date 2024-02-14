  <script setup>
  import { Auth } from '../../services/AuthService';
  import { IonPage } from '@ionic/vue';
  import { defineComponent } from 'vue';
  import { useRouter } from "vue-router";

  const router = useRouter();


      Auth.Instance.endSessionCallback();
      setInterval(() => {
        router.push('/login');
      }, 2500);

  </script>
<template>
    <ion-page>
      <p>Signing Out...</p>
    </ion-page>
  </template>
  

  