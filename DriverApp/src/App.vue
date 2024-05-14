<script setup lang="ts">
import {
  IonApp, IonRouterOutlet
} from '@ionic/vue';

</script>

<template>
  <ion-app>
    <base-layout>
      <ion-router-outlet id="main-content" />
    </base-layout>
  </ion-app>
</template>
