import { createI18n } from 'vue-i18n'
  import it from '../locales/it.json'
  import en from '../locales/en.json'

  const i18n = createI18n({
    legacy: false,
    locale: import.meta.env.VITE_I18N_LOCALE || 'it',
    fallbackLocale: import.meta.env.VITE_I18N_FALLBACK_LOCALE || 'en',
    messages: {
      en,
      it    }
  })

  export default i18n