<script setup>
import { IonButton, IonContent, IonPage, IonSelect, IonSelectOption } from "@ionic/vue";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import { ref } from 'vue'
  import { useI18n } from "vue-i18n"
  import i18n from "../plugins/i18n";

  import { Field } from 'vee-validate'
  import { setLocale } from '@vee-validate/i18n'
  
  const selected = ref('')

  const languages = ref([
    { language: 'en', title: 'English' },
    { language: 'it', title: 'Italiano' }
  ])

  const { t } = useI18n({
    inheritLocale: true,
    useScope: 'local'
  })

const changeLocale = (locale) => {
    i18n.global.locale.value = locale
    setLocale(locale);
  }
const router = useRouter();

</script>
<template>
    <base-layout page-title="Home">
      <div>{{ $t("welcome") }}</div>
      <ion-select v-model="selected" @ionChange="changeLocale(selected)" >
        <ion-select-option disabled value="">{{ $t("selection.language") }}</ion-select-option>
        <ion-select-option v-for="language in languages" :key="language.language" :value="language.language">{{ language.title }}</ion-select-option> 
      </ion-select>
      <ion-button @click="() => router.push('/volunteers')">{{ $t("home.button") }}</ion-button>
   </base-layout>
</template>
