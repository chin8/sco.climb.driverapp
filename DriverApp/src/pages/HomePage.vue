<script setup>
import { IonButton, IonSelect, IonSelectOption, IonCard,IonCardHeader, IonCardContent } from "@ionic/vue";
import { useRouter } from "vue-router";
import { ref } from 'vue'
  import { useI18n } from "vue-i18n"
  import i18n from "../plugins/i18n";
  import { setLocale } from '@vee-validate/i18n'
  import { Auth } from '@/services/AuthService';
  import { AuthActions } from "ionic-appauth";

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
const apiserver = import.meta.env.VITE_SERVER_URL;
let userLocal= ref('');
// eslint-disable-next-line @typescript-eslint/no-unused-vars
let event= ref('');
const subs  =ref([]);
let tokenLocal = ref("");

    subs.value.push(
        Auth.Instance.events$.subscribe((action) => {
          event = JSON.stringify(action);
          if (action.action === AuthActions.SignOutSuccess) {
            router.push('/login');
          }
        }),
        Auth.Instance.user$.subscribe((user) => {
          userLocal = JSON.stringify(user);
        })),
        Auth.Instance.token$.subscribe((token) => {
          tokenLocal= JSON.stringify(token?.accessToken);
        })
        // token = await Auth.Instance.getValidToken();

</script>
<template>
  <base-layout page-title="Home">
    <div>{{ $t("welcome") }}</div>
    <ion-select v-model="selected" @ionChange="changeLocale(selected)">
      <ion-select-option disabled value="">{{
        $t("selection.language")
      }}</ion-select-option>
      <ion-select-option
        v-for="language in languages"
        :key="language.language"
        :value="language.language"
        >{{ language.title }}</ion-select-option
      >
    </ion-select>
    <ion-button @click="() => router.push('/volunteers')">{{
      $t("home.button")
    }}</ion-button>
    <ion-button @click="() => router.push('/profile')">{{
      $t("home.profile")
    }}</ion-button>
    <ion-button @click="() => Auth.Instance.loadUserInfo()">User Info</ion-button>
    <ion-button @click="() => Auth.Instance.signOut()">Sign Out</ion-button>
    <ion-card v-if="userLocal !== ''">
      <ion-card-header> {{ apiserver }}</ion-card-header>
      <ion-card-content>
        {{ userLocal }}
      </ion-card-content>
    </ion-card>
    <ion-card v-if="tokenLocal">
      <ion-card-header>Token</ion-card-header>
      <ion-card-content>
        {{ tokenLocal }}
      </ion-card-content>
    </ion-card>
  </base-layout>
</template>
